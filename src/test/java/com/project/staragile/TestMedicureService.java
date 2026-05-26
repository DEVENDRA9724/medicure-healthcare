package com.project.staragile;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestMedicureService {
    
    @Mock
    private MedicureRepository doctorRepository;
    
    @InjectMocks
    private MedicureService doctorService;
    
    private Doctor testDoctor;
    
    @BeforeEach
    void setUp() {
        testDoctor = new Doctor("TEST001", "Dr. Test User", "General Medicine",
                "Medcurve - Test", "+1-555-9999", "test@medcurve.com", 5);
    }
    
    @Test
    @Order(1)
    void testRegisterDoctor_Success() {
        when(doctorRepository.existsById("TEST001")).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);
        
        Doctor savedDoctor = doctorService.registerDoctor(testDoctor);
        
        assertNotNull(savedDoctor);
        assertEquals("TEST001", savedDoctor.getDoctorRegNo());
        assertEquals("Dr. Test User", savedDoctor.getDoctorName());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }
    
    @Test
    @Order(2)
    void testRegisterDoctor_AlreadyExists() {
        when(doctorRepository.existsById("TEST001")).thenReturn(true);
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            doctorService.registerDoctor(testDoctor);
        });
        
        assertEquals("Doctor with registration number TEST001 already exists!", exception.getMessage());
    }
    
    @Test
    @Order(3)
    void testUpdateDoctor_Success() {
        Doctor updatedDetails = new Doctor("TEST001", "Dr. Updated Name", "Cardiology",
                "Medcurve - Updated", "+1-555-8888", "updated@medcurve.com", 10);
        
        when(doctorRepository.findById("TEST001")).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDetails);
        
        Doctor result = doctorService.updateDoctor("TEST001", updatedDetails);
        
        assertNotNull(result);
        assertEquals("Dr. Updated Name", result.getDoctorName());
        assertEquals("Cardiology", result.getSpecialization());
    }
    
    @Test
    @Order(4)
    void testUpdateDoctor_NotFound() {
        Doctor updatedDetails = new Doctor("TEST999", "Dr. Not Found", "General",
                "Medcurve", "+1-555-0000", "notfound@medcurve.com", 1);
        
        when(doctorRepository.findById("TEST999")).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            doctorService.updateDoctor("TEST999", updatedDetails);
        });
        
        assertEquals("Doctor with registration number TEST999 not found!", exception.getMessage());
    }
    
    @Test
    @Order(5)
    void testSearchDoctorByName_Success() {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findByDoctorNameContainingIgnoreCase("Test")).thenReturn(doctors);
        
        List<Doctor> results = doctorService.searchDoctorByName("Test");
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Dr. Test User", results.get(0).getDoctorName());
    }
    
    @Test
    @Order(6)
    void testSearchDoctorByName_NotFound() {
        when(doctorRepository.findByDoctorNameContainingIgnoreCase("Nonexistent")).thenReturn(Arrays.asList());
        
        List<Doctor> results = doctorService.searchDoctorByName("Nonexistent");
        
        assertTrue(results.isEmpty());
    }
    
    @Test
    @Order(7)
    void testDeleteDoctor_Success() {
        when(doctorRepository.existsById("TEST001")).thenReturn(true);
        doNothing().when(doctorRepository).deleteById("TEST001");
        
        String result = doctorService.deleteDoctor("TEST001");
        
        assertEquals("Doctor with registration number TEST001 has been deleted successfully!", result);
        verify(doctorRepository, times(1)).deleteById("TEST001");
    }
    
    @Test
    @Order(8)
    void testDeleteDoctor_NotFound() {
        when(doctorRepository.existsById("TEST999")).thenReturn(false);
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            doctorService.deleteDoctor("TEST999");
        });
        
        assertEquals("Doctor with registration number TEST999 not found!", exception.getMessage());
    }
    
    @Test
    @Order(9)
    void testGetAllDoctors() {
        List<Doctor> doctors = Arrays.asList(
                new Doctor("001", "Dr. One", "Cardio", "Hospital A", "111", "one@test.com", 5),
                new Doctor("002", "Dr. Two", "Neuro", "Hospital B", "222", "two@test.com", 8)
        );
        when(doctorRepository.findAll()).thenReturn(doctors);
        
        List<Doctor> results = doctorService.getAllDoctors();
        
        assertEquals(2, results.size());
    }
    
    @Test
    @Order(10)
    void testGetDoctorByRegNo_Success() {
        when(doctorRepository.findById("TEST001")).thenReturn(Optional.of(testDoctor));
        
        Optional<Doctor> result = doctorService.getDoctorByRegNo("TEST001");
        
        assertTrue(result.isPresent());
        assertEquals("TEST001", result.get().getDoctorRegNo());
    }
}