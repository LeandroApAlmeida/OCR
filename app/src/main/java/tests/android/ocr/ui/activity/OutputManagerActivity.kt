package tests.android.ocr.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tests.android.ocr.R
import tests.android.ocr.database.model.Output
import tests.android.ocr.databinding.ActivityOutputManagerBinding
import tests.android.ocr.model.viewmodel.CoroutineListener
import tests.android.ocr.model.viewmodel.OutputViewModel
import tests.android.ocr.ui.recyclerview.adapter.SelectableAdapter

/**
 * Activity para manutenção das saídas da rede neural cadastradas no banco de dados.
 */
@AndroidEntryPoint
class OutputManagerActivity : AppCompatActivity(), CoroutineListener {


    /**Componente para acesso aos controles da tela.*/
    private lateinit var binding: ActivityOutputManagerBinding

    /**ViewModel para manutenção de saídas da rede neural.*/
    private val outputViewModel: OutputViewModel by viewModels()


    /**
     * O evento [onCreate] é sobrescrito para inicializar a Activity no modo padrão.
     *
     * @param savedInstanceState estado salvo para configurar uma nova instância.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityOutputManagerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.rcvOutputManList.layoutManager = LinearLayoutManager(this)

        val customAdapter = CustomAdapter()

        customAdapter.setOnSelectItemHandler { _, _ ->

            val enable = customAdapter.selectedItems.isNotEmpty()

            binding.imbOutputManDelete.isEnabled = enable
            binding.imbOutputManDelete.alpha = if (enable) 1.0f else 0.5f
            binding.imbOutputManEdit.isEnabled = enable
            binding.imbOutputManEdit.alpha = if (enable) 1.0f else 0.5f

        }

        binding.rcvOutputManList.adapter = customAdapter

    }


    /**
     * O evento [onResume] do ciclo de vida da Activity é sobrescrito para tratar a reexibição da
     * mesma.
     */
    override fun onResume() {
        super.onResume()
        listOutputs()
    }


    /**
     * Listar as saídas cadastradas.
     */
    private fun listOutputs() {

        outputViewModel.getAllOutputs(this).observe(this) { outputList ->

            if (outputList != null) {

                val customAdapter = (binding.rcvOutputManList.adapter as CustomAdapter)

                customAdapter.data = outputList

                binding.imbOutputManDelete.isEnabled = false
                binding.imbOutputManDelete.alpha = 0.5f
                binding.imbOutputManEdit.isEnabled = false
                binding.imbOutputManEdit.alpha = 0.5f

            }

        }

    }


    /**
     * Tratador do evento de clique no botão "Adicionar saída".
     */
    fun onAddButtonClick(view: View) {
        startActivity(Intent(this, OutputActivity::class.java))
    }


    /**
     * Tratador do evento de clique no botão "Editar saída".
     */
    fun onEditButtonClick(view: View) {

        val customAdapter = (binding.rcvOutputManList.adapter as CustomAdapter)
        val output: Output = customAdapter.selectedItems[0]
        val intent = Intent(this, OutputActivity::class.java)

        intent.putExtra("id", output.id)

        startActivity(intent)

    }


    /**
     * Tratador do evento de clique no botão "Excluir saída".
     */
    fun onDeleteButtonClick(view: View) {

        val owner = this
        val customAdapter = (binding.rcvOutputManList.adapter as CustomAdapter)
        val selectedOutputs: List<Output> = customAdapter.selectedItems

        with (AlertDialog.Builder(this)) {
            setTitle("Atenção")
            setMessage("Confirma a exclusão do(s) item(ns) selecionado(s)?")
            setPositiveButton("Sim") { _, _ ->
                outputViewModel.deleteAllOutputs(selectedOutputs, owner).observe(owner) {
                    listOutputs()
                }
            }
            setNegativeButton("Não", null)
            show()
        }

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


    /**
     * Adaptador de seleção para listagem de saídas na RecyclerView.
     */
    private inner class CustomAdapter: SelectableAdapter<Output>(R.layout.output_layout) {

        override fun compareItemsContents(oldItem: Output, newItem: Output): Boolean {
            return (oldItem.symbol == newItem.symbol) && (oldItem.target == newItem.target) &&
            (oldItem.pronunciation == newItem.pronunciation)
        }

        override fun onBinding(view: View, item: Output, position: Int) {

            val txvSymbol = view.findViewById<TextView>(R.id.txvOutputSymbol)
            val txvTarget = view.findViewById<TextView>(R.id.txvOutputTarget)
            val txvPronunciation = view.findViewById<TextView>(R.id.txvOutputPronunciation)

            txvSymbol.text = item.symbol
            txvTarget.text = item.target
            txvPronunciation.text = item.pronunciation

        }

    }


}