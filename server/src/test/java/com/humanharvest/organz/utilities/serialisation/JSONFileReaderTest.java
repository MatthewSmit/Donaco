package com.humanharvest.organz.utilities.serialisation;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import org.junit.jupiter.api.Test;

public class JSONFileReaderTest {

    private void checkDeserializedClientsFromValidFile(List<Client> outputClients) {
        // Check that all 95 Clients were deserialized.
        assertEquals(95, outputClients.size());

        // Tests on the 50th Client in the file to check that all visible fields were deserialized correctly.
        Client testClient = outputClients.get(50);
        assertEquals("Jaeden", testClient.getFirstName());
        assertEquals("Cade", testClient.getMiddleName());
        assertEquals("Nelson", testClient.getLastName());
        assertEquals(LocalDate.of(1966, 7, 1), testClient.getDateOfBirth());
        assertNull(testClient.getDateOfDeath());
        assertEquals(Gender.MALE, testClient.getGender());
        assertEquals(210.0, testClient.getHeight(), 0.0001);
        assertEquals(143.0, testClient.getWeight(), 0.0001);
        assertEquals(BloodType.B_NEG, testClient.getBloodType());
        assertEquals("145 Old Lane", testClient.getCurrentAddress());
        assertEquals(Region.NELSON, testClient.getRegion());
        assertEquals(LocalDateTime.of(2018, 3, 21, 20, 55, 46), testClient.getCreatedTimestamp());

        // Test that the Client's organs set was also deserialized correctly.
        List<Organ> expectedOrgans = Arrays.asList(
                Organ.HEART, Organ.LUNG, Organ.SKIN, Organ.PANCREAS, Organ.KIDNEY,Organ.BONE
        );
        Map<Organ, Boolean> testOrgans = testClient.getOrganDonationStatus();
        for (Entry<Organ, Boolean> organEntry : testOrgans.entrySet()) {
            if (expectedOrgans.contains(organEntry.getKey())) {
                assertTrue(organEntry.getValue());
            } else {
                assertFalse(organEntry.getValue());
            }
        }
    }

    @Test
    public void readAllFromValidFileTest() throws IOException {
        File inputFile = new File("src/test/resources/many_clients.json");
        try (JSONReadClientStrategy clientStrategy = new JSONReadClientStrategy()) {
            clientStrategy.setup(inputFile);
            List<Client> outputClients = clientStrategy.readAll();
            checkDeserializedClientsFromValidFile(outputClients);
        }
    }

    @Test
    public void readAsStreamFromValidFileTest() throws IOException {
        File inputFile = new File("src/test/resources/many_clients.json");
        try (JSONReadClientStrategy clientStrategy = new JSONReadClientStrategy()) {
            clientStrategy.setup(inputFile);

            List<Client> outputClients = new ArrayList<>();
            while (true) {
                Client current = clientStrategy.readNext();
                if (current == null) {
                    break;
                }
                outputClients.add(current);
            }

            checkDeserializedClientsFromValidFile(outputClients);
        }
    }
}
