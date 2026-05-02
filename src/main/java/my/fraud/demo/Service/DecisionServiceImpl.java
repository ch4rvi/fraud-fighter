package my.fraud.demo.Service;

import my.fraud.demo.Model.DecisionSubjectEvent;
import org.springframework.stereotype.Service;

@Service
public class DecisionServiceImpl implements DecisionService {

    @Override
    public String getDecision (DecisionSubjectEvent decisionSubjectEvent) {
        return "Received event of type " + decisionSubjectEvent.getType() + "from " + decisionSubjectEvent.getSource() + " to decide.";
    }
}
