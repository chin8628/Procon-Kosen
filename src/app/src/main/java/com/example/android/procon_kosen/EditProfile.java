package com.example.android.procon_kosen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;

public class EditProfile extends AppCompatActivity {

    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Spinner spinner = (Spinner) findViewById(R.id.blood_grp_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        EditText name = (EditText) findViewById(R.id.edit_name);
        EditText sibling1 = (EditText) findViewById(R.id.edit_sibling_phone1);
        EditText sibling2 = (EditText) findViewById(R.id.edit_sibling_phone2);
        Spinner blood = (Spinner) findViewById(R.id.blood_grp_spinner);

        // Get value from EditText and Spinner on Edit-profile page
        SharedPreferences sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
        name.setText(sharedpreferences.getString("name", null));
        sibling1.setText(sharedpreferences.getString("sibling1", null));
        sibling2.setText(sharedpreferences.getString("sibling2", null));

        // Get value from spinner's blood group and Set selection
        String[] i = getResources().getStringArray(R.array.blood_group_spinner);
        int index_blood = Arrays.asList(i).indexOf(sharedpreferences.getString("blood", null));
        blood.setSelection(index_blood);

        saveBtn = (Button) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                EditText name = (EditText) findViewById(R.id.edit_name);
                EditText sibling1 = (EditText) findViewById(R.id.edit_sibling_phone1);
                EditText sibling2 = (EditText) findViewById(R.id.edit_sibling_phone2);
                Spinner blood = (Spinner) findViewById(R.id.blood_grp_spinner);

                editor.putString("name", name.getText().toString());
                editor.putString("blood", blood.getSelectedItem().toString());
                editor.putString("sibling1", sibling1.getText().toString());
                editor.putString("sibling2", sibling2.getText().toString());
                editor.apply();

                Intent i = new Intent(EditProfile.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

}