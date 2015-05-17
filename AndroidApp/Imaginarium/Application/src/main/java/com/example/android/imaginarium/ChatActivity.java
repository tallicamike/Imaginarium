package com.example.android.imaginarium;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class ChatActivity extends Activity {

    String story_title;
    ArrayList<Story_Fragment> fragments;

    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();

        user = "Cristina";

        story_title = "Default_story_title";

        if (extras != null) {
            story_title = extras.getString("story_title");
            fragments =  extras.getParcelableArrayList("fragments");
        }

        getActionBar().setTitle(story_title);

        // Construct the data source
        ArrayList<Story_Fragment> arrayOfFragments = new ArrayList<Story_Fragment>();
        // Create the adapter to convert the array to views
        final Story_fragment_adapter adapter = new Story_fragment_adapter(getApplicationContext(),
                arrayOfFragments);
        // Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.list_fragm);
        listView.setAdapter(adapter);

        if (fragments.size() > 0) {
            for (Story_Fragment sf : fragments) {
                adapter.add(sf);
            }
        }

        Button send_text = (Button) findViewById(R.id.send_text);
        final EditText text_box = (EditText) findViewById(R.id.editText);

        send_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = text_box.getText().toString();
                if (! content.matches("")) {
                    Story_Fragment sf = new Story_Fragment(0, user, content, 0, 0, null, null);
                    adapter.add(sf);
                    text_box.setText("");
                    listView.setSelection(adapter.getCount() - 1);
                }
            }
        });

        text_box.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.setEnabled(true);
                v.requestFocus();
            }
        });

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
}
