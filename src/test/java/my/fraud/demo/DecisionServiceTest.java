package my.fraud.demo;

import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import my.fraud.demo.service.DecisionService;
import my.fraud.demo.service.DecisionServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DecisionServiceTest {

    private final DecisionService decisionService = new DecisionServiceImpl();

    @Test
    void getDecisionReturnsNullMessageWhenInputNull() {
        DecisionSubjectEvent decisionSubjectEvent = null;

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertNull(decision.getDecisionAction());
        assertEquals("Error: K vydání rozhodnutí chybí předmět posouzení!", decision.getDecisionText());
    }

    @Test
    void getDecisionReturnsMessageWhenMissingSource() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setName("lol");
        decisionSubjectEvent.setSource(null);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertNull(decision.getDecisionAction());
        assertEquals("Error: K vydání rozhodnutí chybí zdroj!", decision.getDecisionText());
    }

    @Test
    void getDecisionMessageWhenUnknownAmount() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("branch");
        decisionSubjectEvent.setAmount(null);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertNull(decision.getDecisionAction());
        assertEquals("Error: K vydání rozhodnutí chybí částka!", decision.getDecisionText());
    }

    @Test
    void getDecisionReturnsMessageWhenUnknownSource() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setName("name");
        decisionSubjectEvent.setAmount(0);
        decisionSubjectEvent.setSource("unknown_source");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertNull(decision.getDecisionAction());
        assertEquals("Error: Neznámá hodnota source!", decision.getDecisionText());
    }

    @Test
    void getDecisionReturnsMessageWhenNegativeAmount() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("Branch");
        decisionSubjectEvent.setAmount(-1);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals("Error: Částka nesmí být záporná!", decision.getDecisionText());
    }

    @Test
    void getDecisionWhenSourceGeorgeTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(0);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWhenSourceBranchTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("Branch");
        decisionSubjectEvent.setAmount(0);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWhenSourceGeorgeAndAmountOver() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setAmount(101000);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWhenSourceAtmTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("ATM");
        decisionSubjectEvent.setAmount(0);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWhenAmountOverTwoHundred() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("branch");
        decisionSubjectEvent.setAmount(201000);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWhenAmountOverOneHundrerAndSourceBranch() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("branch");
        decisionSubjectEvent.setAmount(101000);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWarningWhenMissingType() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setType(null);
        decisionSubjectEvent.setAmount(0);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertTrue(decision.getDecisionText().contains("Warning: Pro přesnější rozhodnutí poskytněte typ operace."));
    }

    @Test
    void getDecisionWithoutWarningWhenFilledType() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setType("trx");
        decisionSubjectEvent.setAmount(0);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertFalse(decision.getDecisionText().contains("Warning: Pro přesnější rozhodnutí poskytněte typ operace."));

    }

}
