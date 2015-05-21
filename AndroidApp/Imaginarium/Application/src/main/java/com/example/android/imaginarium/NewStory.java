package com.example.android.imaginarium;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewStory extends Fragment {

    String gid, username, title_to_send, desc_to_send;

    public NewStory() {
        // Required empty public constructor
    }

    public void setGID(String set_gid) {
        this.gid = set_gid;
        Log.d("[NewStory]", "GID set to " +  gid);
    }

    public void setUsername(String set_username) {
        this.username = set_username;
        Log.d("[NewStory]", "Username set to " +  username);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V=  inflater.inflate(R.layout.fragment_new_story, container, false);

        Button button = (Button) V.findViewById(R.id.button_create);
        final EditText title = (EditText) V.findViewById(R.id.title);
        final EditText desc = (EditText) V.findViewById(R.id.description);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String title_text = title.getText().toString();
                String desc_text = desc.getText().toString();

                if (title_text.matches("")) {
                    Context context = getActivity().getApplicationContext();
                    CharSequence text = "Title field must not be empty !";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }
                else {
                    title_to_send = title_text;
                    desc_to_send = desc_text;
                    new HttpRequestTask().execute();


                }
            }
        });
        return V;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private class HttpRequestTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                final String url = "http://tallica.koding.io:8080/create_story?gid=" +
                        gid +
                        "&title=" +
                        title_to_send +
                        "&description=" +
                        desc_to_send
                        ;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                int id = restTemplate.getForObject(url, Integer.class);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent myIntent =
                                new Intent(new Intent(getActivity(), ChatActivity.class));

                        Bundle bundle = new Bundle();

                        bundle.putString("gid", gid);
                        bundle.putString("username", username);
                        bundle.putString("story_title", title_to_send);

                        String story_id = "-1";
                        bundle.putString("story_id", story_id);
                        myIntent.putExtras(bundle);

                        startActivity(myIntent);
                    }
                });


                return id;

            } catch (Exception e) {
                Log.e("NewStory", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer reply) {

        }

    }
}
