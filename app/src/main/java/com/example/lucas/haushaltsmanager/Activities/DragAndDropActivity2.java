package com.example.lucas.haushaltsmanager.Activities;

import android.content.ClipDescription;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.AppendInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.CardViewItem.CardViewItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.CardViewRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.ReportBuilder.IChart;
import com.example.lucas.haushaltsmanager.ReportBuilder.PieChart;

import java.util.ArrayList;
import java.util.List;

public class DragAndDropActivity2 extends AbstractAppCompatActivity implements View.OnDragListener {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drag_and_drop_2);

        // Top
        recyclerView = findViewById(R.id.item_holder);
        initializeRecyclerView();

        // Middle
        CardView targetView = findViewById(R.id.drop_target);
        targetView.setOnDragListener(this);
    }

    @Override
    public boolean onDrag(View targetView, DragEvent event) {
        // Defines a variable to store the action type for the incoming event
        final int action = event.getAction();

        // Handles each of the expected events
        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:

                // Determines if this View can accept the dragged data
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Toast.makeText(this, "Drag has started", Toast.LENGTH_SHORT).show();
                    return true;

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                Toast.makeText(this, "Drag has entered", Toast.LENGTH_SHORT).show();

                return true;

            case DragEvent.ACTION_DRAG_LOCATION:

                // Ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                Toast.makeText(this, "Drag has exited", Toast.LENGTH_SHORT).show();

                return true;

            case DragEvent.ACTION_DROP:
//                Toast.makeText(this, "Drag has dropped", Toast.LENGTH_SHORT).show();

                IChart chart = (IChart) event.getLocalState();
                // TODO: Add chart to card view
                return true;

            case DragEvent.ACTION_DRAG_ENDED:

                // Does a getResult(), and displays what happened.
                if (event.getResult()) {
                    Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG).show();

                }

                // returns true; the value is ignored.
                return true;

            // An unknown action type was received.
            default:
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                break;
        }

        return false;
    }

    private void initializeRecyclerView() {
        recyclerView.setLayoutManager(LayoutManagerFactory.horizontal(this));
        recyclerView.setAdapter(createAdapter());
    }

    private RecyclerView.Adapter createAdapter() {
        List<IRecyclerItem> items = createItemList(10);

        return new CardViewRecyclerViewAdapter(items, new AppendInsertStrategy());
    }

    private List<IRecyclerItem> createItemList(int itemCount) {
        List<IRecyclerItem> itemList = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            itemList.add(new CardViewItem(new PieChart(this)));
        }

        return itemList;
    }
}

