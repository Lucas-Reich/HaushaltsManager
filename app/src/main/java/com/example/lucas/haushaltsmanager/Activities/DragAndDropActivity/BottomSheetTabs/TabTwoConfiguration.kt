package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BottomSheetTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lucas.haushaltsmanager.R

class TabTwoConfiguration : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dnd_tab_two_configuration, container, false)
    }
}