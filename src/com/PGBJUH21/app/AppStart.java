package com.PGBJUH21.app;

import com.PGBJUH21.Databasetables.Customer;
import com.PGBJUH21.querytables.AvailableRoom;
import com.PGBJUH21.utilities.AppUtils;

import java.util.ArrayList;


/*

reseadministratören kan skapa bokningar (genom att alla queries för detta kan köras).
Grundläggande val (admin kan markera att detta ska finnas i anslutning till boendet)
Pool
Kvällsunderhållning
Barnklubb
Restaurang

Beskriva sällskapet
Antal personer, deras för- efternamn, email, telefonnr och födelsedatum.

När administratören pratar med en kund (t ex över telefon, chat, mail eller personligen), ska administratören kunna:
registrera in kunden.
söka efter lediga rum med specificerade sökkriterier (se lista nedan), mellan specificerade datum,
och sedan boka dem.
avboka rum.
Administratörens gränssnitt ska vara en meny i konsollen.
Efter ett menyval kan administratören behöva svara på flera frågor efter varandra
för att samla in data, som sedan ska skrivas till databasen, eller ligga till grund för
sökning Exempel på en sådan konsoll-meny med följdfrågor finns här


Skapa bokningar med tilläggstjänster (extrasäng, halvpension, helpension).
Ändra detaljerna i en bokning.
Söka på boenden baserat på avstånd till strand (hur långt borta får boendet max ligga ifrån en strand)
Söka på boenden baserat på till centrum (hur långt borta får boendet max ligga ifrån ett centrum)
Söktraffärar ska kunna ordnas på pris (lågt till högt)
Söktraffärar ska kunna ordnas på omdöme (högt till lågt)





Registrera kund
Söka lediga rum
    - antal personer
    - pool
    - barnklubb
    - restaurang
    - pris
OCH boka dem
Avboka rum
Skapa bokning

VG
Skapa bokning med tilläggstjänst
    - Extrasäng
    - Halvpension
    - Helpension
Ändra detaljer i en bokning
Söka med avstånd till strand
Söka med avstånd till centrum
Ordnas på pris Lågt till högt



Main menu
1. Sök ledigt rum
2. Registrera ny kund
3. Ändra bokning
4.
5.

Create new booking
1. Register new customer
2. Use existing customer
3. Choose hotel
4. Search for room
5.

Register new customer (meny?)
1.
2.
3.
4.
5.

Use existing customer (Meny ?)
1.
2.
3.
4.
5.

Choose hotel
1.
2.
3.
4.
5.
6.
7.
8.

Search for room
1. Custom search
1. Search based on distance to beach
2. Search based on distance to centrum
3. Search based on antal personer
4. Search based on pool
5. Search based on kidsclub
6. Search based on entertainment
7. Search based on restaurant
8.
9.
10.


 */

public class AppStart {

        private DataService ds;
        private ArrayList<Customer> currentParty;

    AppStart(){
        currentParty = new ArrayList<>();
        ds = new DataService();
        ds.connect();

        //System.out.println(ds.getPrice(true,false,true,1));


//        ArrayList<AvailableRoom> room = ds.availableRooms("2022-07-01","2022-07-08","",true,true,true,true);
//
//        for(AvailableRoom rooms : room){
//            System.out.println(rooms);
//        }

//        createCustomerMenu();

        mainMenu();

    }



    public void mainMenu(){
        switch(AppUtils.menuBuilder("Main Menu", 1,3,"Create new booking","Edit existing booking")){
            case 1:
                createCustomerMenu();
                break;
            case 2:
                editBookingMenu();
                break;
            case 3:
                break;
        }
    }

    public Customer customerForm(){

        String input = AppUtils.userInput("full name");
        String[] fullName= input.split(" ");
        String email = AppUtils.userInput("email, leave empty if you don't want to use one");
        String phone = AppUtils.userInput("phone, leave empty if you dont' want to use one");
        String birthdate = AppUtils.userInput("birth date");

        return  ds.getCustomerFromCustomerId(ds.createCustomer(fullName[0], fullName[1], email,phone,birthdate));
    }

    public void createBookingMenu(){
        int partySize = currentParty.size();
        boolean enoughSpace;
        int rooms;
        do{
            System.out.println("How many rooms do you want to book?");
            rooms = AppUtils.userInput(1,partySize);
            System.out.println(rooms);
            System.out.println(currentParty.size());
            if(rooms*5 >= currentParty.size()){
                enoughSpace = true;
            }else{
                System.out.println("Sorry but you need more rooms for that many guests");
                enoughSpace = false;
            }
        }while(!enoughSpace);

        System.out.println("Please answer the following questions with yes/no or y/n or leave empty");
        boolean pool = AppUtils.trueOrFalse("Is it important that your hotel has a Pool?");
        boolean entertainment = AppUtils.trueOrFalse("Is it important that your hotel has Entertainment?");
        boolean kidsClub = AppUtils.trueOrFalse("Is it important that your hotel has a Children club?");
        boolean restaurant = AppUtils.trueOrFalse("Is it important that your hotel has a Restaurant?");
        String chkInDate = AppUtils.userInput("which day you want to check in");
        String chkOutDate = AppUtils.userInput("which day you want to check out");
        String orderBy = AppUtils.userInput("in which way you want to order the result (name, beds, price)");

        int chkInDateAsInt;
        int chkOutDateAsInt;
        int sumOfDays = 0;

        // 2022-06-01
        if((chkInDate.charAt(6) == '6' && chkOutDate.charAt(6) == '6' || chkInDate.charAt(6) == '7' && chkOutDate.charAt(6) == '7' )){
            chkInDateAsInt = Integer.parseInt(chkInDate.replaceAll("-",""));
            chkOutDateAsInt = Integer.parseInt(chkOutDate.replaceAll("-",""));
            sumOfDays = (chkOutDateAsInt - chkInDateAsInt) + 1;
        }else if(chkInDate.charAt(6) == '6' && chkOutDate.charAt(6) == '7'){
            sumOfDays = 30- Integer.parseInt(chkInDate.substring(8,10)) + Integer.parseInt(chkOutDate.substring(8,10));
        }

        ds.createBooking(currentParty.get(0).getId(),chkInDate,chkOutDate);

        ArrayList<AvailableRoom> availableRooms = ds.availableRooms(chkInDate,chkOutDate,orderBy,pool,entertainment,kidsClub,restaurant);
        int count = 1;
        for (AvailableRoom room : availableRooms){
            System.out.println(count + " " + room);
            count++;
        }
        int[] roomNumber = new int[rooms];
        boolean extraBed;
        boolean fullBoard;
        boolean halfBoard;
        int totalPrice = 0;
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
            ds.createParty()
            totalPrice += ds.getPrice(extraBed,fullBoard,halfBoard,availableRooms.get( roomNumber[i]).getRoomId());
        }
        System.out.println(sumOfDays);
        System.out.println(totalPrice);
        System.out.println("Total cost for all rooms, meals and extra beds is : " + totalPrice*sumOfDays);



        if(AppUtils.trueOrFalse("Continue with your booking?")){
        }else{
            createBookingMenu();
        }
    }





    public void createCustomerMenu(){
        AppUtils.clearScreen();
        if(this.currentParty.size() > 0){
            System.out.println("Current party");
            for(Customer c : this.currentParty){
                System.out.println(c);
            }
            System.out.println();
        }
        switch(AppUtils.menuBuilder("Create customer", 1,3,"Create new guest","Choose existing guest","Continue with chosen party","Back to main menu and clear party")){
            case 1:
                this.currentParty.add(customerForm());
                boolean cont;
                do{
                    cont = AppUtils.trueOrFalse("Add another guest? yes/no\"");
                    if(cont){
                        this.currentParty.add(customerForm());
                    }else{
                        cont = false;
                    }
                }while(cont);
                createCustomerMenu();
                break;
            case 2:
                boolean validCustomer = false;
                do {
                    String customer = AppUtils.userInput("name of guest");
                    ArrayList<Customer> listOfCustomers = ds.getCustomer(customer);
                    int c = 1;
                    System.out.println("Choose guest: ");
                    for (Customer existingGuests : listOfCustomers) {
                        System.out.println(c + ". " + existingGuests);
                    }
                    if (listOfCustomers.size() > 0) {
                        int customerNr = AppUtils.userInput(1, listOfCustomers.size());
                        this.currentParty.add(listOfCustomers.get(customerNr-1));
                        validCustomer = true;
                    } else {
                        System.out.println("No guest with that name");
                        createCustomerMenu();
                    }
                }while(!validCustomer);
                createCustomerMenu();
                break;
            case 3:
                if(currentParty.size() == 0){
                    createCustomerMenu();
                }else {
                    createBookingMenu();
                }

                break;
            case 4:
                this.currentParty.clear();
                mainMenu();
                break;
        }

    }

    public void editBookingMenu(){
        switch(AppUtils.menuBuilder("Edit Booking", 1,3,"Test1","Test2","Back to main menu")){
            case 1:
                System.out.println("Search for Booking id");
                ArrayList<String> booking = ds.getGuestsByBookingId(1);
                for(String k : booking){
                    System.out.println(k);
                }
                break;
            case 2:
                System.out.println("Search for customer");
                ArrayList<String> customer = ds.getCustomerFromBooking("Lee Speek");
                for(String k : customer){
                    System.out.println(k);
                }
                break;
            case 3:
                System.out.println("Search for check in date");
                ds.getCheckInDate();
                break;
            case 4:
                mainMenu();
                break;
        }
    }

}
