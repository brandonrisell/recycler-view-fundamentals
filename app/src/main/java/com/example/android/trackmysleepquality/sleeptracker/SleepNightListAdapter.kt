package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding

class SleepNightListAdapter(private val onClick: (nightId: Long) -> Unit): ListAdapter<SleepNight, ViewHolder>(SleepNightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ViewHolder private constructor(itemView: View, private val onClick: (nightId: Long) -> Unit) : RecyclerView.ViewHolder(itemView){

    fun bind(item: SleepNight) {
        val viewBinding = ListItemSleepNightBinding.bind(itemView)

        val sleepLength = viewBinding.sleepLength
        val quality = viewBinding.qualityString
        val qualityImage = viewBinding.qualityImage

        val resources = itemView.context.resources
        sleepLength.text = convertDurationToFormatted(
            item.startTimeMilli, item.endTimeMilli, resources
        )
        quality.text = convertNumericQualityToString(
            item.sleepQuality, resources
        )
        qualityImage.setImageResource(
            when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            }
        )
        itemView.setOnClickListener { onClick(item.nightId) }
    }

    companion object {
        fun from(parent: ViewGroup, onClick: (nightId: Long) -> Unit): ViewHolder {
            val layoutInflater =
                LayoutInflater.from(parent.context)
            val view = layoutInflater
                .inflate(
                    R.layout.list_item_sleep_night,
                    parent, false)
            return ViewHolder(view, onClick)
        }
    }
}

class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}
