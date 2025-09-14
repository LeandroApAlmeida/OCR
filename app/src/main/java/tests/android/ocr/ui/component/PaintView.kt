package tests.android.ocr.ui.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Componente para desenho do símbolo na tela do dispositivo.
 */
class PaintView: View {


    private var params: ViewGroup.LayoutParams? = null

    private var path: Path = Path()

    private var paintBrush: Paint = Paint()

    private var pathList = ArrayList<Path>()


    constructor(context: Context): this(context, null) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()
    }


    /**
     * Inicialização da classe.
     */
    private fun init() {

        paintBrush.isAntiAlias = true
        paintBrush.color = Color.BLACK
        paintBrush.style = Paint.Style.STROKE
        paintBrush.strokeJoin = Paint.Join.ROUND
        paintBrush.strokeCap = Paint.Cap.ROUND
        paintBrush.strokeWidth = 40f

        params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }


    /**
     * Evento de toque na tela.
     *
     * @param event dados sobre o evento.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (isEnabled) {

            val x = event.x
            val y = event.y

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    path.moveTo(x, y)
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    path.lineTo(x, y)
                    pathList.add(path)
                }

                else -> {
                    return false
                }

            }

            postInvalidate()

        }

        return false

    }


    /**
     * Evento de desenho na tela.
     *
     * @param canvas área de desenho.
     */
    override fun onDraw(canvas: Canvas) {

        for (i in pathList.indices) {
            canvas.drawPath(pathList[i], paintBrush)
            invalidate()
        }

    }


    /**
     * Obter o bitmap da imagem desenhada na tela.
     */
    fun getBitmap(): Bitmap {

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        this.draw(Canvas(bitmap))

        return bitmap

    }


    /**
     * Limpar a imagem que está desenhada na tela.
     */
    fun cleanImage() {

        pathList.clear()

        path.reset()

    }


}