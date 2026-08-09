package my.fraud.demo.service;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.model.Account;
import my.fraud.demo.model.DecisionSubjectEvent;
import my.fraud.demo.model.SendTransactionRequest;
import my.fraud.demo.model.TransactionHistoryEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

@Slf4j
@Service
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private List<TransactionHistoryEntry> transactionHistory = new ArrayList();

    public List<TransactionHistoryEntry> getTransactionHistory() { return transactionHistory; }

    private String transactionHistoryLastEntryId = "TRX0000001";

    private void setTransactionHistory(TransactionHistoryEntry transactionHistoryEntry) {
        this.transactionHistory.add(transactionHistoryEntry);
    }

    private String getTransactionHistoryLastEntryId() { return this.transactionHistoryLastEntryId; }

    private void setTransactionHistoryLastEntryId(String transactionId) {this.transactionHistoryLastEntryId =  transactionId;}

    @Override
    public void saveTransactionToHistory(SendTransactionRequest sendTransactionRequest) {

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
        transactionHistoryEntry.setTransactionId(generateTransactionId());

        log.info("About to log {} to transaction history", transactionHistoryEntry);

        this.setTransactionHistory(transactionHistoryEntry);

        log.info("State of transaction history after logging {}", this.transactionHistory);
    }

    private String generateTransactionId() {
        if (transactionHistory.isEmpty()) {
            return getTransactionHistoryLastEntryId();
        } else {
            log.info("Současné transaction history id k navýšení: {}", this.transactionHistoryLastEntryId);
            setTransactionHistoryLastEntryId(incrementLastTransactionId());
            log.info("Nově uložené transaction history id: {}", this.transactionHistoryLastEntryId);
            return getTransactionHistoryLastEntryId();
        }
    }

    private String incrementLastTransactionId() {

        String lastTransactionHistoryEntryId = getTransactionHistoryLastEntryId();

        Integer incrementedTransactionId = getIncrementedTransactionId(lastTransactionHistoryEntryId);

        StringBuilder zeroesFill = fillInZeroesToMeetIdFormat(incrementedTransactionId);

        return "TRX" + zeroesFill + incrementedTransactionId;
    }

    private Integer getIncrementedTransactionId(String lastTransactionHistoryEntryId) {
        String numberSuffixOfLastId = lastTransactionHistoryEntryId.substring(4);

        return parseInt(numberSuffixOfLastId, 10) + 1;
    }

    private StringBuilder fillInZeroesToMeetIdFormat(Integer incrementedTransactionId) {
        Integer idNumberOfDigits = String.valueOf(incrementedTransactionId).length();

        StringBuilder zeroesFill = new StringBuilder();
        for (int i = 0; i < 7 - idNumberOfDigits; i++) {
            zeroesFill.append("0");
        }
        return zeroesFill;
    }
}
