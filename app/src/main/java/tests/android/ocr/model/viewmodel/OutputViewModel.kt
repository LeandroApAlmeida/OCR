package tests.android.ocr.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import tests.android.ocr.database.model.Output
import tests.android.ocr.model.repository.OutputRepository
import javax.inject.Inject

/**
 * ViewModel para manutenção de saídas da rede neural.
 */
@HiltViewModel
class OutputViewModel @Inject constructor(

    /**Repositório para manutenção de saídas da rede neural.*/
    private val outputRepository: OutputRepository

): ViewModel() {


    /**
     * Inserir uma nova saída da rede neural.
     *
     * @param output saída da rede neural a ser inserida.
     *
     * @param listener ouvinte da corrotina.
     */
    fun insert(output: Output, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            outputRepository.insert(output)
            result.postValue(true)
        }

        return result

    }


    /**
     * Atualizar uma saída da rede neural.
     *
     * @param output saída da rede neural a ser atualizada.
     *
     * @param listener ouvinte da corrotina.
     */
    fun update(output: Output, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            outputRepository.update(output)
            result.postValue(true)
        }

        return result

    }


    /**
     * Excluir uma saída da rede neural.
     *
     * @param output saída da rede neural a ser excluída.
     *
     * @param listener ouvinte da corrotina.
     */
    fun delete(output: Output, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            outputRepository.delete(output)
            result.postValue(true)
        }

        return result

    }


    /**
     * Excluir uma lista de saídas da rede neural.
     *
     * @param outputList lista de saídas a serem excluídas.
     *
     * @param listener ouvinte da corrotina.
     */
    fun deleteAll(outputList: List<Output>, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            outputRepository.deleteAll(outputList)
            result.postValue(true)
        }

        return result

    }


    /**
     * Obter todas as saídas da rede neural.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return lista com todas as saídas da rede neural.
     */
    fun getAll(listener: CoroutineListener): LiveData<List<Output>?> {

        val result = MutableLiveData<List<Output>?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(outputRepository.getAll().await())
        }

        return result

    }


    /**
     * Obter a saída da rede neural pelo identificador chave primária.
     *
     * @param idOutput identificador chave primária da saída da rede neural.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return saída da rede neural relacionada, ou null, caso não seja encontrada.
     */
    fun getById(idOutput: Long, listener: CoroutineListener): LiveData<Output?> {

        val result = MutableLiveData<Output?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(outputRepository.getById(idOutput).await())
        }

        return result

    }


}