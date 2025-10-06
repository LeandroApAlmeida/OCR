package tests.android.ocr.ui.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tests.android.ocr.R
import tests.android.ocr.ui.recyclerview.adapter.SelectableAdapter
import tests.android.ocr.database.model.Output
import tests.android.ocr.database.model.Sample
import tests.android.ocr.databinding.ActivitySamplesBinding
import tests.android.ocr.model.viewmodel.CoroutineListener
import tests.android.ocr.model.viewmodel.OutputViewModel
import tests.android.ocr.model.viewmodel.SampleViewModel
import tests.android.ocr.image.ImageBuilder

/**
 * Activity para manutenção das amostras casdastradas no banco de dados.
 */
@AndroidEntryPoint
class SamplesActivity : AppCompatActivity(), CoroutineListener {


    /**Componente para acesso aos controles da tela.*/
    private lateinit var binding: ActivitySamplesBinding

    /**ViewModel para manutenção das amostras no banco de dados.*/
    private val sampleViewModel: SampleViewModel by viewModels()

    /**ViewModel para manutenção de saídas da rede neural.*/
    private val outputViewModel: OutputViewModel by viewModels()

    /**Objeto para processamento de imagem obtida do banco de dados.*/
    private lateinit var imageBuilder: ImageBuilder

    /**Objeto de saída selecionado na lista.*/
    private var output: Output? = null


    /**
     * O evento [onCreate] é sobrescrito para inicializar a Activity no modo padrão.
     *
     * @param savedInstanceState estado salvo para configurar uma nova instância.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivitySamplesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        imageBuilder = ImageBuilder()

        binding.imbSamplesDelete.setOnClickListener { onDeleteButtonClick(it) }
        binding.imbSamplesDetails.setOnClickListener { onDetailsButtonClick(it) }

        binding.spnSamplesOutputList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                output = (binding.spnSamplesOutputList.getItemAtPosition(position) as Output)
                listSamples()
            }
        }

        binding.rcvSamples.layoutManager = GridLayoutManager(this, 6)

        val customAdapter = CustomAdapter(this)

        customAdapter.setOnSelectItemHandler { _, _ ->
            val enable = customAdapter.selectedItems.isNotEmpty()
            binding.imbSamplesDelete.isEnabled = enable
            binding.imbSamplesDelete.alpha = if (enable) 1.0f else 0.5f
            binding.imbSamplesDetails.isEnabled = enable
            binding.imbSamplesDetails.alpha = if (enable) 1.0f else 0.5f
        }

        binding.rcvSamples.adapter = customAdapter

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
     * Listar as saídas na lista de seleção.
     */
    private fun listOutputs() {

        outputViewModel.getAll(this).observe(this) { outputList ->

            if (outputList != null) {

                val arrayAdapter: ArrayAdapter<Output> = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    outputList
                )

                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.spnSamplesOutputList.adapter = arrayAdapter

                var index = 0

                if (output != null) {
                    for (i in 0 until binding.spnSamplesOutputList.count) {
                        val output1: Output = (binding.spnSamplesOutputList.getItemAtPosition(i) as Output)
                        if (output!!.id == output1.id) {
                            index = i
                            break
                        }
                    }
                }

                binding.spnSamplesOutputList.setSelection(index)

            }

        }

    }


    /**
     * Listar as amostras cadastradas de acordo com a saída selecionada na lista.
     */
    private fun listSamples() {

        sampleViewModel.getAll(output!!.id, true, this).observe(this) { samplesList ->

            if (samplesList != null) {

                val customAdapter = (binding.rcvSamples.adapter as CustomAdapter)

                customAdapter.data = samplesList

                binding.imbSamplesDelete.isEnabled = false
                binding.imbSamplesDelete.alpha = 0.5f
                binding.imbSamplesDetails.isEnabled = false
                binding.imbSamplesDetails.alpha = 0.5f
                binding.txvSamplesNumSamples.text = "   Total de amostras: ${samplesList.size}"

            } else {

                binding.txvSamplesNumSamples.text = "   Total de amostras: 0"

            }

        }

    }


    /**
     * Tratador do evento de clique no botão "Excluir amostra".
     */
    fun onDeleteButtonClick(view: View) {

        val owner = this
        val customAdapter = (binding.rcvSamples.adapter as CustomAdapter)
        val selectedSamples: List<Sample> = customAdapter.selectedItems

        with (AlertDialog.Builder(this)) {
            setTitle("Atenção")
            setMessage("Confirma a exclusão da(s) amostra(s) selecionada(s)?")
            setPositiveButton("Sim") { _, _ ->
                sampleViewModel.deleteAll(selectedSamples, owner).observe(owner) {
                    listSamples()
                }
            }
            setNegativeButton("Não", null)
            show()
        }

    }


    /**
     * Tratador do evento de clique no botão "Detalhes da amostra".
     */
    fun onDetailsButtonClick(view: View) {

        val customAdapter = (binding.rcvSamples.adapter as CustomAdapter)
        val sample: Sample = customAdapter.selectedItems[0]

        sampleViewModel.getDataById(sample.id, this).observe(this) { data ->

            with (AlertDialog.Builder(this)) {
                setTitle("Detalhes")
                setMessage(imageBuilder.bitmapToString(imageBuilder.imageStreamToBitmap(data!!)))
                setPositiveButton("OK", null)
                show()
            }

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
     * Adaptador de seleção para listagem de amostras na RecyclerView.
     */
    private inner class CustomAdapter(val owner: SamplesActivity): SelectableAdapter<Sample>(R.layout.sample_layout, false) {

        override fun compareItemsContents(oldItem: Sample, newItem: Sample): Boolean {
            return false
        }

        override fun onBinding(view: View, item: Sample, position: Int) {

            val imvSample = view.findViewById<ImageView>(R.id.imvSampleLayout)

            sampleViewModel.getDataById(item.id, owner).observe(owner) { data ->

                val bitmap = imageBuilder.imageStreamToBitmap(data!!)
                val bitmap2: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val transparentColor = Color.argb(0, 255, 255, 255)

                for (x in 0 until bitmap.width) {
                    for (y in 0 until bitmap.height) {
                        if (bitmap[x, y] != Color.BLACK) {
                            bitmap2[x, y] = transparentColor
                        }
                    }
                }

                imvSample.setImageBitmap(bitmap2)

            }

        }

    }


}