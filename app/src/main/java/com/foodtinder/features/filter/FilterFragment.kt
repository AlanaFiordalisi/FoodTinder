package com.foodtinder.features.filter

import android.os.Bundle
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

class FilterFragment : Fragment() {
    // TODO: Why would I want to have fragment extend a BaseFragment?

    private lateinit var binding: FragmentFilterBinding

    private val viewModel: FilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        setUpSearchButton()
        setUpCuisineTextView(view)

        viewModel.logText.observe(viewLifecycleOwner) {
            binding.logText.text = it
        }

        return view
    }

    private fun setUpSearchButton() {
        binding.button.setOnClickListener {
            viewModel.setDistance(binding.filterDistanceSlider.value)
            viewModel.setPriceRange(binding.filterPriceRatingBar.rating)

            viewModel.onClickSearch()
        }
    }

    private fun setUpCuisineTextView(view: View) {
        // TODO move this to viewmodel?
        val cuisinesArray: Array<String> = viewModel.cuisines.map { cuisine -> cuisine.title }.toTypedArray()
//        val cuisineList: Array<String> = activity?.resources?.getStringArray(R.array.cuisine_list) as Array<String>
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_dropdown_item_1line,
            cuisinesArray
        )

        val textView: AutoCompleteTextView = binding.filterCategoryInput
        textView.setAdapter(adapter)
        textView.onItemClickListener = OnItemClickListener { adapterView, _, i, _ ->
            // Add chip to group below input field
            val selectedCuisine: String = adapterView.getItemAtPosition(i) as String
            addChipToChipGroup(selectedCuisine)

            // Clear text from input field
            textView.setText("")
        }
    }

    private fun addChipToChipGroup(cuisine: String) {
        val cuisineAlias = viewModel.cuisines.first {
            it.title == cuisine
        }.alias

        val chipGroup = binding.filterCategoryChips
        val chip = layoutInflater.inflate(
            R.layout.single_chip,
            chipGroup,
            false) as Chip

        chip.text = cuisine
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(it)
            viewModel.removeCuisine(cuisineAlias)
        }

        chipGroup.addView(chip)
        viewModel.addCuisine(cuisineAlias)
    }
}
