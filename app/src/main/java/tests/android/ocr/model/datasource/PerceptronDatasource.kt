package tests.android.ocr.model.datasource

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import tests.android.ocr.database.dao.LearningDao
import tests.android.ocr.database.dao.OutputDao
import tests.android.ocr.database.dao.ParamsDao
import tests.android.ocr.database.dao.SampleDao
import tests.android.ocr.database.model.Learning
import tests.android.ocr.database.model.Output
import tests.android.ocr.database.model.Sample
import tests.android.ocr.image.ImageBuilder
import tests.android.ocr.model.repository.PerceptronRepository
import tests.android.ocr.perceptron.Perceptron
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.inject.Inject

/**
 * Datasource para execução do perceptron.
 */
class PerceptronDatasource @Inject constructor(

    /**DAO para manutenção do aprendizado da rede neural artificial.*/
    private val learningDao: LearningDao,

    /**DAO para manutenção de padrões de saída da rede neural.*/
    private val outputDao: OutputDao,

    /**DAO para manutenção das amostras no banco de dados.*/
    private val sampleDao: SampleDao,

    /**DAO para manutenção dos parâmetros de treinamento da rede neural.*/
    private val paramsDao: ParamsDao

): PerceptronRepository {


    /**Instância de [Perceptron].*/
    private var perceptron: Perceptron? = null

    /**Instância de [ImageBuilder].*/
    private val imageBuilder: ImageBuilder = ImageBuilder()


    override suspend fun calculateOutput(bitmap: Bitmap) = withContext(Dispatchers.IO) {

        async {

            // Carrega o perceptron, caso não tenha sido carregado anteriormente.

            if (perceptron == null) {

                // Obtém o treinamento da rede neural a partir do banco de dados.
                val learning = learningDao.getLearning()

                if (learning != null) {

                    // O objeto do Perceptron estará salvo na classe treinamento, não apenas os
                    // pesos. Este objeto, portanto, já tem os pesos e bias atualizados.

                    perceptron = deserializePerceptron(learning.data!!)

                } else {

                    // Lança uma exceção caso a rede neural não esteja treinada.

                    throw Exception("Rede neural não treinada.")

                }

            }

            // Converte o bitmap em um vetor float de entradas, linha por linha.
            val input = imageBuilder.bitmapToFloatArray(bitmap)

            // Calcula as saídas, de acordo com o vetor de entradas e o treinamento do Perceptron.
            val result = perceptron!!.calculate(input.toFloatArray())

            // Obtém todas as saídas para identificar o que corresponde às saídas calculadas.
            val outputs = outputDao.getAllOutputs()

            // Padrão de entrada identificado.
            var output: Output? = null

            if (outputs != null) {

                for (out in outputs) {

                    // Vetor das saídas-alvo do padrão listado.
                    val targetData = decodeTargetString(out.target)

                    // Compara se as saídas-alvo são as mesmas do padrão retornado.
                    if (targetData.contentEquals(result.toTypedArray())) {
                        output = out
                        break
                    }

                }

            }

            // Retorna a saída-alvo identificada, ou null, caso não tenha correspondente.
            output

        }

    }


    override suspend fun trainPerceptron() = withContext(Dispatchers.IO) {

        async {

            // Obtém a taxa de aprendizado usada para ajustar os pesos.
            val learningRate = paramsDao.getLearningRate()

            // Obtém o número máximo de épocas para o treinamento.
            val epochs = paramsDao.getEpochs()

            // Obtém o número de entradas do padrão.
            val inputSize = paramsDao.getInputSize()

            // Obtém o número de neurônios de saída.
            val outputSize = paramsDao.getOutputSize()

            // Arranjo de entradas das amostras. Cada entrada representa um pixel da amostra.
            val samples = mutableListOf<FloatArray>()

            // Arranjo de saídas-alvo, uma para cada respectiva amostra. Por exemplo:
            //
            //     samples[0] -> targets[0]
            //
            // Significa amostra na posição 0 e suas respectivas saídas-alvo na mesma posição do
            // arranjo de saídas-alvo.
            val targets = mutableListOf<FloatArray>()

            // Obtém todas as saídas da rede neural, que correspondem às classes que serão
            // reconhecidas pela mesma.
            val outputs = outputDao.getAllOutputs()

            // Loop para recuperar as amostras de acordo com cada saída.

            outputs?.forEach { output: Output ->

                // Obtém todas as amostras relacionadas com a saída.
                val samplesByOutput = sampleDao.getAllSamples(output.id)

                // Loop para converter cada amostra em seu respectivo vetor de entradas.

                samplesByOutput?.forEach { sample: Sample ->

                    // Recupera o bitmap da amostra.
                    val bitmap = imageBuilder.imageStreamToBitmap(sample.data!!)

                    // Converte o bitmap da amostra em seu respectivo vetor de entradas.
                    val floatArray = imageBuilder.bitmapToFloatArray(bitmap)

                    // Obtém as saídas-alvo para a amostra.
                    val target = output.target

                    // Valida se o padrão tem o tamanho correto configurado.
                    if (target.length != paramsDao.getOutputSize()) {
                        throw Exception("Saídas alvo: " + output.target + " incorretas.")
                    }

                    // Adiciona o vetor de entradas da amostra ao respectivo arranjo.
                    samples.add(floatArray.toFloatArray())

                    // Adiciona o vetor de saídas-alvo ao respectivo arranjo.
                    targets.add(decodeTargetString(target).toFloatArray())

                    // Ao adicionar as entradas e saídas-alvo, ambos ficarão na mesma posição
                    // em seus respectivos arranjos. Isso é crucial para o treinamento da rede
                    // neural.

                }

            }

            // Instancia o objeto de Perceptron.
            perceptron = Perceptron(inputSize, outputSize)

            // Realiza o treinamento da rede neural de acordo com os Parâmetros.
            val iterations = perceptron!!.train(
                samples.toTypedArray(),
                targets.toTypedArray(),
                epochs,
                learningRate
            )

            // Atualiza o treinamento da rede neural no banco de dados. Ao invéz de gravar apenas
            // os pesos ajustados, grava o objeto de Perceptron na tabela do banco de dados.
            learningDao.setLearning( Learning(1, serializePerceptron(perceptron!!)) )

            // Retorna o número de épocas até a convergência da rede neural.
            iterations

        }

    }


    /**
     * Transforma a String que representa as saídas-alvo em seu respectivo vetor de Float.
     *
     * @param target String que representa as saídas-alvo.
     *
     * @return vetor de Float relacionado.
     */
    private fun decodeTargetString(target: String): Array<Float> {

        val outArray: Array<Float> = Array(target.length) { 0.0f }

        for (i in target.indices) {
            outArray[i] = if (target[i] == '1') 1.0f else 0.0f
        }

        return outArray

    }


    /**
     * Serializa o objeto de [Perceptron].
     *
     * @param perceptron objeto de [Perceptron].
     *
     * @return representação do objeto [Perceptron] em ByteArray.
     */
    private fun serializePerceptron(perceptron: Perceptron): ByteArray {

        val byteArrayOutputStream = ByteArrayOutputStream()

        ObjectOutputStream(byteArrayOutputStream).use { it.writeObject(perceptron) }

        return byteArrayOutputStream.toByteArray()

    }


    /**
     * Desserializa o objeto de [Perceptron].
     *
     * @param data representação do objeto [Perceptron] em ByteArray.
     *
     * @return objeto de [Perceptron].
     */
    private fun deserializePerceptron(data: ByteArray): Perceptron {

        val byteArrayInputStream = ByteArrayInputStream(data)

        return ObjectInputStream(byteArrayInputStream).use { it.readObject() as Perceptron }

    }


}