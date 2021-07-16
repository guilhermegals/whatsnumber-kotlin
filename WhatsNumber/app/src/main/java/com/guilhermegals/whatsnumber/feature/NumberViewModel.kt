package com.guilhermegals.whatsnumber.feature

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilhermegals.whatsnumber.R
import com.guilhermegals.whatsnumber.core.di.DispatcherModule
import com.guilhermegals.whatsnumber.data.model.NumberStatus
import com.guilhermegals.whatsnumber.data.repository.contract.NumberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NumberViewModel @Inject constructor( // O Repositorio e o Dispatcher da corrotina é injetado pelo Hilt
    private val numberRepository: NumberRepository,
    @DispatcherModule.IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    companion object {
        const val TAG = "NumberViewModel"
    }

    // <editor-fold desc="[ Live Data ]">

    /** Armazena o valor do último número gerado */
    private val _currentNumber: MutableLiveData<String> = MutableLiveData()

    /** Armazena o valor do último status (Aguardando, Erro, Vitoria...) */
    private val _currentStatus: MutableLiveData<NumberStatus> = MutableLiveData()
    val currentStatus: LiveData<NumberStatus> = _currentStatus

    /** Amarzena o valor do último LED */
    private val _ledNumber: MutableLiveData<String> = MutableLiveData()
    val ledNumber: LiveData<String> = Transformations.switchMap(_currentStatus) {
        // Caso o status atual seja de erro ele observa o número atual, que consequentemente armazena o código da requisicao
        if (it == NumberStatus.Error) _currentNumber
        // Casso contrario ele observa o LED
        else _ledNumber
    }

    /** Observa se pode criar uma nova partida ou não */
    val canNewMatch: LiveData<Boolean> = Transformations.map(_currentStatus) {
        it == NumberStatus.Win || it == NumberStatus.Error || it == NumberStatus.Unknown
    }

    /** Armazena o valor do campo de edição de texto do fragmento */
    val textInputNumber = MutableLiveData<String>()

    /** Armazena o id da string de erro do campo de edição de texto */
    private val _textInputError: MutableLiveData<Int> = MutableLiveData()
    val textInputError: LiveData<Int> = _textInputError

    private var attempts = 0
    /** Observa se pode exibir o número de tentativas ou não */
    private val _showAttempts: MutableLiveData<Int> = MutableLiveData()
    val showAttempts: LiveData<Int> = Transformations.map(_currentStatus) {
        if (it != NumberStatus.Win) 0
        else attempts
    }

    // </editor-fold>

    // <editor-fold desc="[ Public Functions ]">

    /** Método responsável por incializar uma nova partida */
    fun newMatch() {
        viewModelScope.launch(dispatcher) {
            val model = numberRepository.getNumber()

            attempts = 0
            textInputNumber.postValue("")
            _ledNumber.postValue("0")
            _currentNumber.postValue(model.value.toString())
            _currentStatus.postValue(model.status)
            _textInputError.postValue(0)

            /*Log.i(TAG, "Number : ${model.value}")
            Log.i(TAG, "Status : ${model.status}")*/
        }
    }

    /**
     * Método responsável por verificar a tentativa realizada pelo usuário
     */
    fun checkAttempt() {
        _textInputError.postValue(0)
        val currentNumber = _currentNumber.value
        val textInputNumber = textInputNumber.value

        if (!currentNumber.isNullOrBlank() && !textInputNumber.isNullOrBlank()) {
            attempts++
            setStatus(currentNumber.toInt(), textInputNumber.toInt())
            _ledNumber.postValue(textInputNumber)
        } else if(textInputNumber.isNullOrBlank()) {
            _textInputError.postValue(R.string.number_fragment_input_empty_error)
        } else {
            _currentStatus.postValue(NumberStatus.Unknown)
        }
    }

    // </editor-fold>

    // <editor-fold desc="[ Private Functions ]">

    /** Método responsável por definir o status do jogo com base nos números de entrada */
    private fun setStatus(currentNumber: Int, inputNumber: Int) {
        when {
            currentNumber > inputNumber -> _currentStatus.postValue(NumberStatus.GreaterThan)
            currentNumber < inputNumber -> _currentStatus.postValue(NumberStatus.LessThan)
            currentNumber == inputNumber -> {
                _currentStatus.postValue(NumberStatus.Win)
                _showAttempts.postValue(attempts)
            }
        }
    }

    // </editor-fold>
}