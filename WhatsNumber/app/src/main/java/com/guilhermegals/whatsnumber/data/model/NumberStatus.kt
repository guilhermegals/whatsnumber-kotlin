package com.guilhermegals.whatsnumber.data.model

sealed class NumberStatus {
    object Unknown : NumberStatus()
    object NoStatus : NumberStatus()
    object Error : NumberStatus()
    object GreaterThan : NumberStatus()
    object LessThan : NumberStatus()
    object Win : NumberStatus()
}