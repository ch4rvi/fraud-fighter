package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.TransactionSource;

import java.util.Date;

@Data
public class TransactionHistoryEntry {
    public Account debtorAccount;
    public Account creditorAccount;
    public Integer amount;
    public String currency;
    public TransactionSource source;
    public Date createdAt;
    public String transactionId;
}