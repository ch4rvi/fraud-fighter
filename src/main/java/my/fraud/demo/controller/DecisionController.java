package my.fraud.demo.controller;
import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionException;
import my.fraud.demo.model.DecisionSubjectEvent;
import my.fraud.demo.service.DecisionService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
public class DecisionController {

    public DecisionService decisionService;
    private final Set<String> ALLOWED_SOURCES = Set.of("branch", "atm", "george");

    public DecisionController(DecisionService decisionService) {
            this.decisionService = decisionService;
    }

    @PostMapping(value = "/api/fraud")
    public Decision getDecision(@RequestBody DecisionSubjectEvent decisionSubjectEvent) throws Exception {
        if (decisionSubjectEvent == null) {
            throw new DecisionException("K vydání rozhodnutí chybí předmět posouzení!");
        } else if (decisionSubjectEvent.getSource() == null) {
            throw new DecisionException("K vydání rozhodnutí chybí zdroj!");
        } else if (decisionSubjectEvent.getAmount() == null) {
            throw new DecisionException("K vydání rozhodnutí chybí částka!");
        } else if (!ALLOWED_SOURCES.contains(decisionSubjectEvent.getSource().toLowerCase())) {
            throw new DecisionException("Neznámá hodnota source!");
        } else if (decisionSubjectEvent.getAmount() < 0) {
            throw new DecisionException("Částka nesmí být záporná!");
        } else {
            log.info("Volání žádosti o rozhodnutí o {}", decisionSubjectEvent);
            return decisionService.getDecision(decisionSubjectEvent);
        }
    }
}