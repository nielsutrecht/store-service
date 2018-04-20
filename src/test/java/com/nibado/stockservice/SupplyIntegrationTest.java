package com.nibado.stockservice;

import com.jayway.jsonpath.JsonPath;
import com.nibado.stockservice.component.Initializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class SupplyIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Initializer initializer;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .build();

        initializer.initialSupply();
    }

    @Test
    public void changeSupply() throws Exception {
        String json = mockMvc.perform(get("/store"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stores", hasSize(3)))
                .andReturn()
                .getResponse().getContentAsString();

        UUID storeId = UUID.fromString(JsonPath.read(json, "$.stores[0].id"));

        json = mockMvc.perform(get("/supply/" + storeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.store", is(storeId.toString())))
                .andExpect(jsonPath("$.items", hasSize(6)))
                .andExpect(jsonPath("$.items[0].availableAmount", is(100)))
                .andExpect(jsonPath("$.items[0].reservedAmount", is(0)))
                .andReturn()
                .getResponse().getContentAsString();

        UUID itemId = UUID.fromString(JsonPath.read(json, "$.items[0].id"));
        String itemName = JsonPath.read(json, "$.items[0].name");

        mockMvc.perform(get("/supply/" + storeId + "/" + itemId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.toString())))
                .andExpect(jsonPath("$.name", is(itemName)))
                .andExpect(jsonPath("$.availableAmount", is(100)))
                .andExpect(jsonPath("$.reservedAmount", is(0)));

        mockMvc.perform(
                patch("/supply/" + storeId + "/" + itemId + "/amount")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":50}"))
                .andDo(print())
                .andExpect(status().isAccepted());

        expectAmount(storeId, itemId, 150, null);

        mockMvc.perform(
                patch("/supply/" + storeId + "/" + itemId + "/amount")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":-150}"))
                .andDo(print())
                .andExpect(status().isAccepted());

        expectAmount(storeId, itemId, 0, null);

        mockMvc.perform(
                patch("/supply/" + storeId + "/" + itemId + "/amount")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":-1}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Supply can't be negative")));

        mockMvc.perform(
                delete("/supply/" + storeId + "/" + itemId))
                .andDo(print())
                .andExpect(status().isAccepted());

        expectAmount(storeId, itemId, 0, null);
    }

    @Test
    public void reservationFlow() throws Exception {
        String json = mockMvc.perform(get("/store"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        UUID storeId = UUID.fromString(JsonPath.read(json, "$.stores[0].id"));

        json = mockMvc.perform(get("/supply/" + storeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        UUID itemId = UUID.fromString(JsonPath.read(json, "$.items[0].id"));

        expectAmount(storeId, itemId, null, 0);

        mockMvc.perform(
                post("/supply/" + storeId + "/" + itemId + "/reservation")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":1, \"durationInMinutes\":15}"))
                .andDo(print())
                .andExpect(status().isAccepted());

        expectAmount(storeId, itemId, null, 1);

        mockMvc.perform(
                post("/supply/" + storeId + "/" + itemId + "/reservation")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":49, \"durationInMinutes\":30}"))
                .andDo(print())
                .andExpect(status().isAccepted());

        expectAmount(storeId, itemId, null, 50);

        mockMvc.perform(
                patch("/supply/" + storeId + "/" + itemId + "/amount")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":-60}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Item still has 50 reservations")));

        mockMvc.perform(
                post("/supply/" + storeId + "/" + itemId + "/reservation")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":100, \"durationInMinutes\":15}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Reserved amount invalid")));

        mockMvc.perform(
                post("/supply/" + storeId + "/" + itemId + "/reservation")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":0, \"durationInMinutes\":15}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Reserved amount invalid")));

        mockMvc.perform(
                post("/supply/" + storeId + "/" + itemId + "/reservation")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"amount\":1, \"durationInMinutes\":40}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Duration can't exceed PT30M and must be positive")));

        mockMvc.perform(
                delete("/supply/" + storeId + "/" + itemId))
                .andDo(print())
                .andExpect(status().isAccepted());

        expectAmount(storeId, itemId, 0, 0);
    }

    private void expectAmount(UUID storeId, UUID itemId, Integer available, Integer reserved) throws Exception {
        ResultActions actions = mockMvc.perform(get("/supply/" + storeId + "/" + itemId))
                .andDo(print())
                .andExpect(status().isOk());

        if (available != null) {
            actions.andExpect(jsonPath("$.availableAmount", is(available)));
        }
        if (reserved != null) {
            actions.andExpect(jsonPath("$.reservedAmount", is(reserved)));
        }
    }
}