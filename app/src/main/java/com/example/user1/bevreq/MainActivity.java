package com.example.user1.bevreq;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    //public static DrinksList globalListOfDrinks;//the point of a global list is for if the user selects to go back to the function
    //selection screen when they've already unticked a drink from the list for a previous function.,
    public final static String EXTRA_MESSAGE = "com.example.bevreq.MESSAGE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        myInitialise();
    }
    @SuppressLint("NewApi") private void myInitialise(){

        //todo : add drinks to the global list here, then it's set for all situations.
        System.out.println("is this the last thing you see?");

        System.out.println(this.getBaseContext().toString());
        Situation.Initialise(this.getBaseContext());
        setupSpinner();
        setupAdjustmentCheckBoxes();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.activity_main, menu);
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        System.out.println("inflated the menu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.new_drink){
            displayQuickToastMessage("Create a new drink!");
            Intent intent = new Intent(this, DrinkAdder.class);
            intent.putExtra(EXTRA_MESSAGE, true);
            startActivity(intent);
        }


        if(id == R.id.new_function){
            Intent intent = new Intent(this, AddEditFunction.class);
            intent.putExtra(EXTRA_MESSAGE, true);
            startActivity(intent);

        }if(id == R.id.restore_defaults){
            Situation.restoreAppDefaults();
            myInitialise();
            displayQuickToastMessage("done");

        }if(id == R.id.edit_drink){
            Intent intent = new Intent(this, DrinkAdder.class);
            intent.putExtra(EXTRA_MESSAGE, false);
            startActivity(intent);

        }
        if(id == R.id.edit_function){
            Intent intent = new Intent(this, AddEditFunction.class);
            intent.putExtra(EXTRA_MESSAGE, false);
            startActivity(intent);

        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void chooseDrinksButtonClicked(View view){
        //	bring up the screen to choose Drinks
        if(addFunctionFromInputs()){
//    		Intent intent = new Intent(this, ChooseDrinksActivity.class);
            Intent intent = new Intent(this, DisplayAmountsActivity.class);

            startActivity(intent);
        }

    }


    public void addButtonClicked(View view){
        //add forms contents to the creation of a new function and clear the form
        if(addFunctionFromInputs())
            clearForm();//clear the form ready for a new input.


    }
    public void newButtonClicked(View view){
        //remove any previously stored functions and start new.
        Situation.reset();
        clearForm();
    }
    private void clearForm(){
        //clears the pax and duration input fields
        ((EditText) findViewById(R.id.pax_message)).setText("");
        ((EditText) findViewById(R.id.duration_message)).setText("");
        findViewById(R.id.pax_message).requestFocus();
        int i;
        for(i=0;i<Situation.functionAdjustmentList.size();i++){
            ((CheckBox)(findViewById(i))).setChecked(false);
        }

    }

    public void setupSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        //create an ArrayAdapter using the string array and a default spinner layout.
        ArrayList<String> testArr = Situation.globalDrinksList.get(0).getFunctionTypes();
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.functions_array,  android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, testArr);
        //specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private Boolean addFunctionFromInputs(){//returns true on success
        int pax = getPaxFromView();
        float duration = getDurationFromView();
        int i;
        ArrayList<String>adjs = new ArrayList<String>();
        for(i=0;i<Situation.functionAdjustmentList.size();i++){
            if(((CheckBox)findViewById(i)).isChecked()){
                adjs.add(Situation.functionAdjustmentList.get(i).adjustName);
            }
        }
        if(pax == 0 || duration == 0){
            displayLongToastMessage("Add numbers for Pax and Duration");
            return false;
        }
        if (adjs.isEmpty())
            adjs = null;

        Situation.addFunction(pax, duration, getFunctionFromView(), adjs);
        return true;
    }
    private String getFunctionFromView(){
        return String.valueOf(((Spinner) findViewById(R.id.spinner1)).getSelectedItem());
    }

    private int getPaxFromView(){//returns 0 if no input
        int retval;
        try{
            retval = Integer.parseInt(((EditText) findViewById(R.id.pax_message)).getText().toString());

        }catch(Exception e){
            retval = 0;
        }
        return retval;
    }
    private float getDurationFromView(){//returns 0 if no input
        float retval;
        try{
            retval = Float.parseFloat(((EditText) findViewById(R.id.duration_message)).getText().toString());
        }catch(Exception e){
            retval = 0;
        }
        return retval;
    }
    private void displayQuickToastMessage(String msg){
        Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void displayLongToastMessage(String msg){
        Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void setupAdjustmentCheckBoxes(){
        LinearLayout ll = (LinearLayout)(findViewById(R.id.linearLayoutAdjusterCheckBoxes));
        ll.removeAllViews();
        int i;
        for (i=0;i<Situation.functionAdjustmentList.size();i++){
            CheckBox newbox = new CheckBox(this);
            newbox.setId(i);//the id corresponds to its place in the globallistofdrinks
            newbox.setText(Situation.functionAdjustmentList.get(i).adjustName/*text for checkbox*/);
            ll.addView(newbox);
        }
    }




}
