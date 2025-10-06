package tests.android.ocr.model.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import tests.android.ocr.database.dao.SampleDao
import tests.android.ocr.database.model.Sample
import tests.android.ocr.model.repository.SampleRepository
import javax.inject.Inject

/**Datasource para manutenção das amostras no banco de dados.*/
class SampleDatasource @Inject constructor(

    /**DAO para manutenção das amostras no banco de dados.*/
    private val sampleDao: SampleDao

): SampleRepository {


    override suspend fun insert(sample: Sample): Unit = withContext(Dispatchers.IO) {

        sampleDao.insert(sample)

    }


    override suspend fun update(sample: Sample): Unit = withContext(Dispatchers.IO) {

        sampleDao.update(sample)

    }


    override suspend fun delete(sample: Sample): Unit = withContext(Dispatchers.IO) {

        sampleDao.delete(sample)

    }


    override suspend fun deleteAll(samplesList: List<Sample>) {

        samplesList.forEach { sample ->
            delete(sample)
        }

    }


    override suspend fun getAll(idOutput: Long, lite: Boolean) = withContext(Dispatchers.IO) {

        async {
            if (lite) {
                sampleDao.getAllLite(idOutput)
            } else {
                sampleDao.getAll(idOutput)
            }
        }

    }


    override suspend fun getById(idSample: Long) = withContext(Dispatchers.IO) {

        async {
            sampleDao.getById(idSample)
        }

    }


    override suspend fun getDataById(idSample: Long) = withContext(Dispatchers.IO) {

        async {
            sampleDao.getDataById(idSample)
        }

    }


    override suspend fun getTotal() = withContext(Dispatchers.IO) {

        async {
            sampleDao.getTotal()
        }

    }


    override suspend fun getTotalByOutput(idOutput: Long) = withContext(Dispatchers.IO) {

        async {
            sampleDao.getTotalByOutput(idOutput)
        }

    }


}