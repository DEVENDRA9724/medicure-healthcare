package com.project.staragile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeleniumGridTest {
    
    // Use host.docker.internal instead of localhost for Docker-to-host communication
    private static final String APP_URL = "http://host.docker.internal:8080";
    private static final String API_URL = "http://host.docker.internal:8080/api/doctors/getAllDoctors";
    
    @Test
    @Order(1)
    public void testApiResponse() throws Exception {
        System.out.println("Testing API at: " + API_URL);
        
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        
        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode, "API should return HTTP 200");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        String jsonResponse = response.toString();
        assertTrue(jsonResponse.contains("DOC1001"), "Response should contain doctor DOC1001");
        assertTrue(jsonResponse.contains("Dr. John Smith"), "Response should contain Dr. John Smith");
        
        System.out.println("✅ API Test Passed!");
        System.out.println("Found doctors in database!");
    }
    
    @Test
    @Order(2)
    public void testDoctorsCount() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("GET");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        String jsonResponse = response.toString();
        int doctorCount = jsonResponse.split("doctorRegNo").length - 1;
        
        assertTrue(doctorCount >= 5, "Should have at least 5 doctors");
        System.out.println("✅ Found " + doctorCount + " doctors in database!");
    }
    
    @Test
    @Order(3)
    public void testDoctorExists() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("GET");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        String jsonResponse = response.toString();
        assertTrue(jsonResponse.contains("Cardiologist"), "Should have Cardiologist");
        assertTrue(jsonResponse.contains("Neurologist"), "Should have Neurologist");
        
        System.out.println("✅ Specializations verified!");
    }
}