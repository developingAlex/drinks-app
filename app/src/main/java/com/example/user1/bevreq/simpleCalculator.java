package com.example.user1.bevreq;

import java.util.ArrayList;

import android.widget.Toast;

public class simpleCalculator implements Calculator {

    @Override
    public int[] getAmounts(ArrayList<Drink> globaldrinks, int pax,
                            float duration, String functionType, ArrayList<String> ads) {
        int numberOfDrinks = globaldrinks.size();
        int returnArray[] = new int[numberOfDrinks];
        int i ;
        for (i=0;i<globaldrinks.size();i++){
            returnArray[i] = getAmountForDrink(globaldrinks.get(i),pax, duration, functionType);
        }
        if (ads !=null){
            int j;
            for(j=0;j<ads.size();j++){//foreach adjuster checkbox that was ticked
                //go through the adjustments list in Situation and find the one that applies
                int ii;
                for(ii=0;ii<Situation.functionAdjustmentList.size();ii++){//foreach type of adjuster
                    if (Situation.functionAdjustmentList.get(ii).adjustName.equals(ads.get(j))){//find the one with the same name as given
                        int jj;
                        for(jj=0;jj<Situation.functionAdjustmentList.get(ii).adjustmentsDrinks.size();jj++){//foreach drink adjustment in that adjuster
                            int index = Situation.globalDrinksList.indexOf(Situation.functionAdjustmentList.get(ii).adjustmentsDrinks.get(jj).drinkToAdjust);
                            returnArray[index] = (int)((float)returnArray[index] * Situation.functionAdjustmentList.get(ii).adjustmentsDrinks.get(jj).adjustment);
                        }
                    }
                }

            }
        }
        return returnArray;
    }

    private int getAmountForDrink(Drink dri, int p, float dur, String t){
        if(dri.getCoefficentOfFunction(t) == 0){
            System.out.println("0 FunctionCoefficient for "+dri.drinkType);
            return 0;

        }
        int retval = 0;
        if (dur<1.0){
            retval = (int) (dri.getCoefficentOfFunction(t)*(float)p*0.8);//shorter than normal
        }else if (dur >3.5){
            retval = (int) (dri.getCoefficentOfFunction(t)*(float)p*1.2);//longer than normal

        }else{
            retval = (int) (dri.getCoefficentOfFunction(t)*(float)p*1);//normal

        }
        if (retval<1)
            retval = 1;
        return retval;

    }
    private void displayQuickToastMessage(String msg){
        Toast.makeText(null, msg, Toast.LENGTH_SHORT).show();
    }
    private void displayLongToastMessage(String msg){
        Toast.makeText(null, msg, Toast.LENGTH_LONG).show();
    }
}
