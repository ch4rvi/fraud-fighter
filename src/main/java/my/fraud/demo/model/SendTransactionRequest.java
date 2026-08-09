package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.TransactionSource;

@Data
public class SendTransactionRequest {
    Account debtorAccount;
    Account creditorAccount;
    Integer amount;
    String currency;
    TransactionSource source;
}

