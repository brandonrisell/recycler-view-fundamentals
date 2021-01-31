package com.example.android.trackmysleepquality.sleepdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepDetailBinding


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SleepDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SleepDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SleepDetailFragment : Fragment(R.layout.fragment_sleep_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get a reference to the binding object and inflate the fragment views.
        val viewBinding = FragmentSleepDetailBinding.bind(view)

        val application = requireNotNull(this.activity).application
        val arguments = SleepDetailFragmentArgs.fromBundle(savedInstanceState ?: Bundle())

        // Create an instance of the ViewModel Factory.
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepDetailViewModelFactory(arguments.sleepNightKey, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val viewModel =
            ViewModelProvider(
                this, viewModelFactory).get(SleepDetailViewModel::class.java)

        // Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        viewModel.navigateToSleepTracker.observe(viewLifecycleOwner) {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    SleepDetailFragmentDirections.actionSleepDetailFragmentToSleepTrackerFragment())
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                viewModel.doneNavigating()
            }
        }

        viewModel.getNight().observe(viewLifecycleOwner) {

            viewBinding.qualityImage.setImageResource(when (it.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })

            viewBinding.qualityString.text = convertNumericQualityToString(it.sleepQuality, resources)

            viewBinding.sleepLength.text = convertDurationToFormatted(it.startTimeMilli, it.endTimeMilli, resources)
        }

        viewBinding.closeButton.setOnClickListener { viewModel.onClose() }
    }
}