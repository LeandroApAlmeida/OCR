package tests.android.ocr.model.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import tests.android.ocr.database.dao.ParamsDao
import tests.android.ocr.model.repository.ParamsRepository
import javax.inject.Inject

/**
 * Datasource para manutenção dos parâmetros de treinamento da rede neural.
 */
class ParamsDatasource @Inject constructor (

    /**DAO para manutenção dos parâmetros de treinamento da rede neural.*/
    private val paramsDao: ParamsDao

): ParamsRepository {


    override suspend fun setInputSize(inputSize: Int): Unit = withContext(Dispatchers.IO) {

        paramsDao.setInputSize(inputSize)

    }


    override suspend fun setOutputSize(outputSize: Int): Unit = withContext(Dispatchers.IO) {

        paramsDao.setOutputSize(outputSize)

    }


    override suspend fun setLearningRate(learningRate: Float): Unit = withContext(Dispatchers.IO) {

        paramsDao.setLearningRate(learningRate)

    }


    override suspend fun setEpochs(epochs: Int): Unit = withContext(Dispatchers.IO) {

        paramsDao.setEpochs(epochs)

    }


    override suspend fun getInputSize() = withContext(Dispatchers.IO) {

        async { paramsDao.getInputSize() }

    }


    override suspend fun getOutputSize() = withContext(Dispatchers.IO) {

        async { paramsDao.getOutputSize() }

    }


    override suspend fun getLearningRate() = withContext(Dispatchers.IO) {

        async { paramsDao.getLearningRate() }

    }


    override suspend fun getEpochs() = withContext(Dispatchers.IO) {

        async { paramsDao.getEpochs() }

    }


}