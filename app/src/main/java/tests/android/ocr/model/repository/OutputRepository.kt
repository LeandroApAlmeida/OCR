package tests.android.ocr.model.repository

import kotlinx.coroutines.Deferred
import tests.android.ocr.database.model.Output

/**
 * Repositório para manutenção de saídas da rede neural.
 */
interface OutputRepository {

    /**
     * Inserir uma nova saída da rede neural.
     *
     * @param output saída da rede neural a ser inserida.
     */
    suspend fun insertOutput(output: Output)

    /**
     * Atualizar uma saída da rede neural.
     *
     * @param output saída da rede neural a ser atualizada.
     */
    suspend fun updateOutput(output: Output)

    /**
     * Excluir uma saída da rede neural.
     *
     * @param output saída da rede neural a ser excluída.
     */
    suspend fun deleteOutput(output: Output)

    /**
     * Excluir uma lista de saídas da rede neural.
     *
     * @param outputList lista de saídas a serem excluídas.
     */
    suspend fun deleteAllOutputs(outputList: List<Output>)

    /**
     * Obter todas saídas da rede neural.
     *
     * @return lista com todas as saídas da rede neural.
     */
    suspend fun getAllOutputs(): Deferred<List<Output>?>

    /**
     * Obter a saída da rede neural pelo identificador chave primária.
     *
     * @param idOutput identificador chave primária dq saída da rede neural.
     *
     * @return saída da rede neural relacionada, ou null, caso não seja encontradq.
     */
    suspend fun getOutputById(idOutput: Long): Deferred<Output?>

}