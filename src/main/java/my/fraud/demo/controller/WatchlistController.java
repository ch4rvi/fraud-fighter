package my.fraud.demo.controller;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.model.AccountWatchlistEntry;
import my.fraud.demo.model.AccountWatchlistModifyRequest;
import my.fraud.demo.service.DecisionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public void modifyAccountStatus(@RequestBody AccountWatchlistModifyRequest accountWatchlistModifyRequest) {
        decisionService.modifyAccountStatus(accountWatchlistModifyRequest);
    }


}
