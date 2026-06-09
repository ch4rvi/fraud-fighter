package my.fraud.demo.service;


import my.fraud.demo.model.*;

import java.util.Optional;

public interface DecisionService {

    Decision getDecision(DecisionSubjectEvent decisionSubjectEvent);

    void addAccountToWatchlist(AccountWatchlistEntry accountWatchlistEntry);

    void modifyAccountStatus(AccountWatchlistModifyRequest accountWatchlistModifyRequest);

    Optional<AccountWatchlistEntry> getWatchlistEntry(GetWatchlistEntryRequest getWatchlistEntryRequest);

}
