package com.job.careerintelligence.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IndiaLocationServiceTest {

    private final IndiaLocationService service = new IndiaLocationService();

    @Test
    public void testValidIndiaLocations() {
        assertTrue(service.isValidLocation("India"));
        assertTrue(service.isValidLocation("Bengaluru, India"));
        assertTrue(service.isValidLocation("Remote - India"));
        assertTrue(service.isValidLocation("Remote India"));
        assertTrue(service.isValidLocation("Hyderabad"));
        assertTrue(service.isValidLocation("Chennai"));
        assertTrue(service.isValidLocation("Delhi"));
        assertTrue(service.isValidLocation("Gurugram"));
        assertTrue(service.isValidLocation("Noida"));
        assertTrue(service.isValidLocation("Mumbai"));
        assertTrue(service.isValidLocation("Pune"));
    }

    @Test
    public void testInvalidLocation() {
        assertFalse(service.isValidLocation("New York, USA"));
        assertFalse(service.isValidLocation("London"));
        assertFalse(service.isValidLocation(null));
    }
}
