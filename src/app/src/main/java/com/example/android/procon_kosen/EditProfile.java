package com.example.android.procon_kosen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditProfile extends AppCompatActivity {

    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditText name = (EditText) findViewById(R.id.edit_name);
        EditText blood = (EditText) findViewById(R.id.edit_blood);
        EditText sibling1 = (EditText) findViewById(R.id.edit_sibling_phone1);
        EditText sibling2 = (EditText) findViewById(R.id.edit_sibling_phone2);

        SharedPreferences sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
        name.setText(sharedpreferences.getString("name", null));
        blood.setText(sharedpreferences.getString("blood", null));
        sibling1.setText(sharedpreferences.getString("sibling1", null));
        sibling2.setText(sharedpreferences.getString("sibling2", null));

        saveBtn = (Button) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                EditText name = (EditText) findViewById(R.id.edit_name);
                EditText blood = (EditText) findViewById(R.id.edit_blood);
                EditText sibling1 = (EditText) findViewById(R.id.edit_sibling_phone1);
                EditText sibling2 = (EditText) findViewById(R.id.edit_sibling_phone2);

                editor.putString("name", name.getText().toString());
                editor.putString("blood", blood.getText().toString());
                editor.putString("sibling1", sibling1.getText().toString());
                editor.putString("sibling2", sibling2.getText().toString());
                editor.apply();

                Intent i = new Intent(EditProfile.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

}