package my.fraud.demo;

import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import my.fraud.demo.service.DecisionService;
import my.fraud.demo.service.DecisionServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecisionServiceTest {

    private final DecisionService decisionService = new DecisionServiceImpl();

    @Test
    public void getDecisionWhenSourceBranchTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("Branch");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.HOLD.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    public void getDecisionWhenSourceAtmTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("ATM");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.DENY.name(), String.valueOf(decision.getDecisionAction()));
    }

    @Test
    public void getDecisionWhenSourceGeorgeTest() {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource("George");

        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        assertEquals(DecisionAction.ALLOW.name(), String.valueOf(decision.getDecisionAction()));
    }
}
