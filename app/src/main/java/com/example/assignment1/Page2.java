package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.assignment1.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Page2 extends AppCompatActivity {


    //Global Variables
    public String[] symptoms_items = new String[]{"Nausea", "Headache", "SoreThroat","Diarrhea","Fever","MuscleAche","LossOfSmellOrTaste","Cough","ShortnessOfBreath","FeelingTired"};
    public Integer symtomToInt(String symptom){
        return  Arrays.asList(symptoms_items).indexOf(symptom);
    }
    public float[] res_v=new float[symptoms_items.length];
    // using above item list initialize an array




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        ArrayList<String> selected_symptoms= new ArrayList<String>();
        ArrayList<Float> selected_rating= new ArrayList<Float>();
        Map<String,Float> combined_list;
        combined_list = new HashMap<>();
        combined_list.put("Nausea", Float.intBitsToFloat(0));
        combined_list.put("Headache", Float.intBitsToFloat(0) );
        combined_list.put("SoreThroat", Float.intBitsToFloat(0) );
        combined_list.put("Diarrhea", Float.intBitsToFloat(0) );
        combined_list.put("Fever", Float.intBitsToFloat(0) );
        combined_list.put("MuscleAche", Float.intBitsToFloat(0) );
        combined_list.put("LossOfSmellOrTaste", Float.intBitsToFloat(0) );
        combined_list.put("Cough", Float.intBitsToFloat(0) );
        combined_list.put("ShortnessOfBreath", Float.intBitsToFloat(0) );
        combined_list.put("FeelingTired", Float.intBitsToFloat(0) );


        // SPINNER INITIALIZATION
        Spinner dropdown = findViewById(R.id.spinner);
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, symptoms_items);
        dropdown.setAdapter(adapter);// set these values



        // temporary save button
        Button b6=(Button) findViewById(R.id.button6);
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner dropdown = findViewById(R.id.spinner);
                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        Log.i("ITEM", (String) parent.getItemAtPosition(position));
                        selected_symptoms.add((String) parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                RatingBar ratingBar=findViewById(R.id.ratingBar);
                float rating = ratingBar.getRating();
                selected_rating.add(rating);
                Log.i("SYMP_LIST",String.valueOf(rating) + " ");
                Toast.makeText(view.getContext(), "Symptom Saved "+selected_symptoms.size()+" "+rating,Toast.LENGTH_SHORT).show();
                if(selected_symptoms.size()!=0){ // saving the entry
                    combined_list.replace(selected_symptoms.get(selected_symptoms.size()-1),rating);
                }
            }
        });


        Button b5=(Button) findViewById(R.id.button5);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code for pushing data into db

                String res="";

                Toast.makeText(view.getContext(),"List saved and ready ",Toast.LENGTH_LONG).show();
                for (int i=0;i<symptoms_items.length;i++) {
                    String key = symptoms_items[i];
                    Float value = combined_list.get(key);
                    Log.i( "DATALOG","Key Value pairs:" + key +" - "+ value);
                    res=String.valueOf(value);
                    res_v[i]=value;
                    // do stuff
                }
                // end for db push

                // For going back to home page
                Intent int2=new Intent( Page2.this,Page1.class); // send back to main context
                int2.putExtra("symp_list",res_v);
                startActivity(int2);
            }
        });
    }
}