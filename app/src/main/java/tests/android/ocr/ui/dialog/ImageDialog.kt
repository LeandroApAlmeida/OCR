package tests.android.ocr.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.core.graphics.get
import androidx.core.graphics.set
import tests.android.ocr.databinding.ImageDialogBinding
import tests.android.ocr.image.ImageBuilder

/**
 * Diálogo para salvamento da amostra no banco de dados.
 *
 * @param context instância de Context.
 *
 * @param bitmap bitmap da imagem desenhada na tela do dispositivo.
 *
 * @param text texto da classe a ser amostrada.
 *
 * @param number número da amostra, iniciando em 1.
 */
class ImageDialog(context: Context, val bitmap: Bitmap, val text: String, val number: Int): Dialog(context) {


    /**Acesso aos componentes de interface gráfica de usuário.*/
    private lateinit var binding: ImageDialogBinding

    /**Tratador de evento de clique no botão Ok.*/
    private var onSaveButtonClickHandler: (() -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ImageDialogBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.txvImageDialogChar.text = text
        binding.txvNumber.text = number.toString()

        // Define o evento de clique no botão Imprimir.
        binding.imbImageDialogOk.setOnClickListener {
            if (onSaveButtonClickHandler != null) {
                dismiss()
                onSaveButtonClickHandler!!.invoke()
            }
        }

        // Define o evento de clique no botão Cancelar.
        binding.imbImageDialogCancel.setOnClickListener {
            dismiss()
        }

        val bitmap2: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val transparentColor = Color.argb(0, 255, 255, 255)

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                if (bitmap[x, y] != Color.BLACK) {
                    bitmap2[x, y] = transparentColor
                }
            }
        }

        binding.imvImageDialogSample.setImageBitmap(bitmap2)

    }


    /**
     * Tratador de evento de clique no botão Imprimir.
     */
    fun setOnSaveButtonClickHandler(onSaveButtonClickHandler: (() -> Unit)?): ImageDialog {

        this.onSaveButtonClickHandler = onSaveButtonClickHandler

        return this

    }


}