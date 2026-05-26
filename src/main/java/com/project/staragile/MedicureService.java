package com.project.staragile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MedicureService {
    
    @Autowired
    private MedicureRepository doctorRepository;
    
    // Register a new doctor
    public Doctor registerDoctor(Doctor doctor) {
        if (doctorRepository.existsById(doctor.getDoctorRegNo())) {
            throw new RuntimeException("Doctor with registration number " + doctor.getDoctorRegNo() + " already exists!");
        }
        return doctorRepository.save(doctor);
    }
    
    // Update existing doctor
    public Doctor updateDoctor(String doctorRegNo, Doctor updatedDoctor) {
        Optional<Doctor> existingDoctor = doctorRepository.findById(doctorRegNo);
        if (existingDoctor.isPresent()) {
            Doctor doctor = existingDoctor.get();
            doctor.setDoctorName(updatedDoctor.getDoctorName());
            doctor.setSpecialization(updatedDoctor.getSpecialization());
            doctor.setHospitalName(updatedDoctor.getHospitalName());
            doctor.setContactNumber(updatedDoctor.getContactNumber());
            doctor.setEmailId(updatedDoctor.getEmailId());
            doctor.setYearsOfExperience(updatedDoctor.getYearsOfExperience());
            return doctorRepository.save(doctor);
        } else {
            throw new RuntimeException("Doctor with registration number " + doctorRegNo + " not found!");
        }
    }
    
    // Search doctor by name
    public List<Doctor> searchDoctorByName(String doctorName) {
        return doctorRepository.findByDoctorNameContainingIgnoreCase(doctorName);
    }
    
    // Delete doctor by registration number
    public String deleteDoctor(String doctorRegNo) {
        if (doctorRepository.existsById(doctorRegNo)) {
            doctorRepository.deleteById(doctorRegNo);
            return "Doctor with registration number " + doctorRegNo + " has been deleted successfully!";
        } else {
            throw new RuntimeException("Doctor with registration number " + doctorRegNo + " not found!");
        }
    }
    
    // Get all doctors
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    // Get doctor by registration number
    public Optional<Doctor> getDoctorByRegNo(String doctorRegNo) {
        return doctorRepository.findById(doctorRegNo);
    }
}