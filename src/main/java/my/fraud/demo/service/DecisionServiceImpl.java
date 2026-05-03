package my.fraud.demo.service;

import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import org.springframework.stereotype.Service;

@Service
public class DecisionServiceImpl implements DecisionService {

    @Override
    public Decision getDecision (DecisionSubjectEvent decisionSubjectEvent) {

        Decision decision = new Decision();

        if (decisionSubjectEvent.getSource().equalsIgnoreCase("branch")) {
            decision.setDecisionAction(DecisionAction.HOLD);
        }
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("atm")) {
            decision.setDecisionAction(DecisionAction.DENY);
        }
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("george")) {
            decision.setDecisionAction(DecisionAction.ALLOW);
        }

        String decisionText = "Received event of type "
                + decisionSubjectEvent.getType() + "from "
                + decisionSubjectEvent.getSource() + " to decide. Decision: " + decision.getDecisionAction().toString();

        decision.setDecisionText(decisionText);

        return decision;
    }
}
