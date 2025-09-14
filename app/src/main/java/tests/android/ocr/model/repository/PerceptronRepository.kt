package tests.android.ocr.model.repository

import android.graphics.Bitmap
import kotlinx.coroutines.Deferred
import tests.android.ocr.database.model.Output

/**
 * Repositório para execução do perceptron.
 */
interface PerceptronRepository {

    /**
     * Reconhecer um padrão de entrada, produzindo um padrão de saída da rede neural.
     *
     * @param bitmap imagem como padrão de entrada.
     *
     * @return padrão de saída da rede neural.
     */
    suspend fun calculateOutput(bitmap: Bitmap): Deferred<Output?>

    /**
     * Treinar o perceptron com as amostras obtidas do banco de dados.
     *
     * @return número de épocas para o treinamento.
     */
    suspend fun trainPerceptron(): Deferred<Int>

}