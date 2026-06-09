package my.fraud.demo.model;

import lombok.Data;

@Data
public class GetWatchlistEntryRequest {
    private String id;
    private Account account;
}
