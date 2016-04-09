package com.example.user1.bevreq;

import java.util.ArrayList;

public class DrinksList {
    private ArrayList<Drink> myDrinks;

    public DrinksList(){
        myDrinks = new ArrayList<Drink>();
    }
    public DrinksList(ArrayList<Drink> x){
        myDrinks = x;
    }
    public Drink getDrinkByName(String x){
        int i;
        for (i=0;i<myDrinks.size();i++){
            if (myDrinks.get(i).drinkType.equals(x)){
                return myDrinks.get(i);
            }
        }
        System.out.println("it's null");
        return null;
    }
    //method to add a drink to the list
    public void addDrink(Drink y){
        myDrinks.add(y);
    }
    //method to get the drinks list
    public ArrayList<Drink> getGlobalList(){
        return myDrinks;
    }

    //method to remove a drink to the list
    public void removeDrink(Drink y){

    }
}
