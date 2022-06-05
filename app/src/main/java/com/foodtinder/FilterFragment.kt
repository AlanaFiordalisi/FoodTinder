package com.foodtinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.foodtinder.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        // Set up AutoCompleteTextView
        val cuisines: Array<String> = activity?.resources?.getStringArray(R.array.cuisine_list) as Array<String>

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_dropdown_item_1line, cuisines
        )
        val textView: AutoCompleteTextView = binding.filterCategoryInput
        textView.setAdapter(adapter)

        return view
    }
}
