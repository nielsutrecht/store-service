package com.nibado.stockservice.controller;

import com.nibado.stockservice.service.SupplyService;
import com.nibado.stockservice.service.domain.Item;
import com.nibado.stockservice.service.domain.Store;
import com.nibado.stockservice.service.domain.StoreItemSupply;
import com.nibado.stockservice.service.domain.StoreSupply;
import com.nibado.stockservice.service.exception.StoreNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SupplyControllerTest {
    private static final UUID STORE_ID = new UUID(1234, 1234);
    private static final Store STORE = new Store(STORE_ID, "Test store");

    private MockMvc mockMvc;
    private SupplyService supplyService = Mockito.mock(SupplyService.class);

    @Before
    public void setup() {
        SupplyController controller = new SupplyController(supplyService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandlers()).build();
    }

    @Test
    public void getSupply() throws Exception {
        List<StoreItemSupply> items = Arrays.asList(
                new StoreItemSupply(new Item(new UUID(0, 1), "Item 1"), 10, 1),
                new StoreItemSupply(new Item(new UUID(0, 2), "Item 2"), 20, 10));

        Mockito.when(supplyService.findStoreSupply(STORE_ID)).thenReturn(new StoreSupply(STORE, items));

        mockMvc.perform(get("/supply/" + STORE_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.store", is(STORE_ID.toString())))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is("00000000-0000-0000-0000-000000000001")))
                .andExpect(jsonPath("$.items[0].name", is("Item 1")))
                .andExpect(jsonPath("$.items[0].availableAmount", is(10)))
                .andExpect(jsonPath("$.items[0].reservedAmount", is(1)))
                .andExpect(jsonPath("$.items[1].id", is("00000000-0000-0000-0000-000000000002")))
                .andExpect(jsonPath("$.items[1].name", is("Item 2")))
                .andExpect(jsonPath("$.items[1].availableAmount", is(20)))
                .andExpect(jsonPath("$.items[1].reservedAmount", is(10)));
    }

    @Test
    public void getSupply_StoreNotFound() throws Exception {
        Mockito.when(supplyService.findStoreSupply(STORE_ID)).thenThrow(new StoreNotFoundException(STORE_ID));

        mockMvc.perform(get("/supply/" + STORE_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Store with id %s not found", STORE_ID))));
    }

    @Test
    public void getSupply_InvalidID() throws Exception {
        mockMvc.perform(get("/supply/xxxxxx"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid UUID string")));
    }
}