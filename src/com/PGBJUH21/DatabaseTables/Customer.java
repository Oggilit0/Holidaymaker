package com.PGBJUH21.DatabaseTables;

import java.sql.Date;

public class Customer {

    private int id;
    private String fName;
    private String lName;
    private String email;
    private String phone;
    private String birthdate;

    public Customer(int id, String fName, String lName, String email, String phone, String birthdate) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;

    }

    public int getId() {
        return id;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String toString(){

        return "id: " + id + " name: " + fName + " " + lName + " email: " + email + " phone: " + phone + " birthdate: " + birthdate;
    }

}
