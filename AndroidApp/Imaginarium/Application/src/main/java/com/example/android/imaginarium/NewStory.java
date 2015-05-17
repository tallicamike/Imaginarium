package com.example.android.imaginarium;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewStory extends Fragment {


    public NewStory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V=  inflater.inflate(R.layout.fragment_new_story, container, false);

        Button button = (Button) V.findViewById(R.id.button_create);
        final EditText title = (EditText) V.findViewById(R.id.title);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String title_text = title.getText().toString();

                if (title_text.matches("")) {
                    Context context = getActivity().getApplicationContext();
                    CharSequence text = "Title field must not be empty !";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }
                else {

                    Intent myIntent =
                        new Intent(new Intent(getActivity(), ChatActivity.class));

                    Bundle bundle = new Bundle();
                    bundle.putString("story_title", title.getText().toString());
                    bundle.putParcelableArrayList("fragments", new ArrayList<Story_Fragment>());
                    myIntent.putExtras(bundle);

                    startActivity(myIntent);
                }
            }
        });
       // getActivity().getActionBar().setTitle("NewStory");
        Log.d("[NewStory]", "OnCreateView");
        return V;
    }

    @Override
    public void onStart() {
        super.onStart();
        //getActivity().getActionBar().setTitle("NewStory");
        Log.d("[NewStory]", "OnStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().getActionBar().setTitle("Imaginarium");
        Log.d("[NewStory]", "OnResume");
    }

}
