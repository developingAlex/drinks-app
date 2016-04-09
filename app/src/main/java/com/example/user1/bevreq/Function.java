package com.example.user1.bevreq;

import java.util.ArrayList;

public class Function {

    String type;
    int pax;
    float duration;
    ArrayList<Drink> negatoryDrinks; //could have represented this in a more optimal way rather than
    //an arraylist of drink objects but it makes it easier to understand.
    int[] amounts;
    ArrayList<String> adjusters;

    public Function(String t, int p, float d, int numberOfDrinkTypes, ArrayList<String> adjusters){
        type = t;
        pax = p;
        duration = d;
        amounts = new int[numberOfDrinkTypes];
        this.adjusters = adjusters;
        negatoryDrinks = new ArrayList<Drink>();
    }

    public void removeDrink(Drink d){
        if (!negatoryDrinks.contains(d))
            negatoryDrinks.add(d);
    }
    public int[] getAmounts(ArrayList<Drink> drinksList){
        Calculater myCalc = new simpleCalculater();
        amounts = myCalc.getAmounts(drinksList, pax, duration, type, adjusters);
        if(!negatoryDrinks.isEmpty()){
            int i;
            for (i=0;i<negatoryDrinks.size();i++){
                amounts[Situation.globalDrinksList.indexOf(negatoryDrinks.get(i))] = 0;
            }
        }
        return amounts;
    }
    public String toString(){
        return type;
    }

}
