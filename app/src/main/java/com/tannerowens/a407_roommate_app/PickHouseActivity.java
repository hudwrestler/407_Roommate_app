package com.tannerowens.a407_roommate_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PickHouseActivity extends AppCompatActivity {

    private Button pickHouseButton;
    private EditText houseName;
    private DatabaseReference mDatabase;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_house);

        pickHouseButton = (Button) findViewById(R.id.pickHouseButton);
        houseName = (EditText) findViewById(R.id.houseNameText);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://roommateapp-a6d3a.firebaseio.com/");
        user = (User) getIntent().getSerializableExtra("user");

        configureBackButton();
        pickHouseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pickHouse();
            }
        });
    }

    private void configureBackButton() {
        Button button = (Button) findViewById(R.id.backButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void pickHouse(){
        //join existing house
        final String h = houseName.getText().toString();
        if (TextUtils.isEmpty(h)) {
            Toast.makeText(PickHouseActivity.this, "Fill in house name", Toast.LENGTH_LONG).show();
        }
        else{
            //Remove user from old house:
            final House oldHouse = user.getHouse();

            DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl
                    ("https://roommateapp-a6d3a.firebaseio.com/house");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean houseExists = false;
                    House house;
                    String houseName;
                    House old = null;

                    for (DataSnapshot houses: dataSnapshot.getChildren()) {//EXISTING HOUSE
                        //Log.i("HOUSES", houses.toString());
                        houseName = houses.getKey();
                        //house = houses.getValue(House.class);
                        //if (house.getName().equals(h)){
                        if(houseName.equals(h)){
                            houseExists = true;
                            house = houses.getValue(House.class);
                            if(house.getUsers().contains(user.getUsername())){
                                user.setHouse(house);
                                HouseWrapper.setHouse(house);
                            }
                            else {
                                house.addUser(user.getUsername());
                                mDatabase.child("house").child(h).setValue(house);
                                user.setHouse(house);
                                HouseWrapper.setHouse(house);
                            }
                        }
                        if(oldHouse != null ){
                            if (houseName.equals(oldHouse.getName()) && !houseName.equals(h)) {
                                old = houses.getValue(House.class);
                            }
                        }
                    }

                    if(!houseExists){//create a new house
                        House newHouse = new House(h,user.getUsername());
                        HouseWrapper.setHouse(newHouse);
                        mDatabase.child("house").child(h).setValue(newHouse);
                        user.setHouse(newHouse);
                    }

                    if (old!=null){//deletes user from old house
                        old.removeUser(user.getUsername());
                        mDatabase.child("house").child(old.getName()).setValue(old);
                        HouseWrapper.setHouse(old);
                    }

                    mDatabase.child("users").child(user.getUsername()).setValue(user);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
