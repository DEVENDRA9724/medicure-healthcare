package com.project.staragile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private MedicureRepository doctorRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Preload sample doctor data
        if (doctorRepository.count() == 0) {
            doctorRepository.save(new Doctor("MED001", "Dr. John Smith", "Cardiology", 
                    "Medcurve - New York", "+1-555-0101", "john.smith@medcurve.com", 15));
            
            doctorRepository.save(new Doctor("MED002", "Dr. Sarah Johnson", "Neurology", 
                    "Medcurve - New York", "+1-555-0102", "sarah.johnson@medcurve.com", 12));
            
            doctorRepository.save(new Doctor("MED003", "Dr. Michael Brown", "Orthopedics", 
                    "Medcurve - New York", "+1-555-0103", "michael.brown@medcurve.com", 10));
            
            doctorRepository.save(new Doctor("MED004", "Dr. Emily Davis", "Pediatrics", 
                    "Medcurve - New York", "+1-555-0104", "emily.davis@medcurve.com", 8));
            
            doctorRepository.save(new Doctor("MED005", "Dr. Robert Wilson", "Cardiology", 
                    "Medcurve - New York", "+1-555-0105", "robert.wilson@medcurve.com", 20));
            
            System.out.println("=== Preloaded 5 sample doctors into database ===");
        }
    }
}