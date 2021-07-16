package com.guilhermegals.whatsnumber.data.repository.contract

import com.guilhermegals.whatsnumber.data.model.NumberModel

interface NumberRepository {

    /** Retorna um valor gerado aleatóriamente por uma API */
    suspend fun getNumber(min : Int = 1, max : Int = 999) : NumberModel
}