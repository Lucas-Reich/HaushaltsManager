package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.AppendInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.CardViewRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.ReportBuilder.DropZoneCard;
import com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems.LineChartCardViewItem;
import com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems.PieChartCardViewItem;
import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget;

import java.util.ArrayList;
import java.util.List;

public class DragAndDropActivity extends AbstractAppCompatActivity implements View.OnDragListener {
    private RecyclerView rView;
    private DropZoneCard dropZoneCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);

        rView = findViewById(R.id.recycler_view);
        rView.setOnDragListener(this);
        setUpRecyclerView();


        findViewById(R.id.drop_zone_root).setOnDragListener(this);

        View dropZoneView = findViewById(R.id.drop_zone);
        dropZoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get tapped view

                // blur background

                // move view to upper third of the screen
                v.animate().x(0).y(0).setDuration(2000);

                // increase size of selected view

                // open BottomSheetModal
            }
        });
        this.dropZoneCard = new DropZoneCard((CardView) dropZoneView);

        findViewById(R.id.drop_zone_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropZoneCard.setDropZoneCount(1);
            }
        });
        findViewById(R.id.drop_zone_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropZoneCard.setDropZoneCount(2);
            }
        });
        findViewById(R.id.drop_zone_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropZoneCard.setDropZoneCount(3);
            }
        });
    }

    @Override

    public boolean onDrag(View targetView, DragEvent event) {

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_LOCATION:
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                // Ignore
                return true;

            case DragEvent.ACTION_DROP:
                if (!isAtDropZone(targetView)) {
                    return true;
                }
                Widget widget = (Widget) event.getLocalState();

                this.dropZoneCard.addDroppedView(widget, event.getX(), event.getY());
                return true;
            default:
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                break;
        }
        return false;
    }

    private boolean isAtDropZone(View targetView) {
        int targetViewId = targetView.getId();

        return R.id.drop_zone_root == targetViewId;
    }

    private void setUpRecyclerView() {
        rView.setLayoutManager(LayoutManagerFactory.horizontal(this));

        List<IRecyclerItem> items = new ArrayList<>();
        items.add(new PieChartCardViewItem(this));
        items.add(new LineChartCardViewItem(this));

        CardViewRecyclerViewAdapter adapter = new CardViewRecyclerViewAdapter(items, new AppendInsertStrategy());
        rView.setAdapter(adapter);
    }
}
