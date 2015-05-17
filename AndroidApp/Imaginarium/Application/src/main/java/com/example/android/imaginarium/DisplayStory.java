package com.example.android.imaginarium;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import java.util.ArrayList;


public class DisplayStory extends Activity {

    String story_title;
    ArrayList<Story_Fragment> arrayOfFragments;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_story);

        Bundle extras = getIntent().getExtras();

        story_title = "Default_story_title";
        if (extras != null) {
            story_title = extras.getString("story_title");
        }
        getActionBar().setTitle(story_title);

        // Construct the data source
        arrayOfFragments = new ArrayList<Story_Fragment>();
        // Create the adapter to convert the array to views
        Story_fragment_adapter adapter = new Story_fragment_adapter(getApplicationContext(),
                arrayOfFragments);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.list_story_fragments);
        listView.setAdapter(adapter);

        String users[] = {"Maria", "Cristi", "Vasile", "Corina", "Florin"};
        String text[] = {
                "AT 8 A. M. it lay on Giuseppi's news-stand, still damp from the presses. " +
                        "Giuseppi, with the cunning of his ilk, philandered on the opposite comer, " +
                        "leaving his patrons to help themselves, no doubt on a theory related to the " +
                        "hypothesis of the watched pot.",
                "This particular newspaper was, according to its custom and design, an educator," +
                        " a guide, a monitor, a champion and a household counsellor and vade mecum.",
                "From its many excellencies might be selected three editorials. One was in simple and chaste but illuminating " +
                        "language directed to parents and teachers, deprecating corporal punishment for children.",
                "Besides these more important chidings and requisitions upon the store of good citizenship was a wise prescription " +
                        "or form of procedure laid out by the editor of the heart- to-heart column in the specific case of a young " +
                        "man who had complained of the obduracy of his lady love, teaching him how he might win her.",
                "Again, there was, on the beauty page, a complete answer to a young lady inquirer who desired " +
                        "admonition toward the securing of bright eyes, rosy cheeks and a beautiful countenance.\n" +
                        "\n" +
                        "One other item requiring special cognizance was a brief \"personal,\" running thus:"
        };

        Story_Fragment sf;
        for (int i = 0; i < 5; i++) {
            sf = new Story_Fragment(i, users[i], text[i], 0, 0, null, null);
            adapter.add(sf);
        }

        Button joinButton = (Button) findViewById(R.id.join);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent =
                        new Intent(new Intent(DisplayStory.this, ChatActivity.class));

                Bundle bundle = new Bundle();
                //myIntent.putExtra("story_title", story_title);
                bundle.putString("story_title", story_title);
                //myIntent.putParcelableArrayListExtra("fragments", arrayOfFragments);
                bundle.putParcelableArrayList("fragments", arrayOfFragments);
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
}

class Story_fragment_adapter extends ArrayAdapter<Story_Fragment> {

    public Story_fragment_adapter(Context context, ArrayList<Story_Fragment> stories_fragments) {
        super(context, 0, stories_fragments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Story_Fragment story_fragment = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.story_fragment, parent, false);
        }
        // Lookup view for data population
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView text = (TextView) convertView.findViewById(R.id.text);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageView);
        VideoView video = (VideoView) convertView.findViewById(R.id.videoView);

        // Populate the data into the template view using the data object
        username.setText(story_fragment.owner + " added :");
        text.setText(story_fragment.text);

        if (story_fragment.has_image == 1) {
            // TODO add image from path
        }
        else if (story_fragment.has_video == 1) {
            // TODO add video from path
        }

        // Return the completed view to render on screen
        return convertView;
    }

}