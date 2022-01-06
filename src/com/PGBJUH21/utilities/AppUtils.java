package com.PGBJUH21.utilities;

import java.util.Scanner;

public class AppUtils {

    public static int userInput(int minValue, int maxValue){

        Scanner console = new Scanner(System.in);
        int menuChoice;
        do{
            try{
                menuChoice = Integer.parseInt(console.nextLine());
            }catch(Exception e){
                menuChoice = -1;
            }
        }while(menuChoice < minValue || menuChoice > maxValue || menuChoice == -1);
        return menuChoice;
    }

    public static String userInput(String regEx){

        Scanner console = new Scanner(System.in);
        String input;
        do{
            if (regEx == "null"){
                input = console.nextLine();
                break;
            }
            input = console.nextLine();
            if(!input.matches(regEx)){
                System.out.println("Input error, please try gain");
            }
        }while(!input.matches(regEx));

        return input;
    }

    public static int menuBuilder(String menuName, int minValue, int maxValue, String ...options){
        System.out.println(menuName);
        int i = 1;
        for(String menuOptions : options){
            System.out.println(i + ". " + menuOptions);
            i++;
        }

        return AppUtils.userInput(minValue,maxValue);

    }

}
