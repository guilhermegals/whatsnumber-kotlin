package com.guilhermegals.whatsnumber

import com.guilhermegals.whatsnumber.data.model.NumberModel
import com.guilhermegals.whatsnumber.data.model.NumberStatus
import com.guilhermegals.whatsnumber.data.repository.contract.NumberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class FakeNumberRepositoryImpl(
    private var number : Int? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NumberRepository {

    companion object {
        const val ERROR_CODE = 402
    }

    fun setReturnNumber(number: Int?) {
        this.number = number
    }

    override suspend fun getNumber(min: Int, max: Int): NumberModel = withContext(dispatcher) {
        if (number != null) return@withContext NumberModel(number!!, NumberStatus.NoStatus)

        return@withContext NumberModel(ERROR_CODE, NumberStatus.Error)
    }
}