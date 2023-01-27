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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.foodtinder.R
import com.foodtinder.databinding.FragmentFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FilterFragment : Fragment() {
    // TODO: Why would I want to have fragment extend a BaseFragment?

    private val viewModel: FilterViewModel by activityViewModels()

    private lateinit var binding: FragmentFilterBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.setCurrentLocation()
            } else {
                showLocationDeniedDialog()
                viewModel.setUsingCurrentLocation(false)
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

        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }

        // update toggle state based on usingCurrentState and reset coordinates when toggled off
        viewModel.usingCurrentLocation.observe(viewLifecycleOwner) { usingCurrentLocation ->
            binding.filterCurrentLocationToggle.isChecked = usingCurrentLocation
            if (!usingCurrentLocation) viewModel.resetCurrentLocation()
        }

        // show loading spinner while the current location is being retrieved
        viewModel.settingCurrentLocation.observe(viewLifecycleOwner) { settingCurrentLocation ->
            when (settingCurrentLocation) {
                true -> {
                    binding.filterSearchButton.visibility = View.INVISIBLE
                    binding.filterLoadingSpinner.visibility = View.VISIBLE
                }
                else -> {
                    binding.filterSearchButton.visibility = View.VISIBLE
                    binding.filterLoadingSpinner.visibility = View.INVISIBLE
                }
            }
        }

        setUpLocationEditText()
        setUpCurrentLocationToggle()
        setUpCuisineTextView(view)
        setUpSearchButton()

        return view
    }

    private fun setUpLocationEditText() {
        // Pre-fill address if one has already been entered
        viewModel.location.value?.let {
            binding.filterLocationEdittext.setText(it)
        }

        // Open AddressSearchFragment to handle address autocomplete logic when clicked
        binding.filterLocationEdittext.setOnClickListener {
            findNavController().navigate(
                FilterFragmentDirections.actionFilterFragmentToAddressSearch()
            )
        }
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
                showLocationRationaleDialog()
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setUpSearchButton() {
        binding.filterSearchButton.setOnClickListener {
            viewModel.setPriceRange(binding.filterPriceRatingBar.rating)
            viewModel.setDistance(binding.filterDistanceSlider.value)

            if (viewModel.usingCurrentLocation.value != true) {
                viewModel.setLocation(binding.filterLocationEdittext.text.toString())
            }

            // ensure a location is set (address or coordinates) before conducting the search
            when (viewModel.locationEntryValid()) {
                true -> viewModel.onClickSearch()
                else -> showLocationRequiredDialog()
            }
        }
    }

    private fun showLocationRequiredDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Location must be set")
            .setMessage("In order to search, you must either enter an address or use current location.")
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setUpCuisineTextView(view: View) {
        val cuisinesArray: Array<String> = viewModel.cuisineOptions.map { cuisine -> cuisine.title }.toTypedArray()
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_dropdown_item_1line,
            cuisinesArray
        )

        val textView: AutoCompleteTextView = binding.filterCategoryInput
        textView.setAdapter(adapter)
        textView.onItemClickListener = OnItemClickListener { adapterView, _, i, _ ->
            // If this cuisine has not yet been added to the list of selected cuisines, add and display it
            val selectedCuisine = adapterView.getItemAtPosition(i) as String
            val cuisineAlias = viewModel.cuisineOptions.first { it.title == selectedCuisine }.alias

            viewModel.cuisines.value?.let { cuisines ->
                if (!cuisines.contains(cuisineAlias)) {
                    addChipToChipGroup(selectedCuisine, cuisineAlias)
                }
            }

            // Clear text from input field
            textView.setText("")
        }
    }

    private fun addChipToChipGroup(cuisine: String, cuisineAlias: String) {
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

    private fun showLocationRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.location_permission_title)
            .setMessage(R.string.location_permission_message)
            .setCancelable(false)
            .setNegativeButton(R.string.location_permission_negative) { dialog, _ ->
                dialog.dismiss()
                viewModel.setUsingCurrentLocation(false)
            }
            .setPositiveButton(R.string.location_permission_positive) { dialog, _ ->
                dialog.dismiss()
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .create()
            .show()
    }

    private fun showLocationDeniedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.location_permission_denied_title))
            .setMessage(getString(R.string.location_permission_denied_message))
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
