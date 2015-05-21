package com.example.android.imaginarium;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.ArrayList;


public class DisplayStory extends Activity {

    String gid;
    String username;

    String story_title, story_id;
    ArrayList<Story_Fragment> arrayOfFragments;

    ClientStory clientStory;
    Story_fragment_adapter adapter;

    ListView listView;

    @Override
    public void onStart() {
        new HttpRequestTask().execute();
        super.onStart();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_story);

        Bundle extras = getIntent().getExtras();

        story_title = "Default_story_title";
        if (extras != null) {
            gid = extras.getString("gid");
            username = extras.getString("username");
            story_title = extras.getString("story_title");
            story_id = extras.getString("story_id");
        }
        getActionBar().setTitle(story_title);


        // Construct the data source
        arrayOfFragments = new ArrayList<Story_Fragment>();
        // Create the adapter to convert the array to views
        adapter = new Story_fragment_adapter(getApplicationContext(),
                arrayOfFragments);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.list_story_fragments);
        listView.setAdapter(adapter);


        Button joinButton = (Button) findViewById(R.id.join);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent =
                        new Intent(new Intent(DisplayStory.this, ChatActivity.class));

                Bundle bundle = new Bundle();
                bundle.putString("gid", gid);
                bundle.putString("username", username);
                bundle.putString("story_title", story_title);
                bundle.putString("story_id", story_id);

                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, ClientStory> {
        @Override
        protected ClientStory doInBackground(Void... params) {
            try {
                final String url = "http://tallica.koding.io:8080//get_story?story_id=" +
                        story_id;

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                clientStory  = restTemplate.getForObject(url, ClientStory.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        listView.setAdapter(null);
                        arrayOfFragments.clear();
                        if (clientStory.getFragments().size() > 0) {

                            for (ClientFragment cf : clientStory.getFragments()) {

                                Story_Fragment sf = new Story_Fragment(0, cf.getAuthorName(),
                                        cf.getText(),
                                        0, null);

                                arrayOfFragments.add(sf);

                            }
                        }

                        adapter = new Story_fragment_adapter(getApplicationContext(), arrayOfFragments);
                        listView.setAdapter(adapter);

                    }
                });

                return clientStory;

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ClientStory v) {

        }
    }
}

