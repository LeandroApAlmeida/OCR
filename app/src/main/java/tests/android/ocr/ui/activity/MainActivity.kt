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


/**
 * Activity principal da aplicação. A activity contém uma barra de menus ao topo, uma barra de ferramentas
 * logo abaixo, uma área de desenho ao centro e botões de reconhecer símbolo/adicionar amostra e
 * limpar a tela no rodapé da tela.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, CoroutineListener {


    /**Componente para acesso aos controles da tela.*/
    private lateinit var binding: ActivityMainBinding

    /**ViewModel para manutenção de amostras no banco de dados.*/
    private val sampleViewModel: SampleViewModel by viewModels()

    /**ViewModel para manutenção de saídas da rede neural.*/
    private val outputViewModel: OutputViewModel by viewModels()

    /**ViewModel para execução do perceptron.*/
    private val perceptronViewModel: PerceptronViewModel by viewModels()

    /**ViewModel para manutenção dos parâmetros de treinamento da rede neural.*/
    private val paramsViewModel: ParamsViewModel by viewModels()

    /**Objeto para processamento de imagem desenhada na tela.*/
    private lateinit var imageBuilder: ImageBuilder

    /**Objeto para conversão de texto em fala.*/
    private lateinit var textToSpeech: TextToSpeech

    /**Objeto para gravar as configurações do app via API SharedPreferences.*/
    private lateinit var settings: Settings

    /**Objeto para acesso ao menu.*/
    private var menu: Menu? = null

    /**Objeto de saída selecionado na lista.*/
    private var output: Output? = null

    /**Status de modo de treinamento.*/
    private var trainingMode = false

    /**Status de Text To Speech (TTS) habilitado.*/
    private var ttsEnabled = true

    /**Escala da imagem processada.*/
    private var imageScale = 0


    /**
     * O evento [onCreate] é sobrescrito para inicializar a Activity no modo padrão.
     *
     * @param savedInstanceState estado salvo para configurar uma nova instância.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        settings = Settings(this.applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        textToSpeech = TextToSpeech(this, this)

        setSupportActionBar(binding.toolbar2)

        imageBuilder = ImageBuilder()

        // Ao selecionar a saída  na lista, altera o valor da variável "output".

        binding.spnSymbol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                output = (binding.spnSymbol.getItemAtPosition(position) as Output)
            }
        }

        binding.swcTraining.setOnCheckedChangeListener { _, isChecked: Boolean ->
            setMode(isChecked)
        }

        binding.txvWait.isVisible = false

        setMode(false)

    }


    /**
     * O evento [onCreateOptionsMenu] é sobrescrito para inflar o menu personalizado da aplicação.
     *
     * @param menu menu da Actinity.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        this.menu = menu

        return super.onCreateOptionsMenu(menu)

    }


    /**
     * O evento [onOptionsItemSelected] é sobrescrito para tratar o clique em cada item de menu.
     *
     * @param item item de menu clicado.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

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


    /**
     * O evento [onResume] do ciclo de vida da Activity é sobrescrito para tratar a reexibição da
     * mesma.
     */
    override fun onResume() {

        super.onResume()

        listOutputs()

        imageScale = kotlin.math.sqrt(paramsViewModel.getInputSize().toDouble()).toInt()

    }


    /**
     * O evento [onDestroy] do ciclo de vida da Activity é sobrescrito para liberar os recursos
     * alocados pela mesma.
     */
    override fun onDestroy() {

        textToSpeech.stop()

        textToSpeech.shutdown()

        super.onDestroy()

    }


    /**
     * O evento [onInit] do ciclo de vida da Activity é sobrescrito para verificar se o mecanismo
     * de Texto para Voz está ativado no sistema.
     */
    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) {
            ttsEnabled = false
        }

        //ttsEnabled = false

    }


    /**
     * Definir o modo de operação da Activity.
     *
     * @param trainingMode modo de operação da Activity.
     */
    private fun setMode(trainingMode: Boolean) {

        this.trainingMode = trainingMode

        if (trainingMode) {

            // Modo de treinamento.

            binding.imbImage.setImageResource(R.drawable.plus_icon)
            binding.spnSymbol.isVisible = true
            binding.txvSymbol.isVisible = true

            if (binding.spnSymbol.count > 0) {
                binding.spnSymbol.isEnabled = true
                binding.imbImage.isEnabled = true
                binding.imbTraining.isVisible = true
            } else {
                binding.spnSymbol.isEnabled = false
                binding.imbImage.isEnabled = false
                binding.imbTraining.isVisible = false
            }

        } else {

            // Modo de reconhecimento.

            binding.imbImage.setImageResource(R.drawable.speaker_icon)
            binding.spnSymbol.isVisible = false
            binding.txvSymbol.isVisible = false
            binding.imbImage.isEnabled = true
            binding.imbTraining.isVisible = false

        }

    }


    /**
     * Listar as saídas para associação das amostras para o treinamento da rede neural.
     */
    private fun listOutputs() {

        outputViewModel.getAllOutputs(this).observe(this) { outputList ->

            if (outputList != null) {

                val arrayAdapter: ArrayAdapter<Output> = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    outputList
                )

                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.spnSymbol.adapter = arrayAdapter

                var index = 0

                if (output != null) {

                    for (i in 0 until binding.spnSymbol.count) {

                        val output1: Output = (binding.spnSymbol.getItemAtPosition(i) as Output)

                        if (output!!.id == output1.id) {
                            index = i
                            break
                        }

                    }

                }

                binding.spnSymbol.setSelection(index)

            }

        }

    }


    /**
     * Tratador do evento de imagem reconhecida.
     */
    private fun recognitionDone() {

        // Aguarda 500 ms e limpa a imagem reconhecida e reabilita os controles da Activity.

        Handler(Looper.getMainLooper()).postDelayed({
            binding.paintView.isEnabled = true
            binding.imbImage.isEnabled = true
            binding.imbImage.alpha = 1.0f
            binding.imbClean.isEnabled = true
            binding.imbClean.alpha = 1.0f
            binding.swcTraining.isEnabled = true
            binding.paintView.cleanImage()
        }, 500)

    }


    /**
     * Pronunciar o texto da saída processada. Caso o mecanismo de Text To Speech (TTS) esteja desativado,
     * apenas exibe uma caixa de diálogo com o texto passado.
     *
     * @param text texto a ser pronunciado.
     */
    private fun speak(text: String) {

        if (ttsEnabled) {

            // Pronuncia o texto.

            val voices: Set<Voice> = textToSpeech.voices

            val selectedVoice = voices?.find { it.name == settings.getString("synthesizer.voice", "") }

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

            // Exibe uma caixa de diálogo com o texto passado.

            val dialog = AlertDialog.Builder(this)
            .setTitle("Resultado do reconhecimento")
            .setMessage(text)
            .setPositiveButton("OK", null)
            .create()

            dialog.setOnDismissListener {
                recognitionDone()
            }

            dialog.show()

        }

    }


    /**
     * Adicionar uma imagem ao banco de dados de amostras.
     */
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


    /**
     * Reconhecer a imagem desenhada na tela.
     */
    private fun recognizeImage() {

        try {

            // Converte a imagem para a miniatura a ser reconhecida.

            val bitmap = imageBuilder.createSampleBitmap(binding.paintView.getBitmap(), imageScale)

            binding.paintView.isEnabled = false
            binding.imbImage.isEnabled = false
            binding.imbImage.alpha = 0.5f
            binding.imbClean.isEnabled = false
            binding.imbClean.alpha = 0.5f
            binding.swcTraining.isEnabled = false

            perceptronViewModel.calculateOutput(bitmap, this).observe(this) { output ->

                if (output != null) {

                    // Há uma saída calculada para a miniatura da imagem. Neste caso, aciona o TTS
                    // para pronunciar a saída reconhecida.

                    speak(output.pronunciation)

                } else {

                    // Não há uma saída calculada para a miniatura da imagem. Neste caso, toca um
                    // som de erro, executanto uma trilha adicionada aos resources da aplicação.

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


    /**
     * Tratador do evento de clique no botão "Reconhecer imagem/Adicionar amostra".
     */
    fun onImageButtonClick(view: View) {
        if (!trainingMode) {
            recognizeImage()
        } else {
            addSampleImage()
        }
    }


    /**
     * Tratador do evento de clique no botão "Treinar a rede neural".
     */
    fun onTrainingButtonClick(view: View) {

        binding.paintView.cleanImage()
        binding.txvWait.isVisible = true
        binding.imbTraining.isEnabled = false
        binding.imbTraining.alpha = 0.5f
        binding.swcTraining.isEnabled = false
        binding.spnSymbol.isEnabled = false
        binding.paintView.isEnabled = false
        binding.imbImage.isEnabled = false
        binding.imbImage.alpha = 0.5f
        binding.imbClean.isEnabled = false
        binding.imbClean.alpha = 0.5f

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
            binding.imbTraining.isEnabled = true
            binding.imbTraining.alpha = 1.0f
            binding.swcTraining.isEnabled = true
            binding.spnSymbol.isEnabled = true
            binding.paintView.isEnabled = true
            binding.imbImage.isEnabled = true
            binding.imbImage.alpha = 1.0f
            binding.imbClean.isEnabled = true
            binding.imbClean.alpha = 1.0f

            menu!!.findItem(R.id.menuSymbols).isEnabled = true
            menu!!.findItem(R.id.menuSamples).isEnabled = true
            menu!!.findItem(R.id.menuSettings).isEnabled = true
            menu!!.findItem(R.id.menuAbout).isEnabled = true

        }

    }


    /**
     * Tratador do evento de clique no botão "Limpar imagem".
     */
    fun onCleanButtonClick(view: View) {
        binding.paintView.cleanImage()
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