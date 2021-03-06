package org.buildmlearn.matchtemplate.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.buildmlearn.matchtemplate.R;
import org.buildmlearn.matchtemplate.data.MatchModel;

import java.util.ArrayList;

/**
 * @brief Custom Adapter for match template's app.
 *
 * Created by Anupam (opticod) on 24/7/16.
 */
public class MatchArrayAdapter_B extends ArrayAdapter<MatchModel> {

    public MatchArrayAdapter_B(Context context, ArrayList<MatchModel> lists) {
        super(context, 0, lists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MatchModel match = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_info_a, parent, false);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text.setText(match.getMatchB());
        if (match.getCorrect() == 2) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.item_correct));
        } else if (match.getCorrect() == 1) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.item_wrong));
        }

        return convertView;
    }

    public static class ViewHolder {

        public TextView text;
    }
}
