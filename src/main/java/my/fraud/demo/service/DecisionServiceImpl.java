package my.fraud.demo.service;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class DecisionServiceImpl implements DecisionService {

    private final Integer AMOUNT_TRESHOLD_TO_HOLD = 100000;
    private final Integer AMOUNT_TRESHOLD_TO_DENY = 200000;
    private final Set<String> ALLOWED_SOURCES = Set.of("branch", "atm", "george");

    @Override
    public Decision getDecision (DecisionSubjectEvent decisionSubjectEvent) {

        Decision decision = new Decision();

        if (isSubjectMessageValidToDecide(decisionSubjectEvent, decision)) {
            return makeDecision(decisionSubjectEvent, decision);
        } else {
            return decision;
        }
    }

    private boolean isSubjectMessageValidToDecide (DecisionSubjectEvent decisionSubjectEvent, Decision decision) {
        if (decisionSubjectEvent == null) {
            decision.setInvalidSubjectMessage("Error: K vydání rozhodnutí chybí předmět posouzení!");
            return false;
        } else if (decisionSubjectEvent.getSource() == null) {
            decision.setInvalidSubjectMessage("Error: K vydání rozhodnutí chybí zdroj!");
            return false;
        } else if (decisionSubjectEvent.getAmount() == null) {
            decision.setInvalidSubjectMessage("Error: K vydání rozhodnutí chybí částka!");
            return false;
        } else if (!ALLOWED_SOURCES.contains(decisionSubjectEvent.getSource().toLowerCase())) {
            decision.setInvalidSubjectMessage("Error: Neznámá hodnota source!");
            return false;
        } else if (decisionSubjectEvent.getAmount() < 0) {
            decision.setInvalidSubjectMessage("Error: Částka nesmí být záporná!");
            return false;
        } else {
            return true;
        }
    }

    private Decision makeDecision(DecisionSubjectEvent decisionSubjectEvent, Decision decision) {
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("george")
                && decisionSubjectEvent.getAmount() <= AMOUNT_TRESHOLD_TO_HOLD) {
            decision.setDecisionAction(DecisionAction.ALLOW);
        }
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("branch")
                || decisionSubjectEvent.getAmount() > AMOUNT_TRESHOLD_TO_HOLD) {
            decision.setDecisionAction(DecisionAction.HOLD);
        }
        if (decisionSubjectEvent.getSource().equalsIgnoreCase("atm")
                || decisionSubjectEvent.getAmount() > AMOUNT_TRESHOLD_TO_DENY
                || (decisionSubjectEvent.getSource().equalsIgnoreCase("branch")
                && decisionSubjectEvent.getAmount() > AMOUNT_TRESHOLD_TO_HOLD)) {
            decision.setDecisionAction(DecisionAction.DENY);
        }

        decision.setDecisionText(createDecisionText(decisionSubjectEvent, decision));

        log.info("Výsledek rozhodnutí {} s komentářem '{}'", decision.getDecisionAction(), decision.getDecisionText());
        return decision;
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
