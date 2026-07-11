package my.fraud.demo;

import my.fraud.demo.enums.AccountRiskLevel;
import my.fraud.demo.model.Account;
import my.fraud.demo.model.AccountWatchlistEntry;
import my.fraud.demo.model.AccountWatchlistModifyRequest;
import my.fraud.demo.model.GetWatchlistEntryRequest;
import my.fraud.demo.service.WatchlistService;
import my.fraud.demo.service.WatchlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WatchlistServiceTest {

    @Mock
    private WatchlistService watchlistService;

    @BeforeEach
    void setUp() {
        watchlistService = new WatchlistServiceImpl();
    }

    @Test
    void returnEntryWhenEntryAddedToWatchlist() {

        Account account = new Account();
        account.setAccountNumber("123");
        account.setBankCode("0100");

        AccountWatchlistEntry accountWatchlistEntry = new AccountWatchlistEntry(account, AccountRiskLevel.LOW);

        watchlistService.addAccountToWatchlist(accountWatchlistEntry);

        assertEquals("123", watchlistService.getAccountWatchlist().get(0).getAccountOnWatch().getAccountNumber());

    }

    @Test
    void returnEntryWithIncrementedIdNumber() {
        Account account = new Account();
        account.setAccountNumber("123");
        account.setBankCode("0100");

        AccountWatchlistEntry accountWatchlistEntry = new AccountWatchlistEntry(account, AccountRiskLevel.LOW);

        AccountWatchlistEntry accountWatchlistEntry2 = new AccountWatchlistEntry(account, AccountRiskLevel.MEDIUM);

        watchlistService.addAccountToWatchlist(accountWatchlistEntry);
        watchlistService.addAccountToWatchlist(accountWatchlistEntry2);

        assertEquals("WID00002", watchlistService.getAccountWatchlist().get(1).getId());
    }

    @Test
    void returnEntryWhenEntryOnWatchlist() {
        Account account = new Account();
        account.setAccountNumber("123");
        account.setBankCode("0100");

        AccountWatchlistEntry accountWatchlistEntry = new AccountWatchlistEntry(account, AccountRiskLevel.LOW);

        GetWatchlistEntryRequest getWatchlistEntryRequest = new GetWatchlistEntryRequest();
        Account requestedAccount = new Account();
        requestedAccount.setAccountNumber("123");
        requestedAccount.setBankCode("0100");
        getWatchlistEntryRequest.setAccount(requestedAccount);

        watchlistService.addAccountToWatchlist(accountWatchlistEntry);

        Optional<AccountWatchlistEntry> potentialEntry = watchlistService.getWatchlistEntry(getWatchlistEntryRequest);

        assertEquals("WID00001", potentialEntry.get().getId());
    }

    @Test
    void returnEntryWhenEntryOnWatchlistMatchingId() {
        AccountWatchlistEntry accountWatchlistEntry = new AccountWatchlistEntry(new Account(), AccountRiskLevel.LOW);
        accountWatchlistEntry.setId("WID00001");


        GetWatchlistEntryRequest getWatchlistEntryRequest = new GetWatchlistEntryRequest();
        getWatchlistEntryRequest.setId("WID00001");

        watchlistService.addAccountToWatchlist(accountWatchlistEntry);

        Optional<AccountWatchlistEntry> potentialEntry = watchlistService.getWatchlistEntry(getWatchlistEntryRequest);

        assertEquals("WID00001", potentialEntry.get().getId());
    }


    @Test
    void returnNotEqualsWhenEntryNotOnWatchlist() {
        Account account = new Account();
        account.setAccountNumber("456");
        account.setBankCode("0100");

        AccountWatchlistEntry accountWatchlistEntry = new AccountWatchlistEntry(account, AccountRiskLevel.LOW);

        GetWatchlistEntryRequest getWatchlistEntryRequest = new GetWatchlistEntryRequest();
        Account requestedAccount = new Account();
        requestedAccount.setAccountNumber("123");
        requestedAccount.setBankCode("0100");
        getWatchlistEntryRequest.setAccount(requestedAccount);

        watchlistService.addAccountToWatchlist(accountWatchlistEntry);

        Optional<AccountWatchlistEntry> potentialEntry = watchlistService.getWatchlistEntry(getWatchlistEntryRequest);

        assertNotEquals("WID0001", potentialEntry.get().getId());

    }

    @Test
    void returnOKWhenModifyingExistingEntryMatchingAccount() {

        Account account = new Account();
        account.setAccountNumber("123");
        account.setBankCode("0100");

        AccountWatchlistEntry accountWatchlistEntry = new AccountWatchlistEntry(account, AccountRiskLevel.LOW);
        AccountWatchlistModifyRequest accountWatchlistModifyRequest = new AccountWatchlistModifyRequest();

        Account accountToModify = new Account();
        accountToModify.setAccountNumber("123");
        accountToModify.setBankCode("0100");

        accountWatchlistModifyRequest.setAccountToModify(accountToModify);
        accountWatchlistModifyRequest.setActive(false);
        accountWatchlistModifyRequest.setModifiedBy("ext91619");


        watchlistService.addAccountToWatchlist(accountWatchlistEntry);
        watchlistService.modifyAccountStatus(accountWatchlistModifyRequest);
        List<AccountWatchlistEntry> potentialEntries = watchlistService.getAccountWatchlist();

        assertFalse(potentialEntries.get(0).isActive());
        assertEquals("ext91619", potentialEntries.get(0).getModifiedBy());
    }

}
