package com.project.staragile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class MedicureController {
    
    @Autowired
    private MedicureService doctorService;
    
    // Register Doctor - POST /registerDoctor
    @PostMapping("/registerDoctor")
    public ResponseEntity<?> registerDoctor(@RequestBody Doctor doctor) {
        try {
            Doctor savedDoctor = doctorService.registerDoctor(doctor);
            return new ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // Update Doctor - PUT /updateDoctor/{doctorRegNo}
    @PutMapping("/updateDoctor/{doctorRegNo}")
    public ResponseEntity<?> updateDoctor(@PathVariable String doctorRegNo, @RequestBody Doctor doctor) {
        try {
            Doctor updatedDoctor = doctorService.updateDoctor(doctorRegNo, doctor);
            return new ResponseEntity<>(updatedDoctor, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    // Search Doctor by Name - GET /searchDoctor/{doctorName}
    @GetMapping("/searchDoctor/{doctorName}")
    public ResponseEntity<?> searchDoctor(@PathVariable String doctorName) {
        List<Doctor> doctors = doctorService.searchDoctorByName(doctorName);
        if (doctors.isEmpty()) {
            return new ResponseEntity<>("No doctors found with name: " + doctorName, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }
    
    // Delete Doctor - DELETE /deletePolicy/{doctorRegNo}
    @DeleteMapping("/deletePolicy/{doctorRegNo}")
    public ResponseEntity<?> deleteDoctor(@PathVariable String doctorRegNo) {
        try {
            String message = doctorService.deleteDoctor(doctorRegNo);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    // Get All Doctors (Additional endpoint)
    @GetMapping("/getAllDoctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }
    
    // Get Doctor by Registration Number (Additional endpoint)
    @GetMapping("/getDoctor/{doctorRegNo}")
    public ResponseEntity<?> getDoctorByRegNo(@PathVariable String doctorRegNo) {
        return doctorService.getDoctorByRegNo(doctorRegNo)
                .map(doctor -> new ResponseEntity<Object>(doctor, HttpStatus.OK))
                .orElse(new ResponseEntity<Object>("Doctor not found!", HttpStatus.NOT_FOUND));
    }
}