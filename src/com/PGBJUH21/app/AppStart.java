package com.PGBJUH21.app;

import com.PGBJUH21.databasetables.Customer;
import com.PGBJUH21.querytables.AvailableRoom;
import com.PGBJUH21.querytables.BookedRoom;
import com.PGBJUH21.utilities.AppUtils;

import java.util.ArrayList;

/**
 * The class where the app is running
 *
 */
public class AppStart {

        private final DataService ds;
        private final ArrayList<Customer> currentParty;

    /**
     * constructor to get things going
     * @throws InterruptedException For sleep to work
     */
    AppStart() throws InterruptedException {
        this.currentParty = new ArrayList<>();
        this.ds = new DataService();
        this.ds.connect();
        mainMenu();
    }


    /**
     * First menu to be shown. Here is where the user will first end up
     * @throws InterruptedException For sleep to work
     */
    public void mainMenu() throws InterruptedException {
        AppUtils.clearScreen();
        switch(AppUtils.menuBuilder("Main Menu", 1,4,"Create new booking","Edit existing booking", "Delete booking", "Search in database")){
            case 1:
                createCustomerMenu();
                break;
            case 2:
                editBookingMenu();
                break;
            case 3:
                deleteBookingMenu();
                break;
            case 4:
                searchDatabase();
                break;
        }
    }

    /**
     * Method to handle user input to search for booked rooms and fetch from database with corresponding method in DataService
     */
    public void searchBookedRooms(){
        String orderBy = AppUtils.userInput("how you want to order the result. Room id, booking id, check in date, check out date, name");
        ArrayList<BookedRoom> bookedRooms =ds.bookedRooms(orderBy);
        int counter = 1;
        for(BookedRoom room : bookedRooms){
            System.out.println(counter + ". " + room);
            counter++;
        }
    }

    /**
     * Method to handle user input to search for customers and fetch from database with corresponding method in DataService
     */
    public void searchCustomers(){
        String orderBy = AppUtils.userInput("how you want to order the result. ,id, Name, email, phone, birthdate");
        ArrayList<Customer> customers = ds.getCustomer(orderBy);
        int counter = 1;
        for(Customer customer : customers){
            System.out.println(counter + ". " + customer);
            counter++;
        }
    }

    /**
     * Menu to handle database searches
     * @throws InterruptedException For sleep to work
     */
    public void searchDatabase() throws InterruptedException {
        AppUtils.clearScreen();
        switch(AppUtils.menuBuilder("Search", 1,4,"Booked rooms", "Customer", "Booking", "Back to main menu")){
            case 1:
                searchBookedRooms();
                System.out.println("1. Return to menu");
                if(AppUtils.userInput(1,1) == 1){
                    searchDatabase();
                }
                break;
            case 2:
                searchCustomers();
                System.out.println("1. Return to menu");
                if(AppUtils.userInput(1,1) == 1){
                    searchDatabase();
                }
                break;
            case 3:
                searchBooking("");
                System.out.println("1. Return to menu");
                if(AppUtils.userInput(1,1) == 1){
                    searchDatabase();
                }
                break;
            case 4:
                mainMenu();
                break;
        }
    }

    /**
     * Method to add guests to party
     */
    public void addGuest(){
        this.currentParty.add(customerForm());
        boolean cont;
        do{
            cont = AppUtils.trueOrFalse("Add another guest? yes/no\"");
            if(cont){
                this.currentParty.add(customerForm());
            }
        }while(cont);
    }

    /**
     * Show selected party in menu.
     */
    public void showParty (){
        if(this.currentParty.size() > 0){
            System.out.println("Current party");
            for(Customer c : this.currentParty){
                System.out.println(c);
            }
            System.out.println();
        }
    }

    /**
     * Menu interaction the let user search for guest by name
     */
    public Customer chooseExistingGuest(){
        boolean validCustomer = false;
        Customer customer = null;
        do {
            String customerName = AppUtils.userInput("name of guest, or leave empty");
            if(customerName.equalsIgnoreCase("")){
                break;
            }
            ArrayList<Customer> listOfCustomers = ds.getCustomer(customerName);
            int c = 1;
            for (Customer existingGuests : listOfCustomers) {
                System.out.println(c + ". " + existingGuests);
                c++;
            }
            if (listOfCustomers.size() > 0){
                System.out.print("Choose guest: ");
            }
            if (listOfCustomers.size() > 0) {
                int customerNr = AppUtils.userInput(1, listOfCustomers.size());
                customer = listOfCustomers.get(customerNr-1);
                validCustomer = true;
            } else {
                System.out.println("No guest with that name");
            }
        }while(!validCustomer);
        return customer;
    }

    /**
     * Menu to let user create a booking party by adding new guest or choose existing.
     * options to go back to main menu and clear the party, and to go forward to continue the booking
     * @throws InterruptedException For sleep to work
     */
    public void createCustomerMenu() throws InterruptedException {
        AppUtils.clearScreen();
        showParty();
        switch(AppUtils.menuBuilder("Booking", 1,4,"Create new guest","Choose existing guest","Continue with chosen party","Back to main menu and clear party")){
            case 1:
                addGuest();
                createCustomerMenu();
                break;
            case 2:
                Customer customer = chooseExistingGuest();
                if(customer != null){
                    this.currentParty.add(customer);
                }
                createCustomerMenu();
                break;
            case 3:
                if(currentParty.size() == 0){
                    createCustomerMenu();
                }else {
                    createBooking();
                }
                break;
            case 4:
                this.currentParty.clear();
                mainMenu();
                break;
        }
    }

    /**
     * Method to handle user input to send to corresponding method in DataServices to change information in database
     */
    public void editBooking(){
        int bookingId = Integer.parseInt(AppUtils.userInput("booking id to be updated"));
        String chkInDate = AppUtils.userInput("check in date to be updated");
        String chkOutDate = AppUtils.userInput("check out date to be updated");
        ds.updateBooking(bookingId,chkInDate,chkOutDate);
    }

    /**
     * Method to handle user input to send to corresponding method in DataServices to change information in database
     */
    public void editGuest(){
        Customer customer = chooseExistingGuest();
        String fullName = AppUtils.userInput("guest full name or leave empty");
        String email = AppUtils.userInput("guest email or leave empty");
        String phone = AppUtils.userInput("guest phone or leave empty");
        String birthdate = AppUtils.userInput("guest birthdate or leave empty");
        ds.updateCustomer(customer.getId(),fullName,email,phone,birthdate);
    }

    /**
     * Menu to handle editing of posts in database
     * @throws InterruptedException For sleep to work
     */
    public void editBookingMenu() throws InterruptedException {
        AppUtils.clearScreen();
        switch(AppUtils.menuBuilder("Edit Booking & Guests", 1,3,"Edit booking","Search for and edit guest","Back to main menu")){
            case 1:
                editBooking();
                editBookingMenu();
                break;
            case 2:
                editGuest();
                editBookingMenu();
                break;
            case 3:
                mainMenu();
                break;
        }
    }

    /**
     * Method to return every booked room
     * @param id input hard coded for a lazy programmer who didn't have time nor energy to fix that simple problem.
     */
    public void searchBooking(String id){
        ArrayList<BookedRoom> bookedRooms;
        if(!id.equalsIgnoreCase("id")){
            String orderBy = AppUtils.userInput("Order of which you want the results. id, Name, Check in date, Check out date");
            bookedRooms = ds.bookedRooms(orderBy);
        }else{
            bookedRooms = ds.bookedRooms("id");
        }

        for(BookedRoom room : bookedRooms){
            System.out.println( room);
        }
    }


    /**
     * Menu to handle deletion from database
     * @throws InterruptedException For sleep to work
     */
    public void deleteBookingMenu() throws InterruptedException {
        AppUtils.clearScreen();
        switch(AppUtils.menuBuilder("Delete booking", 1,2,"Delete booking","Back to main menu")){
            case 1:
                System.out.println("0. Back to main menu");
                System.out.print("Enter booking ID: ");
                int bookingId = AppUtils.userInput(0,1000);
                if(bookingId == 0){
                    mainMenu();
                }
                ds.deleteBooking(bookingId);
                deleteBookingMenu();
                break;
            case 2:
                mainMenu();
                break;
        }
    }


    /**
     * Method to take data and return a customer with corresponding method from DataService
     * @return Customer
     */
    public Customer customerForm(){
        String input = AppUtils.userInput("full name");
        String[] fullName= input.split(" ");
        String email = AppUtils.userInput("email, leave empty if you don't want to use one");
        String phone = AppUtils.userInput("phone, leave empty if you dont' want to use one");
        String birthdate = AppUtils.userInput("birth date");

        return  ds.getCustomerFromCustomerId(ds.createCustomer(fullName[0], fullName[1], email,phone,birthdate));
    }

    /**
     * Method to clean up create booking. Asks the user about how many rooms the party is needed
     * @return Amount of rooms
     */
    public int amountOfRooms(){
        boolean enoughSpace;
        int partySize = currentParty.size();
        int rooms;
        do{
            System.out.println("How many rooms do you want to book?");
            rooms = AppUtils.userInput(1,partySize);
            if(rooms*5 >= currentParty.size()){
                enoughSpace = true;
            }else{
                System.out.println("Sorry but you need more rooms for that many guests");
                enoughSpace = false;
            }
        }while(!enoughSpace);
        return rooms;
    }


    /**
     * Create booking method. Takes data from user to search and create a party.
     * Register is and insert into database with corresponding method from DataService
     * @throws InterruptedException For sleep to work
     */
    public void createBooking() throws InterruptedException {
        boolean extraBed;
        boolean fullBoard;
        boolean halfBoard;
        boolean emptyList;
        int totalPrice = 0;
        int counter = 1;
        int rooms = amountOfRooms();
        int[] roomNumber;
        int sumOfDays;
        int bookingId;
        String chkInDate;
        String chkOutDate;
        ArrayList<AvailableRoom> availableRooms;

        do{
            System.out.println("Please answer the following questions with yes/no or y/n or leave empty");
            boolean pool = AppUtils.trueOrFalse("Is it important that your hotel has a Pool?");
            boolean entertainment = AppUtils.trueOrFalse("Is it important that your hotel has Entertainment?");
            boolean kidsClub = AppUtils.trueOrFalse("Is it important that your hotel has a Children club?");
            boolean restaurant = AppUtils.trueOrFalse("Is it important that your hotel has a Restaurant?");
            chkInDate = AppUtils.userInput("which day you want to check in");
            chkOutDate = AppUtils.userInput("which day you want to check out");
            String orderBy = AppUtils.userInput("in which way you want to order the result (name, beds, price, beach(distance to), downtown(distance to), review)");
            sumOfDays = calculateDays(chkInDate, chkOutDate);
            bookingId = ds.createBooking(currentParty.get(0).getId(),chkInDate,chkOutDate);
            availableRooms = listAvailableRooms(chkInDate,chkOutDate,orderBy,pool,entertainment,kidsClub,restaurant);
            roomNumber = new int[rooms];

            if(availableRooms.isEmpty()){
                emptyList = true;
                System.out.println("No rooms found, please try again!");
            }else{
                emptyList = false;
            }
        }while(emptyList);


        for(int i = 0 ; i < rooms; i++){
            System.out.println("Please pick which room you are interested in");
            roomNumber[i] = AppUtils.userInput(1,availableRooms.size())-1;
            System.out.println("You picked " + availableRooms.get( roomNumber[i] ) );
            extraBed = AppUtils.trueOrFalse("Do you want an extra bed in your room?");
            fullBoard = AppUtils.trueOrFalse("Do you want full board?");
            if(!fullBoard){
                halfBoard = AppUtils.trueOrFalse("Do you want half board?");
            }else {
                halfBoard = false;
            }
            System.out.println("Please pick the persons staying in the room");

            for (Customer existingGuests : currentParty) {
                System.out.println(counter + ". " + existingGuests);
                counter++;
            }



            int customerId = AppUtils.userInput(1, currentParty.size())-1;
            ds.createParty(currentParty.get(customerId).getId(),bookingId,availableRooms.get( roomNumber[i]).getRoomId());
            totalPrice += ds.getPrice(extraBed,fullBoard,halfBoard,availableRooms.get( roomNumber[i]).getRoomId());
        }

        bookingComplete(chkInDate,chkOutDate,sumOfDays,rooms,availableRooms,roomNumber,totalPrice);
        this.currentParty.clear();
        mainMenu();


    }

    /**
     * Method to display everything from the booking to make the booking method cleaner
     * @param chkInDate Check in date
     * @param chkOutDate Check out date
     * @param sumOfDays how many days the booking is
     * @param rooms How many rooms the booking contains
     * @param availableRooms List of all available rooms
     * @param roomNumber Which room numbers that's included in booking
     * @param totalPrice Total price for everything
     * @throws InterruptedException For sleep to work
     */
    public void bookingComplete(String chkInDate, String chkOutDate, int sumOfDays, int rooms, ArrayList<AvailableRoom> availableRooms, int roomNumber[],int totalPrice) throws InterruptedException {
        System.out.println("Your booking is now completed");
        System.out.println("You have chosen to stay between "+ chkInDate +" - " + chkOutDate + " for " + sumOfDays + " days.");
        Thread.sleep(1000);
        System.out.println("You have chosen room(s):" );
        for(int i = 0; i < rooms; i++){
            System.out.println(availableRooms.get( roomNumber[i]));
        }
        Thread.sleep(1000);
        System.out.println("Total cost for all rooms, meals and extra beds is : $" + totalPrice*sumOfDays);
        System.out.println("Returning to main menu");
        for(int i = 0; i < 10 ; i ++){
            System.out.print(".");
            Thread.sleep(1000);
        }
    }

    /**
     * Method to clean up some code in create booking method
     * @param chkInDate Check in date
     * @param chkOutDate Check out date
     * @param orderBy Order of which list will be ordered
     * @param pool true or false
     * @param entertainment true or false
     * @param kidsClub true or false
     * @param restaurant true or false
     * @return rooms available to book
     */
    private ArrayList<AvailableRoom> listAvailableRooms(String chkInDate, String chkOutDate, String orderBy, boolean pool, boolean entertainment, boolean kidsClub, boolean restaurant) {
        ArrayList<AvailableRoom> availableRooms = ds.availableRooms(chkInDate,chkOutDate,orderBy,pool,entertainment,kidsClub,restaurant);
        int count = 1;
        for (AvailableRoom room : availableRooms){
            System.out.println(count + " " + room);
            count++;
        }
        return availableRooms;
    }

    /**
     * Calculate amount of days from 2 dates between 2022-06-01 - 2022-07-31
     * @param chkInDate First date
     * @param chkOutDate Second date
     * @return Amount of days
     */
    private int calculateDays(String chkInDate, String chkOutDate) {
        int chkInDateAsInt;
        int chkOutDateAsInt;
        int sumOfDays = 0;
        if((chkInDate.charAt(6) == '6' && chkOutDate.charAt(6) == '6' || chkInDate.charAt(6) == '7' && chkOutDate.charAt(6) == '7' )){
            chkInDateAsInt = Integer.parseInt(chkInDate.replaceAll("-",""));
            chkOutDateAsInt = Integer.parseInt(chkOutDate.replaceAll("-",""));
            sumOfDays = (chkOutDateAsInt - chkInDateAsInt) + 1;
        }else if(chkInDate.charAt(6) == '6' && chkOutDate.charAt(6) == '7'){
            sumOfDays = 30- Integer.parseInt(chkInDate.substring(8,10)) + Integer.parseInt(chkOutDate.substring(8,10));
        }
        return sumOfDays;

    }


}
