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

/**
 * Activity para configuração dos parâmetros para treinamento do Perceptron.
 */
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {


    /**Componente para acesso aos controles da tela.*/
    private lateinit var binding: ActivitySettingsBinding

    /**ViewModel para manutenção dos parâmetros de treinamento da rede neural.*/
    private val paramsViewModel: ParamsViewModel by viewModels()

    /**Objeto para conversão de texto em fala.*/
    private lateinit var textToSpeech: TextToSpeech

    /**Objeto para gravar as configurações do app via API SharedPreferences.*/
    private lateinit var settings: Settings

    /**Vozes do TTS*/
    private var voices: Set<Voice>? = null

    /**Lista das vozes para TTS em português.*/
    private val voiceNames = mutableListOf<String>()

    /**Status de Text To Speech (TTS) habilitado.*/
    private var ttsEnabled = true


    /**
     * O evento [onCreate] é sobrescrito para inicializar a Activity no modo padrão.
     *
     * @param savedInstanceState estado salvo para configurar uma nova instância.
     */
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


    /**
     * O evento [onInit] do ciclo de vida da Activity é sobrescrito para listar as configurações de
     * voz nos respectivos campos.
     */
    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            voices = textToSpeech.voices.filter {
                it.locale == Locale("pt", "BR") || it.locale == Locale("pt", "PT")
            }.toSet()

            voiceNames.clear()

            voices?.forEach { voice ->
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

            val selectedVoice = voices?.find { it.name == settings.getString("synthesizer.voice", "") }

            if (selectedVoice != null) {
                textToSpeech.voice = selectedVoice
            }

        } else {

            ttsEnabled = false

            binding.btnTest.isEnabled = false

            binding.spnVoices.isEnabled = false

        }

    }


    /**
     * O evento [onDestroy] do ciclo de vida da Activity é sobrescrito para liberar os recursos
     * alocados pela mesma.
     */
    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }


    /**
     * Carregar os valores de configurações nos respectivos campos.
     */
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


    /**
     * Salvar o valor do tamanho do vetor de entradas.
     */
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


    /**
     * Salvar o valor do tamanho do vetor de saídas (número de neurônios).
     */
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


    /**
     * Salvar o valor da taxa de aprendizado.
     */
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


    /**
     * Salvar o número máximo de épocas para o treinamento.
     */
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


    /**
     * Tratador do evento de clique no botão "Testar voz".
     */
    private fun onTestVoiceButtonClick(view: View) {

        try {

            val selectedVoice = voices?.find { it.name == settings.getString("synthesizer.voice", "") }

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

        } catch (ex: Exception) {

            with(AlertDialog.Builder(this)) {
                setTitle("Erro")
                setMessage(ex.message)
                setPositiveButton("OK", null)
                show()
            }

        }

    }


}