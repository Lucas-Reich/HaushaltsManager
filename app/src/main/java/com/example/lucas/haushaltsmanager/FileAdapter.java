package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class FileAdapter extends ArrayAdapter<File> {

    private static class ViewHolder {
        TextView txtFileName;
    }

    public FileAdapter(List<File> data, Context context) {
        super(context, R.layout.file_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        File file = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.file_item, parent, false);

            viewHolder.txtFileName = (TextView) convertView.findViewById(R.id.file_item_name);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtFileName.setText(file.getName());

        return convertView;
    }
}
