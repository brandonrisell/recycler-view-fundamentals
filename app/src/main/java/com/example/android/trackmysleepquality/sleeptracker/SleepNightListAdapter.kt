package com.example.android.trackmysleepquality.sleeptracker

import android.annotation.SuppressLint
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class SleepNightListAdapter(private val onClick: (nightId: Long) -> Unit): ListAdapter<ListItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent, onClick)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as ListItem.SleepNightData
                holder.bind(nightItem.sleepNight)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.Header -> ITEM_VIEW_TYPE_HEADER
            is ListItem.SleepNightData -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(ListItem.Header)
                else -> listOf(ListItem.Header) + list.map { ListItem.SleepNightData(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
}

class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_header, parent, false)
            return HeaderViewHolder(view)
        }
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

class SleepNightDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.id == newItem.id
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem == newItem
    }
}

sealed class ListItem {
    abstract val id: Long
    data class SleepNightData(val sleepNight: SleepNight): ListItem()      {
        override val id = sleepNight.nightId
    }

    object Header: ListItem() {
        override val id = Long.MIN_VALUE
    }
}
