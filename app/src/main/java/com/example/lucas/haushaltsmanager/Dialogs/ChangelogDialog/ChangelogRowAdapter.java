package com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;

import java.util.List;

public class ChangelogRowAdapter extends ArrayAdapter<ChangelogItem> {
    private static class ViewHolder {
        TextView type;
        TextView description;
    }

    public ChangelogRowAdapter(List<ChangelogItem> data, Context context) {
        super(context, R.layout.changelog_row, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChangelogItem item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.changelog_row, parent, false);

            viewHolder.type = convertView.findViewById(R.id.changelog_row_type);
            viewHolder.description = convertView.findViewById(R.id.changelog_row_description);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.type.setText(String.format("- %s: ", item.getType()));
        viewHolder.description.setText(item.getDescription());

        return convertView;
    }
}
