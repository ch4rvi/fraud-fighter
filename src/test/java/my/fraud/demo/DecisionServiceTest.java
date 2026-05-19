package my.fraud.demo;

import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.Account;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import my.fraud.demo.service.DecisionService;
import my.fraud.demo.service.DecisionServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DecisionServiceTest {

    private final DecisionService decisionService = new DecisionServiceImpl();

    @Test
    void getDecisionReturnsAllowWhenSourceGeorgeTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(0);
        Account debtorAccount = new Account("111", "0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsHoldWhenSourceBranchTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("Branch");
        decisionSubjectEvent.setAmount(0);
        Account debtorAccount = new Account("111", "0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsDenyWhenSourceAtmTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("ATM");
        decisionSubjectEvent.setAmount(0);
        Account debtorAccount = new Account("111", "0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY, decision.getDecisionAction());
    }

    @Test
    void getDecisionReturnsAllowWhenAmountUnderHoldTresholdTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(99999);
        Account debtorAccount = new Account("111", "0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsHoldWhenAmountUnderDenyTresholdTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(199999);
        Account debtorAccount = new Account("111", "0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsDenyWhenAmountOverDenyTresholdTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(201000);
        Account debtorAccount = new Account("111", "0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsAllowWhenAccountNotOnWatchlist() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(1);
        Account debtorAccount = new Account("111", "0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW, decision.getDecisionAction());
    }

    @Test
    void getDecisionReturnsAllowWhenAccountOnWatchlistLowRisk() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(1);
        Account debtorAccount = new Account("123", "0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW, decision.getDecisionAction());
    }

    @Test
    void getDecisionReturnsHoldWhenAccountOnWatchlistMediumRisk() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(1);
        Account debtorAccount = new Account("456", "0200");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD, decision.getDecisionAction());
    }

    @Test
    void getDecisionReturnsDenyWhenAccountOnWatchlistHighRisk() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(1);
        Account debtorAccount = new Account("789", "0300");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY, decision.getDecisionAction());
    }

}
