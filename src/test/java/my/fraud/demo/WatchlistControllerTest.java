package my.fraud.demo;

import my.fraud.demo.controller.WatchlistController;
import my.fraud.demo.enums.AccountRiskLevel;
import my.fraud.demo.model.Account;
import my.fraud.demo.model.AccountWatchlistEntry;
import my.fraud.demo.model.GetWatchlistEntryRequest;
import my.fraud.demo.service.WatchlistService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WatchlistController.class)
public class WatchlistControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WatchlistService watchlistService;

    private final String ADD_TO_WATCHLIST_VALID_REQUEST = "{ \"id\": \"WID00001\", \"accountRiskLevel\": \"LOW\", \"owner\": \"ext91619\" }";

    private final String ADD_TO_WATCHLIST_MISSING_OWNER = "{ \"id\": \"WID00001\", \"accountRiskLevel\": \"LOW\", \"owner\": null }";

    private final String MODIFY_MISSING_MODIFIED_BY = "{ \"id\": \"WID00001\", \"accountToModify\": {}, \"active\": \"false\" }";

    private final String MODIFY_MISSING_PROPER_IDENTIFICATION_ACCOUNT_NUMBER = "{ \"accountToModify\": { \"accountNumber\": \"123\"}, \"modifiedBy\": \"ext91619\", \"active\": \"false\" }";

    private final String MODIFY_MISSING_PROPER_IDENTIFICATION_BANK_CODE = "{ \"accountToModify\": { \"bankCode\": \"0100\"}, \"modifiedBy\": \"ext91619\", \"active\": \"false\" }";

    private final String GET_NON_EXISTENT_ENTRY = "{ \"id\": \"WID00003\", \"accountToModify\": {} }";

    private final String GET_VALID_REQUEST = "{ \"account\": { \"accountNumber\": \"123\", \"bankCode\": \"0100\" } }";


    @Test
    void return200WhenValidAddToWatchlistRequest() throws Exception {
        mockMvc.perform(
                post("/api/fraud/watchlist/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_TO_WATCHLIST_VALID_REQUEST)
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    void returnBadRequestWhenAddToWatchlistWithoutOwner() throws Exception {
        mockMvc.perform(
                post("/api/fraud/watchlist/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_TO_WATCHLIST_MISSING_OWNER)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Owner is missing. Add to watchlist request is not valid."));
    }

    @Test
    void returnBadRequestWhenModifyWithoutModifiedBy() throws Exception {
        mockMvc.perform(
                post("/api/fraud/watchlist/modify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MODIFY_MISSING_MODIFIED_BY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ModifiedBy is missing. Can't accept modify request without responsible person."));
    }

    @Test
    void returnBadRequestWhenModifyWithoutIdAndBankCode() throws Exception {
        mockMvc.perform(
                post("/api/fraud/watchlist/modify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MODIFY_MISSING_PROPER_IDENTIFICATION_ACCOUNT_NUMBER)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Entry identification is missing. Fill in correct entry Id or identify account with account number and bankcode."));
    }

    @Test
    void returnBadRequestWhenModifyWithoutIdAndAccountNumber() throws Exception {
        mockMvc.perform(
                post("/api/fraud/watchlist/modify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MODIFY_MISSING_PROPER_IDENTIFICATION_BANK_CODE)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Entry identification is missing. Fill in correct entry Id or identify account with account number and bankcode."));
    }

    @Test
    void returnBadRequestWhenModifyingNonExistentEntry() throws Exception {
        Optional<AccountWatchlistEntry> potentialEntry = Optional.empty();
        Mockito.doReturn(potentialEntry).when(watchlistService).getWatchlistEntry(new GetWatchlistEntryRequest());

        mockMvc.perform(
                post("/api/fraud/watchlist/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(GET_NON_EXISTENT_ENTRY)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No matching entry found for this id or account."));
    }

    @Test
    void returnWatchlistEntryWhenValidRequest() throws Exception{
        Account accountOnWatch = new Account();
        accountOnWatch.setAccountNumber("123");
        accountOnWatch.setBankCode("0100");
        AccountWatchlistEntry entry = new AccountWatchlistEntry(accountOnWatch, AccountRiskLevel.LOW);
        entry.setId("WID00001");
        Optional<AccountWatchlistEntry> potentialEntry = Optional.of(entry);

        Mockito.when(watchlistService.getWatchlistEntry(Mockito.any(GetWatchlistEntryRequest.class)))
                .thenReturn(potentialEntry);

        mockMvc.perform(
                post("/api/fraud/watchlist/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GET_VALID_REQUEST)
        )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.accountOnWatch.accountNumber").value("123"))
                .andExpect(jsonPath("$.accountOnWatch.bankCode").value("0100"))
                .andExpect(jsonPath("$.accountRiskLevel").value("LOW"));

    }

}
