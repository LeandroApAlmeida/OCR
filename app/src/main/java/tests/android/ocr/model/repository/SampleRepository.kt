package tests.android.ocr.model.repository

import kotlinx.coroutines.Deferred
import tests.android.ocr.database.model.Sample

/**
 * Repositório para manutenção das amostras no banco de dados.
 */
interface SampleRepository {

    /**
     * Inserir uma nova amostra.
     *
     * @param sample amostra a ser inserida.
     */
    suspend fun insert(sample: Sample)

    /**
     * Atualizar uma amostra.
     *
     * @param sample amostra a ser atualizada.
     */
    suspend fun update(sample: Sample)

    /**
     * Excluir uma amostra.
     *
     * @param sample amostra a ser excluída.
     */
    suspend fun delete(sample: Sample)

    /**
     * Excluir uma lista de amostras.
     *
     * @param list lista de amostras a serem excluídas.
     */
    suspend fun deleteAll(samplesList: List<Sample>)

    /**
     * Obter todas as amostras de acordo com o padrão de saída da rede neural.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @param lite se true, não carrega os dados da amostra, se false, carrega os dados da amostra.
     *
     * @return lista com as amostras associadas com o padrão de saída da rede neural.
     */
    suspend fun getAll(idOutput: Long, lite: Boolean): Deferred<List<Sample>?>

    /**
     * Obter uma amostra pelo seu identificador chave primária.
     *
     * @param idSample identificador chave primária da amostra.
     *
     * @return amostra relacionada, ou null, caso a amostra não seja encontrada.
     */
    suspend fun getById(idSample: Long): Deferred<Sample?>

    /**
     * Obter os dados da amostra identificada pelo identificador chave primária.
     *
     * @param idSample identificador chave primária da amostra.
     *
     * @return dados da amostra relacionada, ou null, caso a amostra não seja encontrada.
     */
    suspend fun getDataById(idSample: Long): Deferred<ByteArray?>

    /**
     * Obter o número total de amostras no banco de dados.
     *
     * @return número total de amostras no banco de dados.
     */
    suspend fun getTotal(): Deferred<Int>

    /**
     * Obter o número total de amostras por padrão de saída da rede neural.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @return número total de amostras por padrão de saída da rede neural.
     */
    suspend fun getTotalByOutput(idOutput: Long): Deferred<Int>

}