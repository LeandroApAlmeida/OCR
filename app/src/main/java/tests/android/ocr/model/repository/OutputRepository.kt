package tests.android.ocr.model.repository

import kotlinx.coroutines.Deferred
import tests.android.ocr.database.model.Output

/**
 * Repositório para manutenção de padrões de saída da rede neural.
 */
interface OutputRepository {

    /**
     * Inserir um novo padrão de saída da rede neural.
     *
     * @param output padrão de saída da rede neural a ser inserido.
     */
    suspend fun insertOutput(output: Output)

    /**
     * Atualizar um padrão de saída da rede neural.
     *
     * @param output padrão de saída da rede neural a ser atualizado.
     */
    suspend fun updateOutput(output: Output)

    /**
     * Excluir um padrão de saída da rede neural.
     *
     * @param output padrão de saída da rede neural a ser excluído.
     */
    suspend fun deleteOutput(output: Output)

    /**
     * Excluir uma lista de padrões de saída da rede neural.
     *
     * @param outputList lista de padrões de saída a serem excluídos.
     */
    suspend fun deleteAllOutputs(outputList: List<Output>)

    /**
     * Obter todos os padrões de saída da rede neural.
     *
     * @return lista com todos os padrões de saída da rede neural.
     */
    suspend fun getAllOutputs(): Deferred<List<Output>?>

    /**
     * Obter o padrão de saída da rede neural pelo identificador chave primária.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @return padrão de saída da rede neural relacionado, ou null, caso não seja encontrado.
     */
    suspend fun getOutputById(idOutput: Long): Deferred<Output?>

}