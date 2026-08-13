package my.fraud.demo.service;

import my.fraud.demo.model.Decision;
import my.fraud.demo.model.SendTransactionRequest;

public interface TransactionService {
    Decision send(SendTransactionRequest sendTransactionRequest);
}
