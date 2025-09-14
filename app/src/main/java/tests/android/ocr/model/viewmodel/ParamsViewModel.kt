package tests.android.ocr.model.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import tests.android.ocr.database.model.Learning
import tests.android.ocr.model.repository.ParamsRepository
import javax.inject.Inject

/**
 * ViewModel para manutenção dos parâmetros de treinamento da rede neural.
 */
@HiltViewModel
class ParamsViewModel @Inject constructor(

    /**Repositório para manutenção dos parâmetros de treinamento da rede neural.*/
    private val paramsRepository: ParamsRepository

): ViewModel() {


    /**
     * Definir o número de entradas.
     *
     * @param inputSize número de entradas.
     */
    fun setInputSize(inputSize: Int) {

        runBlocking {
            paramsRepository.setInputSize(inputSize)
        }

    }


    /**
     * Definir o número de neurônios de saída.
     *
     * @param outputSize número de neurônios de saída.
     */
    fun setOutputSize(outputSize: Int) {

        runBlocking {
            paramsRepository.setOutputSize(outputSize)
        }

    }


    /**
     * Definir a taxa de aprendizado.
     *
     * @param learningRate taxa de aprendizado.
     */
    fun setLearningRate(learningRate: Float) {

        runBlocking {
            paramsRepository.setLearningRate(learningRate)
        }

    }


    /**
     * Definir o número máximo de épocas para o treinamento.
     *
     * @param epochs número máximo de épocas para o treinamento.
     */
    fun setEpochs(epochs: Int) {

        runBlocking {
            paramsRepository.setEpochs(epochs)
        }

    }


    /**
     * Obter o número de entradas.
     *
     * @return número de entradas.
     */
    fun getInputSize(): Int {

        val result = runBlocking { paramsRepository.getInputSize().await() }

        return result

    }


    /**
     * Obter o número de neurônios de saída.
     *
     * @return número de neurônios de saída.
     */
    fun getOutputSize(): Int {

        val result = runBlocking { paramsRepository.getOutputSize().await() }

        return result

    }


    /**
     * Obter a taxa de aprendizado.
     *
     * @return taxa de aprendizado.
     */
    fun getLearningRate(): Float {

        val result = runBlocking { paramsRepository.getLearningRate().await() }

        return result

    }


    /**
     * Obter o número máximo de épocas para o aprendizado.
     *
     * @return número máximo de épocas para o aprendizado.
     */
    fun getEpochs(): Int {

        val result = runBlocking { paramsRepository.getEpochs().await() }

        return result

    }


}