package com.example.weatherapp.ui.home

import android.os.Bundle
import android.transition.TransitionManager
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.extension.*
import com.example.weatherapp.utils.Dialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Display home fragment of weatherEntity detail
 */
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel> { getViewModelFactory() }
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            this.lifecycleOwner = this@HomeFragment.viewLifecycleOwner
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun observeLoadImage() {
        homeViewModel.iconUrl.observe(this.viewLifecycleOwner, Observer {
            if (it != null) {
                Picasso.get().load(it).resize(60, 60).into(binding.ivWeatherIcon)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSnackbar()
        setupNetworkObserve()
        val fab: FloatingActionButton = view.findViewById(R.id.location_button)

        fab.setOnClickListener {
            homeViewModel.fetchData()
        }

        (requireContext() as MainActivity).observePermissionState().distinctUntilChanged()
            .observe(this.viewLifecycleOwner,
                Observer {
                    if (it) {
                        homeViewModel.refresh(true)
                    }
                })

        observeLoadImage()
        initOnClick()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu)
        val menuItem = menu.findItem(R.id.menu_search)
        val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView
        searchView.queryHint = "Enter city name..."
        // Older version
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                hideKeyboardFrom(this@HomeFragment.context, searchView)
//                if (query != null)
//                    homeViewModel.fetchDataByName(query)
//                else {
//                    homeViewModel.showResultMessage(R.string.empty_string)
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return true
//            }
//        })
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboardFrom(this@HomeFragment.context, searchView)
                    if (query != null)
                        homeViewModel.fetchDataByName(query)
                    else {
                        homeViewModel.showResultMessage(R.string.empty_string)
                    }
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                homeViewModel.refresh(true)
                true
            }
            R.id.menu_delete -> {
                homeViewModel.delete()
                true
            }
            else -> false
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackBar(this, homeViewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            // This is safe-args defined in navigation.xml
            homeViewModel.showResultMessage(args.userMessage)
        }
    }

    private fun setupNetworkObserve() {
        homeViewModel.networkError.observe(this.viewLifecycleOwner, Observer {
            if (it) {
                val dialogFragment =
                    Dialog(
                        R.string.network_error_title, R.string.network_error_message,
                        { dialog, _ ->
                            dialog.dismiss()
                        }, null
                    )
                dialogFragment.show(parentFragmentManager, DIALOG_FRAGMENT_TAG)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        homeViewModel.savedInstanceState()
        super.onSaveInstanceState(outState)
    }

    private fun initOnClick() {
        binding.tvDT.setOnClickListener {
            homeViewModel.changeCurrentUTC()
        }
        binding.bodyContainerWeatherSys.setOnClickListener {
            homeViewModel.changeCurrentUTC()
        }
        binding.cardViewName.setOnClickListener {
            val isVisible = binding.bodyContainerName.visibility == View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.containerName)
            binding.bodyContainerName.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
        binding.cardViewBase.setOnClickListener {
            val isVisible = binding.bodyContainerBase.visibility == View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.containerBase)
            binding.bodyContainerBase.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
        binding.cardViewCoord.setOnClickListener {
            val isVisible = binding.bodyContainerCoord.visibility == View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.containerCoord)
            binding.bodyContainerCoord.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
        binding.cardViewWeather.setOnClickListener {
            val isVisible = binding.bodyContainerWeather.visibility == View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.containerWeather)
            binding.bodyContainerWeather.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

    }
}

const val DIALOG_FRAGMENT_TAG = "DIALOG_FRAGMENT_TAG"