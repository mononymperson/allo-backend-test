package com.allobank.splitbill.billgroup;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
class BillGroupControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private BillGroupRepository billGroupRepository;

        @Autowired
        private com.allobank.splitbill.participant.ParticipantRepository participantRepository;

        @BeforeEach
        void cleanDatabase() {
                participantRepository.deleteAll();
                billGroupRepository.deleteAll();
        }

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
}
