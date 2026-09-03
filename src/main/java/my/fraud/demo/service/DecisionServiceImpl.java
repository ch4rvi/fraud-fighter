package my.fraud.demo.service;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.enums.AccountRiskLevel;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.enums.Rule;
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

        List<RuleDecisionResult> ruleDecisionResults = getResultForEachRule(decisionSubjectEvent);
        log.info("Výsledky jednotlivých pravidel: {}", ruleDecisionResults);

        setHighestDecisionAction(decision, ruleDecisionResults);
        log.info("Výsledná DecisionAction {}", decision.getDecisionAction());

        decision.setDecisionText(createDecisionText(decisionSubjectEvent, decision));

        decision.setTriggeredRules(collectTriggeredRules(ruleDecisionResults));
        log.info("Kolekce triggrovaných pravidel {}", decision.getTriggeredRules());

        log.info("Výsledek rozhodnutí {} s komentářem '{}'", decision.getDecisionAction(), decision.getDecisionText());
        return decision;
    }

    private void setHighestDecisionAction(Decision decision, List<RuleDecisionResult> ruleDecisionResults) {
        decision.setDecisionAction(DecisionAction.ALLOW);
        selectHighestLevelDecisionAction(decision, ruleDecisionResults);
    }

    private List<RuleDecisionResult> getResultForEachRule( DecisionSubjectEvent decisionSubjectEvent) throws DecisionException{
        List<RuleDecisionResult> ruleDecisionResults = new ArrayList<>();
        ruleDecisionResults.add(getDecisionActionForSource(decisionSubjectEvent));
        ruleDecisionResults.add(getDecisionActionForAmount(decisionSubjectEvent));
        ruleDecisionResults.add(getDecisionActionForAccount(decisionSubjectEvent, watchlistService.getAccountWatchlist()));
        ruleDecisionResults.add(getDecisionActionForVelocityRule(decisionSubjectEvent, transactionHistoryService.getTransactionHistory()));

        return ruleDecisionResults;
    }

    private List<Rule> collectTriggeredRules(List<RuleDecisionResult> ruleDecisionResults) {
        return ruleDecisionResults
                .stream()
                .filter(result -> result.getRuleAction() != DecisionAction.ALLOW)
                .map(RuleDecisionResult::getRuleName)
                .collect(Collectors.toList());
    }

    private void selectHighestLevelDecisionAction(Decision decision, List<RuleDecisionResult> ruleDecisionResults) {
        ruleDecisionResults.forEach(result -> {
            if (decision.getDecisionAction() == DecisionAction.ALLOW
                    && result.getRuleAction() == DecisionAction.HOLD) {
                decision.setDecisionAction(DecisionAction.HOLD);
            }
            if (decision.getDecisionAction() == DecisionAction.ALLOW
                    && result.getRuleAction() == DecisionAction.DENY) {
                decision.setDecisionAction(DecisionAction.DENY);
            }
            if (decision.getDecisionAction() == DecisionAction.HOLD
                    && result.getRuleAction() == DecisionAction.DENY) {
                decision.setDecisionAction(DecisionAction.DENY);
            }
        });
    }

    private RuleDecisionResult getDecisionActionForAmount(DecisionSubjectEvent decisionSubjectEvent) throws DecisionException{
        RuleDecisionResult ruleDecisionResult = new RuleDecisionResult();
        ruleDecisionResult.setRuleName(Rule.TRANSACTION_AMOUNT_RULE);

        if (decisionSubjectEvent.getAmount() > AMOUNT_TRESHOLD_TO_DENY) {
            ruleDecisionResult.setRuleAction(DecisionAction.DENY);
        }
        if (decisionSubjectEvent.getAmount() > AMOUNT_TRESHOLD_TO_HOLD && decisionSubjectEvent.getAmount() <= AMOUNT_TRESHOLD_TO_DENY ) {
            ruleDecisionResult.setRuleAction(DecisionAction.HOLD);
        }
        if (decisionSubjectEvent.getAmount() <= AMOUNT_TRESHOLD_TO_HOLD) {
            ruleDecisionResult.setRuleAction(DecisionAction.ALLOW);
        }
        return ruleDecisionResult;
    }

    private RuleDecisionResult getDecisionActionForSource(DecisionSubjectEvent decisionSubjectEvent){
        RuleDecisionResult ruleDecisionResult = new RuleDecisionResult();
        ruleDecisionResult.setRuleName(Rule.TRANSACTION_SOURCE_RULE);

        if (decisionSubjectEvent.getSource().equalsIgnoreCase("ib")) {
            ruleDecisionResult.setRuleAction(DecisionAction.ALLOW);
        }
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("branch")) {
            ruleDecisionResult.setRuleAction(DecisionAction.HOLD);
        }
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("atm")) {
            ruleDecisionResult.setRuleAction(DecisionAction.DENY);
        }
        return ruleDecisionResult;
    }

    private RuleDecisionResult getDecisionActionForAccount(DecisionSubjectEvent decisionSubjectEvent, List<AccountWatchlistEntry> accountWatchlist) throws DecisionException {
        if (decisionSubjectEvent == null) {
            return null;
        }

        Optional<AccountWatchlistEntry> matchingWatchlistEntry = accountWatchlist.stream()
                .filter(e -> isMatchingAccount(decisionSubjectEvent.getDebtorAccount(), e))
                .findFirst();

        RuleDecisionResult ruleDecisionResult = new RuleDecisionResult();
        ruleDecisionResult.setRuleName(Rule.ACCOUNT_WATCHLIST_RULE);

        if (matchingWatchlistEntry.isPresent()) {
            ruleDecisionResult.setRuleAction(getDecisionActionForRiskLevel(matchingWatchlistEntry.get()));
        } else {
            ruleDecisionResult.setRuleAction(DecisionAction.ALLOW);
        }
        return ruleDecisionResult;
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

    private RuleDecisionResult getDecisionActionForVelocityRule(DecisionSubjectEvent decisionSubjectEvent, List<TransactionHistoryEntry> transactionHistory) {
        List<TransactionHistoryEntry> accountTransactionHistory = filterTransactionHistoryForAccount(transactionHistory, decisionSubjectEvent.getDebtorAccount());

        RuleDecisionResult ruleDecisionResult = new RuleDecisionResult();
        ruleDecisionResult.setRuleName(Rule.TRANSACTION_VELOCITY_RULE);

        if (catchThreeHitsInTenSecondsCriteria(accountTransactionHistory)) {
            log.info("Velocity rule result HOLD");
            ruleDecisionResult.setRuleAction(DecisionAction.HOLD);
        } else {
            log.info("Velocity rule result ALLOW");
            ruleDecisionResult.setRuleAction(DecisionAction.ALLOW);
        }
        return ruleDecisionResult;
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
