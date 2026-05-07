package my.fraud.demo.service;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DecisionServiceImpl implements DecisionService {

    @Override
    public Decision getDecision (DecisionSubjectEvent decisionSubjectEvent) {

        Decision decision = new Decision();
        String decisionText;


        if (decisionSubjectEvent == null) {
            decisionText = "Error: K vydání rozhodnutí chybí předmět posouzení!";
        } else if (decisionSubjectEvent.getSource() == null) {
            decisionText = "Error: K vydání rozhodnutí chybí zdroj!";
        } else if (!decisionSubjectEvent.getSource().equalsIgnoreCase("branch")
                && !decisionSubjectEvent.getSource().equalsIgnoreCase("atm")
                && !decisionSubjectEvent.getSource().equalsIgnoreCase("george")) {
            decisionText = "Error: Neznámá hodnota source!";
        } else { //TODO: tohle přepsat na switch (kata)
            if (decisionSubjectEvent.getSource().equalsIgnoreCase("branch")) {
                decision.setDecisionAction(DecisionAction.HOLD);
            }
            if (decisionSubjectEvent.getSource().equalsIgnoreCase("atm")) {
                decision.setDecisionAction(DecisionAction.DENY);
            }
            if (decisionSubjectEvent.getSource().equalsIgnoreCase("george")) {
                decision.setDecisionAction(DecisionAction.ALLOW);
            }

            decisionText = "Received event of type "
                    + decisionSubjectEvent.getType() + " from "
                    + decisionSubjectEvent.getSource() + " to decide. Decision: " + decision.getDecisionAction().toString();

            if (decisionSubjectEvent.getType() == null) {
                decisionText = decisionText + " Warning: Pro přesnější rozhodnutí poskytněte typ operace.";
            }
        }

        decision.setDecisionText(decisionText);

        log.info("Výsledek rozhodnutí {} s komentářem '{}'", decision.getDecisionAction(), decision.getDecisionText());
        return decision;
    }
}
