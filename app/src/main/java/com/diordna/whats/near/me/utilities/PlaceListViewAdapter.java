package com.diordna.whats.near.me.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.diordna.whats.near.me.R;
import com.diordna.whats.near.me.model.Place;
import com.diordna.whats.near.me.ui.activities.PlaceListActivity;

import java.util.List;

public class PlaceListViewAdapter extends ArrayAdapter<Place>{

    private PlaceListActivity activity;
    private List<Place> friendList;

    public PlaceListViewAdapter(PlaceListActivity context, int resource, List<Place> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.friendList = objects;
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Place getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.hospital_list_item, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        Place rowPlace=getItem(position);
        holder.placeRef.setText(rowPlace.reference);
        holder.placeName.setText(rowPlace.name);
        holder.placeAreaName.setText(rowPlace.vicinity );
        holder.placeAddress.setText(rowPlace.formatted_address);


        /*holder.placeRefWeb.setText(rowPlace.reference);
        holder.placeRefCall.setText(rowPlace.reference);
        holder.placeRefDirection.setText(rowPlace.reference)*/;
        //get first letter of each String item
        String firstLetter = String.valueOf(getItem(position).name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(getItem(position));
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(firstLetter, color); // radius in px

        holder.imageView.setImageDrawable(drawable);

        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView placeName;
        private TextView placeAreaName;
        private TextView placeAddress;
        private TextView placeRating;
        private TextView placeRef;

        /*private TextView placeRefWeb;
        private TextView placeRefCall;
        private TextView placeRefDirection;*/

        public ViewHolder(View v) {
            imageView = (ImageView) v.findViewById(R.id.iv_place_name);
            placeName = (TextView) v.findViewById(R.id.txt_place_name);
            placeAreaName = (TextView) v.findViewById(R.id.txt_area_name);
            placeAddress = (TextView) v.findViewById(R.id.txt_full_address);

            placeRef = (TextView) v.findViewById(R.id.txt_place_ref);
/*
            placeRefWeb = (TextView) v.findViewById(R.id.txt_pacel_ref_website);
            placeRefCall = (TextView) v.findViewById(R.id.txt_pacel_ref_call);
            placeRefDirection = (TextView) v.findViewById(R.id.txt_pacel_ref_dir);*/
        }
    }
}
