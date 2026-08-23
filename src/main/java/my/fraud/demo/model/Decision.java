package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.enums.Rule;

import java.util.List;

@Data
public class Decision {
     DecisionAction decisionAction;
     List<Rule> triggeredRules;
     String decisionText;
}
