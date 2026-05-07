package my.fraud.demo.controller;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;
import my.fraud.demo.service.DecisionService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class DecisionController {

    public DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
            this.decisionService = decisionService;
    }

    @PostMapping(value = "/api/fraud")
    public Decision getDecision(@RequestBody DecisionSubjectEvent decisionSubjectEvent) {
        log.info("Volání žádosti o rozhodnutí o {}", decisionSubjectEvent);
        return decisionService.getDecision(decisionSubjectEvent);
    }
}
