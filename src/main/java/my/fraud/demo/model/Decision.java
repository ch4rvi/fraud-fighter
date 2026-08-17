package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.DecisionAction;

import java.util.List;

@Data
public class Decision {
     DecisionAction decisionAction;
     List<String> triggeredRules;
     String decisionText;
}
