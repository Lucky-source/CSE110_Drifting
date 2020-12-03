package com.example.drifting.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.drifting.AddFriendActivity;
import com.example.drifting.HomeFragment;
import com.example.drifting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import backend.util.database.Bottle_back;

public class ViewBottleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bottle);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height = dm.heightPixels;

        // set canvas width and height.
        getWindow().setLayout((int)(width*1), (int)(height*0.75));

        String msg = "";
        String fromUser = "";
        String city = "";
        String comment = "";
        String bottleID = "";
        String fromUserID = "";

        if (HomeFragment.currBottle != null) {
             msg = HomeFragment.currBottle.message;
             fromUser = HomeFragment.currBottle.fromUser;
             city = HomeFragment.currBottle.city;
             comment = HomeFragment.currBottle.comment;
             bottleID = HomeFragment.currBottle.bottleID;
             fromUserID = HomeFragment.currBottle.userID;
        }


        TextView messageView = findViewById(R.id.bottle_message_textview);
        messageView.setText(msg);

        TextView fromUserView = findViewById(R.id.from_var_textview);
        fromUserView.setText(fromUser);

        TextView locationView = findViewById(R.id.location_var_textview);
        locationView.setText(city);

        TextView commentView = findViewById(R.id.comment_field_textview);
        commentView.setText(comment);

        Button close_button = findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        LinearLayout fromLayout = findViewById(R.id.from_layout);
        fromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ViewBottleActivity.this, AddFriendActivity.class));

            }
        });

        //------------------------------------------------------------------------

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("bottle");
        DatabaseReference this_bottle_data = reference.child(bottleID);
        Map<String, Object> bottle_update = new HashMap<>();
        bottle_update.put("isViewed", true);
        this_bottle_data.updateChildren(bottle_update);
    }

    @Nullable
    @Override
    public View onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }
}