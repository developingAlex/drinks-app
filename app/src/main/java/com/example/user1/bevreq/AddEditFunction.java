package com.example.user1.bevreq;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.Menu;
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

public class AddEditFunction extends AppCompatActivity {
    private boolean creatingNewFunction = true;
    FunctionsSpinnerActivity fsa = new FunctionsSpinnerActivity();
    Spinner functionSpinner;
    Button deleteButton;
    ArrayList<EditText> ratioList = new ArrayList<EditText>();
    EditText functionNameEditText;
    int nameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_function);


        creatingNewFunction = getIntent().getBooleanExtra(MainActivity.EXTRA_MESSAGE, true);

        functionSpinner = new Spinner(this);
        //create an ArrayAdapter using the string array and a default spinner layout.
        ArrayList<String> testArr = Situation.globalFunctionsList;
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.functions_array,  android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, testArr);
        //specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply the adapter to the spinner
        functionSpinner.setAdapter(adapter);
        functionSpinner.setOnItemSelectedListener(new FunctionsSpinnerActivity());

        if(creatingNewFunction)
            this.setTitle("Create Function");
        LinearLayout vl = (LinearLayout)(findViewById(R.id.edit_function_layout_dynamic));
        LinearLayout hl = new LinearLayout(this);
        TextView tv = new TextView(this);
        tv.setText("Function Type:");
        tv.setTypeface(Typeface.DEFAULT_BOLD);
//		tv.setTextSize(tv.getTextSize() );
        hl.addView(tv);
        functionNameEditText = new EditText(this);
        functionNameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        if(creatingNewFunction){
            hl.addView(functionNameEditText);
        }else{
            hl.addView(functionSpinner);


            ((Button)(findViewById(R.id.create_function_button))).setText("Save Changes");
        }

        vl.addView(hl);
        EditText et = new EditText(this);
//		create list of drink ratios for input or edit:
        int i;
        for(i=0;i<Situation.globalDrinksList.size();i++){
            hl = new LinearLayout(this);
            tv = new TextView(this);
            tv.setText(Situation.globalDrinksList.get(i).drinkType +" ratio:");
            et = new EditText(this);
//			et.getLayoutParams().width=LinearLayout.LayoutParams.MATCH_PARENT;
//			et.setLayoutParams(new LayoutParams(32,32)); //dodgy
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            hl.addView(tv);
            ratioList.add(et);
            hl.addView(et);
            vl.addView(hl);
        }

        if(!creatingNewFunction){
            deleteButton = new Button(this);
            deleteButton.setText("Delete Function");
            deleteButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    deleteButtonClicked(v);			    }
            });
            vl.addView(deleteButton);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_function, menu);
        return true;
    }

    public class FunctionsSpinnerActivity extends Activity implements OnItemSelectedListener {
        //		Note, this depends on the order of functions being correct.(same order as how drink objects store them)
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            String functionType = (String) parent.getItemAtPosition(pos);
            System.out.println("you selected "+functionType);
//	         fill out the ratiolist:
            int i;
            for (i=0;i<ratioList.size();i++){
                String ratioString;
                float ratioFloat =Situation.globalDrinksList.get(i).getCoefficentOfFunction(functionType);
                ratioString = Float.toString(ratioFloat);
                ratioList.get(i).setText(ratioString);
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }
    public void deleteButtonClicked(View view){
        //TODO add a confirmation dialog box here.
        displayQuickToastMessage("delete clicked");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //TODO actually delete stuff,
//    	be careful here to maintain correct ordering..

    }

    public void cancelButtonClicked(View view){
        displayQuickToastMessage("cancel clicked");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void makeFunctionButtonClicked(View view){
        if(creatingNewFunction){
            if(functionNameEditText.getText().toString().length()<1){
                displayQuickToastMessage("Enter a function type..");
                return;
            }
            int i;
            for(i=0;i<ratioList.size();i++){
                if(ratioList.get(i).getText().toString().length() < 1){
                    ratioList.get(i).setText("0.0");
                }
            }
            String fName = functionNameEditText.getText().toString();
            Situation.globalFunctionsList.add(fName);

            for(i=0;i<Situation.globalDrinksList.size();i++){
                float ratio = Float.parseFloat(ratioList.get(i).getText().toString());
                Situation.globalDrinksList.get(i).addFunction(fName, ratio);
            }
            Situation.storeStateToFile();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            displayQuickToastMessage("make a function clicked with name:" + functionNameEditText.getText().toString());
        }else{//edit mode.
            displayQuickToastMessage("save changes clicked");
            int i;
            for(i=0;i<ratioList.size();i++){
                if(ratioList.get(i).getText().toString().length() < 1){
                    ratioList.get(i).setText("0.0");
                }
            }
            //need to know what number the function is.functionSpinner
            int functionsCoef = Situation.globalFunctionsList.indexOf(functionSpinner.getSelectedItem().toString());
            float newCoef;
            for(i=0;i<Situation.globalDrinksList.size();i++){
                newCoef = Float.parseFloat(ratioList.get(i).getText().toString());
                Situation.globalDrinksList.get(i).changeCoef(functionsCoef, newCoef);
            }
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
