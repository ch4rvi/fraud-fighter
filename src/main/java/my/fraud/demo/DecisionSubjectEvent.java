package my.fraud.demo;

import lombok.Data;

@Data
public class DecisionSubjectEvent {
    String name;
    String type;
    String source;
}
