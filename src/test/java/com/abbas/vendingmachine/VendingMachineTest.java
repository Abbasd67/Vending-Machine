package com.abbas.vendingmachine;

import com.abbas.vendingmachine.entities.Enums;
import com.abbas.vendingmachine.models.BuyModel;
import com.abbas.vendingmachine.models.BuyProductModel;
import com.abbas.vendingmachine.models.LoginModel;
import com.abbas.vendingmachine.models.UserModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VendingMachineTest extends DemoApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user")
                        .content(asJsonString(new UserModel("Abbas", "Dehghan", "BUYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK")));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content(asJsonString(new LoginModel("Abbas", "Dehghan")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void testDeposit() throws Exception {

        String token = getToken("Buyer", "Buyer");
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/deposit?amount=20")
                        .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("20")));

    }

    @Test
    public void testBuy() throws Exception {

        String token = getToken("Buyer2", "Buyer2");
        BuyProductModel buy1 = new BuyProductModel(1, 2);
        BuyProductModel buy2 = new BuyProductModel(2, 3);
        BuyModel model = new BuyModel(List.of(buy1, buy2));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/buy")
                        .header("Authorization", "Bearer " + token)
                        .content(asJsonString(model))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.products").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalSpent").value(65))
                .andExpect(MockMvcResultMatchers.jsonPath("$.products[*].amountAvailable").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.products[0].amountAvailable").value(98))
                .andExpect(MockMvcResultMatchers.jsonPath("$.products[1].amountAvailable").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.changes[0]").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changes[1]").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.changes[2]").value(5));

    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getToken(String userName, String password) throws Exception {

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content(asJsonString(new LoginModel(userName, password)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("token").toString();
    }
}
