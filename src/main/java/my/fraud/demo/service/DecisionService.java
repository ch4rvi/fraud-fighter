package my.fraud.demo.service;


import my.fraud.demo.model.AccountWatchlistEntry;
import my.fraud.demo.model.Decision;
import my.fraud.demo.model.DecisionSubjectEvent;

public interface DecisionService {

    Decision getDecision (DecisionSubjectEvent decisionSubjectEvent);

    void addAccountToWatchlist(AccountWatchlistEntry accountWatchlistEntry);

}
