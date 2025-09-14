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


    override suspend fun insertSample(sample: Sample): Unit = withContext(Dispatchers.IO) {

        sampleDao.insertSample(sample)

    }


    override suspend fun updateSample(sample: Sample): Unit = withContext(Dispatchers.IO) {

        sampleDao.updateSample(sample)

    }


    override suspend fun deleteSample(sample: Sample): Unit = withContext(Dispatchers.IO) {

        sampleDao.deleteSample(sample)

    }


    override suspend fun deleteAllSamples(samplesList: List<Sample>) {

        samplesList.forEach { sample ->
            deleteSample(sample)
        }

    }


    override suspend fun getAllSamples(idOutput: Long, lite: Boolean) = withContext(Dispatchers.IO) {

        async {
            if (lite) {
                sampleDao.getAllSamplesLite(idOutput)
            } else {
                sampleDao.getAllSamples(idOutput)
            }
        }

    }


    override suspend fun getSampleById(idSample: Long) = withContext(Dispatchers.IO) {

        async {
            sampleDao.getSampleById(idSample)
        }

    }


    override suspend fun getSampleDataById(idSample: Long) = withContext(Dispatchers.IO) {

        async {
            sampleDao.getSampleDataById(idSample)
        }

    }


    override suspend fun getTotalSamples() = withContext(Dispatchers.IO) {

        async {
            sampleDao.getTotalSamples()
        }

    }


    override suspend fun getTotalSamplesByOutput(idOutput: Long) = withContext(Dispatchers.IO) {

        async {
            sampleDao.getTotalSamplesByOutput(idOutput)
        }

    }


}