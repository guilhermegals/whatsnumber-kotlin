package com.guilhermegals.whatsnumber.data.repository.implementation

import com.guilhermegals.whatsnumber.core.di.DispatcherModule
import com.guilhermegals.whatsnumber.data.api.service.NumberApiService
import com.guilhermegals.whatsnumber.data.model.NumberModel
import com.guilhermegals.whatsnumber.data.model.NumberStatus
import com.guilhermegals.whatsnumber.data.repository.contract.NumberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class NumberRepositoryImpl @Inject constructor( // O serviço da API e o Dispatcher da corrotina é injetado pelo Hilt
    private val numberApiService: NumberApiService,
    @DispatcherModule.IoDispatcher val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NumberRepository {

    // <editor-fold desc="[ Public Functions ]">

    /** Retorna um valor gerado aleatóriamente por uma API */
    override suspend fun getNumber(min: Int, max: Int): NumberModel = withContext(dispatcher) {
        try {
            // Obtem o resultado gerado pela API
            val result = numberApiService.getNumber(min, max)

            // Pega o corpo do resultado
            val resultBody = result.body()?.value

            if (result.isSuccessful && resultBody != null) {
                // Caso o resultado seja válido é retornado o valor gerado
                return@withContext NumberModel(resultBody, NumberStatus.NoStatus)
            } else {
                // Caso o resultado seja inválido é retornado o status code da requisição e um status de Erro
                return@withContext NumberModel(result.code(), NumberStatus.Error)
            }

        } catch (httpException: HttpException) {
            // Caso ocorra uma exceção Http
            return@withContext NumberModel(httpException.code(), NumberStatus.Error)
        } catch (exception: Exception) {
            // Caso ocorra uma exceção Genérica
            return@withContext NumberModel(-1, NumberStatus.Error)
        }
    }

    // </editor-fold>
}