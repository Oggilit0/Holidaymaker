package com.PGBJUH21.app;

import com.PGBJUH21.databasetables.Customer;
import com.PGBJUH21.querytables.AvailableRoom;
import com.PGBJUH21.querytables.BookedRoom;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

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
        } catch (SQLException throwable) {
            throwable.printStackTrace();
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
     * @param orderBy Input string to order by
     * @return returns array with booked rooms
     */
    public ArrayList<BookedRoom> bookedRooms(String orderBy){
        switch(orderBy.toLowerCase(Locale.ROOT)){
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
        String query = """
                SELECT room.room_id, booking.booking_id, booking.check_in_date, booking.check_out_date,\s
                customer.first_name || " " || customer.last_name AS 'Full Name'
                FROM booking
                INNER JOIN party ON booking.booking_id = party.booking_id\s
                INNER JOIN room ON party.room_id = room.room_id
                INNER JOIN customer ON customer.customer_id = booking.customer_id_responsible
                WHERE booking.check_in_date IS NOT NULL
                AND booking.check_out_date IS NOT NULL
                GROUP BY booking.booking_id""";
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
        } catch (SQLException throwable){
            throwable.printStackTrace();
        }
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
     * @param extraBed extra bed
     * @param fullBoard full board
     * @param halfBoard half board
     * @param room_id room_id
     * @return price as an int
     */
    public int getPrice(boolean extraBed, boolean fullBoard, boolean halfBoard, int room_id){
        int price = 0;
        String query;

        if(extraBed && fullBoard){
            query = """
                    SELECT SUM(room.price+hotel.price_extra_bed+hotel.price_full_board) AS cost
                    FROM room
                    INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
                    WHERE room_id = ?""";
        }else if(extraBed && halfBoard){
            query = """
                    SELECT SUM(room.price+hotel.price_extra_bed+hotel.price_half_board) AS cost
                    FROM room
                    INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
                    WHERE room_id = ?""";
        }else if (extraBed){
            query = """
                    SELECT SUM(room.price+hotel.price_extra_bed) AS cost
                    FROM room
                    INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
                    WHERE room_id = ?""";
        }else if (fullBoard){
            query = """
                    SELECT SUM(room.price+hotel.price_full_board) AS cost
                    FROM room
                    INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
                    WHERE room_id = ?""";
        }else if (halfBoard){
            query = """
                    SELECT SUM(room.price+hotel.price_half_board) AS cost
                    FROM room
                    INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
                    WHERE room_id = ?""";
        }else {
            query = """
                    SELECT SUM(room.price) AS cost
                    FROM room
                    INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
                    WHERE room_id = ?""";
        }
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,room_id);

            ResultSet resultSet = statement.executeQuery();

            price = resultSet.getInt("cost");

        } catch (SQLException throwable){
            throwable.printStackTrace();
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
     * @param chkInDate check in date
     * @param chkOutDate check out date
     * @param orderBy order by
     * @param pool pool
     * @param entertainment entertainment
     * @param kidsClub kids club
     * @param restaurant restaurant
     * @return list of available rooms
     */
    public ArrayList<AvailableRoom> availableRooms(String chkInDate, String chkOutDate, String orderBy, boolean pool, boolean entertainment, boolean kidsClub, boolean restaurant){
        ArrayList<AvailableRoom> availableRooms = new ArrayList<>();

        switch(orderBy.toLowerCase(Locale.ROOT)){
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

        String query = """
                SELECT room.room_id, hotel.hotel_name, room.price, room.beds, hotel.distance_beach, hotel.distance_downtown, hotel.review
                FROM room
                INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
                WHERE room_id NOT IN (SELECT room.room_id
                FROM booking
                INNER JOIN party ON booking.booking_id = party.booking_id\s
                INNER JOIN room ON party.room_id = room.room_id
                INNER JOIN hotel ON hotel.hotel_id = room.hotel_id
                INNER JOIN customer ON customer.customer_id = booking.customer_id_responsible
                WHERE  ((? <= check_out_date AND ? >= check_in_date)\s
                    OR (? <= check_out_date AND ? >= check_in_date))
                GROUP BY room.room_id
                ORDER BY room.room_id)""";

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

        } catch (SQLException throwable){
            throwable.printStackTrace();
        }
        return availableRooms;
    }

    /**
     * This method do 2 things. Fetch a customer by name and order it by input.
     * Couldn't bring myself to do 2 methods for it
     *
     *
     * Clean query:
     *
     * SELECT * FROM customer WHERE first_name = ? AND last_name = ? ORDER BY customer_id
     *
     *
     * @param methodString input string as either order by or full name
     * @return arraylist of customers
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

        } catch (SQLException throwable){
            throwable.printStackTrace();
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

        } catch (SQLException throwable){
            throwable.printStackTrace();
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
     * @param bookingId booking id
     * @param chkInDate check in date
     * @param chkOutDate check out date
     */
    public void updateBooking(int bookingId, String chkInDate, String chkOutDate){

        String query = "Update booking SET check_in_date = ? , check_out_date = ? WHERE booking_id = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, chkInDate);
            statement.setString(2, chkOutDate);
            statement.setInt(3, bookingId);


            statement.executeUpdate();

            } catch(SQLException throwable){
                throwable.printStackTrace();
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
     * @param bookingId booking id
     */
    public void deleteBooking(int bookingId){

        String query = "DELETE FROM booking WHERE booking_id = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setInt(1, bookingId);


            statement.executeUpdate();

        } catch(SQLException throwable){
            throwable.printStackTrace();
        }
        System.out.println("booking id: " + bookingId + " successfully deleted from system");
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
     * @param fName first name
     * @param lName last name
     * @param email email
     * @param phone phone
     * @param birthdate birthdate
     * @return customer id as int
     */
    public int createCustomer(String fName, String lName, String email, String phone, String birthdate ){
        int createdId = 0;
        String query = "INSERT INTO customer (first_name,last_name,email,phone,birthdate) VALUES(?, ?, ?, ?, ?)";
        try{
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,fName);
            statement.setString(2,lName);
            if(email.equalsIgnoreCase("")){
                statement.setString(3,null);
            }else{
                statement.setString(3,email);
            }
            if(phone.equalsIgnoreCase("")){
                statement.setString(4,null);
            }else{
                statement.setString(4,phone);
            }

            statement.setString(5,birthdate);
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
     *
     * Method to insert a party into database
     *
     *
     * Clean query:
     *
     * INSERT INTO party (customer_id,booking_id,room_id) VALUES(?, ?, ?)
     *
     * @param customerId customer id
     * @param bookingId booking id
     * @param roomId room id
     */
    public void createParty(int customerId, int bookingId, int roomId ){
        String query = "INSERT INTO party (customer_id,booking_id,room_id) VALUES(?, ?, ?)";
        try{
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,customerId);
            statement.setInt(2,bookingId);
            statement.setInt(3,roomId);

            statement.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }
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
     * @param responsible responsible id
     * @param chkInDate check in date
     * @param chkOutDate check out date
     * @return returns booking id as int
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
     * @param id customer id
     * @return customer as Customer obj
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

        } catch (SQLException throwable){
            throwable.printStackTrace();
        }
        return customer;
    }
}
