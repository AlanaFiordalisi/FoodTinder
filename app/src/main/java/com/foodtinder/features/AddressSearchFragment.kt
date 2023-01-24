package com.foodtinder.features

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.foodtinder.R
import com.foodtinder.databinding.FragmentAddressSearchBinding
import com.foodtinder.features.filter.FilterViewModel
import com.mapbox.search.autofill.AddressAutofill
import com.mapbox.search.autofill.AddressAutofillSuggestion
import com.mapbox.search.autofill.Query
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView

class AddressSearchFragment : Fragment() {

    private val viewModel: FilterViewModel by activityViewModels()

    private lateinit var addressAutofill: AddressAutofill
    private lateinit var binding: FragmentAddressSearchBinding
    private lateinit var searchEngineUiAdapter: AddressAutofillUiAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressSearchBinding.inflate(layoutInflater, container, false)

        addressAutofill = AddressAutofill.create(getString(R.string.mapbox_access_token))
        searchEngineUiAdapter = AddressAutofillUiAdapter(
            view = binding.searchResultsView,
            addressAutofill = addressAutofill
        )

        setUpAddressEditText()
        setUpSearchResultsView()
        setUpSearchEngineUiAdapter()

        binding.confirmButton.setOnClickListener {
            setLocationAndNavigateBack(binding.queryText.text.toString())
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Focus on the EditText and show the soft keyboard
        binding.queryText.requestFocus()
        val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(binding.queryText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setUpAddressEditText() {
        // Pre-fill address if one has already been selected
        viewModel.location.value?.let { currentAddress ->
            binding.queryText.setText(currentAddress)
        }

        binding.queryText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // nothing to do
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO: ignoreNextQueryTextUpdate?

                // Show no suggestions in SearchResultsView when the text box is empty
                text?.isEmpty().let {
                    binding.searchResultsView.setAdapterItems(emptyList())
                }

                val query = Query.create(text.toString())
                if (query != null) {
                    lifecycleScope.launchWhenStarted {
                        searchEngineUiAdapter.search(query)
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // nothing to do
            }

        })
    }

    private fun setUpSearchResultsView() {
        binding.searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )
    }

    private fun setUpSearchEngineUiAdapter() {
        searchEngineUiAdapter.addSearchListener(object : AddressAutofillUiAdapter.SearchListener {

            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                setLocationAndNavigateBack(suggestion.formattedAddress.formatAddress())
            }

            override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {
                // Nothing to do
            }

            override fun onError(e: Exception) {
                // Nothing to do
            }
        })
    }

    fun String.formatAddress(): String {
        val start = this.indexOf(",")
        val end = start + 1
        return this.removeRange(start, end)
    }

    private fun setLocationAndNavigateBack(location: String) {
        viewModel.setLocation(location)
        findNavController().navigate(
            AddressSearchFragmentDirections.actionAddressSearchToFilterFragment()
        )
    }
}