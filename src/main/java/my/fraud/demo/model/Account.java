package my.fraud.demo.model;


import lombok.Data;

@Data
public class Account {
    String accountNumber;
    String bankCode;

    public Account(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }

}
