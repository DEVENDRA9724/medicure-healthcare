package com.project.staragile;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "doctors")
public class Doctor {
    
    @Id
    private String doctorRegNo;
    private String doctorName;
    private String specialization;
    private String hospitalName;
    private String contactNumber;
    private String emailId;
    private Integer yearsOfExperience;
    
    // Default Constructor
    public Doctor() {
    }
    
    // Parameterized Constructor
    public Doctor(String doctorRegNo, String doctorName, String specialization, 
                  String hospitalName, String contactNumber, String emailId, 
                  Integer yearsOfExperience) {
        this.doctorRegNo = doctorRegNo;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.hospitalName = hospitalName;
        this.contactNumber = contactNumber;
        this.emailId = emailId;
        this.yearsOfExperience = yearsOfExperience;
    }
    
    // Getters and Setters
    public String getDoctorRegNo() {
        return doctorRegNo;
    }
    
    public void setDoctorRegNo(String doctorRegNo) {
        this.doctorRegNo = doctorRegNo;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getHospitalName() {
        return hospitalName;
    }
    
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getEmailId() {
        return emailId;
    }
    
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    
    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "doctorRegNo='" + doctorRegNo + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", specialization='" + specialization + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", emailId='" + emailId + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                '}';
    }
}