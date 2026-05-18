package my.fraud.demo.service;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.enums.AccountRiskLevel;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DecisionServiceImpl implements DecisionService {

    private final Integer AMOUNT_TRESHOLD_TO_HOLD = 100000;
    private final Integer AMOUNT_TRESHOLD_TO_DENY = 200000;

    private final Account accountA = new Account("123", "0100");
    private final Account accountB = new Account("456", "0200");
    private final Account accountC = new Account("789", "0300");

    private final AccountWatchlistEntry entryA = new AccountWatchlistEntry(accountA, AccountRiskLevel.LOW);
    private final AccountWatchlistEntry entryB = new AccountWatchlistEntry(accountB, AccountRiskLevel.MEDIUM);
    private final AccountWatchlistEntry entryC = new AccountWatchlistEntry(accountC, AccountRiskLevel.HIGH);

    private final List<AccountWatchlistEntry> accountWatchlist = List.of(entryA, entryB, entryC);



    @Override
    public Decision getDecision (DecisionSubjectEvent decisionSubjectEvent) {
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
                && getDecisionActionForAccount(decisionSubjectEvent, accountWatchlist) == DecisionAction.ALLOW) {
            decision.setDecisionAction(DecisionAction.ALLOW);
        }
        if (getDecisionActionForSource(decisionSubjectEvent) == DecisionAction.HOLD
                || getDecisionActionForAmount(decisionSubjectEvent) == DecisionAction.HOLD
                || getDecisionActionForAccount(decisionSubjectEvent, accountWatchlist) == DecisionAction.HOLD) {
            decision.setDecisionAction(DecisionAction.HOLD);
        }
        if (getDecisionActionForSource(decisionSubjectEvent) == DecisionAction.DENY
                || getDecisionActionForAmount(decisionSubjectEvent) == DecisionAction.DENY
                || getDecisionActionForAccount(decisionSubjectEvent, accountWatchlist) == DecisionAction.DENY
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
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("george")) {
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
}
