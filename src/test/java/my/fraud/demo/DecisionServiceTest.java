package my.fraud.demo;

import lombok.extern.log4j.Log4j;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import my.fraud.demo.service.DecisionService;
import my.fraud.demo.service.DecisionServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DecisionServiceTest {

    private final DecisionService decisionService = new DecisionServiceImpl();

    @Test void getDecisionReturnsNullMessageWhenInputNull() {
        DecisionSubjectEvent decisionSubjectEvent = null;

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertNull(decision.getDecisionAction());
        assertEquals("Error: K vydání rozhodnutí chybí předmět posouzení!", decision.getDecisionText());
    }

    @Test void getDecisionReturnsMessageWhenMissingSource() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setName("lol");
        decisionSubjectEvent.setSource(null);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertNull(decision.getDecisionAction());
        assertEquals("Error: K vydání rozhodnutí chybí zdroj!", decision.getDecisionText());
    }

    @Test void getDecisionReturnsMessageWhenUnknownSource() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setName("name");
        decisionSubjectEvent.setSource("unknown_source");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertNull(decision.getDecisionAction());
        assertEquals("Error: Neznámá hodnota source!", decision.getDecisionText());
    }

    @Test
    void getDecisionWhenSourceBranchTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("Branch");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWhenSourceAtmTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("ATM");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWhenSourceGeorgeTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    void getDecisionWarningWhenMissingType() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setType(null);

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertTrue(decision.getDecisionText().contains("Warning: Pro přesnější rozhodnutí poskytněte typ operace."));
    }

    @Test
    void getDecisionWithoutWarningWhenFilledType() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");
        decisionSubjectEvent.setType("trx");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertFalse(decision.getDecisionText().contains("Warning: Pro přesnější rozhodnutí poskytněte typ operace."));

    }

}
