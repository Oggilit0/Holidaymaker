package com.PGBJUH21.app;

import com.PGBJUH21.Databasetables.Customer;
import com.PGBJUH21.querytables.AvailableRoom;
import com.PGBJUH21.querytables.BookedRoom;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * My Queries are, in some places, modular based oon user inputs. So I've tried to paste an
 * untouched query in every java doc for easier observation
 *
 *
 */
public class DataService {

    Connection conn = null;

    public void connect(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src/com/PGBJUH21/database/BookingRegister.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Method to fetch booked rooms from database
     *
     * Clean query:
     *
     * SELECT room.room_id, booking.booking_id, booking.check_in_date, booking.check_out_date,
     * customer.first_name || " " || customer.last_name AS 'Full Name'
     * FROM booking
     * INNER JOIN party ON booking.booking_id = party.booking_id
     * INNER JOIN room ON party.room_id = room.room_id
     * INNER JOIN customer ON customer.customer_id = booking.customer_id_responsible
     * WHERE booking.check_in_date IS NOT NULL
     * AND booking.check_out_date IS NOT NULL
     * GROUP BY booking.booking_id ORDER BY booking.booking_id
     *
     * @param orderBy
     * @return
     */
    public ArrayList<BookedRoom> bookedRooms(String orderBy){
        switch(orderBy){
            case "room id":
                orderBy = " ORDER BY room_id";
                break;
            case "booking_id","id":
                orderBy = " ORDER BY booking.booking_id";
                break;
            case "check in date":
                orderBy = " ORDER BY check_in_date";
                break;
            case "check out date":
                orderBy = " ORDER BY check_out_date";
                break;
            case "name":
                orderBy = " ORDER BY first_name";
                break;
        }
        ArrayList<BookedRoom> bookedRooms = new ArrayList<>();
        String query = "SELECT room.room_id, booking.booking_id, booking.check_in_date, booking.check_out_date, \n" +
                "customer.first_name || \" \" || customer.last_name AS 'Full Name'\n" +
                "FROM booking\n" +
                "INNER JOIN party ON booking.booking_id = party.booking_id \n" +
                "INNER JOIN room ON party.room_id = room.room_id\n" +
                "INNER JOIN customer ON customer.customer_id = booking.customer_id_responsible\n" +
                "WHERE booking.check_in_date IS NOT NULL\n" +
                "AND booking.check_out_date IS NOT NULL\n" +
                "GROUP BY booking.booking_id";
        query += orderBy;
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
        System.out.println(query);
        return bookedRooms;
    }

    /**
     * Method to fetch prices for rooms + extras from database
     *
     *
     * Clean query:
     *
     * SELECT SUM(room.price+hotel.price_extra_bed+hotel.price_full_board) AS cost
     * FROM room
     * INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
     * WHERE room_id = ?
     *
     * @param extraBed
     * @param fullBoard
     * @param halfBoard
     * @param room_id
     * @return
     */
    public int getPrice(boolean extraBed, boolean fullBoard, boolean halfBoard, int room_id){
        int price = 0;
        String query = "";

        if(extraBed && fullBoard){
            query = "SELECT SUM(room.price+hotel.price_extra_bed+hotel.price_full_board) AS cost\n" +
                    "FROM room\n" +
                    "INNER JOIN hotel ON hotel.hotel_id = room.hotel_id\n" +
                    "WHERE room_id = ?";
        }else if(extraBed && halfBoard){
            query = "SELECT SUM(room.price+hotel.price_extra_bed+hotel.price_half_board) AS cost\n" +
                    "FROM room\n" +
                    "INNER JOIN hotel ON hotel.hotel_id = room.hotel_id\n" +
                    "WHERE room_id = ?";
        }else if (extraBed){
            query = "SELECT SUM(room.price+hotel.price_extra_bed) AS cost\n" +
                    "FROM room\n" +
                    "INNER JOIN hotel ON hotel.hotel_id = room.hotel_id\n" +
                    "WHERE room_id = ?";
        }else if (fullBoard){
            query = "SELECT SUM(room.price+hotel.price_full_board) AS cost\n" +
                    "FROM room\n" +
                    "INNER JOIN hotel ON hotel.hotel_id = room.hotel_id\n" +
                    "WHERE room_id = ?";
        }else if (halfBoard){
            query = "SELECT SUM(room.price+hotel.price_half_board) AS cost\n" +
                    "FROM room\n" +
                    "INNER JOIN hotel ON hotel.hotel_id = room.hotel_id\n" +
                    "WHERE room_id = ?";
        }else {
            query = "SELECT SUM(room.price) AS cost\n" +
                    "FROM room\n" +
                    "INNER JOIN hotel ON hotel.hotel_id = room.hotel_id\n" +
                    "WHERE room_id = ?";
        }
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,room_id);

            ResultSet resultSet = statement.executeQuery();

            price = resultSet.getInt("cost");

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return price;
    }

    /**
     * Method to fetch all available rooms from database
     *
     *
     *Clean query:
     *
     * SELECT room.room_id, hotel.hotel_name, room.price, room.beds, hotel.distance_beach, hotel.distance_downtown, hotel.review
     * FROM room
     * INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
     * WHERE room_id NOT IN (SELECT room.room_id
     * FROM booking
     * INNER JOIN party ON booking.booking_id = party.booking_id
     * INNER JOIN room ON party.room_id = room.room_id
     * INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
     * INNER JOIN customer ON customer.customer_id = booking.customer_id_responsible
     * WHERE  ((? <= check_out_date AND ? >= check_in_date)
     *     OR (? <= check_out_date AND ? >= check_in_date))
     * GROUP BY room.room_id
     * ORDER BY room.room_id) AND pool = ? AND entertainment = ? AND kids_club = ? AND restaurant = ?  ORDER BY review DESC
     *
     *
     * @param chkInDate
     * @param chkOutDate
     * @param orderBy
     * @param pool
     * @param entertainment
     * @param kidsClub
     * @param restaurant
     * @return
     */
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
            case "beach":
                orderBy = " ORDER BY distance_Beach";
                break;
            case "downtown":
                orderBy = " ORDER BY distance_downtown";
                break;
            case "review":
                orderBy = " ORDER BY review DESC";
                break;

        }

        String query = "SELECT room.room_id, hotel.hotel_name, room.price, room.beds, hotel.distance_beach, hotel.distance_downtown, hotel.review\n" +
                "FROM room\n" +
                "INNER JOIN hotel ON hotel.hotel_id = room.hotel_id\n" +
                "WHERE room_id NOT IN (SELECT room.room_id\n" +
                "FROM booking\n" +
                "INNER JOIN party ON booking.booking_id = party.booking_id \n" +
                "INNER JOIN room ON party.room_id = room.room_id\n" +
                "INNER JOIN hotel ON hotel.hotel_id = room.hotel_id\n" +
                "INNER JOIN customer ON customer.customer_id = booking.customer_id_responsible\n" +
                "WHERE  ((? <= check_out_date AND ? >= check_in_date) \n" +
                "    OR (? <= check_out_date AND ? >= check_in_date))\n" +
                "GROUP BY room.room_id\n" +
                "ORDER BY room.room_id)";

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

        query +=  " " +orderBy;

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
                int distBeach = resultSet.getInt("distance_beach");
                int distDowntown = resultSet.getInt("distance_downtown");
                int review = resultSet.getInt("review");

                availableRooms.add(new AvailableRoom(hotelName,roomId,beds,price,distBeach,distDowntown,review));
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return availableRooms;
    }

    /**
     * This methods do 2 things. Fetch a customer by name and order it by input.
     * Couldn't bring myself to do 2 methods for it
     *
     *
     * Clean query:
     *
     * SELECT * FROM customer WHERE first_name = ? AND last_name = ? ORDER BY customer_id
     *
     *
     * @param methodString
     * @return
     */
    public ArrayList<Customer> getCustomer(String methodString){
        String[] name = methodString.split(" ");
        boolean customerName = false;
        switch(methodString){
            case "id":
                methodString = " ORDER BY customer_id";
                break;
            case "name":
                methodString = " ORDER BY first_name";
                break;
            case "email":
                methodString = " ORDER BY email";
                break;
            case "phone":
                methodString = " ORDER BY phone";
                break;
            case "birthdate":
                methodString = " ORDER BY birthdate";
                break;
            default:
                methodString = " WHERE first_name = ? AND last_name = ?";
                customerName = true;
        }

        ArrayList<Customer> customers = new ArrayList<>();

        String query = "SELECT * FROM customer";
        query += methodString;
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            if(customerName){
                statement.setString(1,name[0]);
                statement.setString(2,name[1]);
            }
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

    /**
     *Method to update a customer in database
     *
     * Clean query:
     *
     * Update customer SET first_name = ?, last_name = ?, email = ?, phone = ?, birthdate = ? WHERE customer_id = ?
     *
     */
    public void updateCustomer(int customer_id, String fullName, String email, String phone, String birthdate){
        String[] name = new String[2];
        if(!fullName.equalsIgnoreCase("")){
            name = fullName.split(" ");
        }


        String query = "Update customer SET ";

        int counter = 0;
        if(!fullName.equalsIgnoreCase("")){
            query += "first_name = ?, last_name = ?";
            if(!email.equalsIgnoreCase("") || !phone.equalsIgnoreCase("") || !birthdate.equalsIgnoreCase("")){
                query += ", ";

            }
            counter ++;
            counter ++;
        }

        if(!email.equalsIgnoreCase("")){
            query += "email = ?";
            if(!phone.equalsIgnoreCase("") || !birthdate.equalsIgnoreCase("")){
                query += ", ";

            }
            counter ++;
        }

        if(!phone.equalsIgnoreCase("")){
            query += "phone = ?";
            if(!birthdate.equalsIgnoreCase("")){
                query += ", ";

            }
            counter ++;
        }
        if(!birthdate.equalsIgnoreCase("")){
            query += "birthdate = ?";
            counter ++;
        }

        query += " WHERE customer_id = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);

            if(counter != 0) {
                if (!fullName.equalsIgnoreCase("")) {
                    statement.setString(1,name[0]);
                    statement.setString(2,name[1]);
                }
                if (!email.equalsIgnoreCase("")) {
                    if (counter == 1) {
                        statement.setString(1,email);
                    } else {
                        statement.setString(3,email);
                    }
                }
                if (!phone.equalsIgnoreCase("")) {
                    if (counter == 1) {
                        statement.setString(1,phone);
                    } else if (counter == 2) {
                        statement.setString(2,phone);
                    } else if(counter == 3) {
                        statement.setString(3,phone);
                    }else {
                        statement.setString(4,phone);
                    }
                }
                if (!birthdate.equalsIgnoreCase("")) {
                    if (counter == 1) {
                        statement.setString(1,birthdate);
                    } else if (counter == 2) {
                        statement.setString(2,birthdate);
                    } else if (counter == 3) {
                        statement.setString(3,birthdate);
                    } else if (counter == 4){
                        statement.setString(4,birthdate);
                    }else {
                        statement.setString(5,birthdate);
                    }
                }

                if(counter == 1){
                    statement.setInt(2,customer_id);
                }else if(counter == 2){
                    statement.setInt(3,customer_id);
                }else if(counter == 3){
                    statement.setInt(4,customer_id);
                }else if(counter == 4){
                    statement.setInt(5,customer_id);
                }else{
                    statement.setInt(6,customer_id);
                }
            }

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
            System.out.println("An existing guest was updated successfully");
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }

    /**
     *
     * Method to update a booking in database
     *
     * Clean query:
     *
     * Update booking SET check_in_date = ? , check_out_date = ? WHERE booking_id = ?
     *
     *
     * @param bookingId
     * @param chkInDate
     * @param chkOutDate
     */
    public void updateBooking(int bookingId, String chkInDate, String chkOutDate){

        String query = "Update booking SET check_in_date = ? , check_out_date = ? WHERE booking_id = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, chkInDate);
            statement.setString(2, chkOutDate);
            statement.setInt(3, bookingId);


            statement.executeUpdate();

            } catch(SQLException throwables){
                throwables.printStackTrace();
            }
        System.out.println("Booking was updated successfully");
    }

    /**
     *
     * Method to delete a booking in database
     *
     *
     * Clean query:
     *
     * DELETE FROM booking WHERE booking_id = ?
     *
     * @param bookingId
     */
    public void deleteBooking(int bookingId){

        String query = "DELETE FROM booking WHERE booking_id = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setInt(1, bookingId);


            statement.executeUpdate();

        } catch(SQLException throwables){
            throwables.printStackTrace();
        }
        System.out.println("booking id: " + bookingId + " successfully deleted from system");
    }

    public ArrayList<String> getBookings(String orderBy){

        if(orderBy.equalsIgnoreCase("name")){
            orderBy = " ORDER BY customer.first_name";
        }else if(orderBy.equalsIgnoreCase("check_in_date")){
            orderBy = " ORDER BY booking.check_in_date";
        }else if(orderBy.equalsIgnoreCase("check_out_date")){
            orderBy = " ORDER BY booking.check_out_date";
        }else if (orderBy.equalsIgnoreCase("booking_id")){
            orderBy = " ORDER BY booking.booking_id";
        }else{
            orderBy = "";
        }


        ArrayList<String> bookings = new ArrayList<>();
        String query = "SELECT booking.booking_id, (customer.first_name || \" \" || customer.last_name) AS full_name, booking.check_in_date, booking.check_out_date\n" +
                "FROM booking\n" +
                "INNER JOIN customer ON customer.customer_id = booking.customer_id_responsible\n";
        query += orderBy;
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();



            while(resultSet.next()){
                String booking = "";
                booking += "Booking id: " + resultSet.getInt("booking_id");
                booking += " Name: " + resultSet.getString("full_name");
                booking += " Date: " + resultSet.getString("check_in_date");
                booking += " - " + resultSet.getString("check_out_date");

                bookings.add(booking);
            }

        } catch(SQLException throwables){
            throwables.printStackTrace();
        }
        System.out.println(query);
        return bookings;
    }

    /**
     *
     * Method to insert a customer into database
     *
     *
     * Clean query:
     *
     * INSERT INTO customer (first_name,last_name,email,phone,birthdate) VALUES(?, ?, ?, ?, ?)
     * SELECT * FROM customer WHERE customer_id = ?
     *
     * @param fName
     * @param lName
     * @param email
     * @param phone
     * @param birthdate
     * @return
     */
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

    /**
     *
     * Method to insert a party into database
     *
     *
     * Clean query:
     *
     * INSERT INTO party (customer_id,booking_id,room_id) VALUES(?, ?, ?)
     *
     * @param customerId
     * @param bookingId
     * @param roomId
     * @return
     */
    public int createParty(int customerId, int bookingId, int roomId ){
        int createdId = 0;
        String query = "INSERT INTO party (customer_id,booking_id,room_id) VALUES(?, ?, ?)";
        try{
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,customerId);
            statement.setInt(2,bookingId);
            statement.setInt(3,roomId);

            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();

            createdId = keys.getInt(1);


        }catch (Exception e){
            e.printStackTrace();
        }
        return createdId;
    }

    /**
     *
     * Method to insert a booking into database
     *
     *
     * Clean query:
     *
     * INSERT INTO booking (customer_id_responsible, check_in_date, check_out_date) VALUES(?, ?, ?)
     *
     * @param responsible
     * @param chkInDate
     * @param chkOutDate
     * @return
     */
    public int createBooking(int responsible, String chkInDate, String chkOutDate) {
        int createdId = 0;
        String query = "INSERT INTO booking (customer_id_responsible, check_in_date, check_out_date) VALUES(?, ?, ?)";
        try{
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,responsible);
            statement.setString(2,chkInDate);
            statement.setString(3,chkOutDate);

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            while(keys.next()){
                createdId = keys.getInt(1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return createdId;
    }

    /**
     * Method to fetch a customer from database
     *
     *
     * Clean query:
     *
     * SELECT * FROM customer WHERE customer_id = ?
     *
     * @param id
     * @return
     */
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
        System.out.println(query);
        return customer;
    }

//    public ArrayList<String> getGuestsByBookingId(int bookingId) {
//        ArrayList<String> customers = new ArrayList<>();
//        String query = "SELECT DISTINCT booking.booking_id, customer.first_name || \" \" || customer.last_name AS 'Full Name'\n" +
//                "FROM booking\n" +
//                "INNER JOIN customer ON customer.customer_id = party.customer_id\n" +
//                "INNER JOIN party ON booking.booking_id = party.booking_id\n" +
//                "WHERE booking.booking_id = ?\n" +
//                "ORDER BY booking.booking_id";
//        try {
//            PreparedStatement statement = conn.prepareStatement(query);
//            statement.setInt(1,bookingId);
//            ResultSet resultSet = statement.executeQuery();
//
//            while(resultSet.next()){
//                //int id = resultSet.getInt("booking_id");
//                String fName = resultSet.getString("Full Name");
//                customers.add(fName);
//            }
//
//        } catch (SQLException throwables){
//            throwables.printStackTrace();
//        }
//
//        return customers;
//    }

//    public ArrayList<String> getCustomerFromBooking(String name) {
//        ArrayList<String> customers = new ArrayList<>();
//        String[] splitName = name.split(" ");
//        String query = "SELECT DISTINCT booking.booking_id, customer.first_name || \" \" || customer.last_name AS 'Full Name'\n" +
//                "FROM booking\n" +
//                "INNER JOIN customer ON customer.customer_id = party.customer_id\n" +
//                "INNER JOIN party ON booking.booking_id = party.booking_id\n" +
//                "WHERE customer.first_name = ? AND customer.last_name = ? \n" +
//                "ORDER BY booking.booking_id";
//        try {
//            PreparedStatement statement = conn.prepareStatement(query);
//            statement.setString(1,splitName[0]);
//            statement.setString(2,splitName[1]);
//            ResultSet resultSet = statement.executeQuery();
//
//            while(resultSet.next()){
//                int id = resultSet.getInt("booking_id");
//                String fName = resultSet.getString("Full Name");
//                customers.add(id + " " + fName);
//            }
//
//        } catch (SQLException throwables){
//            throwables.printStackTrace();
//        }
//
//        return customers;
//    }
}
