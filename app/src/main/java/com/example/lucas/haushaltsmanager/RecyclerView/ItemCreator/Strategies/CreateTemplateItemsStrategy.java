package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking;

import java.util.ArrayList;
import java.util.List;

public class CreateTemplateItemsStrategy implements RecyclerItemCreatorStrategyInterface<TemplateBooking> {
    @Override
    public List<IRecyclerItem> create(List<TemplateBooking> templateBookings) {
        if (templateBookings.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> templateItems = new ArrayList<>();
        for (TemplateBooking templateBooking : templateBookings) {
            templateItems.add(new TemplateItem(templateBooking));
        }

        return templateItems;
    }
}
