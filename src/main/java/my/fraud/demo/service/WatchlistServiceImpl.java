package my.fraud.demo.service;

import lombok.extern.slf4j.Slf4j;
import my.fraud.demo.model.AccountWatchlistEntry;
import my.fraud.demo.model.AccountWatchlistModifyRequest;
import my.fraud.demo.model.GetWatchlistEntryRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.Integer.parseInt;

@Slf4j
@Service
public class WatchlistServiceImpl implements WatchlistService {
    private final String WATCHLIST_FIRST_ENTRY_ID = "WID00001";

    private List<AccountWatchlistEntry> accountWatchlist = new ArrayList<>();
    public List<AccountWatchlistEntry> getAccountWatchlist() { return accountWatchlist; }

    @Override
    public void addAccountToWatchlist(AccountWatchlistEntry accountWatchlistEntry) {
        accountWatchlistEntry.setId(generateWatchlistEntryId());
        accountWatchlistEntry.setCreatedAt(new Date());
        accountWatchlistEntry.setActive(true);

        log.info("To save {}", accountWatchlistEntry);
        accountWatchlist.add(accountWatchlistEntry);
        log.info("Stav watchlistu po přidání {}", accountWatchlist);
    }

    public String generateWatchlistEntryId() {
        if (accountWatchlist.isEmpty()) {
            return WATCHLIST_FIRST_ENTRY_ID;
        } else {
            String lastWatchlistEntryId = getLastWatchlistEntryId(accountWatchlist);
            Integer incrementedIdNumber = incrementIdNumber(lastWatchlistEntryId);
            return constructIdWithPrefix(incrementedIdNumber);
        }
    }

    private String getLastWatchlistEntryId(List<AccountWatchlistEntry> accountWatchlist) {
        Integer lastEntryPosition = accountWatchlist.size() - 1;
        AccountWatchlistEntry lastWatchlistEntry = accountWatchlist.get(lastEntryPosition);
        return lastWatchlistEntry.getId();
    }

    private Integer incrementIdNumber(String lastWatchlistEntryId) {
        String entryIdNumberSuffix = lastWatchlistEntryId.substring(4);
        return parseInt(entryIdNumberSuffix, 10) + 1;
    }

    private String constructIdWithPrefix(Integer incrementedIdNumber) {
        Integer numberOfDigits = String.valueOf(incrementedIdNumber).length();
        return "WID" + fillZeroIdPrefix(numberOfDigits) + incrementedIdNumber;
    }

    private String fillZeroIdPrefix(int numberOfDifits) {
        String zeroPrefix = "";
        for (int i = numberOfDifits; i < 5; i++) {
            zeroPrefix = zeroPrefix + "0";
        }
        return zeroPrefix;
    }

    @Override
    public void modifyAccountStatus(AccountWatchlistModifyRequest accountWatchlistModifyRequest) {
        accountWatchlist.forEach(entry -> {
            if (filterWatchlistEntryByIdOrAccount(entry, accountWatchlistModifyRequest)) {
                log.info("Modifikuju stav pro záznam {}", entry);
                modifyAccountWatchlistEntry(entry, accountWatchlistModifyRequest);
            }
        });
        log.info("Stav watchlistu po modifikaci {}", accountWatchlist);
    }

    private void modifyAccountWatchlistEntry(AccountWatchlistEntry accountWatchlistEntry,AccountWatchlistModifyRequest accountWatchlistModifyRequest) {
        accountWatchlistEntry.setActive(accountWatchlistModifyRequest.isActive());
        accountWatchlistEntry.setModifiedAt(new Date());
        accountWatchlistEntry.setModifiedBy(accountWatchlistModifyRequest.getModifiedBy());
    }

    public boolean filterWatchlistEntryByIdOrAccount(AccountWatchlistEntry accountWatchlistEntry, AccountWatchlistModifyRequest accountWatchlistModifyRequest) {
        return isMatchingId(accountWatchlistEntry, accountWatchlistModifyRequest)
                || isMatchingAccount(accountWatchlistEntry, accountWatchlistModifyRequest);
    }

    private boolean isMatchingId(AccountWatchlistEntry accountWatchlistEntry, AccountWatchlistModifyRequest accountWatchlistModifyRequest) {
        return accountWatchlistEntry.getId().equals(accountWatchlistModifyRequest.getId());
    }

    private boolean isMatchingAccount(AccountWatchlistEntry accountWatchlistEntry, AccountWatchlistModifyRequest accountWatchlistModifyRequest) {
        return (accountWatchlistEntry.getAccountOnWatch().getAccountNumber().equals(accountWatchlistModifyRequest.getAccountToModify().getAccountNumber())
                && (accountWatchlistEntry.getAccountOnWatch().getBankCode().equals(accountWatchlistModifyRequest.getAccountToModify().getBankCode())));
    }

    @Override
    public Optional<AccountWatchlistEntry> getWatchlistEntry(GetWatchlistEntryRequest getWatchlistEntryRequest) {
        return accountWatchlist.stream()
                .filter(entry -> isMatchingAccountOrEntryId(entry, getWatchlistEntryRequest))
                .findFirst();
    }

    private boolean isMatchingAccountOrEntryId(AccountWatchlistEntry accountWatchlistEntry, GetWatchlistEntryRequest getWatchlistEntryRequest) {
        return (isMatchingAccountWatchlistEntryId(accountWatchlistEntry, getWatchlistEntryRequest)
                || isMatchingAccount(accountWatchlistEntry, getWatchlistEntryRequest));
    }

    private boolean isMatchingAccountWatchlistEntryId(AccountWatchlistEntry accountWatchlistEntry, GetWatchlistEntryRequest getWatchlistEntryRequest) {
        return accountWatchlistEntry.getId().equals(getWatchlistEntryRequest.getId());
    }

    private boolean isMatchingAccount(AccountWatchlistEntry accountWatchlistEntry, GetWatchlistEntryRequest getWatchlistEntryRequest) {
        return (accountWatchlistEntry.getAccountOnWatch().getAccountNumber().equals(getWatchlistEntryRequest.getAccount().getAccountNumber())
                || accountWatchlistEntry.getAccountOnWatch().getBankCode().equals(getWatchlistEntryRequest.getAccount().getBankCode()));
    }
}
