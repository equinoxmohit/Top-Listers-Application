package com.mohit.topapplicationlistfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class FeedAdapter extends ArrayAdapter {

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry currentRecord = applications.get(position);

        viewHolder.tvName.setText(currentRecord.getName());
        viewHolder.tvArtist.setText(currentRecord.getArtist());
        viewHolder.tvReleaseDate.setText(currentRecord.getReleaseDate());
        viewHolder.tvSummary.setText(currentRecord.getSummary());


        return convertView;
    }


    public class ViewHolder {
        final TextView tvName, tvArtist, tvReleaseDate, tvSummary;

        public ViewHolder(View view) {
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.tvArtist = (TextView) view.findViewById(R.id.tvArtist);
            this.tvReleaseDate = (TextView) view.findViewById(R.id.tvReleaseDate);
            this.tvSummary = (TextView) view.findViewById(R.id.tvSummary);
        }
    }
}
