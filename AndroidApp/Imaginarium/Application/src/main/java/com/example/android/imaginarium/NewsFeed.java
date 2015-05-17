package com.example.android.imaginarium;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFeed extends Fragment {

    int is_newsfeed;

    public NewsFeed() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V =  inflater.inflate(R.layout.fragment_newsfeed, container, false);

        Bundle bundle = this.getArguments();
        is_newsfeed = bundle.getInt("is_newsfeed");

        // Construct the data source
        ArrayList<Story_Info> arrayOfStories = new ArrayList<Story_Info>();
        // Create the adapter to convert the array to views
        Story_newsfeed_prev_adapter adapter = new Story_newsfeed_prev_adapter(getActivity().getApplicationContext(),
                arrayOfStories);
        // Attach the adapter to a ListView
        final ListView listView = (ListView) V.findViewById(R.id.newsfeed);
        listView.setAdapter(adapter);

        String s1 = "Title_", s2 = "Description_", s3 = "12/04/2015", s4 = "4 hours ago";
        ArrayList<String> p = new ArrayList<>();

        p.add(0, "Part1");
        p.add(0, "Part2");
        p.add(0, "Part3");
        p.add(0, "Part4");

        Story_Info prev;

        for (int i = 0; i < 5; i++) {
            prev = new Story_Info(i, s1 + i, s2 + i, p, s3, s4);
            adapter.add(prev);
        }
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//
//                Log.d("Click", "MY CLICK");
//                Toast.makeText(getActivity().getApplicationContext(), "Show Full Image", Toast.LENGTH_LONG).show();
//            }
//        });
//
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Click", "Clicked");
                TextView tv = (TextView) view.findViewById(R.id.id_title);
                String story_title = tv.getText().toString();
                //Log.d("Click", "text is " + text);
                //Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT);
                Bundle bundle = new Bundle();
                Intent myIntent;
                if (is_newsfeed == 0)
                    myIntent = new Intent(new Intent(getActivity(), ChatActivity.class));
                else
                    myIntent = new Intent(new Intent(getActivity(), DisplayStory.class));

                bundle.putString("story_title", story_title);
                bundle.putParcelableArrayList("fragments", new ArrayList<Story_Fragment>());
                myIntent.putExtras(bundle);

                startActivity(myIntent);

            }
        });

//        Button goToStory = (Button) getActivity().findViewById(R.id.goToStory);
//        //final TextView title = (TextView) getActivity().findViewById(R.id.id_title);
//
//        if (goToStory == null) {
//            Log.d("[BUTTON]", " NULL");
//        }
//        else {
//            Log.d("[BUTTON]", " NOT NULL");
//        }

 //       goToStory.setOnClickListener(new View.OnClickListener() {
 //           @Override
 //           public void onClick(View v) {
//                final int position = listView.getPositionForView(v);
//                if (position != ListView.INVALID_POSITION) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Not valid action", Toast.LENGTH_SHORT);
//                }
//                else {
//                    Intent myIntent =
//                            new Intent(new Intent(getActivity(), ChatActivity.class));
//
//                    Bundle bundle = new Bundle();
//
//                    //String story_title = title.getText().toString();
//
//                    bundle.putString("story_title", "Default");
//                    bundle.putParcelableArrayList("fragments", new ArrayList<Story_Fragment>());
//                    myIntent.putExtras(bundle);
//
//                    startActivity(myIntent);
//                }
//            }
//        });

        return V;

    }

}

class Story_newsfeed_prev_adapter extends ArrayAdapter<Story_Info> {

    public Story_newsfeed_prev_adapter(Context context, ArrayList<Story_Info> stories) {
        super(context, 0, stories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Story_Info story = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.newsfeed_preview, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.id_title);
        TextView description = (TextView) convertView.findViewById(R.id.id_desc);
        TextView participants = (TextView) convertView.findViewById(R.id.id_part);
        TextView started = (TextView) convertView.findViewById(R.id.id_start);
        TextView last = (TextView) convertView.findViewById(R.id.id_last);
        // Populate the data into the template view using the data object
        title.setText(story.title);
        description.setText(story.description);

        String parts = new String("");

        for (String p : story.participants) {
            parts += p + ",";
        }

        participants.setText(parts);
        started.setText(story.started);
        last.setText(story.last);



//        ((ListView)convertView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView tv = (TextView) view.findViewById(R.id.id_title);
//                String text = tv.getText().toString();
//                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
//            }
//        });

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView tv = (TextView) v.findViewById(R.id.id_title);
//                String text = tv.getText().toString();
//                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
//            }
//        });

        // Return the completed view to render on screen
        return convertView;
    }


}