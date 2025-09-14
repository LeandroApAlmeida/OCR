package tests.android.ocr.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tests.android.ocr.android.OcrApplication
import tests.android.ocr.database.dao.LearningDao
import tests.android.ocr.database.dao.OutputDao
import tests.android.ocr.database.dao.ParamsDao
import tests.android.ocr.database.dao.SampleDao
import tests.android.ocr.database.model.Learning
import tests.android.ocr.database.model.Output
import tests.android.ocr.database.model.Params
import tests.android.ocr.database.model.Sample
import java.io.File

/**
 * Classe para acesso ao banco de dados SQLite via framework ROOM.
 */
@Database (

    entities = [
        Learning::class,
        Output::class,
        Sample::class,
        Params::class
    ],

    version = 1

)
abstract class AppDatabase: RoomDatabase() {


    /**Obter o Data Access Object (DAO) para a entidade Learning.*/
    abstract fun getLearningDao(): LearningDao

    /**Obter o Data Access Object (DAO) para a entidade Output.*/
    abstract fun getOutputDao(): OutputDao

    /**Obter o Data Access Object (DAO) para a entidade Sample.*/
    abstract fun getSampleDao(): SampleDao

    /**Obter o Data Access Object (DAO) para a entidade Params.*/
    abstract fun getParamsDao(): ParamsDao


    companion object {


        /**Nome do banco de dados.*/
        private const val DATABASE_NAME = "ocr_db"


        /**Instância do banco de dados.*/
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**Instância do banco de dados.*/
        val instance: AppDatabase get() {

            return synchronized(this) {

                if (INSTANCE == null || !isDatabaseOpen(INSTANCE)) {

                    val instance = Room.databaseBuilder(
                        OcrApplication.getInstance(),
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).build()

                    INSTANCE = instance

                }

                INSTANCE!!

            }

        }


        /**
         * Verifica se o banco de dados está aberto.
         * @param db instância do banco de dados.
         * @return true, se o banco de dados está aberto, false, se não está.
         */
        private fun isDatabaseOpen(db: AppDatabase?): Boolean {

            return try {
                db?.openHelper?.writableDatabase?.isOpen == true
            } catch (e: Exception) {
                false
            }

        }


    }


}