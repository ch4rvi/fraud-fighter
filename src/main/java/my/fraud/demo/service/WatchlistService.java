package my.fraud.demo.service;

import my.fraud.demo.model.AccountWatchlistEntry;
import my.fraud.demo.model.AccountWatchlistModifyRequest;
import my.fraud.demo.model.GetWatchlistEntryRequest;

import java.util.List;
import java.util.Optional;

public interface WatchlistService {
    void addAccountToWatchlist(AccountWatchlistEntry accountWatchlistEntry);

    void modifyAccountStatus(AccountWatchlistModifyRequest accountWatchlistModifyRequest);

    Optional<AccountWatchlistEntry> getWatchlistEntry(GetWatchlistEntryRequest getWatchlistEntryRequest);
    List<AccountWatchlistEntry> getAccountWatchlist();
}
