package com.foodtinder.features.filter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.foodtinder.R
import com.foodtinder.databinding.FragmentFilterBinding
import com.google.android.material.chip.Chip

class FilterFragment : Fragment() {
    // TODO: Why would I want to have fragment extend a BaseFragment?

    private lateinit var binding: FragmentFilterBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val viewModel: FilterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.setCurrentLocation()
            } else {
                // dialog to explain that they cannot use current location, but they must
                // enter an address instead
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        setUpCurrentLocationToggle()
        setUpCuisineTextView(view)
        setUpSearchButton()

        return view
    }

    private fun setUpCurrentLocationToggle() {
        binding.filterCurrentLocationToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) handlePermission()
            viewModel.setUsingCurrentLocation(isChecked)
            binding.filterLocationEdittext.isVisible = !isChecked
        }
    }

    private fun handlePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> viewModel.setCurrentLocation()
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // show rationale dialog
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setUpSearchButton() {
        binding.button.setOnClickListener {
            viewModel.setPriceRange(binding.filterPriceRatingBar.rating)
            viewModel.setDistance(binding.filterDistanceSlider.value)

            if (!viewModel.usingCurrentLocation) {
                viewModel.setLocation(binding.filterLocationEdittext.text.toString())
            }

            viewModel.onClickSearch()
        }
    }

    private fun setUpCuisineTextView(view: View) {
        val cuisinesArray: Array<String> = viewModel.cuisines.map { cuisine -> cuisine.title }.toTypedArray()
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
