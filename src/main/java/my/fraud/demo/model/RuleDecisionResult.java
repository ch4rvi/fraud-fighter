package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.enums.Rule;

@Data
public class RuleDecisionResult {
    private Rule ruleName;
    private DecisionAction ruleAction;
}
