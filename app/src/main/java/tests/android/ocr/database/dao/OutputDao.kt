package tests.android.ocr.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import tests.android.ocr.database.model.Output

/**
 * DAO para manutenção das saídas da rede neural. Cada saída corresponde à informação de símbolo a
 * ser reconhecido, valores das saídas-alvo e pronúncia do padrão pelo mecanismo de sintetizador
 * de fala.
 */
@Dao
interface OutputDao {


    /**
     * Inserir uma nova saída da rede neural.
     *
     * @param output saída da rede neural a ser inserida.
     */
    @Insert
    suspend fun insertOutput(output: Output): Long


    /**
     * Atualizar uma saída da rede neural.
     *
     * @param output saída da rede neural a ser atualizada.
     */
    @Update
    suspend fun updateOutput(output: Output)


    /**
     * Excluir uma saída da rede neural.
     *
     * @param output saída da rede neural a ser excluída.
     */
    @Delete
    suspend fun deleteOutput(output: Output)


    /**
     * Excluir uma lista de saídas da rede neural.
     *
     * @param outputList lista de saídas a serem excluídas.
     */
    @Delete
    suspend fun deleteAllOutputs(outputList: List<Output>)


    /**
     * Obter todas as saídas da rede neural.
     *
     * @return lista com todas as saídas da rede neural.
     */
    @Query("SELECT * FROM output ORDER BY symbol ASC")
    suspend fun getAllOutputs(): List<Output>?


    /**
     * Obter a saída da rede neural pelo identificador chave primária.
     *
     * @param id identificador chave primária da saída da rede neural.
     */
    @Query("SELECT * FROM output WHERE id = :id")
    suspend fun getOutputById(id: Long): Output?


}