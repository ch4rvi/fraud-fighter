package my.fraud.demo.controller;

import my.fraud.demo.model.Decision;
import my.fraud.demo.model.SendTransactionRequest;
import my.fraud.demo.service.TransactionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    private TransactionService transactionService;

    private TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    } ;

    @PostMapping("/api/fraud/transaction")
    public Decision send(@RequestBody SendTransactionRequest sendTransactionRequest) {
        return transactionService.send(sendTransactionRequest);
    }

}
