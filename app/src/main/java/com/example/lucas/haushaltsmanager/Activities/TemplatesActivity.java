package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.TemplateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.TemplateListRecyclerViewAdapter;

import java.util.List;

public class TemplatesActivity extends AbstractAppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {
    private TemplateRepository mTemplateRepo;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        mTemplateRepo = new TemplateRepository(this);

        updateListView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_list);

        recyclerView = findViewById(R.id.template_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        initializeToolbar();

    }

    @Override
    public void onItemClick(IRecyclerItem item, int position) {
        if (getCallingActivity() == null) {
            return;
        }

        if (!(item instanceof TemplateItem)) {
            return;
        }

        // TODO: Ich sollte Templates bearbeitbar machen.
        //  Außerdem sollte man Templates auch löschen können.

        Intent returnTemplateIntent = new Intent();
        returnTemplateIntent.putExtra("templateObj", ((Template) item.getContent()).getTemplate());
        setResult(Activity.RESULT_OK, returnTemplateIntent);
        finish();
    }

    @Override
    public void onItemLongClick(IRecyclerItem item, int position) {
        // Do nothing
    }

    private void updateListView() {
        TemplateListRecyclerViewAdapter adapter = new TemplateListRecyclerViewAdapter(
                loadData()
        );

        recyclerView.setAdapter(adapter);
    }

    private List<IRecyclerItem> loadData() {
        List<Template> templates = mTemplateRepo.getAll();

        return ItemCreator.createTemplateItems(templates);
    }
}
