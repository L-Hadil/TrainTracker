package com.example.traintracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrainAdapter(
    private val trains: List<TrainSchedule>,
    private val onClick: (TrainSchedule) -> Unit
) : RecyclerView.Adapter<TrainAdapter.TrainViewHolder>() {

    class TrainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvTrainName: TextView = view.findViewById(R.id.tvTrainName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_train, parent, false)
        return TrainViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        val train = trains[position]
        holder.tvTime.text = train.time
        holder.tvTrainName.text = train.trainName
        holder.tvPrice.text = train.price

        holder.itemView.setOnClickListener { onClick(train) }
    }

    override fun getItemCount() = trains.size
}
