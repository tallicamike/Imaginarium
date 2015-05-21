package com.example.android.imaginarium;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;


public class ChatActivity extends Activity {


    String gid;
    String username;

    ArrayList<Story_Fragment> arrayOfFragments;

    ClientStory clientStory;
    Story_fragment_adapter adapter;

    ListView listView;

    @Override
    protected void onStart() {
        super.onStart();
        new HttpGetTask().execute();
    }

    String story_title, story_id;
    ArrayList<Story_Fragment> fragments;

    String fragm_text;

    Uri picture_URI;


    TextView uploaded_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();

        story_title = "Default_story_title";

        if (extras != null) {
            gid = extras.getString("gid");
            username = extras.getString("username");
            story_title = extras.getString("story_title");
            story_id = extras.getString("story_id");
        }

        getActionBar().setTitle(story_title);

        uploaded_picture = null;

        // Construct the data source
        arrayOfFragments = new ArrayList<Story_Fragment>();
        // Create the adapter to convert the array to views
        adapter = new Story_fragment_adapter(getApplicationContext(),
                arrayOfFragments);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.list_fragm);
        listView.setAdapter(adapter);

        fragments = new ArrayList<>();

        Button send_text = (Button) findViewById(R.id.send_text);
        Button attach_file = (Button) findViewById(R.id.attach);
        uploaded_picture = (TextView) findViewById(R.id.uploaded_file);

        final EditText text_box = (EditText) findViewById(R.id.editText);

        send_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragm_text = text_box.getText().toString();
                if (! fragm_text.matches("")) {
                    Story_Fragment sf;

                    sf = new Story_Fragment(0, username, fragm_text, 0, null);
                    adapter.add(sf);
                    text_box.setText("");
                    listView.setSelection(adapter.getCount() - 1);

                    new HttpRequestTask().execute();

                }
            }
        });

        attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Setect picture"), 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                picture_URI = data.getData();
                uploaded_picture.setText("Uploaded: " +  picture_URI.toString());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    private class HttpRequestTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                final String url = "http://tallica.koding.io:8080/continue_story?gid=" +
                        gid +
                        "&story_id=" +
                        story_id +
                        "&text=" +
                        fragm_text;

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getForObject(url, Integer.class);


            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }


    }

    private class HttpGetTask extends AsyncTask<Void, Void, ClientStory> {
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
                        if (clientStory != null  && clientStory.getFragments() != null) {
                            if (clientStory.getFragments().size() > 0) {

                                for (ClientFragment cf : clientStory.getFragments()) {

                                    Story_Fragment sf = new Story_Fragment(0, cf.getAuthorName(),
                                            cf.getText(),
                                            0, null);

                                    arrayOfFragments.add(sf);

                                }
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
