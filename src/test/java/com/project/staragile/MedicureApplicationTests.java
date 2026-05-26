package com.project.staragile;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MedicureApplicationTests {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/doctors";
    }
    
    @Test
    void contextLoads() {
        assertTrue(true);
    }
    
    @Test
    void testRegisterDoctorAPI() {
        Doctor doctor = new Doctor("API001", "Dr. API Test", "Cardiology",
                "Medcurve - Test", "+1-555-7777", "api@test.com", 3);
        
        ResponseEntity<Doctor> response = restTemplate.postForEntity(
                getBaseUrl() + "/registerDoctor", doctor, Doctor.class);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void testSearchDoctorAPI() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/searchDoctor/Smith", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}