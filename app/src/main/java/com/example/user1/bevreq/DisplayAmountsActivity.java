package com.example.user1.bevreq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayAmountsActivity extends AppCompatActivity {

    /*if we need such a large spiel at the start of this class then we should probably consider
    * breaking it up into smaller, easier to infer, classes.*/

    /*
     * The displayAmountsActivity will take in the following information
     * to use in calculating the numbers:
     * pax
     * duration
     * function type(dinner, lunch, etc)
     * function type adjustments (diabetics, non-drinkers, etc)
     * list of unticked drinks.
     *
     * Then works out the numbers for each drink, by going
     * pax*duration*drink.FunctionCoefficient(function)*function_type_adjuster
     * ...for each drink type,
     * now your left with a list of bottle numbers,
     * if there are any unticked drinks then change those drinks to 0
     * and evenly distribute what number of bottles they would have been
     * among that groups 'associatedDrinksArrayList' eg: if sparkling wine is unticked,
     * and there was originally predicted to be 30 bottles of sparkling,
     * and sparkling has the associatedDrinksArrayList : red, white, beer,
     * then those 30 bottles worth of drinks (eg 5 drinks to a bottle)
     * or 30bottles * 5 drinks per bottle = 150 drinks,
     * are to be split up between red, white, and beer,
     * so 50 glasses of extra red, (if it's 7 glasses to a bottle) then
     * thats an extra 50/7 = 7.1 bottles, rounded is 7, 7 bottles of red
     * added on top of what red's count already is. ...make sense?
     *
     * possible problems: with such a large array of drink choices, esp for
     * small functions, the amount of glasses of beverages in total across all
     * drink types will be a lot more than the crowd could ever drink, because
     * of the chance of the crowd favouring a particular type of drink.
     * therefore if a drink is removed from the menu, it may not be wise to
     * maintain the same amount of glasses of beverage in total by splitting
     * the glasses of the would-be drink amoung it's associatedDrinksArrayList, so for small functions
     * say below 100, it might be worth considering removing a certain amount of
     * glasses from the equation... you could do this by taking the pax number
     * and diving it by 1000 to get a fraction, so for 100pax it'd be 0.1 or
     * 10%, therefore, only 10% of the glasses worth of sparkling would be re-
     * distributed among the 'associatedDrinksArrayList' of sparkling.
     * if the pax was 1000, then the fraction would be 1 and all the lost glasses
     * from the exclusion of sparkling would be compensated for by the addition to
     * sparklings 'associatedDrinksArrayList', now for over 1000 pax this becomes an issue because it
     * means now providing overall MORE glasses to the crowd than previously estimated
     * so just place a limit on the variable, can't be greater than 1.
     * */
    int pax, duration;



    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("I got up to the onCreate function of DisplayAmountsActivity");
        super.onCreate(savedInstanceState);
        System.out.println("1");
        setContentView(R.layout.activity_display_amounts);
        System.out.println("2");
        //Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            // Show the Up button in the action bar.
            System.out.println("3");
            //getActionBar().setDisplayHomeAsUpEnabled(true);
            System.out.println("4");
        }



        TextView textViewDuration = new TextView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ScrollView sv = new ScrollView(this);
        ll.addView(textViewDuration);
        textViewDuration.setTextSize(20);
        //textViewDuration.setText(intent.getIntExtra(MainActivity.EXTRA_MESSAGE_DURATION, 1));
        textViewDuration.setText("");


        //calculate amounts:

        //display:
        textViewDuration.setText(Situation.getDrinkAmountsAsString());
        sv.addView(ll);
        setContentView(sv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_amounts, menu);
        return true;
    }
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayQuickToastMessage(String msg){
        Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void displayLongToastMessage(String msg){
        Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

}
