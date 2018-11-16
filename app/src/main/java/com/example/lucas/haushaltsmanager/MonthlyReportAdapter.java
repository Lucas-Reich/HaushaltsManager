package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.CardPopulator.TimeFrameCardPopulator;
import com.example.lucas.haushaltsmanager.Entities.Report.Report;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.Utils.ViewUtils;

import java.util.List;

/**
 * source: https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
 */
public class MonthlyReportAdapter extends RecyclerView.Adapter<MonthlyReportAdapter.ViewHolder> {

    private Context mContext;
    private List<ReportInterface> mReports;

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }

    MonthlyReportAdapter(Context context, List<ReportInterface> reports) {
        mContext = context;
        mReports = reports;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflateRootView(parent, R.layout.timeframe_report_card);

        applyMargin(itemView);

        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Report month = (Report) mReports.get(position);

        TimeFrameCardPopulator timeFrameCardPopulator = new TimeFrameCardPopulator(
                (CardView) viewHolder.itemView,
                mContext
        );
        timeFrameCardPopulator.setData(month);
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    private void applyMargin(View view) {
        CardView.LayoutParams clp = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        clp.setMargins(
                ViewUtils.dpToPx(16),
                ViewUtils.dpToPx(16),
                ViewUtils.dpToPx(16),
                0
        );

        view.setLayoutParams(clp);
    }

    private View inflateRootView(ViewGroup parent, @LayoutRes int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }
}
