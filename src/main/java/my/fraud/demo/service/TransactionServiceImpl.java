package my.fraud.demo.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.model.Account;
import my.fraud.demo.model.SendTransactionRequest;
import my.fraud.demo.model.TransactionHistoryEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {


    private List<TransactionHistoryEntry> transactionHistory = new ArrayList();

    private void setTransactionHistory(TransactionHistoryEntry transactionHistoryEntry) {
        this.transactionHistory.add(transactionHistoryEntry);
    }



    @Override
    public void send(SendTransactionRequest sendTransactionRequest) {

        TransactionHistoryEntry transactionHistoryEntry = new TransactionHistoryEntry();

        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber(sendTransactionRequest.getDebtorAccount().getAccountNumber());
        debtorAccount.setBankCode(sendTransactionRequest.getDebtorAccount().getBankCode());

        Account creditorAccount = new Account();
        creditorAccount.setAccountNumber(sendTransactionRequest.getCreditorAccount().getAccountNumber());
        creditorAccount.setBankCode(sendTransactionRequest.getCreditorAccount().getBankCode());

        transactionHistoryEntry.setDebtorAccount(debtorAccount);
        transactionHistoryEntry.setCreditorAccount(creditorAccount);
        transactionHistoryEntry.setAmount(sendTransactionRequest.getAmount());
        transactionHistoryEntry.setCurrency(sendTransactionRequest.getCurrency());
        transactionHistoryEntry.setSource(sendTransactionRequest.getSource());
        transactionHistoryEntry.setCreatedAt(new Date());

        log.info("About to log {} to transaction history", transactionHistoryEntry);

        this.setTransactionHistory(transactionHistoryEntry);

        log.info("State of transaction history after logging {}", this.transactionHistory);

    }
}
