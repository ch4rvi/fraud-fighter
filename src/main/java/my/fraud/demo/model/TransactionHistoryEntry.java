package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.TransactionSource;

import java.util.Date;

@Data
public class TransactionHistoryEntry {
    Account debtorAccount;
    Account creditorAccount;
    String amount;
    String currency;
    TransactionSource source;
    Date createdAt;
    String transactionId;
}