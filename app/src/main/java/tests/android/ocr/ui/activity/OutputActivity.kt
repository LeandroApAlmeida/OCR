package tests.android.ocr.ui.activity

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tests.android.ocr.database.model.Output
import tests.android.ocr.databinding.ActivityOutputBinding
import tests.android.ocr.model.viewmodel.CoroutineListener
import tests.android.ocr.model.viewmodel.OutputViewModel
import tests.android.ocr.model.viewmodel.ParamsViewModel

/**
 * Activity para cadastro das saídas da rede neural.
 */
@AndroidEntryPoint
class OutputActivity : AppCompatActivity(), CoroutineListener {


    /**Componente para acesso aos controles da tela.*/
    private lateinit var binding: ActivityOutputBinding

    /**ViewModel para manutenção de saídas da rede neural.*/
    private val outputViewModel: OutputViewModel by viewModels()

    /**ViewModel para manutenção dos parâmetros de treinamento da rede neural.*/
    private val paramsViewModel: ParamsViewModel by viewModels()

    /**Saída a ter o cadastro alterado.*/
    private var output: Output? = null


    /**
     * O evento [onCreate] é sobrescrito para inicializar a Activity no modo correto (inserção/edição).
     *
     * @param savedInstanceState estado salvo para configurar uma nova instância.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityOutputBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.imbOutputSave.setOnClickListener { onSaveButtonClick(it) }
        binding.imbOutputCancel.setOnClickListener { onCancelButtonClick(it) }

        try {

            val outputSize = paramsViewModel.getOutputSize()

            binding.txvTargetLength.text = "Saídas-alvo: (Use 1 para saída ativada e 0 para saída desativada)"

            binding.edtOutputTarget.hint = "Exemplo: 1" + "0".repeat(outputSize - 1) + " ($outputSize saídas)"

            binding.edtOutputTarget.filters = arrayOf(InputFilter.LengthFilter(outputSize))

        } catch (_: Exception) {

            binding.txvTargetLength.text = "Saídas-alvo:"

        }

        if (intent.extras != null) {
            // Define o modo de edição.
            setOutput(intent.extras!!.getLong("id"))
        }

    }


    /**
     * Define o modo como edição.
     *
     * @param id identificador chave primária da saída a ser editada.
     */
    private fun setOutput(id: Long) {

        outputViewModel.getOutputById(id, this).observe(this) { output ->

            if (output != null) {

                this.output = output

                binding.edtOutputSymbol.setText(output.symbol)
                binding.edtOutputTarget.setText(output.target)
                binding.edtOutputPronunciation.setText(output.pronunciation)

            }

        }

    }


    /**
     * Tratador do evento de clique nos botão "Salvar".
     */
    private fun onSaveButtonClick(view: View) {

        val symbol = binding.edtOutputSymbol.text.toString()
        val target = binding.edtOutputTarget.text.toString()
        val pronunc = binding.edtOutputPronunciation.text.toString()

        var error = false

        if (isEmptyString(symbol)) {
            binding.edtOutputSymbol.error = "Símbolo a ser inserido"
            error = true
        }

        if (isEmptyString(target)) {
            binding.edtOutputTarget.error = "Saída da rede neural"
            error = true
        }

        if (isEmptyString(pronunc)) {
            binding.edtOutputPronunciation.error = "Pronúncia do símbolo"
            error = true
        }

        if (!error) {

            if (output == null) {

                val output2 = Output(
                    0,
                    symbol,
                    target,
                    pronunc
                )

                outputViewModel.insertOutput(output2, this).observe(this) {
                    finish()
                }

            } else {

                output!!.symbol = symbol
                output!!.target = target
                output!!.pronunciation = pronunc

                outputViewModel.updateOutput(output!!, this).observe(this) {
                    finish()
                }

            }

        }

    }


    /**
     * Tratador de clique no botão "Cancelar".
     */
    private fun onCancelButtonClick(view: View) {
        finish()
    }


    /**
     * Tratador de exceção lançada na corrotina.
     *
     * @param ex exceção lançada na corrotina.
     */
    override fun onCoroutineException(ex: Throwable) {
        with(AlertDialog.Builder(this)) {
            setTitle("Erro")
            setMessage(ex.message)
            setPositiveButton("OK", null)
            show()
        }
    }


}