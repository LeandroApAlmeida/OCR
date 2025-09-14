package tests.android.ocr.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import tests.android.ocr.database.model.Sample
import tests.android.ocr.model.repository.SampleRepository
import javax.inject.Inject

/**
 * ViewModel para manutenção das amostras no banco de dados.
 */
@HiltViewModel
class SampleViewModel @Inject constructor(

    /**Repositório para manutenção das amostras no banco de dados.*/
    private val sampleRepository: SampleRepository

): ViewModel() {


    /**
     * Inserir uma nova amostra.
     *
     * @param sample amostra a ser inserida.
     *
     * @param listener ouvinte da corrotina.
     */
    fun insertSample(sample: Sample, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            sampleRepository.insertSample(sample)
            result.postValue(true)
        }

        return result

    }


    /**
     * Atualizar uma amostra.
     *
     * @param sample amostra a ser atualizada.
     *
     * @param listener ouvinte da corrotina.
     */
    fun updateSample(sample: Sample, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            sampleRepository.updateSample(sample)
            result.postValue(true)
        }

        return result

    }


    /**
     * Excluir uma amostra.
     *
     * @param sample amostra a ser excluída.
     *
     * @param listener ouvinte da corrotina.
     */
    fun deleteSample(sample: Sample, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            sampleRepository.deleteSample(sample)
            result.postValue(true)
        }

        return result

    }


    /**
     * Excluir uma lista de amostras.
     *
     * @param list lista de amostras a serem excluídas.
     *
     * @param listener ouvinte da corrotina.
     */
    fun deleteAllSamples(samplesList: List<Sample>, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            sampleRepository.deleteAllSamples(samplesList)
            result.postValue(true)
        }

        return result

    }


    /**
     * Obter todas as amostras de acordo com o padrão de saída da rede neural.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @param lite se true, não carrega os dados da amostra, se false, carrega os dados da amostra.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return lista com as amostras associadas com o padrão de saída da rede neural.
     */
    fun getAllSamples(idOutput: Long, lite: Boolean, listener: CoroutineListener): LiveData<List<Sample>?> {

        val result = MutableLiveData<List<Sample>?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(sampleRepository.getAllSamples(idOutput, lite).await())
        }

        return result

    }


    /**
     * Obter uma amostra pelo seu identificador chave primária.
     *
     * @param idSample identificador chave primária da amostra.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return amostra relacionada, ou null, caso a amostra não seja encontrada.
     */
    fun getSampleById(idSample: Long, listener: CoroutineListener): LiveData<Sample?> {

        val result = MutableLiveData<Sample?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(sampleRepository.getSampleById(idSample).await())
        }

        return result

    }


    /**
     * Obter os dados da amostra identificada pelo identificador chave primária.
     *
     * @param idSample identificador chave primária da amostra.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return dados da amostra relacionada, ou null, caso a amostra não seja encontrada.
     */
    fun getSampleDataById(idSample: Long, listener: CoroutineListener): LiveData<ByteArray?> {

        val result = MutableLiveData<ByteArray?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(sampleRepository.getSampleDataById(idSample).await())
        }

        return result

    }


    /**
     * Obter o número total de amostras no banco de dados.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return número total de amostras no banco de dados.
     */
    fun getTotalSamples(listener: CoroutineListener): LiveData<Int> {

        val result = MutableLiveData<Int>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(sampleRepository.getTotalSamples().await())
        }

        return result

    }


    /**
     * Obter o número total de amostras por padrão de saída da rede neural.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return número total de amostras por padrão de saída da rede neural.
     */
    fun getTotalSamplesByOutput(idOutput: Long, listener: CoroutineListener): LiveData<Int> {

        val result = MutableLiveData<Int>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(sampleRepository.getTotalSamplesByOutput(idOutput).await())
        }

        return result

    }


}