package com.example.android.imaginarium;

import java.util.ArrayList;

/**
 * Created by cristina on 12.05.2015.
 */
public class Story_Info {

    int id;

    public String title, description, started, last;
    public ArrayList<String> participants;



    public Story_Info(int id, String title, String description,
                      ArrayList<String> participants,
                      String started, String last) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.participants = participants;
        this.started = started;
        this.last = last;
    }
}
