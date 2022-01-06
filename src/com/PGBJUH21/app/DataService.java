package com.PGBJUH21.app;

import com.PGBJUH21.DatabaseTables.Customer;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DataService {

    Connection conn = null;

    public void connect(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src/com/PGBJUH21/database/BookingRegister.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Customer> getAllCustomers(){
        // order by, string orderby, ORDER BY ?
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customer";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            while(resultSet.next()){
                // vi behöver nu hämta ut varje värde från varje column i raden
                int id = resultSet.getInt("customer_id");
                String fName = resultSet.getString("first_name");
                String lName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String birthdate = resultSet.getString("birthdate");
                customers.add(new Customer(id,fName,lName,email,phone,birthdate));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return customers;
    }

    public ArrayList<Customer> getCustomer(String fullName){
        String[] name = fullName.split(" ");
        // order by, string orderby, ORDER BY ?
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customer WHERE first_name = ? AND last_name = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,name[0]);
            statement.setString(2,name[1]);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                int id = resultSet.getInt("customer_id");
                String fName = resultSet.getString("first_name");
                String lName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String birthdate = resultSet.getString("birthdate");
                customers.add(new Customer(id,fName,lName,email,phone,birthdate));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return customers;
    }

    public int createCustomer(String fName, String lName, String email, String phone, String birthdate ){
        int createdId = 0;
        String query = "INSERT INTO customer (first_name,last_name,email,phone,birthdate) VALUES(?, ?, ?, ?, ?)";
        try{
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,fName);
            statement.setString(2,lName);
            if(email == ""){
                statement.setString(3,null);
            }else{
                statement.setString(3,email);
            }
            if(phone == ""){
                statement.setString(4,null);
            }else{
                statement.setString(4,phone);
            }

            statement.setString(5,birthdate);
            // Commit to database
            statement.executeUpdate();
            // get result
            ResultSet keys = statement.getGeneratedKeys();

            while(keys.next()){
                createdId = keys.getInt(1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Done");
        return createdId;
    }

}
