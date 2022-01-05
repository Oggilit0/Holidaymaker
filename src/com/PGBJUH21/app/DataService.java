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

    public ArrayList<Customer> getCustomer(){
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
                System.out.println(birthdate);
                customers.add(new Customer(id,fName,lName,email,phone,birthdate));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return customers;
    }

}
