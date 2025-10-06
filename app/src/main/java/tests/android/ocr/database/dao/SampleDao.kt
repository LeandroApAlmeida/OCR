package tests.android.ocr.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import tests.android.ocr.database.model.Sample

/**
 * DAO para manutenção das amostras no banco de dados.
 */
@Dao
interface SampleDao {


    /**
     * Inserir uma nova amostra.
     *
     * @param sample amostra a ser inserida.
     */
    @Insert
    suspend fun insert(sample: Sample): Long


    /**
     * Atualizar uma amostra.
     *
     * @param sample amostra a ser atualizada.
     */
    @Update
    suspend fun update(sample: Sample)


    /**
     * Excluir uma amostra.
     *
     * @param sample amostra a ser excluída.
     */
    @Delete
    suspend fun delete(sample: Sample)


    /**
     * Excluir uma lista de amostras.
     *
     * @param list lista de amostras a serem excluídas.
     */
    @Delete
    suspend fun deleteAll(list: List<Sample>)


    /**
     * Obter todas as amostras de acordo com o padrão de saída da rede neural.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @return lista com as amostras associadas com o padrão de saída da rede neural.
     */
    @Query("SELECT * FROM sample WHERE id_output = :idOutput ORDER BY id")
    suspend fun getAll(idOutput: Long): List<Sample>?


    /**
     * Obter todas as amostras de acordo com o padrão de saída da rede neural, porém, sem carregar
     * os dados da amostra.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @return lista com as amostras associadas com o padrão de saída da rede neural.
     */
    @Query("SELECT id, id_output FROM sample WHERE id_output = :idOutput ORDER BY id")
    suspend fun getAllLite(idOutput: Long): List<Sample>?


    /**
     * Obter uma amostra pelo seu identificador chave primária.
     *
     * @param id identificador chave primária da amostra.
     *
     * @return amostra relacionada, ou null, caso a amostra não seja encontrada.
     */
    @Query("SELECT * FROM sample WHERE id = :id")
    suspend fun getById(id: Long): Sample?


    /**
     * Obter os dados da amostra identificada pelo identificador chave primária.
     *
     * @param id identificador chave primária da amostra.
     *
     * @return dados da amostra relacionada, ou null, caso a amostra não seja encontrada.
     */
    @Query("SELECT data FROM sample WHERE id = :id")
    suspend fun getDataById(id: Long): ByteArray?


    /**
     * Obter o número total de amostras no banco de dados.
     *
     * @return número total de amostras no banco de dados.
     */
    @Query("SELECT count(*) FROM sample")
    suspend fun getTotal(): Int


    /**
     * Obter o número total de amostras por padrão de saída da rede neural.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @return número total de amostras por padrão de saída da rede neural.
     */
    @Query("SELECT count(*) FROM sample WHERE id_output = :idOutput")
    suspend fun getTotalByOutput(idOutput: Long): Int


}