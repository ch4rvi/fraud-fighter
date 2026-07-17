package my.fraud.demo.service;

import my.fraud.demo.model.SendTransactionRequest;

public interface TransactionService {
    void send(SendTransactionRequest sendTransactionRequest);
}
