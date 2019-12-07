package com.example.lucas.haushaltsmanager.ListAdapter;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.CardPopulator.TimeFrameCardPopulator;
import com.example.lucas.haushaltsmanager.Entities.Report.Report;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.R;

import java.util.List;

/**
 * source: https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
 */
public class MonthlyReportAdapter extends RecyclerView.Adapter<MonthlyReportAdapter.ViewHolder> {

    private List<ReportInterface> mReports;
    private Resources mResources;

    public MonthlyReportAdapter(List<ReportInterface> reports, Resources resources) {
        mReports = reports;
        mResources = resources;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.timeframe_report_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Report month = (Report) mReports.get(position);

        TimeFrameCardPopulator timeFrameCardPopulator = new TimeFrameCardPopulator(
                (CardView) viewHolder.itemView,
                mResources
        );
        timeFrameCardPopulator.setData(month);
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }
}
