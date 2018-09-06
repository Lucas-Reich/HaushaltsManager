package com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;

import java.util.List;

public class ChangelogAdapter extends ArrayAdapter<Release> {

    private static class ViewHolder {
        TextView releaseVersion;
        TextView releaseDescription;
        ListView releaseItems;
    }

    public ChangelogAdapter(List<Release> data, Context context) {
        super(context, R.layout.changelog_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Release release = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.changelog_item, parent, false);

            viewHolder.releaseVersion = convertView.findViewById(R.id.changelog_header_version);
            viewHolder.releaseDescription = convertView.findViewById(R.id.changelog_header_description);
            viewHolder.releaseItems = convertView.findViewById(R.id.changelog_item_row_container);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.releaseVersion.setText(release.getReleaseVersion());
        viewHolder.releaseDescription.setText(release.getReleaseDescription());// wenn keine desc angegeben ist soll auch der graue hintergrund nicht sichtbar sein
        viewHolder.releaseItems.setAdapter(getRowAdapter(release));

        return convertView;
    }

    /**
     * Methode um den ListAdapter für die Lister der Änderungen zu erzeugen.
     *
     * @param release Release
     * @return ListAdapter
     */
    private ChangelogRowAdapter getRowAdapter(Release release) {

        return new ChangelogRowAdapter(release.getItems(), getContext());
    }
}
