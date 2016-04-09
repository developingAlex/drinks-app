package com.example.user1.bevreq;


import java.util.ArrayList;

/*This class encapsulates the properties of a particular drink*/
public class Drink {
    //variables:
    public String drinkType;
    class displayNames2{
        ArrayList<String> names;
        ArrayList<Integer> units;

    }
    public ArrayList<String> displayNames; //eg {"bottles of Heavy Beer","Heavy Beer Longnecks" "kegs of Heavy Beer"}
    public ArrayList<Integer> displayNameUnits;//eg {1,2,150}

    public ArrayList<String> associatedDrinksArrayList;//Associated drinks increase in quantity to
    // compensate for the loss of a drink they associate with. for Example, apple juice, orange
    // juice and pear juice are associated, if there is no apple juice then the other two should
    // increase so that there is still the same amount of juice available to customers. The app would
    // know that there is no apple juice because the user unticked "apple juice" when given the option to untick drinks.
    // (currently not implemented in this version).
    private String LIST_SEPARATOR = ".";
    class drinkFunctionFormula {
        String Function;
        float FunctionCoefficient;
        public drinkFunctionFormula(String nameOfFunction, float coefficientOfFunction){
            Function = nameOfFunction;
            FunctionCoefficient = coefficientOfFunction;
        }
    }

    public  ArrayList<drinkFunctionFormula> coefficientsArrayList = new ArrayList<drinkFunctionFormula>(); //order should copy the order of other drinks.


    //methods:
    public void addFunction(String functionName, float functionCoefficient){
        drinkFunctionFormula formulaToAdd = new drinkFunctionFormula(functionName, (float) functionCoefficient);
        coefficientsArrayList.add(formulaToAdd);
    }

    public Drink(String drinkName, String drinkDisplayName){
        drinkType = drinkName;//eg HeavyBeer
        displayNames = new ArrayList<String>();
        displayNameUnits = new ArrayList<Integer>();
        associatedDrinksArrayList = new ArrayList<String>();
        displayNames.add(drinkDisplayName); //eg bottles of Heavy Beer
        displayNameUnits.add(1);//eg if drinkDisplayName was "longneck bottles of Heavy Beer" this
        // would be 2. because there is two beers in one longneck.

    }
    public Drink(ArrayList<String> deserializeThis, ArrayList<String> functionTypes){
        if((deserializeThis.size() < 3)||deserializeThis.isEmpty()){
            System.out.println("String Array passed to drink constructor is too short to be legit");
            System.exit(3);
        }

        displayNames = new ArrayList<String>();
        displayNameUnits = new ArrayList<Integer>();
        associatedDrinksArrayList = new ArrayList<String>();

        drinkType = deserializeThis.get(0);
        System.out.println("drinkType Added:"+ drinkType);
        int i;
        int endOfDisplayNamesIndex = -1;
        int endOfFriendsIndex = -1;
        for(i=0;i<deserializeThis.size();i++){
            if(deserializeThis.get(i).equals(LIST_SEPARATOR)){
                endOfDisplayNamesIndex = i;
                break;
            }
        }
        int j;
        for(j=i+1;j<deserializeThis.size();j++){
            if(deserializeThis.get(j).equals(LIST_SEPARATOR)){
                endOfFriendsIndex = j;
                break;
            }
        }
        //displaynames
        for(i=1;i<endOfDisplayNamesIndex;i=i+2){//start i at one because drinkType is already read.
            displayNames.add(deserializeThis.get(i));
            System.out.println("displayNameAdded:"+deserializeThis.get(i));
            displayNameUnits.add (Integer.parseInt(deserializeThis.get(i+1)));
            System.out.println("displayNameUnitAdded:"+deserializeThis.get(i+1));
        }
        //associatedDrinksArrayList
        for (i=endOfDisplayNamesIndex+1;i<endOfFriendsIndex;i++){
            associatedDrinksArrayList.add(deserializeThis.get(i));
            System.out.println("added friend:"+deserializeThis.get(i));
        }
        for(i=endOfFriendsIndex;i<deserializeThis.size();i++){
            System.out.println(deserializeThis.get(i));
        }
        //check that the supplied functionTypes list is the right size:
        int size1 = deserializeThis.size()-(endOfFriendsIndex+1);
        int size2 = functionTypes.size();
        if (size1 != size2){
            System.out.println("mismatch between remaining coefficientsArrayList' and functionTypes sizes:"+size1+","+size2);
            System.exit(3);

        }
        //coefficientsArrayList
        for(i=endOfFriendsIndex+1;i<deserializeThis.size();i++){
            coefficientsArrayList.add(new drinkFunctionFormula(functionTypes.get(i - 1 - endOfFriendsIndex), Float.parseFloat(deserializeThis.get(i))));
        }

    }
    public ArrayList<String> serializeThis(){
        //returns an array of strings which when given to this class' constructor should create an object that is a copy of this object
        ArrayList<String> mySer = new ArrayList<String>();
        mySer.add(drinkType);
        int i;
        for (i=0;i<displayNames.size();i++){
            mySer.add(displayNames.get(i));
            mySer.add(Integer.toString(displayNameUnits.get(i)));
        }
        mySer.add(LIST_SEPARATOR);
        for (i=0;i< associatedDrinksArrayList.size();i++){
            mySer.add(associatedDrinksArrayList.get(i));
        }
        mySer.add(LIST_SEPARATOR);

        for(i=0;i< coefficientsArrayList.size();i++){
            System.out.println("adding the FunctionCoefficient: "+Float.toString(coefficientsArrayList.get(i).FunctionCoefficient));
            mySer.add(Float.toString(coefficientsArrayList.get(i).FunctionCoefficient));
        }
        return mySer;
    }


    public void addNewDisplayNameUnit(int units, String displayName){
        displayNameUnits.add(units);
        displayNames.add(displayName);
    }

    public void removeDisplayNameUnit(String displayName){
        for (int i = 0; i < displayNames.size(); i++){
            if(displayNames.get(i) == displayName){
                displayNames.remove(i);
                displayNameUnits.remove(i);
                break;
            }
        }
    }

    public void changeDisplayNameUnit(int units, String displayName){
        for (int i = 0; i < displayNames.size(); i++){
            if(displayNames.get(i) == displayName){
                displayNameUnits.set(i,units);
                break;
            }
        }
    }

    public void adjustCoefficient(String functionToAffect, float newCoefficient){
        int i;
        for(i=0;i< coefficientsArrayList.size();i++){
            if(coefficientsArrayList.get(i).Function.equals(functionToAffect)){
                coefficientsArrayList.get(i).FunctionCoefficient = newCoefficient;
                return;
            }
        }
    }
    public void changeCoef(int indexOfFunctionOfCoefToChange, float newCoef){
        coefficientsArrayList.get(indexOfFunctionOfCoefToChange).FunctionCoefficient = newCoef;
    }

    public void addAssociatedDrink(Drink associatedDrink){

        if (!associatedDrink.equals(this)){//can't add itself
            if (!associatedDrinksArrayList.contains(associatedDrink.drinkType)){//can't double add
                associatedDrinksArrayList.add(associatedDrink.drinkType);
                associatedDrink.addAssociatedDrink(this);
            }
        }
        //if associatedDrink is not yet in the list of other drinks,
        //add associatedDrink to list and call associatedDrink.addAssociatedDrink(this)
        //now when the user unticks a drink from the list, we can
        //easily look at all it's 'associatedDrinksArrayList' to know which drinks we
        //should increase to compensate.

    }

    public float getCoefficentOfFunction(String function){
        //go through coefficientsArrayList and find the drinkFunctionFormula with corresponding function
        int i;
        for (i=0;i< coefficientsArrayList.size();i++){
            if(coefficientsArrayList.get(i).Function.equals(function)){
                return coefficientsArrayList.get(i).FunctionCoefficient;
            }
        }

        //todo: alternatively, decide if we want the program to die at this point.
        return (float)0.0;
    }
    public ArrayList<String> getFunctionTypes(){
        int i;
        ArrayList<String> functionTypesArrayList = new ArrayList<String>();
        for (i=0;i< coefficientsArrayList.size();i++){
            functionTypesArrayList.add(coefficientsArrayList.get(i).Function);
        }
        return functionTypesArrayList;
    }


}
