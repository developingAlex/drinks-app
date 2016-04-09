package com.example.user1.bevreq;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkAdder extends Activity {

    //	creatingNewDrink determines whether we are adding a new drink, or editing an existing one.
    private boolean creatingNewDrink = true;
    DrinksSpinnerActivity dsa = new DrinksSpinnerActivity();
    Spinner drinkSpinner;
    ArrayList<EditText> ratioList = new ArrayList<EditText>();
    EditText displayNameEditText;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ratioList.clear();
        creatingNewDrink = getIntent().getBooleanExtra(MainActivity.EXTRA_MESSAGE, true);
        String xx = creatingNewDrink?"creating a new drink : true":"creating a new drink : false";
        System.out.println(xx);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_adder);
//		LinearLayout ll = new LinearLayout(this);
//		ScrollView sv = new ScrollView(this);
        LinearLayout functionsView = new LinearLayout(this);
        functionsView.setOrientation(LinearLayout.VERTICAL);

//		generate "Display Name:" [input]
        LinearLayout hl = new LinearLayout(this);
        hl.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(this);
        tv.setText("Drink Name:");
        EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        hl.addView(tv);

        drinkSpinner = new Spinner(this);
        //create an ArrayAdapter using the string array and a default spinner layout.
        ArrayList<String> testArr = Situation.getGlobalDrinksListAsNames();
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.functions_array,  android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, testArr);
        //specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply the adapter to the spinner
        drinkSpinner.setAdapter(adapter);
        if(creatingNewDrink){
            hl.addView(et);

        }else{
            ((Button)(findViewById(R.id.create_drink_button))).setText("Save Changes");
            hl.addView(drinkSpinner);
            drinkSpinner.setOnItemSelectedListener(dsa);

            deleteButton = new Button(this);
            deleteButton.setText("Delete Drink");
            deleteButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    deleteButtonClicked(v);			    }
            });
            functionsView.addView(deleteButton);


        }

        functionsView.addView(hl);
        displayNameEditText = et;


//		generate list of "Ratio for x functions:" [input]
        int i = 0;
        while (i<Situation.globalFunctionsList.size()){
            tv = new TextView(this);
            tv.setText("Ratio for "+Situation.globalFunctionsList.get(i)+" functions:");
            et = new EditText(this);
            ratioList.add(et);
//			et.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
            et.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
//			CheckBox box2add = new CheckBox(this);
//			box2add.setText("he");
            hl = new LinearLayout(this);
            hl.addView(tv);
            hl.addView(et);
            functionsView.addView(hl);
            i++;
        }

        if(!creatingNewDrink){
            this.setTitle("Edit an existing drink");
            displayNameEditText.setEnabled(false);
            displayNameEditText.setText("Drink to Edit:");

            Drink drinkToEdit = Situation.globalDrinksList.get(0);
            System.out.println("pulled this from globalDrinksList:"+drinkToEdit.drinkType);
            System.out.println(drinkToEdit.coefficientsArrayList.get(0).FunctionCoefficient);
            for(i=0;i<ratioList.size();i++){
                float ratioAsFloat = drinkToEdit.getCoefficentOfFunction(
                        Situation.globalFunctionsList.get(i));
                ratioList.get(i).setText(Float.toString(ratioAsFloat));
            }
        }

//		sv.addView(ll);
        ((LinearLayout)findViewById(R.id.drink_adder_layout_dynamic)).addView(functionsView);

    }

    public class DrinksSpinnerActivity extends Activity implements OnItemSelectedListener {
        //		Note, this depends on the order of functions being correct.(same order as how drink objects store them)
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            String drinkType = (String) parent.getItemAtPosition(pos);
            System.out.println("you selected "+drinkType);
            Drink drinkToEdit = Situation.getDrinkByName(drinkType);
//	       fill out the ratio editText's
            int i;
            for(i=0;i<ratioList.size();i++){
                ratioList.get(i).setText(Float.toString(drinkToEdit.coefficientsArrayList.get(i).FunctionCoefficient));
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drink_adder, menu);
        return true;
    }

    public void deleteButtonClicked(View view){
        //TODO add a confirmation dialog box here.
        displayQuickToastMessage("delete clicked");
        //delete the selected drink from the global drink list, based on list number,
        //get the number from the currently selected drink in the
        //spinner list thing.
        Drink drinkToRemove = Situation.getDrinkByName(drinkSpinner.getSelectedItem().toString());

        Situation.deleteDrinkEntirelyFromApp(drinkToRemove);
        //update the current display to no longer display the drink that was deleted, or return to the main activity.
        Situation.storeStateToFile();
        finish();
//    	be careful here to maintain correct ordering..

    }

    public void makeDrinkButtonClicked(View view){
        if(creatingNewDrink){
            if(  (displayNameEditText).getText().toString().length() <1  ){
                displayQuickToastMessage("Enter a name");
            }else{
                displayQuickToastMessage("makeDrink clicked");
                String displayname =
                        (displayNameEditText).getText().toString();
                //			displayname = displayname.substring(0, 50);
                Drink newDrinkToAdd = new Drink("user-"+displayname, displayname);
                int i;
                for(i=0;i<Situation.globalFunctionsList.size();i++){
                    float ratio;
                    ratio = Float.parseFloat(
                            ((EditText)ratioList.get(i)).getText().toString());
                    newDrinkToAdd.addFunction(Situation.globalFunctionsList.get(i), ratio);

                }
                System.out.println("globalDrinksList.size()="+Situation.globalDrinksList.size());
                Situation.globalDrinksList.add(newDrinkToAdd);
                System.out.println("globalDrinksList.size()="+Situation.globalDrinksList.size());
                Situation.storeStateToFile();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }
        }else{
            displayQuickToastMessage("runs here when saving changes to an existing drink");
            Drink drinkToEdit = Situation.getDrinkByName(drinkSpinner.getSelectedItem().toString());
            displayQuickToastMessage("editing this drink:" + drinkToEdit.drinkType);
            int i;
            for(i=0;i<ratioList.size();i++){
                System.out.print("changed FunctionCoefficient:"+drinkToEdit.coefficientsArrayList.get(i).FunctionCoefficient);
                drinkToEdit.coefficientsArrayList.get(i).FunctionCoefficient = Float.parseFloat(
                        ((EditText)ratioList.get(i)).getText().toString());
                System.out.println(" to "+drinkToEdit.coefficientsArrayList.get(i).FunctionCoefficient);


            }
//		         int indexOfEditedDrink = Situation.globalDrinksList.indexOf(drinkToEdit);

            Situation.storeStateToFile();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }

    }
    private void displayQuickToastMessage(String msg){
            Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();

    }
    private void displayLongToastMessage(String msg){
            Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }
}
