package tests.android.ocr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import tests.android.ocr.database.model.Params

/**
 * DAO para manutenção dos parâmetros de treinamento da rede neural.
 */
@Dao
interface ParamsDao {


    /**
     * Gravar os parâmetros de treimento da rede neural.
     *
     * @param params parâmetros de treinamento da rede neural.
     */
    @Insert
    suspend fun insertParams(params: Params)


    /**
     * Atualizar os parâmetros de treinamento da rede neural.
     *
     * @param params parâmetros de treinamento da rede neural.
     */
    @Update
    suspend fun updateParams(params: Params)


    /**
     * Obter os parâmetros de treinamento da rede neural.
     *
     * @return treinamento da rede neural, ou null, caso o registro não exista.
     */
    @Query("SELECT * FROM params WHERE id = (SELECT max(id) FROM params)")
    suspend fun getParams(): Params?


    /**
     * Obter o número de registros da tabela.
     *
     * @return número de registros da tabela.
     */
    @Query("SELECT count(*) FROM params")
    suspend fun count(): Int


    /**
     * Criar o registro no banco de dados, caso ele ainda não exista.
     */
    @Transaction
    suspend fun createIfNotExists() {

        if (count() <= 0) {

            val params = Params(
                1,      // Identificador será sempre 1.
                3600,   // Imagem com 60x60 pixels.
                10,     // 10 neurônios na camada de saída. Permite 2^10 padrões (1024 padrões).
                0.01f,  // Taxa de aprendizado.
                120     // Número máximo de interações se a rede não convergir.
            )

            insertParams(params)

        }

    }


    /**
     * Definir o número de entradas. Como a captura é uma imagem quadrada, use x^2, onde x é o número
     * de pixels na horizontal e na vertical da miniatura.
     *
     * @param inputSize número de entradas.
     */
    @Transaction
    suspend fun setInputSize(inputSize: Int) {

        createIfNotExists()

        val params = getParams()

        if (params != null) {
            params.inputSize = inputSize
            updateParams(params)
        }

    }


    /**
     * Definir o número de neurônios de saída. O número de saídas depende dos símbolos a serem
     * reconhecidos.
     *
     * @param outputSize número de neurônios de saída.
     */
    @Transaction
    suspend fun setOutputSize(outputSize: Int) {

        createIfNotExists()

        val params = getParams()

        if (params != null) {
            params.outputSize = outputSize
            updateParams(params)
        }

    }


    /**
     * Definir a taxa de aprendizado.
     *
     * @param learningRate taxa de aprendizado.
     */
    @Transaction
    suspend fun setLearningRate(learningRate: Float) {

        createIfNotExists()

        val params = getParams()

        if (params != null) {
            params.learningRate = learningRate
            updateParams(params)
        }

    }


    /**
     * Definir o número máximo de épocas para o treinamento.
     *
     * @param epochs número máximo de épocas para o treinamento.
     */
    @Transaction
    suspend fun setEpochs(epochs: Int) {

        createIfNotExists()

        val params = getParams()

        if (params != null) {
            params.epochs = epochs
            updateParams(params)
        }

    }


    /**
     * Obter o número de entradas.
     *
     * @return número de entradas.
     */
    @Transaction
    suspend fun getInputSize(): Int {
        createIfNotExists()
        return getParams()!!.inputSize
    }


    /**
     * Obter o número de neurônios de saída.
     *
     * @return número de neurônios de saída.
     */
    @Transaction
    suspend fun getOutputSize(): Int {
        createIfNotExists()
        return getParams()!!.outputSize
    }


    /**
     * Obter a taxa de aprendizado.
     *
     * @return taxa de aprendizado.
     */
    @Transaction
    suspend fun getLearningRate(): Float {
        createIfNotExists()
        return getParams()!!.learningRate
    }


    /**
     * Obter o número máximo de épocas para o aprendizado.
     *
     * @return número máximo de épocas para o aprendizado.
     */
    @Transaction
    suspend fun getEpochs(): Int {
        createIfNotExists()
        return getParams()!!.epochs
    }


}