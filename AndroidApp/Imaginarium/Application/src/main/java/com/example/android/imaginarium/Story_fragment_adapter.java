package com.example.android.imaginarium;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cristina on 20.05.2015.
 */
public class Story_fragment_adapter extends ArrayAdapter<Story_Fragment> {

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

        // Populate the data into the template view using the data object
        username.setText(story_fragment.owner + " added :");
        text.setText(story_fragment.text);

        if (story_fragment.has_media == 1) {
            new DownloadImageTask(img)
                    .execute(story_fragment.path_to_media);

        }

        // Return the completed view to render on screen
        return convertView;
    }
}

