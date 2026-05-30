package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.AccountRiskLevel;

import java.util.Date;

@Data
public class AccountWatchlistEntry {

    private Account accountOnWatch;
    private AccountRiskLevel accountRiskLevel;
    private String id;
    private Date createdAt;
    private String username;


    public AccountWatchlistEntry(Account accountOnWatch, AccountRiskLevel accountRiskLevel) {
        this.accountOnWatch = accountOnWatch;
        this.accountRiskLevel = accountRiskLevel;
    }
}
