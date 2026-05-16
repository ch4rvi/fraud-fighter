package my.fraud.demo.model;

import lombok.Data;

@Data
public class DecisionSubjectEvent {
    String name;
    String type;
    String source;
    Integer amount;
    Account debtorAccount;
}
