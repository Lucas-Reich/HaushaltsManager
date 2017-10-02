package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> implements View.OnClickListener {

    private ArrayList<Category> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtCategoryName;
    }

    public CategoryAdapter(ArrayList<Category> data, Context context) {
        super(context, R.layout.category_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Category categoryObject = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.category_item, parent, false);
            viewHolder.txtCategoryName = (TextView) convertView.findViewById(R.id.listview_categoryName);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtCategoryName.setText(categoryObject.getCategoryName());

        return convertView;
    }


}