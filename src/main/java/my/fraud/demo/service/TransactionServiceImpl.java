package my.fraud.demo.service;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.enums.DecisionAction;
import my.fraud.demo.model.*;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private DecisionService decisionService;
    private TransactionHistoryService transactionHistoryService;

    public TransactionServiceImpl(DecisionService decisionService, TransactionHistoryService transactionHistoryService) {
        this.decisionService = decisionService;
        this.transactionHistoryService = transactionHistoryService;
    }

    @Override
    public Decision send(SendTransactionRequest sendTransactionRequest) {

        DecisionSubjectEvent decisionSubjectEvent = createDecisionSubjectEventFromSendTransaction(sendTransactionRequest);
        Decision decision = decisionService.getDecision(decisionSubjectEvent);

        if (decision.getDecisionAction().equals(DecisionAction.ALLOW)) {
            log.info("Payment OK. Saving to transaction history: {}", sendTransactionRequest);
            transactionHistoryService.saveTransactionToHistory(sendTransactionRequest);
        } else {
            log.info("Payment NOK. Saving to transaction history denied: {}", sendTransactionRequest);
        }
        return decision;
    }

    private DecisionSubjectEvent createDecisionSubjectEventFromSendTransaction(SendTransactionRequest sendTransactionRequest) {
        DecisionSubjectEvent decisionSubjectEvent = new DecisionSubjectEvent();
        decisionSubjectEvent.setSource(sendTransactionRequest.getSource().name());
        decisionSubjectEvent.setAmount(sendTransactionRequest.getAmount());
        Account debtorAccount = new Account();
        debtorAccount.setAccountNumber(sendTransactionRequest.getDebtorAccount().getAccountNumber());
        debtorAccount.setBankCode(sendTransactionRequest.getDebtorAccount().getBankCode());
        decisionSubjectEvent.setDebtorAccount(debtorAccount);

        return decisionSubjectEvent;
    }

}
