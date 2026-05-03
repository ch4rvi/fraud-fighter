package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.DecisionAction;

@Data
public class Decision {
     DecisionAction decisionAction;
     String decisionText;
}
