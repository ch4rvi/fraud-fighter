package my.fraud.demo;

import org.springframework.web.bind.annotation.*;

@RestController
public class DecisionController {
    @PostMapping(value = "api/fraud")
    public String getDecision(@RequestBody DecisionSubjectEvent decisionSubjectEvent) {
        return "Received event of type " + decisionSubjectEvent.type + "from " + decisionSubjectEvent.source + " to decide.";
    }



}
