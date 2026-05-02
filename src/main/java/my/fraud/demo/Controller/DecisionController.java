package my.fraud.demo.Controller;

import my.fraud.demo.Model.DecisionSubjectEvent;
import my.fraud.demo.Service.DecisionService;
import org.springframework.web.bind.annotation.*;

@RestController
public class DecisionController {

    public DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
            this.decisionService = decisionService;
    }

    @PostMapping(value = "api/fraud")
    public String getDecision(@RequestBody DecisionSubjectEvent decisionSubjectEvent) {
        return decisionService.getDecision(decisionSubjectEvent);
    }
}
