package my.fraud.demo.model;

import lombok.Data;

@Data
public class AccountWatchlistModifyRequest {
    private boolean active;
    private Account accountToModify;
    private String id;
    private String modifiedBy;

}
