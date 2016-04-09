package com.example.user1.bevreq;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;


/*The situation is the object that basically represents the situation (function event) that the
 * user is catering for. it contains:
 * list of all available drinks
 * list of functions for the current situation(eg current situation =  dinner with PDD so
 * list of functions contains two: dinner function type for how many hours, and PDD function
 * type for how many hours.), where each function contains
 * function type,
 * pax
 * duration
 * list of drinks that are taken off the menu
 *
 * The situation object can call upon the calculater object to provide the numbers for each drink
 * type.
 * */
//todo, make this whole thing static. because there will ever only be one instance
public class Situation {
    public static ArrayList<Drink> globalDrinksList;//to be initialised from file but never modified
    public static ArrayList<String> globalFunctionsList;//to be initialised from file but never modified
    private static Context myContext;
    public static float firstDrinkCoef = (float)0.0;//for testing
    private final static float BOX_KEG_THRESHOLD = (float)0.9;//the threshold to reach before deciding to report bottles in units of cases or kegs
    //	private static final String CONFIG_FILE = "configFile.ini";
    static class DrinkAdjustment{
        Drink drinkToAdjust;
        float adjustment;
        private DrinkAdjustment(Drink d, float f){
            drinkToAdjust = d;
            adjustment = f;
        }
    }

    static class  FunctionAdjustment{
        String adjustName;
        ArrayList<DrinkAdjustment> adjustmentsDrinks = new ArrayList<DrinkAdjustment>();
        private FunctionAdjustment(String n){
            adjustName = n;
        }
        private void addDrinkAdjustment(Drink d, float f){
            //TODO: CHECK THE DRINK BEING ADDED ISN'T ALREADY ADDED
            adjustmentsDrinks.add(new DrinkAdjustment(d,f));
        }
        private void removeDrinkAdjustment(Drink d){
            for (int i = 0; i< adjustmentsDrinks.size();i++){
                if (adjustmentsDrinks.get(i).drinkToAdjust == d){
                    adjustmentsDrinks.remove(i);
                    break;
                }
            }
        }
        private void removeDrinkAdjustment(Integer indexOfDrink){
            adjustmentsDrinks.remove(indexOfDrink);
        }
    }

    public static ArrayList<FunctionAdjustment> functionAdjustmentList;

    public static ArrayList<Function> functionList;
    static String VALUE_SEPARATER2 = String.valueOf((char)((byte)2));
    static String VALUE_SEPARATER3 = String.valueOf((char)((byte)3));
    static String VALUE_SEPARATER4 = String.valueOf((char)((byte)4));

    public static void Initialise(Context myC){
        myContext = myC;
        //populate the globalDrinksList from file
        functionList = new ArrayList<Function>();
        globalFunctionsList = new ArrayList<String>();
        functionAdjustmentList = new ArrayList<FunctionAdjustment>();
        globalDrinksList = new ArrayList<Drink>();
//		getDrinksFromFile();
        //updateIniFile();
        retrieveStateFromFile();
        firstDrinkCoef = globalDrinksList.get(0).coefficientsArrayList.get(0).FunctionCoefficient;
    }
    public  static void addFunction(int pax, float duration, String type, ArrayList<String> adjusters){
        functionList.add(new Function(type, pax, duration, globalDrinksList.size(), adjusters));
        System.out.println("globalDrinksList.size()="+globalDrinksList.size());
    }
    /*this method will remove a drink from a function so that the
    function doesn't even display the drink in it's results. for example
    wine at Breakfast.*/
    public static void removeDrinkFromFunctionOnly(int functionId, Drink d){
        functionList.get(functionId).removeDrink(d);
    }

    public static void deleteDrinkEntirelyFromApp(Drink drinkToDelete){
        //iterate through the global drinks list until we find the drink to remove
        // also remove any reference to this drink in drink adjustments list.*/

        if (isDrinkReferencedInAdjustments(drinkToDelete) == true) {
            removeDrinkFromAdjustments(drinkToDelete);
        }

        int indexOfDrinkToDeleteInGlobalDrinksList=-1;
        for(int i = 0; i< globalDrinksList.size();i++){
            if (globalDrinksList.get(i) == (drinkToDelete)){
                indexOfDrinkToDeleteInGlobalDrinksList = i;
                break;
            }
        }
        //check that we actually found the drink to delete.
        if (indexOfDrinkToDeleteInGlobalDrinksList != -1){
            globalDrinksList.remove(indexOfDrinkToDeleteInGlobalDrinksList);
        }

    }

    public static void removeDrinkFromAdjustments(Drink drinkToRemove){
        for (int i = 0; i<functionAdjustmentList.size(); i++){
            for (int ii = 0; ii < functionAdjustmentList.get(i).adjustmentsDrinks.size(); ii++) {
                if (functionAdjustmentList.get(i).adjustmentsDrinks.get(ii).drinkToAdjust == drinkToRemove ){
                    functionAdjustmentList.get(i).removeDrinkAdjustment(ii);
                }
            }
        }
    }

    public static Boolean isDrinkReferencedInAdjustments(Drink drink){
        for (int i = 0; i<functionAdjustmentList.size(); i++){
            for (int ii = 0; ii < functionAdjustmentList.get(i).adjustmentsDrinks.size(); ii++) {
                if (functionAdjustmentList.get(i).adjustmentsDrinks.get(ii).drinkToAdjust == drink ){
                    return true;
                }
            }
        }
        return false;
    }

    public static int[] getAmounts(int functionId){
        return functionList.get(functionId).getAmounts(globalDrinksList);
    }

    public static Drink getDrinkByName(String name){
        int i;
        for (i=0;i<globalDrinksList.size();i++){
            if (globalDrinksList.get(i).drinkType.equals(name))
                return globalDrinksList.get(i);
        }
        return null;
    }

    public static ArrayList<String> getGlobalDrinksListAsNames () {
        int i;
        ArrayList<String> globalDrinksListAsNames = new ArrayList<String>();
        for (i=0;i<globalDrinksList.size();i++){
            globalDrinksListAsNames.add(globalDrinksList.get(i).drinkType);
        }
        return globalDrinksListAsNames;
    }

    public static String getDrinkAmountsAsString(){
        String DrinkAmountsAsString = "";

        int currentDrink,currentFunction;
        float amount;
        for (currentDrink = 0; currentDrink < globalDrinksList.size(); currentDrink++){
            amount = 0;
            for(currentFunction=0;currentFunction<functionList.size();currentFunction++){
                amount += getAmounts(currentFunction)[currentDrink];
            }
            if(amount != 0){

                //find the value that it should be reported in,
                int currentDisplayName;
                String nameToDisplay = globalDrinksList.get(currentDrink).displayNames.get(0);
                for(currentDisplayName=globalDrinksList.get(currentDrink).displayNames.size()-1;currentDisplayName>=0;currentDisplayName--){


                    int amountPerBoxOrKeg = globalDrinksList.get(currentDrink).displayNameUnits.get(currentDisplayName);
                    if(amount >= BOX_KEG_THRESHOLD*amountPerBoxOrKeg){
                        amount = getAmountInBoxesOrKegs((int) amount, amountPerBoxOrKeg);
                        nameToDisplay = globalDrinksList.get(currentDrink).displayNames.get(currentDisplayName);
                        break;
                    }
                }
                if(amount%1 !=0 ){  //make 2.0 bottles display as 2 bottles.
                    DrinkAmountsAsString += amount +" "+ nameToDisplay +"\n";
                }else{
                    DrinkAmountsAsString += (int)amount +" "+ nameToDisplay +"\n";
                }
            }
        }
        return DrinkAmountsAsString;
    }
    
    private static float getAmountInBoxesOrKegs(int numberOfBottles, int bottlesInACaseOrKeg){
        float initialResult = (float)numberOfBottles / (float)bottlesInACaseOrKeg;
        return roundUpToOneDecimalPlace(initialResult);//because we don't need to know that
        // it requires 1.324 kegs, 1.3 is enough.
    }

    private static float roundUpToOneDecimalPlace(float numberToRoundUp){
        numberToRoundUp = (float)((int)((float) numberToRoundUp * 10))/10;//this line acts to round up the result to the nearest first decimal place
        return numberToRoundUp;
    }

    public static String getBreakdown(){
        return null;//TODO: what was this meant to do??
    }

    public static void reset(){

        functionList.clear();
//		storeStateToFile();
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		retrieveStateFromFile();



    }
    //TODO: is this not used or something?
    public static void createDrink(String uniqueName, String displayName, ArrayList<String> coefName, ArrayList<Float> coefValue){

    }

    //	@SuppressWarnings("unchecked")
    public static void retrieveStateFromFile(){
//		clear everything and attempt to recreate state from file.
        globalDrinksList.clear();
        globalFunctionsList.clear();
        functionList.clear();
        functionAdjustmentList.clear();
        ArrayList<String> myState;
//		try{
        myState = MyStateStorer.retrieveState(myContext);
//		}catch (NullPointerException e){
        if (myState == null){
//			failed to retrieve from file for whatever reason.
//			create from defaults
            System.out.println("getting from defaults, couldn't find file");
            restoreAppDefaults();
//			retrieveStateFromFile();
            return;
        }
		/*till the first byte of 2, it's function types*/
        int i;

        System.out.println("Printing the ArrayList returned by MyStateStorer.retrieveState");
        for(i=0;i<myState.size();i++){
            System.out.println(myState.get(i));
        }
        System.out.println("Done: Printing the ArrayList returned by MyStateStorer.retrieveState");
        System.out.println(myState.indexOf(VALUE_SEPARATER2));
        List<String> functions = myState.subList(0, myState.indexOf(VALUE_SEPARATER2));
        System.out.println("got Up to Her3");
        globalFunctionsList = new ArrayList<String>(functions);


        /** following block is just for testing.*/
        System.out.println("\n\nin testFile(): elements of the recreated globalFunctionsList:");
        for(i=0;i<globalFunctionsList.size();i++){
            System.out.println(globalFunctionsList.get(i));
        }
        /****************************************/


//		next is the drinks
        int indexOfFirstDrink = myState.indexOf(VALUE_SEPARATER2) +1;
        int endIndexOfLastDrink = myState.indexOf(VALUE_SEPARATER3) -1;
        createTheseDrinks(new ArrayList<String> (myState.subList(indexOfFirstDrink, endIndexOfLastDrink)));

        int indexOfFirstAdjuster = myState.indexOf(VALUE_SEPARATER3) +1;
        createTheseAdjusters(new ArrayList<String> (myState.subList(indexOfFirstAdjuster, myState.size())));
        System.out.println("*\n*\nSTART: Printing the state after attempt to restore from file\n*\n*");
        printState();
        System.out.println("*\n*\nEND: Printing the state after attempt to restore from file\n*\n*");

    }

    private static void createTheseDrinks(ArrayList<String> givenArray){

        int indexOfEndOfCurrentDrink = givenArray.indexOf(VALUE_SEPARATER4) ;
        if(indexOfEndOfCurrentDrink == -1){

            Drink newDrink = new Drink(
                    new ArrayList<String>(givenArray), globalFunctionsList);
            globalDrinksList.add(newDrink);
        }else{
            Drink newDrink = new Drink(
                    new ArrayList<String>(givenArray.subList(0, indexOfEndOfCurrentDrink)),
                    globalFunctionsList);
            globalDrinksList.add(newDrink);
            createTheseDrinks(
                    new ArrayList<String>(givenArray.subList(indexOfEndOfCurrentDrink+1,givenArray.size())));
        }
    }

    private static void createTheseAdjusters(ArrayList<String> givenArray){
        if (givenArray.isEmpty())
            return;
        System.out.println("jj443\nAttempting to create adjuster with array:");
        int i;
        for(i=0;i<givenArray.size();i++){
            System.out.println(givenArray.get(i));
        }



//		of the form: adjusters separated by byte2,
//		each adjuster is of the form: string adjustername, followed by pairs of string drinkType, float adjuster.
        int indexOfEndOfFirstAdjuster = givenArray.indexOf(VALUE_SEPARATER2);
//		create an adjuster,
//		recursively pass the remainder back to this function.
        if(indexOfEndOfFirstAdjuster != -1){
            FunctionAdjustment adjuster = new FunctionAdjustment(givenArray.get(0));
            for(i=1;i<indexOfEndOfFirstAdjuster;i=i+2){
                adjuster.addDrinkAdjustment(getDrinkByName(givenArray.get(i)), Float.parseFloat(givenArray.get(i+1)));
            }
            functionAdjustmentList.add(adjuster);
            createTheseAdjusters(new ArrayList<String>(givenArray.subList(indexOfEndOfFirstAdjuster + 1, givenArray.size() - 1)));
        }else{
//			last adjuster, or there were never any adjusters
            FunctionAdjustment adjuster = new FunctionAdjustment(givenArray.get(0));
            for(i=1;i<givenArray.size();i=i+2){
                adjuster.addDrinkAdjustment(getDrinkByName(givenArray.get(i)), Float.parseFloat(givenArray.get(i+1)));
            }
            functionAdjustmentList.add(adjuster);
        }
    }

    public static void printState(){
        int i, ii;
//    	int iii;
        if(globalDrinksList.isEmpty()){
            System.out.println("Global Drink List is empty for some reason?");
            return;
        }

        System.out.println("Function types: ");
        for (i=0;i<globalDrinksList.get(0).getFunctionTypes().size();i++){
            System.out.println("Function type "+i+": "+globalDrinksList.get(0).getFunctionTypes().get(i));
        }

        for(i=0;i<globalDrinksList.size();i++){
            System.out.println("\ndrinkType: "+globalDrinksList.get(i).drinkType);
            for(ii=0;ii<globalDrinksList.get(i).displayNames.size();ii++){
                System.out.println(globalDrinksList.get(i).displayNames.get(ii));
                System.out.println(globalDrinksList.get(i).displayNameUnits.get(ii));
            }
            //print out all coefficients:
            for(ii=0;ii<globalDrinksList.get(i).coefficientsArrayList.size();ii++){
                System.out.println(globalDrinksList.get(i).coefficientsArrayList.get(ii).Function+
                        ":"+globalDrinksList.get(i).coefficientsArrayList.get(ii).FunctionCoefficient);
            }
//    		print associatedDrinksArrayList
            for(ii=0;ii<globalDrinksList.get(i).associatedDrinksArrayList.size();ii++){
                System.out.println(globalDrinksList.get(i).associatedDrinksArrayList.get(ii));
            }

        }
//    	print adjusters:
        for(i=0;i<functionAdjustmentList.size();i++){
            System.out.println(functionAdjustmentList.get(i).adjustName);
            for (ii=0;ii<functionAdjustmentList.get(i).adjustmentsDrinks.size();ii++){
                System.out.println(functionAdjustmentList.get(i).adjustmentsDrinks.get(ii).drinkToAdjust.drinkType +
                        ":"+functionAdjustmentList.get(i).adjustmentsDrinks.get(ii).adjustment);
            }
        }


    }

    public static void restoreAppDefaults(){
        globalDrinksList.clear();
        globalFunctionsList.clear();
        functionList.clear();
        functionAdjustmentList.clear();
        createDefaults();
        storeStateToFile();
    }

    private static void createDefaults(){
        //todo: get from file instead.
        Drink red,white,spark,heavy,light,mw,coke,dietcoke,lift,sprite,apple,oj,pine,mixedj;
        red = new Drink("Red Wine","Red Wine Bottles");
        red.addNewDisplayNameUnit(6, "Cases of Red Wine (6x700mL)");
//    	red.addFunction("dinner", (float)0.1);
        red.addFunction("dinner", (float)0.25);
        red.addFunction("PDD", (float) 0.06);
//    	red.addFunction("PDD", (float)0.05);
        red.addFunction("Lunch", (float)0.2);

        white = new Drink("White Wine","White Wine Bottles");
        white.addNewDisplayNameUnit(6, "Cases of White Wine");
//    	white.addFunction("dinner", (float)0.1);
        white.addFunction("dinner", (float)0.25);
        white.addFunction("PDD", (float)0.06);
//    	white.addFunction("PDD", (float)0.05);
        white.addFunction("Lunch", (float)0.2);

        spark = new Drink("Sparkling Wine","Sparkling Wine Bottles");
        spark.addFunction("dinner", (float)0.08);
//    	spark.addFunction("dinner", (float)0.05);
        spark.addFunction("PDD", (float)0.05);
        spark.addFunction("Lunch", (float) 0.06);

        heavy = new Drink("Full Strength Beer","Full Strength 375mL Beer Bottles");
        heavy.addNewDisplayNameUnit(2, "750mL Bottles of Full Strength Beer");
        heavy.addNewDisplayNameUnit(24,"Cases of Full Strength Beer");
        heavy.addNewDisplayNameUnit(150,"Kegs of Full Strength Beer");



        heavy.addFunction("dinner", (float)0.3);
        heavy.addFunction("PDD", (float)0.05);
        heavy.addFunction("Lunch", (float)0.5);

        light = new Drink("Light Beer","Light 375mL Beer Bottles");
        light.addFunction("dinner", (float)0.05);
        light.addFunction("PDD", (float)0.05);
        light.addFunction("Lunch", (float)0.2);

        mw = new Drink("Mineral Water","Mineral Water Bottles");
        mw.addFunction("dinner", (float)0.025);
        mw.addFunction("PDD", (float)0.05);
        mw.addFunction("Lunch", (float)0.15);

        coke = new Drink("Coke","Coke Bottles");
        coke.displayNames.add("Cases of Coke");
        coke.displayNameUnits.add(12);
        coke.addFunction("dinner", (float)0.02);
        coke.addFunction("PDD", (float)0.05);
        coke.addFunction("Lunch", (float)0.1);

        dietcoke = new Drink("Diet Coke","Diet Coke Bottles");
        dietcoke.addFunction("dinner", (float)0.01);
        dietcoke.addFunction("PDD", (float)0.05);
        dietcoke.addFunction("Lunch", (float)0.05);

        lift = new Drink("Lift", "Lift Bottles");
        lift.addFunction("dinner", (float)0.017);
        lift.addFunction("PDD", (float)0.05);
        lift.addFunction("Lunch", (float)0.1);

        sprite = new Drink("sprite","Sprite Bottles");
        sprite.addFunction("dinner", (float)0.01);
        sprite.addFunction("PDD", (float)0.04);
        sprite.addFunction("Lunch", (float)0.05);
        sprite.addAssociatedDrink(dietcoke);

        apple = new Drink("Apple Juice","Apple Juice Bottles");
        apple.addFunction("dinner", (float)0.0025);
        apple.addFunction("PDD", (float)0.05);
        apple.addFunction("Lunch", (float)0.2);

        oj = new Drink("Orange Juice","Orange Juice(4L) Bottles");
        oj.addFunction("dinner", (float)0.01);
        oj.addFunction("PDD", (float)0.05);
        oj.addFunction("Lunch", (float)0.2);

        pine = new Drink("Pineapple Juice", "Pineapple Juice Bottles");
        pine.addFunction("dinner", (float)0.0025);
        pine.addFunction("PDD", (float)0.05);
        pine.addFunction("Lunch", (float)0.2);

        mixedj = new Drink("Orange/Mango Juice", "Orange/Mango Juice Bottles");
        mixedj.addFunction("dinner", (float)0.0025);
        mixedj.addFunction("PDD", (float)0.05);
        mixedj.addFunction("Lunch", (float)0.2);

        globalFunctionsList.add("dinner");
        globalFunctionsList.add("PDD");
        globalFunctionsList.add("Lunch");

        //create an arraylist of globalListOfDrinks

        globalDrinksList.add(red);
        globalDrinksList.add(white);
        globalDrinksList.add(spark);
        globalDrinksList.add(heavy);
        globalDrinksList.add(light);
        globalDrinksList.add(mw);
        globalDrinksList.add(coke);
        globalDrinksList.add(dietcoke);
        globalDrinksList.add(lift);
        globalDrinksList.add(sprite);
        globalDrinksList.add(apple);
        globalDrinksList.add(oj);
        globalDrinksList.add(pine);
        globalDrinksList.add(mixedj);

        //add a couple of adjustments:
        FunctionAdjustment heavyBeer = new FunctionAdjustment("Heavy Beer");
        heavyBeer.addDrinkAdjustment(heavy, (float) 1.5);
        functionAdjustmentList.add(heavyBeer);

        FunctionAdjustment heavyChampagne = new FunctionAdjustment("Heavy Champage");
        heavyChampagne.addDrinkAdjustment(spark, (float) 1.5);
        heavyChampagne.addDrinkAdjustment(white, (float) 1.5);
        functionAdjustmentList.add(heavyChampagne);
    }

    @SuppressWarnings("unchecked")
    public static void storeStateToFile(){
//    	write out state to file.
        if(globalDrinksList.isEmpty())
            return;
        /**
         * byte of 2 VALUE_SEPARATER2 separates each drinks parts (type, display names, associatedDrinksArrayList, coefficientsArrayList)
         * byte of 4 VALUE_SEPARATER4 separates each drink
         * byte of 3 VALUE_SEPARATER3 separates drinks from the adjusters at the end of the file.
         * */

        ArrayList<String> toWrite = new ArrayList<String>();
        toWrite = (ArrayList<String>)globalFunctionsList.clone();
        toWrite.add(VALUE_SEPARATER2);
//    	writing functions
        int i, ii;
        for(i=0;i<globalDrinksList.size();i++){



            toWrite.addAll(globalDrinksList.get(i).serializeThis());
            toWrite.add(VALUE_SEPARATER4);
        }
        toWrite.add(VALUE_SEPARATER3);
//    	all drinks written, adding a byte3.
        for(i=0;i<functionAdjustmentList.size();i++){
            toWrite.add(functionAdjustmentList.get(i).adjustName);
            for(ii=0;ii<functionAdjustmentList.get(i).adjustmentsDrinks.size();ii++){
                toWrite.add(functionAdjustmentList.get(i).adjustmentsDrinks.get(ii).drinkToAdjust.drinkType);
                toWrite.add(Float.toString(functionAdjustmentList.get(i).adjustmentsDrinks.get(ii).adjustment));
//    			writing function adjusters.
            }
            toWrite.add(VALUE_SEPARATER2);

        }


        for(i=0;i<toWrite.size();i++){
            System.out.println(toWrite.get(i));
        }
        System.out.println("printing what I'm writing...");

        MyStateStorer.storeState(toWrite, myContext);

    }

}
