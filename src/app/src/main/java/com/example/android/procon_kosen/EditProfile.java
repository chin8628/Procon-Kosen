package com.example.android.procon_kosen;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;

public class EditProfile extends AppCompatActivity {

    private Button mSaveBtn, mTestBtn;
    private EditText mName, mSibling1, mSibling2, mBirthday;
    private Spinner nBlood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Spinner spinner = (Spinner) findViewById(R.id.blood_grp_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mName = (EditText) findViewById(R.id.edit_name);
        mSibling1 = (EditText) findViewById(R.id.edit_sibling_phone1);
        mSibling2 = (EditText) findViewById(R.id.edit_sibling_phone2);
        mBirthday = (EditText) findViewById(R.id.edit_birthday);

        nBlood = (Spinner) findViewById(R.id.blood_grp_spinner);

        // Get value from EditText and Spinner on Edit-profile page
        SharedPreferences sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
        mName.setText(sharedpreferences.getString("name", null));
        mSibling1.setText(sharedpreferences.getString("sibling1", null));
        mSibling2.setText(sharedpreferences.getString("sibling2", null));
        mBirthday.setText(sharedpreferences.getString("birthday", null));

        // Get value from spinner's blood group and Set selection
        String[] i = getResources().getStringArray(R.array.blood_group_spinner);
        int index_blood = Arrays.asList(i).indexOf(sharedpreferences.getString("blood", null));
        nBlood.setSelection(index_blood);

        mSaveBtn = (Button) findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences("contentProfle", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("name", mName.getText().toString());
                editor.putString("blood", nBlood.getSelectedItem().toString());
                editor.putString("sibling1", mSibling1.getText().toString());
                editor.putString("sibling2", mSibling2.getText().toString());
                editor.putString("birthday", mBirthday.getText().toString());
                editor.apply();

                Intent i = new Intent(EditProfile.this, MainActivity.class);
                startActivity(i);
            }
        });

    }

    public void showDatePickerDialog(View v) {
        EditText DateEditText = (EditText) findViewById(R.id.edit_birthday);
        DatePickerFragment dialog = new DatePickerFragment(DateEditText);
        dialog.show(getSupportFragmentManager(), "DialogDate");
    }

}