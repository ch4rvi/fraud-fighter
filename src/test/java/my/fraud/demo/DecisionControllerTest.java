package my.fraud.demo;

import my.fraud.demo.controller.DecisionController;
import my.fraud.demo.service.DecisionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(DecisionController.class)
public class DecisionControllerTest {

    private final String MISSING_SOURCE_REQUEST = "{ \"name\": \"ano\", \"type\": \"transaction\", \"amount\": 3 , \"debtorAccount\" : { \"accountNumber\":  \"123\", \"bankCode\":  \"0800\" } }";
    private final String MISSING_AMOUNT_REQUEST = "{ \"name\": \"ano\", \"type\": \"transaction\", \"source\": \"branch\" , \"debtorAccount\" : { \"accountNumber\":  \"123\", \"bankCode\":  \"0800\" } }";
    private final String UNKNOWN_SOURCE_REQUEST = "{ \"name\": \"ano\", \"type\": \"transaction\", \"amount\": 3, \"source\": \"unknown_source\" , \"debtorAccount\" : { \"accountNumber\":  \"123\", \"bankCode\":  \"0800\" } }";
    private final String NEGATIVE_AMOUNT_REQUEST = "{ \"name\": \"ano\", \"type\": \"transaction\", \"amount\": -3, \"source\": \"branch\" , \"debtorAccount\" : { \"accountNumber\":  \"123\", \"bankCode\":  \"0800\" } }";
    private final String MISSING_DEBTOR_ACCOUNT_REQUEST = "{ \"name\": \"ano\", \"type\": \"transaction\", \"amount\": 3, \"source\": \"branch\" , \"debtorAccount\" : null }";
    private final String MISSING_DEBTOR_ACCOUNT_NUMBER_REQUEST = "{ \"name\": \"ano\", \"type\": \"transaction\", \"amount\": 3, \"source\": \"branch\" , \"debtorAccount\" : { \"bankCode\":  \"0800\" } }";
    private final String MISSING_DEBTOR_ACCOUNT_BANK_CODE_REQUEST = "{ \"name\": \"ano\", \"type\": \"transaction\", \"amount\": 3, \"source\": \"branch\" , \"debtorAccount\" : { \"accountNumber\":  \"123\" } }";
    private final String VALID_REQUEST = "{ \"name\": \"ano\", \"type\": \"transaction\", \"amount\": 3, \"source\": \"branch\" , \"debtorAccount\" : { \"accountNumber\":  \"123\", \"bankCode\":  \"0800\" } }";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DecisionService decisionService;

    @Test
    void decisionControllerReturnsBadRequestErrorWhenRequestNull() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("null")
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("K vydání rozhodnutí chybí předmět posouzení!"))
        ;
    }

    @Test
    void decisionControllerReturnsBadRequestErrorWhenMissingSource() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MISSING_SOURCE_REQUEST)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("K vydání rozhodnutí chybí zdroj!"));
    }

    @Test
    void decisionControllerReturnsBadRequestErrorWhenMissingAmount() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MISSING_AMOUNT_REQUEST)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("K vydání rozhodnutí chybí částka!"));
    }

    @Test
    void decisionControllerReturnsBadRequestErrorWhenUnknownSource() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UNKNOWN_SOURCE_REQUEST)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Neznámá hodnota source!"));
    }

    @Test
    void decisionControllerReturnsBadRequestErrorWhenNegativeAmount() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEGATIVE_AMOUNT_REQUEST)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Částka nesmí být záporná!"));
    }

    @Test
    void decisionControllerReturnsBadRequestErrorWhenMissingDebtorAccount() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MISSING_DEBTOR_ACCOUNT_REQUEST)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Chybí údaje pro účet odesilatele!"));
    }

    @Test
    void decisionControllerReturnsBadRequestErrorWhenMissingDebtorAccountNumber() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MISSING_DEBTOR_ACCOUNT_NUMBER_REQUEST)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Chybí číslo účtu odesilatele!"));
    }

    @Test
    void decisionControllerReturnsBadRequestErrorWhenMissingDebtorBankCode() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MISSING_DEBTOR_ACCOUNT_BANK_CODE_REQUEST)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Chybí kód banky!"));
    }

    @Test
    void decisionControllerReturns200WhenValidRequest() throws Exception {
        mockMvc.perform(
                post("/api/fraud")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST)
        )
        .andExpect(status().is2xxSuccessful());
    }

}
