package com.example.android.imaginarium;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFeed extends Fragment {

    int is_newsfeed;

    String gid, username, friendList;

    ArrayList<Story_Info> arrayOfStories;
    Story_newsfeed_prev_adapter adapter;
    ListView listView;;

    NewsFeedData data;

    public NewsFeed() {

    }

    @Override
    public void onStart() {
        new HttpRequestTask().execute();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V =  inflater.inflate(R.layout.fragment_newsfeed, container, false);

        Bundle bundle = this.getArguments();
        is_newsfeed = bundle.getInt("is_newsfeed");
        gid = bundle.getString("gid");
        username = bundle.getString("username");
        friendList = bundle.getString("friends");
        // Construct the data source
        arrayOfStories = new ArrayList<Story_Info>();
        // Create the adapter to convert the array to views
        adapter = new Story_newsfeed_prev_adapter(getActivity().getApplicationContext(),
                arrayOfStories);
        // Attach the adapter to a ListView
        listView = (ListView) V.findViewById(R.id.newsfeed);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout ll = (LinearLayout) listView.getChildAt(position);
                TextView tv = (TextView) ll.findViewById(R.id.id_title);

                String story_title = tv.getText().toString();

                String story_id = "";

                for (ClientStory cs : data.getStories()) {
                    if (cs.getTitle().matches(story_title))
                        story_id = "" + cs.getStoryId();
                }

                Bundle bundle = new Bundle();
                Intent myIntent;
                if (is_newsfeed == 0)
                    myIntent = new Intent(new Intent(getActivity(), ChatActivity.class));
                else
                    myIntent = new Intent(new Intent(getActivity(), DisplayStory.class));

                bundle.putString("gid", gid);
                bundle.putString("username", username);
                bundle.putString("story_title", story_title);

                bundle.putString("story_id", story_id);

                myIntent.putExtras(bundle);

                startActivity(myIntent);

            }
        });

        return V;

    }

    private class HttpRequestTask extends AsyncTask<Void, Void, NewsFeedData> {
        @Override
        protected NewsFeedData doInBackground(Void... params) {
            try {
                final String url = "http://tallica.koding.io:8080/get_friends_stories?gid=" +
                        gid +
                        "&friends=";

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                data  = restTemplate.getForObject(url, NewsFeedData.class);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String s1 = "Title_", s2 = "Description_", s3 = "20/05/2015", s4 = "4 hours ago";
                        ArrayList<String> p = new ArrayList<>();


                        Story_Info prev;

                        int i = 0;

                        listView.setAdapter(null);
                        arrayOfStories.clear();

                        if (data.getStories().size() > 0) {
                            for (ClientStory cs : data.getStories()) {

                                p.clear();
                                for (ClientFragment cf : cs.getFragments()) {
                                    p.add(0, cf.getAuthorName());

                                }

                                prev = new Story_Info(cs.getStoryId(), cs.getTitle(), "",
                                        p, s3, cs.getLastUpdate());

                                arrayOfStories.add(prev);

                            }
                        }
                        else {
                            prev = new Story_Info(0, "No stories to display", "", null, "", "");
                            arrayOfStories.add(prev);
                        }
                        adapter = new Story_newsfeed_prev_adapter(getActivity(),arrayOfStories);
                        listView.setAdapter(adapter);

                    }
                });

                return data;

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(NewsFeedData v) {

        }
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

        if (story.participants.size() > 0)
            for (String p : story.participants) {
                parts += p + ",";
            }

        participants.setText(parts);
        started.setText(story.started);
        last.setText(story.last);

        // Return the completed view to render on screen
        return convertView;
    }


    }