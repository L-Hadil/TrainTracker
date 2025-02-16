package com.example.traintracker

data class TrainSchedule(
    val departure: String,
    val arrival: String,
    val date: String,
    val time: String,
    val trainName: String,
    val price: String
)