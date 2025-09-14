package tests.android.ocr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import tests.android.ocr.database.model.Learning

/**
 * DAO para manutenção do aprendizado da rede neural artificial.
 */
@Dao
interface LearningDao {


    /**
     * Gravar o aprendizado da rede neural.
     *
     * @param learning aprendizado a ser gravado.
     */
    @Insert
    suspend fun insertLearning(learning: Learning)


    /**
     * Atualizar o aprendizado da rede neural.
     *
     * @param learning aprendizado a ser atualizado.
     */
    @Update
    suspend fun updateLearning(learning: Learning)


    /**
     * Atualizar o aprendizado da rede neural (se não existir, insere o registro no banco de dados).
     *
     * @param learning aprendizado da rede neural.
     */
    @Transaction
    suspend fun setLearning(learning: Learning) {

        val learning2 = getLearning()

        if (learning2 != null) {
            updateLearning(learning)
        } else {
            insertLearning(learning)
        }

    }


    /**
     * Obter o aprendizado da rede neural.
     *
     * @return aprendizado da rede neural.
     */
    @Query("SELECT * FROM learning WHERE id = (SELECT max(id) FROM learning)")
    suspend fun getLearning(): Learning?


}