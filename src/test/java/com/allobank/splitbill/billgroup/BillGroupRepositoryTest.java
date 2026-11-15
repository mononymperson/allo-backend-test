package com.allobank.splitbill.billgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
class BillGroupRepositoryTest {
    @Autowired
    private BillGroupRepository billGroupRepository;

    @Test
    void shouldCreateReadUpdateAndDeleteBillGroup() {
        BillGroup created = billGroupRepository.saveAndFlush(new BillGroup());

        assertThat(created.getId()).isNotNull();
        UUID id = created.getId();

        BillGroup read = billGroupRepository.findById(id).orElseThrow();
        assertThat(read.getId()).isEqualTo(id);

        BillGroup updated = billGroupRepository.saveAndFlush(read);
        assertThat(updated.getId()).isEqualTo(id);

        billGroupRepository.deleteById(id);
        billGroupRepository.flush();

        assertThat(billGroupRepository.findById(id)).isEmpty();
    }
}
