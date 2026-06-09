package my.fraud.demo.controller;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.model.*;
import my.fraud.demo.service.DecisionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
public class WatchlistController {

    public WatchlistController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    private final DecisionService decisionService;

    @PostMapping("/api/fraud/watchlist/add")
    public void addAccount(@RequestBody AccountWatchlistEntry accountWatchlistEntry) {
        log.info("Voláme add account s {}", accountWatchlistEntry);
        decisionService.addAccountToWatchlist(accountWatchlistEntry);
    }

    @PostMapping("/api/fraud/watchlist/modify")
    public void modifyAccountStatus(@RequestBody AccountWatchlistModifyRequest accountWatchlistModifyRequest) throws WatchlistException {

        if (accountWatchlistModifyRequest.getModifiedBy() == null) {
            throw new WatchlistException("ModifiedBy is missing. Can't accept modify request without responsible person.");
        }

        if (accountWatchlistModifyRequest.getId() == null && (accountWatchlistModifyRequest.getAccountToModify().getAccountNumber() == null
                || accountWatchlistModifyRequest.getAccountToModify().getBankCode() == null)) {
            throw new WatchlistException("Entry identification is missing. Fill in correct entry Id or identify account with account number and bankcode.");
        }

        GetWatchlistEntryRequest getWatchlistEntryRequest = new GetWatchlistEntryRequest();
        if (accountWatchlistModifyRequest.getId() != null) {
            getWatchlistEntryRequest.setId(accountWatchlistModifyRequest.getId());
        } else {
            Account account = new Account();
            account.setAccountNumber(accountWatchlistModifyRequest.getAccountToModify().getAccountNumber());
            account.setBankCode(accountWatchlistModifyRequest.getAccountToModify().getBankCode());
            getWatchlistEntryRequest.setAccount(account);
        }
        Optional<AccountWatchlistEntry> foundWatchlistEntry = decisionService.getWatchlistEntry(getWatchlistEntryRequest);
        if (foundWatchlistEntry.isEmpty()) {
            throw new WatchlistException("No matching etnr found for this id or account.");
        }
        if (foundWatchlistEntry.get().isActive() == accountWatchlistModifyRequest.isActive()) {
            throw new WatchlistException("Cannot proceed. Entry is of the same status as the request.");
        }
        decisionService.modifyAccountStatus(accountWatchlistModifyRequest);

    }

    @GetMapping("/api/fraud/watchlist/get")
    public AccountWatchlistEntry getWatchlistEntry(@RequestBody GetWatchlistEntryRequest getWatchlistEntryRequest) throws WatchlistException {
        log.info("Volání watchlist get s {}", getWatchlistEntryRequest);
        Optional<AccountWatchlistEntry> foundWatchlistEntry = decisionService.getWatchlistEntry(getWatchlistEntryRequest);
        log.info("Volání watchlist get s výsledkem {}", foundWatchlistEntry);
        if (foundWatchlistEntry.isEmpty()) {
            throw new WatchlistException("No matching entry found for this id or account.");
        }

        return foundWatchlistEntry.get();
    }
}
