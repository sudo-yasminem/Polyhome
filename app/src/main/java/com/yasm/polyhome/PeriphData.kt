package com.yasm.polyhome

data class PeriphData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int
)
