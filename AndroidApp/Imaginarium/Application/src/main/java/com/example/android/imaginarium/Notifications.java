package com.example.android.imaginarium;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment {


    public Notifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getActivity().getActionBar().setTitle("Notifications");
        Log.d("[Notifications]", "OnCreateView");
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("[Notifications]", "OnStart");
        //getActivity().getActionBar().setTitle("Notifications");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("[Notifications]", "OnResume");
 //       getActivity().getActionBar().setTitle("Notifications");
    }
}
