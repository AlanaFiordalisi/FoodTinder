package com.foodtinder.features.filter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.foodtinder.R
import com.foodtinder.databinding.FragmentFilterBinding
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
        val cuisines: Array<String> = activity?.resources?.getStringArray(R.array.cuisine_list) as Array<String>

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_dropdown_item_1line,
            cuisines
        )
        val textView: MultiAutoCompleteTextView = binding.filterCategoryInput
        textView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        textView.setAdapter(adapter)
        textView.onItemClickListener = OnItemClickListener { adapterView, _, i, _ ->
            val selectedCuisine: String = adapterView.getItemAtPosition(i) as String
            createRecipientChip(selectedCuisine)
        }

        return view
    }

    private fun createRecipientChip(selectedCuisine: String) {
        val chip = ChipDrawable.createFromResource(requireActivity(), R.xml.chip)
        val span = ImageSpan(chip)
        val cursorPosition: Int = binding.filterCategoryInput.selectionStart
        val spanLength: Int = selectedCuisine.length + 2
        val text: Editable = binding.filterCategoryInput.text
//        chip.chipIcon = ContextCompat.getDrawable(
//            this@MainActivity,
//            selectedCuisine.getAvatarResource()
//        )
        chip.text = selectedCuisine
        chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
        chip.chipBackgroundColor = ColorStateList.valueOf(
            ContextCompat.getColor(requireActivity(), R.color.light_green_200)
        )

        text.setSpan(
            span,
            cursorPosition - spanLength,
            cursorPosition,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}
