package tests.android.ocr.model.repository

import kotlinx.coroutines.Deferred

/**
 * Repositório para manutenção dos parâmetros de treinamento da rede neural.
 */
interface ParamsRepository {

    /**
     * Definir o número de entradas.
     *
     * @param inputSize número de entradas.
     */
    suspend fun setInputSize(inputSize: Int)

    /**
     * Definir o número de neurônios de saída.
     *
     * @param outputSize número de neurônios de saída.
     */
    suspend fun setOutputSize(outputSize: Int)

    /**
     * Definir a taxa de aprendizado.
     *
     * @param learningRate taxa de aprendizado.
     */
    suspend fun setLearningRate(learningRate: Float)

    /**
     * Definir o número máximo de épocas para o treinamento.
     *
     * @param epochs número máximo de épocas para o treinamento.
     */
    suspend fun setEpochs(epochs: Int)

    /**
     * Obter o número de entradas.
     *
     * @return número de entradas.
     */
    suspend fun getInputSize(): Deferred<Int>

    /**
     * Obter o número de neurônios de saída.
     *
     * @return número de neurônios de saída.
     */
    suspend fun getOutputSize(): Deferred<Int>

    /**
     * Obter a taxa de aprendizado.
     *
     * @return taxa de aprendizado.
     */
    suspend fun getLearningRate(): Deferred<Float>

    /**
     * Obter o número máximo de épocas para o aprendizado.
     *
     * @return número máximo de épocas para o aprendizado.
     */
    suspend fun getEpochs(): Deferred<Int>

}