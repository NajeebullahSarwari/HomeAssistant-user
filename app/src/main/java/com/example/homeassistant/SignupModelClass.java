package com.example.homeassistant;

public class SignupModelClass {
   private String fullName,email,provider_type,location,phoneNo;


    public SignupModelClass(){

    }

    public SignupModelClass(String fullName, String email, String provider_type, String location, String phoneNo) {
        this.fullName = fullName;
        this.email = email;
        this.provider_type = provider_type;
        this.location = location;
        this.phoneNo = phoneNo;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getProvider_type() {
        return provider_type;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNo() {
        return phoneNo;
    }
}
