package com.abbas.vendingmachine;

import com.abbas.vendingmachine.entities.Enums;
import com.abbas.vendingmachine.entities.Product;
import com.abbas.vendingmachine.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/user")
                        .content(asJsonString(new UserModel("Abbas", "Dehghan", Enums.Role.BUYER)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK")));

        this.mockMvc.perform(MockMvcRequestBuilders
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

        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCdXllciIsImV4cCI6MTY0Mjg3OTEwOCwiaWF0IjoxNjQyODYxMTA4fQ.WsWDSkiUVfrAntTEtBPUhE0T1c6iMivxECu3ouszE2z6ggRgnJcmgD4ifqTx35AneKyy8jz1D9o7eR1rxw9vYA";

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/deposit?amount=20")
                        .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("20")));

    }

    @Test
    public void testBuy() throws Exception {

        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCdXllcjIiLCJleHAiOjE2NDI4ODA1ODYsImlhdCI6MTY0Mjg2MjU4Nn0.t2njc9z7G7sfrjPR8JK3pCRyosOayF8-v1xCQD9ZZ_FzvQRvMdLqgxxaDR5eOYbbk7ogo2SbSnfw1EkGIzxggA";

        BuyProductModel buy1 = new BuyProductModel(1, 2);
        BuyProductModel buy2 = new BuyProductModel(2, 3);
        BuyModel model = new BuyModel(List.of(buy1, buy2));

        this.mockMvc.perform(MockMvcRequestBuilders
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

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
