package my.fraud.demo;

import my.fraud.demo.enums.AccountRiskLevel;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.Account;
import my.fraud.demo.model.AccountWatchlistEntry;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import my.fraud.demo.service.DecisionService;
import my.fraud.demo.service.DecisionServiceImpl;
import my.fraud.demo.service.WatchlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class DecisionServiceTest {

    @Mock
    WatchlistService watchlistService;

    private DecisionService decisionService;

    @BeforeEach
    void setUp() {
        decisionService = new DecisionServiceImpl(watchlistService);
    }

    @Test
    void getDecisionReturnsAllowWhenSourceGeorgeTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(0);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("111");
        debtorAccount.setBankCode("0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);


        assertEquals(DecisionAction.ALLOW.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsHoldWhenSourceBranchTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("Branch");
        decisionSubjectEvent.setAmount(0);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("111");
        debtorAccount.setBankCode("0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsDenyWhenSourceAtmTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("ATM");
        decisionSubjectEvent.setAmount(0);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("111");
        debtorAccount.setBankCode("0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY, decision.getDecisionAction());
    }

    @Test
    void getDecisionReturnsAllowWhenAmountUnderHoldTresholdTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(99999);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("111");
        debtorAccount.setBankCode("0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsHoldWhenAmountUnderDenyTresholdTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(199999);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("111");
        debtorAccount.setBankCode("0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsDenyWhenAmountOverDenyTresholdTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(201000);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("111");
        debtorAccount.setBankCode("0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionReturnsAllowWhenAccountNotOnWatchlist() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(1);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("111");
        debtorAccount.setBankCode("0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW, decision.getDecisionAction());
    }

    @Test
    void getDecisionReturnsAllowWhenAccountOnWatchlistLowRisk() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(1);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("123");
        debtorAccount.setBankCode("0100");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW, decision.getDecisionAction());
    }

    @Test
    void getDecisionReturnsHoldWhenAccountOnWatchlistMediumRisk() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(1);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("456");
        debtorAccount.setBankCode("0200");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Account account = new Account();
        account.setAccountNumber("456");
        account.setBankCode("0200");
        AccountWatchlistEntry entry = new AccountWatchlistEntry(account, AccountRiskLevel.MEDIUM);

        List<AccountWatchlistEntry> accountWatchlist = new ArrayList<>();
        accountWatchlist.add(entry);

        Mockito.when(watchlistService.getAccountWatchlist()).thenReturn(accountWatchlist);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD, decision.getDecisionAction());
    }

    @Test
    void getDecisionReturnsDenyWhenAccountOnWatchlistHighRisk() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(1);
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber("789");
        debtorAccount.setBankCode("0300");
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        Account account = new Account();
        account.setAccountNumber("789");
        account.setBankCode("0300");
        AccountWatchlistEntry entry = new AccountWatchlistEntry(account, AccountRiskLevel.HIGH);

        List<AccountWatchlistEntry> accountWatchlist = new ArrayList<>();
        accountWatchlist.add(entry);

        Mockito.when(watchlistService.getAccountWatchlist()).thenReturn(accountWatchlist);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY, decision.getDecisionAction());
    }

}
