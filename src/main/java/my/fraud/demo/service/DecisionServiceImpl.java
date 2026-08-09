package my.fraud.demo.service;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.enums.AccountRiskLevel;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;



@Slf4j
@Service
public class DecisionServiceImpl implements DecisionService {

    private WatchlistService watchlistService;
    private TransactionHistoryService transactionHistoryService;

    public DecisionServiceImpl(WatchlistService watchlistService, TransactionHistoryService transactionHistoryService) {
        this.watchlistService = watchlistService;
        this.transactionHistoryService = transactionHistoryService;
    }

    private final Integer AMOUNT_TRESHOLD_TO_HOLD = 100000;
    private final Integer AMOUNT_TRESHOLD_TO_DENY = 200000;


    @Override
    public Decision getDecision(DecisionSubjectEvent decisionSubjectEvent) {
        try {
            Decision decision = new Decision();
            return makeDecision(decisionSubjectEvent, decision);
        } catch (DecisionException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    private Decision makeDecision(DecisionSubjectEvent decisionSubjectEvent, Decision decision) throws DecisionException{
        if (getDecisionActionForSource(decisionSubjectEvent) == DecisionAction.ALLOW
                && getDecisionActionForAmount(decisionSubjectEvent) == DecisionAction.ALLOW
                && getDecisionActionForAccount(decisionSubjectEvent, watchlistService.getAccountWatchlist()) == DecisionAction.ALLOW
                && getDecisionActionForVelocityRule(decisionSubjectEvent, transactionHistoryService.getTransactionHistory()) == DecisionAction.ALLOW) {
            decision.setDecisionAction(DecisionAction.ALLOW);
        }
        if (getDecisionActionForSource(decisionSubjectEvent) == DecisionAction.HOLD
                || getDecisionActionForAmount(decisionSubjectEvent) == DecisionAction.HOLD
                || getDecisionActionForAccount(decisionSubjectEvent, watchlistService.getAccountWatchlist()) == DecisionAction.HOLD
        || getDecisionActionForVelocityRule(decisionSubjectEvent, transactionHistoryService.getTransactionHistory()) == DecisionAction.HOLD) {
            decision.setDecisionAction(DecisionAction.HOLD);
        }
        if (getDecisionActionForSource(decisionSubjectEvent) == DecisionAction.DENY
                || getDecisionActionForAmount(decisionSubjectEvent) == DecisionAction.DENY
                || getDecisionActionForAccount(decisionSubjectEvent, watchlistService.getAccountWatchlist()) == DecisionAction.DENY
                || (getDecisionActionForSource(decisionSubjectEvent) == DecisionAction.HOLD
                && getDecisionActionForAmount(decisionSubjectEvent) == DecisionAction.HOLD)) {
            decision.setDecisionAction(DecisionAction.DENY);
        }

        decision.setDecisionText(createDecisionText(decisionSubjectEvent, decision));

        log.info("Výsledek rozhodnutí {} s komentářem '{}'", decision.getDecisionAction(), decision.getDecisionText());
        return decision;
    }

    private DecisionAction getDecisionActionForAmount(DecisionSubjectEvent decisionSubjectEvent) throws DecisionException{
        if (decisionSubjectEvent.getAmount() > AMOUNT_TRESHOLD_TO_DENY) {
            return DecisionAction.DENY;
        }
        if (decisionSubjectEvent.getAmount() > AMOUNT_TRESHOLD_TO_HOLD) {
            return DecisionAction.HOLD;
        }
        if (decisionSubjectEvent.getAmount() <= AMOUNT_TRESHOLD_TO_HOLD) {
            return DecisionAction.ALLOW;
        }
        throw new DecisionException("Hodnotě amount neodpovídá žádná DecisionAction.");
    }

    private DecisionAction getDecisionActionForSource(DecisionSubjectEvent decisionSubjectEvent) throws DecisionException{
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("ib")) {
            return DecisionAction.ALLOW;
        }
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("branch")) {
            return DecisionAction.HOLD;
        }
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("atm")) {
            return DecisionAction.DENY;
        }
        throw new DecisionException("Hodnotě source neodpovídá žádná DecisionAction.");
    }

    private DecisionAction getDecisionActionForAccount(DecisionSubjectEvent decisionSubjectEvent, List<AccountWatchlistEntry> accountWatchlist) throws DecisionException {
        if (decisionSubjectEvent == null) {
            return null;
        }

        Optional<AccountWatchlistEntry> matchingWatchlistEntry = accountWatchlist.stream()
                .filter(e -> isMatchingAccount(decisionSubjectEvent.getDebtorAccount(), e))
                .findFirst();

        if (matchingWatchlistEntry.isPresent()) {
            return getDecisionActionForRiskLevel(matchingWatchlistEntry.get());
        } else {
            return DecisionAction.ALLOW;
        }
    }

    private boolean isMatchingAccount(Account account, AccountWatchlistEntry accountWatchlistEntry) {
        return account.getAccountNumber().equals(accountWatchlistEntry.getAccountOnWatch().getAccountNumber())
                && account.getBankCode().equals(accountWatchlistEntry.getAccountOnWatch().getBankCode());
    }

    private DecisionAction getDecisionActionForRiskLevel(AccountWatchlistEntry accountWatchlistEntry) throws DecisionException{
        if (accountWatchlistEntry.getAccountRiskLevel() == AccountRiskLevel.LOW) {
            return DecisionAction.ALLOW;
        } else if (accountWatchlistEntry.getAccountRiskLevel() == AccountRiskLevel.MEDIUM) {
            return DecisionAction.HOLD;
        } else if (accountWatchlistEntry.getAccountRiskLevel() == AccountRiskLevel.HIGH) {
            return DecisionAction.DENY;
        }
        throw new DecisionException("Hodnotě account neodpovídá žádná DecisionAction.");
    }

    private String createDecisionText(DecisionSubjectEvent decisionSubjectEvent, Decision decision) {
        String decisionText = "Received event at the amount of " + decisionSubjectEvent.getAmount().toString()
                + " of type " + decisionSubjectEvent.getType()
                + " from " + decisionSubjectEvent.getSource()
                + " to decide. Decision: " + decision.getDecisionAction().toString();

        if (decisionSubjectEvent.getType() == null) {
            decisionText += " Warning: Pro přesnější rozhodnutí poskytněte typ operace.";
        }
        return decisionText;
    }

    private DecisionAction getDecisionActionForVelocityRule(DecisionSubjectEvent decisionSubjectEvent, List<TransactionHistoryEntry> transactionHistory) {
        List<TransactionHistoryEntry> accountTransactionHistory = filterTransactionHistoryForAccount(transactionHistory, decisionSubjectEvent.getDebtorAccount());
        if (catchThreeHitsInTenSecondsCriteria(accountTransactionHistory)) {
            log.info("Velocity rule result HOLD");
            return DecisionAction.HOLD;
        } else {
            log.info("Velocity rule result ALLOW");
            return DecisionAction.ALLOW;
        }
    }

    private boolean catchThreeHitsInTenSecondsCriteria(List<TransactionHistoryEntry> accountTransactionHistory) {
        if (accountTransactionHistory.size() < 3) {
            log.info("Transakční historie neobsahuje dostatečný počet transakcí pro velocity rule. Aktuální počet: {}", accountTransactionHistory.size());
            return false;
        }
        Collections.sort(accountTransactionHistory, Comparator.comparing(entry -> entry.getCreatedAt()));
        log.info("Transakční historie seřazena dle data {}", accountTransactionHistory);
        List<TransactionHistoryEntry> lastThree = getLastThreeTransactionHistoryEntries(accountTransactionHistory);
        log.info("Záznam relevantní pro velocity rule {}", lastThree);

        return secondsCountDividingLastThreeEntries(lastThree) < 1000 * 10;
    }

    private long secondsCountDividingLastThreeEntries(List<TransactionHistoryEntry> accountTransactionHistory) {
        return accountTransactionHistory.get(0).getCreatedAt().getTime() - accountTransactionHistory.get(2).getCreatedAt().getTime();
    }

    private List<TransactionHistoryEntry> getLastThreeTransactionHistoryEntries(List<TransactionHistoryEntry> transactionHistory) {
        List<TransactionHistoryEntry> lastThreeTransactionHistoryEntries = new ArrayList<>();
        for (int i = transactionHistory.size() - 3; i < transactionHistory.size(); i++) {
            lastThreeTransactionHistoryEntries.add(transactionHistory.get(i));
        }
        return lastThreeTransactionHistoryEntries;
    }

    private List<TransactionHistoryEntry> filterTransactionHistoryForAccount(List<TransactionHistoryEntry> transactionHistory, Account account) {
        return transactionHistory
                .stream()
                .filter(entry -> isMatchingAccount(entry, account))
                .collect(Collectors.toList());
    }

    private boolean isMatchingAccount(TransactionHistoryEntry entry, Account debtorAccount) {
        return entry.getDebtorAccount().getAccountNumber().equals(debtorAccount.getAccountNumber())
                && entry.getDebtorAccount().getBankCode().equals(debtorAccount.getBankCode());
    }
}
