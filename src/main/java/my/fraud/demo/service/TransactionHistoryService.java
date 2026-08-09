package my.fraud.demo.service;

import my.fraud.demo.model.SendTransactionRequest;
import my.fraud.demo.model.TransactionHistoryEntry;

import java.util.List;

public interface TransactionHistoryService {
    void saveTransactionToHistory(SendTransactionRequest sendTransactionRequest);

    List<TransactionHistoryEntry> getTransactionHistory();
}
