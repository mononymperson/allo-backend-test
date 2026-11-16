package com.allobank.splitbill.billgroup;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class BillGroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateBillGroup() throws Exception {
        mockMvc.perform(post("/api/bill-groups/").contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                        {
                                          "name": "Trip Bandung",
                                          "participants": ["Daus", "Duas"]
                                        }
                                        """))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.message").value("Bill group created successfully"))
                        .andExpect(jsonPath("$.data.name").value("Trip Bandung"))
                        .andExpect(jsonPath("$.data.participants[0].name").value("Daus"))
                        .andExpect(jsonPath("$.data.participants[1].name").value("Duas"))
                        .andExpect(jsonPath("$.errors").value(nullValue()));
    }

    @Test
    void shouldRejectBillGroupWithFewerThanTwoParticipants() throws Exception {
        mockMvc.perform(post("/api/bill-groups/").contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                        {
                                          "name": "Trip Bandung",
                                          "participants": ["Daus"]
                                        }
                                        """))
                        .andExpect(status().isUnprocessableEntity())
                        .andExpect(jsonPath("$.message").value("Validation failed"))
                        .andExpect(jsonPath("$.data").value(nullValue()))
                        .andExpect(jsonPath("$.errors.participants")
                                        .value("size must be between 2 and 2147483647"));
    }

    @Test
    void shouldGetGroupParticipants() throws Exception {
        String created = mockMvc.perform(post("/api/bill-groups/").contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                        {
                                          "name": "Trip Bandung",
                                          "participants": ["Daus", "Duas"]
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode createdJson = objectMapper.readTree(created);
        UUID billGroupId = UUID.fromString(createdJson.path("data").path("id").asText());

        mockMvc.perform(get("/api/bill-groups/" + billGroupId + "/participants")
                        .contextPath("/api"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value("Participants retrieved successfully"))
                        .andExpect(jsonPath("$.data[0].name").value("Daus"))
                        .andExpect(jsonPath("$.data[1].name").value("Duas"))
                        .andExpect(jsonPath("$.errors").value(nullValue()));
    }

    @Test
    void shouldReturnNotFoundWhenGroupDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/bill-groups/" + UUID.randomUUID() + "/participants")
                        .contextPath("/api"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Bill group was not found"))
                        .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
