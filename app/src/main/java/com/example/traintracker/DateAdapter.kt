package com.example.traintracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DateAdapter(
    private val dates: List<DateItem>,
    private val onDateSelected: (DateItem) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = 0

    data class DateItem(
        val dayName: String,
        val dayNumber: String,
        val fullDate: String,
        var isSelected: Boolean = false
    )

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayName: TextView = view.findViewById(R.id.tvDayName)
        val tvDayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val container: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val dateItem = dates[position]
        holder.tvDayName.text = dateItem.dayName
        holder.tvDayNumber.text = dateItem.dayNumber

        // Mise à jour de l'apparence selon la sélection
        if (position == selectedPosition) {
            // Par exemple, fond bleu pour la sélection
            holder.tvDayNumber.setBackgroundResource(R.drawable.circle_background)
            holder.tvDayNumber.setTextColor(Color.WHITE)
        } else {
            holder.tvDayNumber.background = null
            holder.tvDayNumber.setTextColor(Color.WHITE)
        }

        holder.container.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
            onDateSelected(dateItem)
        }
    }

    override fun getItemCount() = dates.size
}
