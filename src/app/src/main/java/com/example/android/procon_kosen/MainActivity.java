package com.example.android.procon_kosen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView name = (TextView) findViewById(R.id.name);
        TextView blood = (TextView) findViewById(R.id.blood);
        TextView sibling1 = (TextView) findViewById(R.id.sibling_phone1);
        TextView sibling2 = (TextView) findViewById(R.id.sibling_phone2);

        SharedPreferences sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
        name.setText(sharedpreferences.getString("name", ""));
        blood.setText(sharedpreferences.getString("blood", ""));
        sibling1.setText(sharedpreferences.getString("sibling1", ""));
        sibling2.setText(sharedpreferences.getString("sibling2", ""));

        editBtn = (Button) findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditProfile.class);
                startActivity(i);
            }
        });
    }
}
