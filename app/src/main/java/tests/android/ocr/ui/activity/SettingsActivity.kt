package tests.android.ocr.ui.activity

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tests.android.ocr.android.Settings
import tests.android.ocr.databinding.ActivitySettingsBinding
import tests.android.ocr.model.viewmodel.ParamsViewModel
import java.util.Locale

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {


    private val paramsViewModel: ParamsViewModel by viewModels()

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var textToSpeech: TextToSpeech

    private lateinit var settings: Settings

    private val voiceNames = mutableListOf<String>()

    private var ttsEnabled = true


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        settings = Settings(this.applicationContext)

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        textToSpeech = TextToSpeech(this, this)

        binding.spnVoices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                settings.setString("synthesizer.voice", binding.spnVoices.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ação caso nenhum item seja selecionado
            }

        }

        binding.btnTest.setOnClickListener { onTestVoiceButtonClick(it) }
        binding.edtInputSize.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) { saveInputSize() } }
        binding.edtOutputSize.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) { saveOutputSize() } }
        binding.edtLearningRate.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) { saveLearningRate() } }
        binding.edtEpochs.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) { saveEpochs() } }

        loadParams()

    }


    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            val voices: Set<Voice> = textToSpeech.voices.filter {
                it.locale == Locale("pt", "BR") || it.locale == Locale("pt", "PT")
            }.toSet()

            voiceNames.clear()

            voices.forEach { voice ->
                voiceNames.add(voice.name)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, voiceNames)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spnVoices.adapter = adapter

            val voice = settings.getString("synthesizer.voice", "")

            if (voice != "") {

                var idx = 0

                for (i in 0 until adapter.count) {
                    if (adapter.getItem(i).toString() == voice) {
                        idx = i
                        break
                    }
                }

                binding.spnVoices.setSelection(idx)

            }

            val selectedVoice = voices.find { it.name == settings.getString("synthesizer.voice", "") }

            if (selectedVoice != null) {
                textToSpeech.voice = selectedVoice
            }

        } else {

            ttsEnabled = false

            binding.btnTest.isEnabled = false

            binding.spnVoices.isEnabled = false

        }

    }


    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }


    private fun loadParams() {
        try {
            binding.edtInputSize.setText(paramsViewModel.getInputSize().toString())
            binding.edtOutputSize.setText(paramsViewModel.getOutputSize().toString())
            binding.edtLearningRate.setText(paramsViewModel.getLearningRate().toString())
            binding.edtEpochs.setText(paramsViewModel.getEpochs().toString())
        } catch (ex: Exception) {
            with(AlertDialog.Builder(this)) {
                setTitle("Erro")
                setMessage(ex.message)
                setPositiveButton("OK", null)
                show()
            }
        }
    }


    private fun saveInputSize() {
        try {
            paramsViewModel.setInputSize(binding.edtInputSize.text.toString().toInt())
        } catch (ex: Exception) {
            with(AlertDialog.Builder(this)) {
                setTitle("Erro")
                setMessage(ex.message)
                setPositiveButton("OK", null)
                show()
            }
        }
    }


    private fun saveOutputSize() {
        try {
            paramsViewModel.setOutputSize(binding.edtOutputSize.text.toString().toInt())
        } catch (ex: Exception) {
            with(AlertDialog.Builder(this)) {
                setTitle("Erro")
                setMessage(ex.message)
                setPositiveButton("OK", null)
                show()
            }
        }
    }


    private fun saveLearningRate() {
        try {
            paramsViewModel.setLearningRate(binding.edtLearningRate.text.toString().toFloat())
        } catch (ex: Exception) {
            with(AlertDialog.Builder(this)) {
                setTitle("Erro")
                setMessage(ex.message)
                setPositiveButton("OK", null)
                show()
            }
        }
    }


    private fun saveEpochs() {
        try {
            paramsViewModel.setEpochs(binding.edtEpochs.text.toString().toInt())
        } catch (ex: Exception) {
            with(AlertDialog.Builder(this)) {
                setTitle("Erro")
                setMessage(ex.message)
                setPositiveButton("OK", null)
                show()
            }
        }
    }


    private fun onTestVoiceButtonClick(view: View) {

        val voices: Set<Voice> = textToSpeech.voices

        val selectedVoice = voices.find { it.name == settings.getString("synthesizer.voice", "") }

        if (selectedVoice != null) {

            textToSpeech.voice = selectedVoice

            val bundle = Bundle()

            bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)

            textToSpeech.speak(
                "Este é um teste do sintetizador de voz em português.",
                TextToSpeech.QUEUE_FLUSH,
                bundle,
                ""
            )

        }

    }


}