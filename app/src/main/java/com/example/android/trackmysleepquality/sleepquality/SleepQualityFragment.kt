/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleepquality

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the database is updated.
 */
class SleepQualityFragment : Fragment(R.layout.fragment_sleep_quality) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get a reference to the binding object and inflate the fragment views.
        val viewBinding = FragmentSleepQualityBinding.bind(view)

        val application = requireNotNull(this.activity).application
        val arguments = SleepQualityFragmentArgs.fromBundle(requireArguments())

        // Create an instance of the ViewModel Factory.
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val viewModel =
            ViewModelProvider(
                this, viewModelFactory).get(SleepQualityViewModel::class.java)

        // Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        viewModel.navigateToSleepTracker.observe(viewLifecycleOwner) {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                viewModel.doneNavigating()
            }
        }

        viewBinding.qualityZeroImage.setOnClickListener { viewModel.onSetSleepQuality(quality = 0) }
        viewBinding.qualityOneImage.setOnClickListener { viewModel.onSetSleepQuality(quality = 1) }
        viewBinding.qualityTwoImage.setOnClickListener { viewModel.onSetSleepQuality(quality = 2) }
        viewBinding.qualityThreeImage.setOnClickListener { viewModel.onSetSleepQuality(quality = 3) }
        viewBinding.qualityFourImage.setOnClickListener { viewModel.onSetSleepQuality(quality = 4) }
        viewBinding.qualityFiveImage.setOnClickListener { viewModel.onSetSleepQuality(quality = 5) }
    }
}
