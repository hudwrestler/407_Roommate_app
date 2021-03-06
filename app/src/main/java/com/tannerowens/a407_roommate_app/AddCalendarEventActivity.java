package com.tannerowens.a407_roommate_app;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddCalendarEventActivity extends AppCompatActivity{

    private int day;
    private int month;
    private int year;
    private User user;
    private House group;
    private int i;
    private ArrayList<User> users = new ArrayList<>();
    private RadioButton personal;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_calendar_activity);


        day = (Integer) getIntent().getSerializableExtra("day");
        month = (Integer) getIntent().getSerializableExtra("month");
        year = (Integer) getIntent().getSerializableExtra("year");
        user = (User) getIntent().getSerializableExtra("user");
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://roommateapp-a6d3a.firebaseio.com/");
        personal = (RadioButton) findViewById(R.id.radioMe);

        getHouse();
        getHouseMembers();
        configureDateText();
        configureBackButton();
        configureAddButton();
    }

    private void configureDateText() {
        TextView date = (TextView) findViewById(R.id.dateText);
        date.setText(month + "/" + day + "/" + year);
    }

    private void configureBackButton() {
        Button button = (Button) findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void configureAddButton() {
        Button button = (Button) findViewById(R.id.addEvent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText event_txt = (EditText) findViewById(R.id.EventNameText);
                String name = event_txt.getText().toString();

                EditText location_txt = (EditText) findViewById(R.id.EventLocation);
                String location = location_txt.getText().toString();

                EditText start_txt = (EditText) findViewById(R.id.EventTimeStart);
                String startTime = start_txt.getText().toString();

                EditText end_txt = (EditText) findViewById(R.id.EventTimeEnd);
                String endTime = end_txt.getText().toString();

                CalendarEvent event = new CalendarEvent(name, location, startTime, endTime, month, day, year);
                if (!personal.isChecked()) {
                    if (group != null) {
                        for (int i = 0; i < users.size(); i++) {
                            User currUser = users.get(i);
                            currUser.addCalendarEvent(event);
                            mDatabase.child("users").child(currUser.getUsername()).setValue(currUser);
                        }
                    }
                    else {
                        user.addCalendarEvent(event);
                        mDatabase.child("users").child(user.getUsername()).setValue(user);
                    }
                      /*  for (i = 0; i < users.size(); i++) {
                            if (i != 0) {
                                User currUser = users.get(i);
                                currUser.addCalendarEvent(event);
                                mDatabase.child("users").child(currUser.getUsername()).setValue(currUser);
                            }
                            else {
                                user.addCalendarEvent(event);
                                mDatabase.child("users").child(user.getUsername()).setValue(user);
                            }
                        } */
                   /* }
                    else {
                        user.addCalendarEvent(event);
                        mDatabase.child("users").child(user.getUsername()).setValue(user);
                    } */
                }
                else {
                    user.addCalendarEvent(event);
                    mDatabase.child("users").child(user.getUsername()).setValue(user);
                }
                Intent intent = new Intent();
                intent.putExtra("user" , user );
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });
    }

    private void getHouse() {
        if (user.getHouse() != null) {
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://roommateapp-a6d3a.firebaseio.com/house");
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot house : dataSnapshot.getChildren()) {
                        if (user.getHouse().equals(house.getKey())) {
                            House h = house.getValue(House.class);
                            group = h;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            group = null;
        }
    }

    private void getHouseMembers() {
        if(group != null) {
            for (i = 0; i < group.getUsers().size(); i++) {
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://roommateapp-a6d3a.firebaseio.com/users");
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot member : dataSnapshot.getChildren()) {
                            if (group.getUsers().get(i).equals(member.getKey().toString())) {
                                User u = member.getValue(User.class);
                                users.add(u);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        else {
            users.add(user);
        }
    }


}




