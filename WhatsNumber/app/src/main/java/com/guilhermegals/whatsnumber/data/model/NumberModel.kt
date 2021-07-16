package com.guilhermegals.whatsnumber.data.model

data class NumberModel(
    val value : Int = 0,
    val status : NumberStatus = NumberStatus.Unknown
)