package com.foodtinder.features.filter

import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.foodtinder.R
import com.foodtinder.databinding.FragmentFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable


class FilterFragment : Fragment() {
    // TODO: Why would I want to have fragment extend a BaseFragment?

    private lateinit var binding: FragmentFilterBinding
//     TODO: lol wut this can't be allowed
//    private lateinit var context: Context

    private val viewModel: FilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        // Set up search button
        binding.button.setOnClickListener {
            val distance = binding.filterDistanceSlider.value

            // TODO: convert value to string like "1" or "1, 2, 3"
            val priceRange = binding.filterPriceRatingBar.rating

            // TODO: get list of all cuisines from chip group
            val cuisines = listOf("")

            viewModel.onClickSearch(
                distance
            )
        }

        // Set up AutoCompleteTextView
        val cuisineList: Array<String> = activity?.resources?.getStringArray(R.array.cuisine_list) as Array<String>
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_dropdown_item_1line,
            cuisineList
        )

        val textView: AutoCompleteTextView = binding.filterCategoryInput
        textView.setAdapter(adapter)
        textView.onItemClickListener = OnItemClickListener { adapterView, _, i, _ ->
            // Add chip to group below input field
            val selectedCuisine: String = adapterView.getItemAtPosition(i) as String
            addChipToChipGroup(selectedCuisine)

            // Remove text from input field
            textView.setText("")
        }

        return view
    }

    private fun addChipToChipGroup(cuisine: String) {
        val chipGroup = binding.filterCategoryChips
        val chip = layoutInflater.inflate(
            R.layout.single_chip,
            chipGroup,
            false) as Chip

        chip.text = cuisine
        chipGroup.addView(chip)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(it)
        }
    }
}
