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
    private Date modifiedAt;
    private String owner;
    private String modifiedBy;
    private boolean active;


    public AccountWatchlistEntry(Account accountOnWatch, AccountRiskLevel accountRiskLevel) {
        this.accountOnWatch = accountOnWatch;
        this.accountRiskLevel = accountRiskLevel;
    }
}
