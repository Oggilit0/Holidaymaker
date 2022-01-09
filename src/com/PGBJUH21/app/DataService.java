package com.PGBJUH21.app;

import com.PGBJUH21.Databasetables.Customer;
import com.PGBJUH21.Databasetables.Hotel;
import com.PGBJUH21.querytables.AvailableRoom;
import com.PGBJUH21.querytables.BookedRoom;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParsePosition;
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

    public ArrayList<BookedRoom> bookedRooms(){
        ArrayList<BookedRoom> bookedRooms = new ArrayList<>();
        String query = "SELECT room.room_id, booking.booking_id, booking.check_in_date, booking.check_out_date, \n" +
                "customer.first_name || \" \" || customer.last_name AS 'Full Name'\n" +
                "FROM booking\n" +
                "INNER JOIN party ON booking.booking_id = party.booking_id \n" +
                "INNER JOIN room ON party.room_id = room.room_id\n" +
                "INNER JOIN customer ON customer.customer_id = booking.customer_id_responsible\n" +
                "WHERE booking.check_in_date IS NOT NULL\n" +
                "AND booking.check_out_date IS NOT NULL\n" +
                "GROUP BY booking.customer_id_responsible\n" +
                "ORDER BY room.room_id";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                int roomId = resultSet.getInt("room_id");
                int bookingId = resultSet.getInt("booking_id");
                String chkInDate = resultSet.getString("check_in_date");
                String chkOutDate = resultSet.getString("check_out_date");
                String name = resultSet.getString("Full name");
                bookedRooms.add(new BookedRoom(roomId,bookingId,chkInDate,chkOutDate,name));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return bookedRooms;
    }

    public ArrayList<AvailableRoom> availableRooms(String chkInDate, String chkOutDate, String orderBy, boolean pool, boolean entertainment, boolean kidsClub, boolean restaurant){
        ArrayList<AvailableRoom> availableRooms = new ArrayList<>();

        switch(orderBy){
            case "name":
                orderBy = " ORDER BY hotel_name";
                break;
            case "beds":
                orderBy = " ORDER BY beds";
                break;
            case "price":
                orderBy = " ORDER BY price";
                break;
        }
        String query = "SELECT hotel.hotel_name, room.room_id, room.beds, room.price\n" +
                "FROM booking\n" +
                "INNER JOIN party ON booking.booking_id = party.booking_id \n" +
                "INNER JOIN room ON party.room_id = room.room_id\n" +
                "INNER JOIN Hotel ON room.hotel_id = hotel.hotel_id\n" +
                "WHERE NOT ((? <= check_out_date AND ? >= check_in_date) \n" +
                "    OR (? <= check_out_date AND ? >= check_in_date))\n"
                ;

        int counter = 0;
        if(pool || entertainment || kidsClub || restaurant){
            query += " AND";
        }
        if(pool){
            query += " pool = ?";
            if(entertainment || kidsClub || restaurant){
                query += " AND";
                counter ++;
            }
        }

        if(entertainment){
            query += " entertainment = ?";
            if(kidsClub || restaurant){
                query += " AND";
                counter ++;
            }
        }
        if(kidsClub){
            query += " kids_club = ?";
            if(restaurant){
                query += " AND";
                counter ++;
            }
        }
        if(restaurant){
            query += " restaurant = ?";
            counter ++;
        }

        query += " GROUP BY room.room_id" + orderBy;

        try {

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,chkInDate);
            statement.setString(2,chkInDate);
            statement.setString(3,chkOutDate);
            statement.setString(4,chkOutDate);

            if(counter != 0) {
                if (pool) {
                    statement.setBoolean(5, pool);
                }
                if (entertainment) {
                    if (counter == 1) {
                        statement.setBoolean(5, entertainment);
                    } else {
                        statement.setBoolean(6, entertainment);
                    }
                }
                if (kidsClub) {
                    if (counter == 1) {
                        statement.setBoolean(5, kidsClub);
                    } else if (counter == 2) {
                        statement.setBoolean(6, kidsClub);
                    } else {
                        statement.setBoolean(7, kidsClub);
                    }
                }
                if (restaurant) {
                    if (counter == 1) {
                        statement.setBoolean(5, restaurant);
                    } else if (counter == 2) {
                        statement.setBoolean(6, restaurant);
                    } else if (counter == 3) {
                        statement.setBoolean(7, restaurant);
                    } else {
                        statement.setBoolean(8, restaurant);
                    }
                }
            }

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                String hotelName = resultSet.getString("hotel_name");
                int roomId = resultSet.getInt("room_id");
                int beds = resultSet.getInt("beds");
                int price = resultSet.getInt("price");

                availableRooms.add(new AvailableRoom(hotelName,roomId,beds,price));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return availableRooms;

    }


    public ArrayList<Customer> getAllCustomers(){
        // order by, string orderby, ORDER BY ?
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customer";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
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

    public ArrayList<Hotel> orderByPrice(){
        return null;
    }

    public ArrayList<Hotel> orderByDistance(String distance){
        ArrayList<Hotel> hotels = new ArrayList<>();
        String query = "";
        if(distance.equalsIgnoreCase("beach")){
            query = "SELECT * FROM hotel ORDER BY distance_beach";
        }else{
            query = "SELECT * FROM hotel ORDER BY distance_downtown";
        }


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                int id = resultSet.getInt("hotel_id");
                String name = resultSet.getString("hotel_name");
                boolean hotelPool = resultSet.getBoolean("pool");
                boolean hotelKidsClub = resultSet.getBoolean("kids_club");
                boolean hotelEntertainment = resultSet.getBoolean("entertainment");
                boolean hotelRestaurant = resultSet.getBoolean("restaurant");
                String disBeach = resultSet.getString("distance_beach");
                String disTown = resultSet.getString("distance_downtown");
                int extraBed = resultSet.getInt("price_extra_bed");
                int halfBoard = resultSet.getInt("price_half_board");
                int fullBoard = resultSet.getInt("price_full_board");
                int review = resultSet.getInt("review");

                hotels.add(new Hotel(id, name, hotelPool, hotelKidsClub, hotelEntertainment,
                        hotelRestaurant, disBeach, disTown, extraBed, halfBoard, fullBoard, review));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return hotels;
    }

    public ArrayList<Hotel> getHotel2(String query){

        ArrayList<Hotel> hotels = new ArrayList<>();

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                int id = resultSet.getInt("hotel_id");
                String name = resultSet.getString("hotel_name");
                boolean hotelPool = resultSet.getBoolean("pool");
                boolean hotelKidsClub = resultSet.getBoolean("kids_club");
                boolean hotelEntertainment = resultSet.getBoolean("entertainment");
                boolean hotelRestaurant = resultSet.getBoolean("restaurant");
                String disBeach = resultSet.getString("distance_beach");
                String disTown = resultSet.getString("distance_downtown");
                int extraBed = resultSet.getInt("price_extra_bed");
                int halfBoard = resultSet.getInt("price_half_board");
                int fullBoard = resultSet.getInt("price_full_board");
                int review = resultSet.getInt("review");


                hotels.add(new Hotel(id, name, hotelPool, hotelKidsClub, hotelEntertainment,
                        hotelRestaurant, disBeach, disTown, extraBed, halfBoard, fullBoard, review));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return hotels;
    }

//    public String getHotelByUserChoise(boolean pool, boolean entertainment, boolean kidsClub, boolean restaurant){
//
//        String query = "SELECT * FROM hotel";
//        int counter = 0;
//        if(pool || entertainment || kidsClub || restaurant){
//            query += " WHERE";
//        }
//        if(pool){
//            query += " pool = ?";
//            if(entertainment || kidsClub || restaurant){
//                query += " AND";
//                counter ++;
//            }
//        }
//
//        if(entertainment){
//            query += " entertainment = ?";
//            if(kidsClub || restaurant){
//                query += " AND";
//                counter ++;
//            }
//        }
//        if(kidsClub){
//            query += " kids_club = ?";
//            if(restaurant){
//                query += " AND";
//                counter ++;
//            }
//        }
//        if(restaurant){
//            query += " restaurant = ?";
//            counter ++;
//        }
//
//        return query;
//    }


    public ArrayList<Hotel> getHotel(boolean pool, boolean entertainment, boolean kidsClub, boolean restaurant){

        ArrayList<Hotel> hotels = new ArrayList<>();
        String query = "SELECT * FROM hotel";
        int counter = 0;
        if(pool || entertainment || kidsClub || restaurant){
            query += " WHERE";
        }
        if(pool){
            query += " pool = ?";
            if(entertainment || kidsClub || restaurant){
                query += " AND";
                counter ++;
            }
        }

        if(entertainment){
            query += " entertainment = ?";
            if(kidsClub || restaurant){
                query += " AND";
                counter ++;
            }
        }
        if(kidsClub){
            query += " kids_club = ?";
            if(restaurant){
                query += " AND";
                counter ++;
            }
        }
        if(restaurant){
            query += " restaurant = ?";
            counter ++;
        }

        try {
            PreparedStatement statement = conn.prepareStatement(query);

            if(counter != 0) {
                if (pool) {
                    statement.setBoolean(1, pool);
                }
                if (entertainment) {
                    if (counter == 1) {
                        statement.setBoolean(1, entertainment);
                    } else {
                        statement.setBoolean(2, entertainment);
                    }
                }
                if (kidsClub) {
                    if (counter == 1) {
                        statement.setBoolean(1, kidsClub);
                    } else if (counter == 2) {
                        statement.setBoolean(2, kidsClub);
                    } else {
                        statement.setBoolean(3, kidsClub);
                    }
                }
                if (restaurant) {
                    if (counter == 1) {
                        statement.setBoolean(1, restaurant);
                    } else if (counter == 2) {
                        statement.setBoolean(2, restaurant);
                    } else if (counter == 3) {
                        statement.setBoolean(3, restaurant);
                    } else {
                        statement.setBoolean(4, restaurant);
                    }
                }
            }

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                int id = resultSet.getInt("hotel_id");
                String name = resultSet.getString("hotel_name");
                boolean hotelPool = resultSet.getBoolean("pool");
                boolean hotelKidsClub = resultSet.getBoolean("kids_club");
                boolean hotelEntertainment = resultSet.getBoolean("entertainment");
                boolean hotelRestaurant = resultSet.getBoolean("restaurant");
                String disBeach = resultSet.getString("distance_beach");
                String disTown = resultSet.getString("distance_downtown");
                int extraBed = resultSet.getInt("price_extra_bed");
                int halfBoard = resultSet.getInt("price_half_board");
                int fullBoard = resultSet.getInt("price_full_board");
                int review = resultSet.getInt("review");


                hotels.add(new Hotel(id, name, hotelPool, hotelKidsClub, hotelEntertainment,
                        hotelRestaurant, disBeach, disTown, extraBed, halfBoard, fullBoard, review));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return hotels;
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
        return createdId;
    }

    public void createBooking(int responsible, String chkInDate, String chkOutDate) {
        int createdId = 0;
        String query = "INSERT INTO booking (customer_id_responsible, check_in_date, check_out_date) VALUES(?, ?, ?)";
        try{
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,responsible);
            statement.setString(2,chkInDate);
            statement.setString(3,chkOutDate);

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
    }

    public ArrayList<String> getGuestsByBookingId(int bookingId) {
        ArrayList<String> customers = new ArrayList<>();
        String query = "SELECT DISTINCT booking.booking_id, customer.first_name || \" \" || customer.last_name AS 'Full Name'\n" +
                "FROM booking\n" +
                "INNER JOIN customer ON customer.customer_id = party.customer_id\n" +
                "INNER JOIN party ON booking.booking_id = party.booking_id\n" +
                "WHERE booking.booking_id = ?\n" +
                "ORDER BY booking.booking_id";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,bookingId);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                //int id = resultSet.getInt("booking_id");
                String fName = resultSet.getString("Full Name");
                customers.add(fName);
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return customers;
    }

    public ArrayList<String> getCustomerFromBooking(String name) {
        ArrayList<String> customers = new ArrayList<>();
        String[] splitName = name.split(" ");
        String query = "SELECT DISTINCT booking.booking_id, customer.first_name || \" \" || customer.last_name AS 'Full Name'\n" +
                "FROM booking\n" +
                "INNER JOIN customer ON customer.customer_id = party.customer_id\n" +
                "INNER JOIN party ON booking.booking_id = party.booking_id\n" +
                "WHERE customer.first_name = ? AND customer.last_name = ? \n" +
                "ORDER BY booking.booking_id";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,splitName[0]);
            statement.setString(2,splitName[1]);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                int id = resultSet.getInt("booking_id");
                String fName = resultSet.getString("Full Name");
                customers.add(id + " " + fName);
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return customers;
    }

    public void getCheckInDate() {
    }

    public Customer getCustomerFromCustomerId(int id) {
        Customer customer = null;
        String query = "SELECT * FROM customer WHERE customer_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();

            int customerId = resultSet.getInt("customer_id");
            String fName = resultSet.getString("first_name");
            String lName = resultSet.getString("last_name");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");
            String birthdate = resultSet.getString("birthdate");

            customer = new Customer(customerId,fName,lName,email,phone,birthdate);

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return customer;
    }
}
