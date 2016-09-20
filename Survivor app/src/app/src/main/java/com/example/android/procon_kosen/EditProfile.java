package com.example.android.procon_kosen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class EditProfile extends AppCompatActivity {

    private Button mSaveBtn, mCancleBtn;
    private EditText mName, mSibling1, mSibling2, mBirthday;
    private Spinner mBlood;

    // Check entire EditText have some is empty
    private boolean isSomeEditTextEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FirstTimeVisitClass visit = new FirstTimeVisitClass(this);
        if (!visit.getVisited()) {
            visit.setVisited();
        }

        Spinner spinner = (Spinner) findViewById(R.id.blood_grp_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mName = (EditText) findViewById(R.id.edit_name);
        mSibling1 = (EditText) findViewById(R.id.edit_sibling_phone1);
        mSibling2 = (EditText) findViewById(R.id.edit_sibling_phone2);
        mBirthday = (EditText) findViewById(R.id.edit_birthday);

        mBlood = (Spinner) findViewById(R.id.blood_grp_spinner);

        // Get value from EditText and Spinner on Edit-profile page
        SharedPreferences sharedpreferences = getSharedPreferences("contentProfile", Context.MODE_PRIVATE);
        mName.setText(sharedpreferences.getString("name", null));
        mSibling1.setText(sharedpreferences.getString("sibling1", null));
        mSibling2.setText(sharedpreferences.getString("sibling2", null));
        mBirthday.setText(sharedpreferences.getString("birthday", null));

        // Get value from spinner's blood group and Set selection
        String[] i = getResources().getStringArray(R.array.blood_group_spinner);
        int index_blood = Arrays.asList(i).indexOf(sharedpreferences.getString("blood", null));
        mBlood.setSelection(index_blood);

        mSaveBtn = (Button) findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSomeEditTextEmpty) {
                    Toast.makeText(getApplicationContext(), getString(R.string.alert_empty_field), Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedpreferences = getBaseContext().getSharedPreferences("contentProfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("name", mName.getText().toString());
                    editor.putString("blood", mBlood.getSelectedItem().toString());
                    editor.putString("sibling1", mSibling1.getText().toString());
                    editor.putString("sibling2", mSibling2.getText().toString());
                    editor.putString("birthday", mBirthday.getText().toString());
                    editor.apply();

                    Intent i = new Intent(EditProfile.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });

        mCancleBtn = (Button) findViewById(R.id.cancle_btn);
        mCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mName.addTextChangedListener(textValidate);
        mBirthday.addTextChangedListener(textValidate);
        mSibling1.addTextChangedListener(textValidate);
        mSibling2.addTextChangedListener(textValidate);

    }

    public void showDatePickerDialog(View v) {
        EditText DateEditText = (EditText) findViewById(R.id.edit_birthday);
        DatePickerFragment dialog = new DatePickerFragment(DateEditText);
        dialog.show(getSupportFragmentManager(), "DialogDate");
    }

    public void highLightEmptyEditText() {
        EditText[] textEditView = {mName, mBirthday, mSibling1, mSibling2};
        isSomeEditTextEmpty = false;
        for(EditText e:textEditView) {
            if (e.getText().toString().isEmpty()) {
                isSomeEditTextEmpty = true;
                e.setBackgroundResource(R.drawable.apptheme_textfield_activated_holo_light);
            } else {
                e.setBackgroundResource(R.drawable.apptheme_textfield_default_holo_light);
            }
        }
    }

    private final TextWatcher textValidate = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        public void afterTextChanged(Editable s) {
            highLightEmptyEditText();
        }
    };

}