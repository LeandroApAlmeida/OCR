package tests.android.ocr.ui.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import tests.android.ocr.R
import tests.android.ocr.android.Settings
import tests.android.ocr.database.model.Output
import tests.android.ocr.database.model.Sample
import tests.android.ocr.databinding.ActivityMainBinding
import tests.android.ocr.model.viewmodel.CoroutineListener
import tests.android.ocr.model.viewmodel.OutputViewModel
import tests.android.ocr.model.viewmodel.SampleViewModel
import tests.android.ocr.image.ImageBuilder
import tests.android.ocr.model.viewmodel.ParamsViewModel
import tests.android.ocr.model.viewmodel.PerceptronViewModel
import tests.android.ocr.ui.dialog.ImageDialog

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, CoroutineListener {


    private lateinit var binding: ActivityMainBinding

    private val sampleViewModel: SampleViewModel by viewModels()

    private val outputViewModel: OutputViewModel by viewModels()

    private val perceptronViewModel: PerceptronViewModel by viewModels()

    private val paramsViewModel: ParamsViewModel by viewModels()

    private lateinit var imageBuilder: ImageBuilder

    private lateinit var textToSpeech: TextToSpeech

    private lateinit var settings: Settings

    private var menu: Menu? = null

    private var output: Output? = null

    private var trainingMode = false

    private var ttsEnabled = true

    private var imageScale = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        settings = Settings(this.applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        textToSpeech = TextToSpeech(this, this)

        setSupportActionBar(binding.toolbar2)

        imageBuilder = ImageBuilder()

        binding.spnMainSymbol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                output = (binding.spnMainSymbol.getItemAtPosition(position) as Output)
            }
        }

        binding.swcMainTraining.setOnCheckedChangeListener { _, isChecked: Boolean ->
            setMode(isChecked)
        }

        binding.txvWait.isVisible = false

        setMode(false)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        this.menu = menu

        return super.onCreateOptionsMenu(menu)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onMenuItemClick(item.itemId)
    }


    override fun onResume() {
        super.onResume()
        listOutputs()
        imageScale = kotlin.math.sqrt(paramsViewModel.getInputSize().toDouble()).toInt()
    }


    private fun onMenuItemClick(itemId: Int): Boolean {

        when (itemId) {

            R.id.menuSymbols -> {
                startActivity(Intent(this, OutputManagerActivity::class.java))
            }

            R.id.menuSamples -> {
                startActivity(Intent(this, SamplesActivity::class.java))
            }

            R.id.menuSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }

            R.id.menuAbout -> {

                with(AlertDialog.Builder(this)) {
                    setTitle("Sobre...")
                    setMessage("\nDesenvolvedor: Leandro Ap. Almeida\n\nData: 05/09/2025")
                    setPositiveButton("OK", null)
                    show()
                }

            }

        }

        return true

    }


    fun onCleanButtonClick(view: View) {
        binding.paintView.cleanImage()
    }


    override fun onDestroy() {

        textToSpeech.stop()

        textToSpeech.shutdown()

        super.onDestroy()

    }


    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) {
            ttsEnabled = false
        }

    }


    private fun recognitionDone() {

        Handler(Looper.getMainLooper()).postDelayed({
            binding.paintView.isEnabled = true
            binding.imbMainImage.isEnabled = true
            binding.imbMainImage.alpha = 1.0f
            binding.imbMainClean.isEnabled = true
            binding.imbMainClean.alpha = 1.0f
            binding.swcMainTraining.isEnabled = true
            binding.paintView.cleanImage()
        }, 500)

    }


    private fun speak(text: String) {

        if (ttsEnabled) {

            val voices: Set<Voice> = textToSpeech.voices

            val voice = settings.getString("synthesizer.voice", "")

            val selectedVoice = voices.find { it.name == voice}

            if (selectedVoice != null) {

                textToSpeech.voice = selectedVoice

            }

            val bundle = Bundle()

            bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)

            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                   recognitionDone()
                }

                override fun onError(utteranceId: String?) {}

            })

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, "")

        } else {

            with(AlertDialog.Builder(this)) {
                setTitle("Resultado do reconhecimento")
                setMessage(text)
                setPositiveButton("OK", null)
                show()
            }

        }

    }


    private fun setMode(trainingMode: Boolean) {

        this.trainingMode = trainingMode

        if (trainingMode) {

            binding.imbMainImage.setImageResource(R.drawable.plus_icon)
            binding.spnMainSymbol.isVisible = true
            binding.txvSymbol.isVisible = true

            if (binding.spnMainSymbol.count > 0) {
                binding.spnMainSymbol.isEnabled = true
                binding.imbMainImage.isEnabled = true
                binding.imbMainTraining.isVisible = true
            } else {
                binding.spnMainSymbol.isEnabled = false
                binding.imbMainImage.isEnabled = false
                binding.imbMainTraining.isVisible = false
            }

        } else {

            binding.imbMainImage.setImageResource(R.drawable.speaker_icon)
            binding.spnMainSymbol.isVisible = false
            binding.txvSymbol.isVisible = false
            binding.imbMainImage.isEnabled = true
            binding.imbMainTraining.isVisible = false

        }

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

                binding.spnMainSymbol.adapter = arrayAdapter

                var index = 0

                if (output != null) {

                    for (i in 0 until binding.spnMainSymbol.count) {

                        val output1: Output = (binding.spnMainSymbol.getItemAtPosition(i) as Output)

                        if (output!!.id == output1.id) {
                            index = i
                            break
                        }

                    }

                }

                binding.spnMainSymbol.setSelection(index)

            }

        }

    }


    private fun addSampleImage() {

        try {

            val bitmap = imageBuilder.createSampleBitmap(binding.paintView.getBitmap(), imageScale)

            sampleViewModel.getTotalSamplesByOutput(output!!.id, this).observe(this) { total ->

                ImageDialog(this, bitmap, output!!.symbol, total + 1).setOnSaveButtonClickHandler {

                    val sample = Sample(0, output!!.id, imageBuilder.bitmapToImageStream(bitmap))

                    sampleViewModel.insertSample(sample, this).observe(this)  {
                        binding.paintView.cleanImage()
                    }

                }.show()

            }

        } catch (ex: Exception) {

            with(AlertDialog.Builder(this)) {
                setTitle("Erro")
                setMessage(ex.message)
                setPositiveButton("OK", null)
                show()
            }

        }

    }


    private fun recognizeImage() {

        try {

            val bitmap = imageBuilder.createSampleBitmap(binding.paintView.getBitmap(), imageScale)

            binding.paintView.isEnabled = false
            binding.imbMainImage.isEnabled = false
            binding.imbMainImage.alpha = 0.5f
            binding.imbMainClean.isEnabled = false
            binding.imbMainClean.alpha = 0.5f
            binding.swcMainTraining.isEnabled = false

            perceptronViewModel.calculateOutput(bitmap, this).observe(this) { output ->

                if (output != null) {

                    speak(output.pronunciation)

                } else {

                    val mediaPlayer = MediaPlayer.create(this, R.raw.error)

                    mediaPlayer.start()

                    mediaPlayer.setOnCompletionListener {
                        mediaPlayer.release()
                    }

                    recognitionDone()

                }

            }

        } catch (ex: Exception) {

            recognitionDone()

            with(AlertDialog.Builder(this)) {
                setTitle("Erro")
                setMessage(ex.message)
                setPositiveButton("OK", null)
                show()
            }

        }

    }


    fun onImageButtonClick(view: View) {
        if (!trainingMode) {
            recognizeImage()
        } else {
            addSampleImage()
        }
    }


    fun onTrainingButtonClick(view: View) {

        binding.paintView.cleanImage()
        binding.txvWait.isVisible = true
        binding.imbMainTraining.isEnabled = false
        binding.imbMainTraining.alpha = 0.5f
        binding.swcMainTraining.isEnabled = false
        binding.spnMainSymbol.isEnabled = false
        binding.paintView.isEnabled = false
        binding.imbMainImage.isEnabled = false
        binding.imbMainImage.alpha = 0.5f
        binding.imbMainClean.isEnabled = false
        binding.imbMainClean.alpha = 0.5f

        menu!!.findItem(R.id.menuSymbols).isEnabled = false
        menu!!.findItem(R.id.menuSamples).isEnabled = false
        menu!!.findItem(R.id.menuSettings).isEnabled = false
        menu!!.findItem(R.id.menuAbout).isEnabled = false

        perceptronViewModel.trainPerceptron(this).observe(this) { iterations ->

            val msg: String = if (iterations > 0) {
                "A rede neural Perceptron foi treinada com sucesso em ${iterations} interações."
            } else {
                "A rede neural Perceptron foi treinada, porém não convergiu."
            }

            with(AlertDialog.Builder(this)) {
                setTitle("Concluído!")
                setMessage(msg)
                setPositiveButton("OK", null)
                show()
            }

            binding.txvWait.isVisible = false
            binding.imbMainTraining.isEnabled = true
            binding.imbMainTraining.alpha = 1.0f
            binding.swcMainTraining.isEnabled = true
            binding.spnMainSymbol.isEnabled = true
            binding.paintView.isEnabled = true
            binding.imbMainImage.isEnabled = true
            binding.imbMainImage.alpha = 1.0f
            binding.imbMainClean.isEnabled = true
            binding.imbMainClean.alpha = 1.0f

            menu!!.findItem(R.id.menuSymbols).isEnabled = true
            menu!!.findItem(R.id.menuSamples).isEnabled = true
            menu!!.findItem(R.id.menuSettings).isEnabled = true
            menu!!.findItem(R.id.menuAbout).isEnabled = true

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


}