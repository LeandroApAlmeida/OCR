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
import tests.android.ocr.model.repository.ParamsRepository
import javax.inject.Inject

/**
 * ViewModel para manutenção de padrões de saída da rede neural.
 */
@HiltViewModel
class OutputViewModel @Inject constructor(

    /**Repositório para manutenção de padrões de saída da rede neural.*/
    private val outputRepository: OutputRepository

): ViewModel() {


    /**
     * Inserir um novo padrão de saída da rede neural.
     *
     * @param output padrão de saída da rede neural a ser inserido.
     *
     * @param listener ouvinte da corrotina.
     */
    fun insertOutput(output: Output, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            outputRepository.insertOutput(output)
            result.postValue(true)
        }

        return result

    }


    /**
     * Atualizar um padrão de saída da rede neural.
     *
     * @param output padrão de saída da rede neural a ser atualizado.
     *
     * @param listener ouvinte da corrotina.
     */
    fun updateOutput(output: Output, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            outputRepository.updateOutput(output)
            result.postValue(true)
        }

        return result

    }


    /**
     * Excluir um padrão de saída da rede neural.
     *
     * @param output padrão de saída da rede neural a ser excluído.
     *
     * @param listener ouvinte da corrotina.
     */
    fun deleteOutput(output: Output, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            outputRepository.deleteOutput(output)
            result.postValue(true)
        }

        return result

    }


    /**
     * Excluir uma lista de padrões de saída da rede neural.
     *
     * @param outputList lista de padrões de saída a serem excluídos.
     *
     * @param listener ouvinte da corrotina.
     */
    fun deleteAllOutputs(outputList: List<Output>, listener: CoroutineListener): LiveData<Boolean> {

        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            outputRepository.deleteAllOutputs(outputList)
            result.postValue(true)
        }

        return result

    }


    /**
     * Obter todos os padrões de saída da rede neural.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return lista com todos os padrões de saída da rede neural.
     */
    fun getAllOutputs(listener: CoroutineListener): LiveData<List<Output>?> {

        val result = MutableLiveData<List<Output>?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(outputRepository.getAllOutputs().await())
        }

        return result

    }


    /**
     * Obter o padrão de saída da rede neural pelo identificador chave primária.
     *
     * @param idOutput identificador chave primária do padrão de saída da rede neural.
     *
     * @param listener ouvinte da corrotina.
     *
     * @return padrão de saída da rede neural relacionado, ou null, caso não seja encontrado.
     */
    fun getOutputById(idOutput: Long, listener: CoroutineListener): LiveData<Output?> {

        val result = MutableLiveData<Output?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }

        viewModelScope.launch(handler) {
            result.postValue(outputRepository.getOutputById(idOutput).await())
        }

        return result

    }


}