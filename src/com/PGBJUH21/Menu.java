package com.PGBJUH21;

public class Menu {

    public static void mainMenu(){
        switch(AppUtils.menuBuilder("Main Menu", 1,3,"Test1","Test2","Test3")){
            case 1:
                System.out.println("1");
                break;
            case 2:
                System.out.println("2");
                break;
            case 3:
                System.out.println("3");
                break;
        }

    }

}
