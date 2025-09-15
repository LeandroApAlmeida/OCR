package tests.android.ocr.model.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import tests.android.ocr.database.dao.OutputDao
import tests.android.ocr.database.dao.ParamsDao
import tests.android.ocr.database.model.Output
import tests.android.ocr.model.repository.OutputRepository
import javax.inject.Inject

/**
 * Datasource para manutenção das saídas da rede neural.
 */
class OutputDatasource @Inject constructor(

    /**DAO para manutenção das saídas da rede neural.*/
    private val outputDao: OutputDao,

    /**DAO para manutenção dos parâmetros de treinamento da rede neural.*/
    private val paramsDao: ParamsDao

): OutputRepository {


    /**
     * Valida o formato das saídas alvo digitado. Uma saída-alvo deve ter o seguinte formato:
     *
     * dddd
     *
     * Onde:
     *
     * d: Dígito 0 ou 1, representando saída desativada ou saída ativada, respectivamente.
     *
     */
    private suspend fun validateTarget(output: Output) {

        val outputSize = paramsDao.getOutputSize()

        val outputs = outputDao.getAllOutputs()

        var containsInvalidChars = false

        for (i in 0 until  output.target.length) {
            if (output.target[i] != '0' && output.target[i] != '1') {
                containsInvalidChars = true
                break
            }
        }

        // Valida se as saídas alvo têm caracteres inválidos.

        if (containsInvalidChars) {
            throw Exception("Formato inválido. Use 1 para saída ativada e 0 para saída desativada.")
        }

        // Valida se o número de saídas alvo é compatível com o que está configurado.

        if (output.target.length != outputSize) {
            throw Exception("O número de saídas alvo está incorreto (correto: ${outputSize})")
        }

        // Valida se as saídas são todas zero, que é um padrão inválido.

        var containsOnlyZero = true

        for (i in 0 until  output.target.length) {
            if (output.target[i] != '0') {
                containsOnlyZero = false
                break
            }
        }

        if (containsOnlyZero) {
            throw Exception("As saídas alvos não podem conter apenas zeros.")
        }

        // Verifica se as saídas-alvo já estão sendo utilizadas em outra saída.

        var isUnique = true

        for (out in outputs!!) {
            if (out.target == output.target) {
                isUnique = false
                break
            }
        }

        if (!isUnique) {
            throw Exception("A saída-alvo está alocada a outro símbolo.")
        }

    }


    override suspend fun insertOutput(output: Output): Unit = withContext(Dispatchers.IO) {

        validateTarget(output)

        outputDao.insertOutput(output)

    }


    override suspend fun updateOutput(output: Output): Unit = withContext(Dispatchers.IO) {

        validateTarget(output)

        outputDao.updateOutput(output)

    }


    override suspend fun deleteOutput(output: Output): Unit = withContext(Dispatchers.IO) {

        outputDao.deleteOutput(output)

    }


    override suspend fun deleteAllOutputs(outputList: List<Output>): Unit = withContext(Dispatchers.IO) {

        outputList.forEach { output ->
            deleteOutput(output)
        }

    }


    override suspend fun getAllOutputs() = withContext(Dispatchers.IO) {

        async {
            outputDao.getAllOutputs()
        }

    }


    override suspend fun getOutputById(idOutput: Long) = withContext(Dispatchers.IO) {

        async {
            outputDao.getOutputById(idOutput)
        }

    }


}