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

@AndroidEntryPoint
class SamplesActivity : AppCompatActivity(), CoroutineListener {


    private lateinit var binding: ActivitySamplesBinding

    private val sampleViewModel: SampleViewModel by viewModels()

    private val outputViewModel: OutputViewModel by viewModels()

    private lateinit var imageBuilder: ImageBuilder

    private var output: Output? = null


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


    override fun onResume() {
        super.onResume()
        listOutputs()
    }


    private fun listOutputs() {

        outputViewModel.getAllOutputs(this).observe(this) { outputList ->

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


    private fun listSamples() {

        sampleViewModel.getAllSamples(output!!.id, true, this).observe(this) { samplesList ->

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


    fun onDeleteButtonClick(view: View) {

        val owner = this
        val customAdapter = (binding.rcvSamples.adapter as CustomAdapter)
        val selectedSamples: List<Sample> = customAdapter.selectedItems

        with (AlertDialog.Builder(this)) {
            setTitle("Atenção")
            setMessage("Confirma a exclusão da(s) amostra(s) selecionada(s)?")
            setPositiveButton("Sim") { _, _ ->
                sampleViewModel.deleteAllSamples(selectedSamples, owner).observe(owner) {
                    listSamples()
                }
            }
            setNegativeButton("Não", null)
            show()
        }

    }


    fun onDetailsButtonClick(view: View) {

        val customAdapter = (binding.rcvSamples.adapter as CustomAdapter)
        val sample: Sample = customAdapter.selectedItems[0]

        sampleViewModel.getSampleDataById(sample.id, this).observe(this) { data ->

            with (AlertDialog.Builder(this)) {
                setTitle("Detalhes")
                setMessage(imageBuilder.bitmapToString(imageBuilder.imageStreamToBitmap(data!!)))
                setPositiveButton("OK", null)
                show()
            }

        }

    }


    override fun onCoroutineException(ex: Throwable) {

        with(AlertDialog.Builder(this)) {
            setTitle("Erro")
            setMessage(ex.message)
            setPositiveButton("OK", null)
            show()
        }

    }


    private inner class CustomAdapter(val owner: SamplesActivity): SelectableAdapter<Sample>(R.layout.sample_layout, false) {

        override fun compareItemsContents(oldItem: Sample, newItem: Sample): Boolean {
            return false
        }

        override fun onBinding(view: View, item: Sample, position: Int) {

            val imvSample = view.findViewById<ImageView>(R.id.imvSampleLayout)

            sampleViewModel.getSampleDataById(item.id, owner).observe(owner) { data ->

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