package my.fraud.demo.model;

import lombok.Data;
import my.fraud.demo.enums.AccountRiskLevel;

@Data
public class AccountWatchlistEntry {

    private Account accountOnWatch;
    private AccountRiskLevel accountRiskLevel;

    public AccountWatchlistEntry(Account accountOnWatch, AccountRiskLevel accountRiskLevel) {
        this.accountOnWatch = accountOnWatch;
        this.accountRiskLevel = accountRiskLevel;
    }
}
