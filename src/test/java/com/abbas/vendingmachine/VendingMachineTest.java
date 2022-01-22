package com.abbas.vendingmachine;

import com.abbas.vendingmachine.entities.Enums;
import com.abbas.vendingmachine.models.LoginModel;
import com.abbas.vendingmachine.models.UserModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
                        .accept(MediaType.APPLICATION_JSON))
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

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
