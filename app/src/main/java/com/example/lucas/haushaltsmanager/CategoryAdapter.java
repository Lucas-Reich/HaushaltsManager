package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> implements View.OnClickListener {

    private static class ViewHolder {
        RoundedTextView circTextView;
        TextView txtCategoryName;
    }

    public CategoryAdapter(ArrayList<Category> data, Context context) {
        super(context, R.layout.category_item, data);
    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {

        Category categoryObject = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.category_item, parent, false);

            viewHolder.txtCategoryName = (TextView) convertView.findViewById(R.id.category_item_name);
            viewHolder.circTextView = (RoundedTextView) convertView.findViewById(R.id.category_item_circ_textview);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtCategoryName.setText(String.format("%s", categoryObject.getTitle()));
        viewHolder.circTextView.setCenterText(String.format("%s", categoryObject.getTitle().substring(0,1).toUpperCase()));
        viewHolder.circTextView.setCircleColor(categoryObject.getColor());
        viewHolder.circTextView.setTextColor(Color.WHITE);

        return convertView;
    }
}