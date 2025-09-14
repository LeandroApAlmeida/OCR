package tests.android.ocr.model.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import tests.android.ocr.database.model.Learning
import tests.android.ocr.database.model.Output
import tests.android.ocr.model.repository.PerceptronRepository
import javax.inject.Inject

/**
 * ViewModel para execução do perceptron.
 */
@HiltViewModel
class PerceptronViewModel @Inject constructor (

    /**Repositório para execução do perceptron.*/
    private val perceptronRepository: PerceptronRepository

): ViewModel() {


    /**
     * Reconhecer um padrão de entrada, produzindo um padrão de saída da rede neural.
     *
     * @param bitmap imagem como padrão de entrada.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return padrão de saída da rede neural.
     */
    fun calculateOutput(bitmap: Bitmap, listener: CoroutineListener): LiveData<Output?> {

        val result = MutableLiveData<Output?>()

        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(perceptronRepository.calculateOutput(bitmap).await())
        }

        return result

    }


    /**
     * Treinar o perceptron com as amostras obtidas do banco de dados.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return número de épocas para o treinamento.
     */
    fun trainPerceptron(listener: CoroutineListener): LiveData<Int> {

        val result = MutableLiveData<Int>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(perceptronRepository.trainPerceptron().await())
        }

        return result

    }


}