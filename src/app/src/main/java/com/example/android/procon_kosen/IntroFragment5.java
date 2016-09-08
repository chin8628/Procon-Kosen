package com.example.android.procon_kosen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class IntroFragment5 extends Fragment {

    private Button mEditProfileVisited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.intro_slide_fragment_5, container, false);

        mEditProfileVisited = (Button) rootView.findViewById(R.id.edit_profile_visit_1st);
        mEditProfileVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfile.class);
                startActivity(i);
            }
        });

        return rootView;
    }

}
