package tests.android.ocr.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tests.android.ocr.database.AppDatabase
import java.io.ByteArrayOutputStream
import kotlin.text.StringBuilder

/**
 * Classe para processamento de imagem para o reconhecimento pela rede neural ou sua gravação
 * como uma amostra no banco de dados.
 */
class ImageBuilder {


    /**
     * Cortar o desenho feito na entrada, que vem de uma imagem retangular, para uma imagem
     * quadrada (a mesma dimensão em todos os lados da imagem). Este processo é feito de tal forma
     * que a imagem fique perfeitamente alinhada ao centro tanto na horizontal quanto na vertical.[<br><br>]
     * Tal processo é parte da etapa preliminar do processo de reconhecimento de imagens, aonde faz-se
     * a normalização dos dados de entrada para posteriormente serem conectados a uma rede neural
     * artificial.
     *
     * @param bitmap bitmap original.
     *
     * @return bitmap quadrado, sendo que a dimensão dos lados deste quadrado é a mesma da do maior
     * eixo da imagem (horizontal ou vertical). Considerando-se qua a imagem seja uma letra "t"
     * manuscrita, por exemplo, normalmente a maior dimensão é a do eixo vertical, pois o desenho
     * de um "t" é mais alto que largo. Uma letra "m" manuscrita, ao contrário, normalmente é maior
     * no eixo horizontal, pois ela é mais larga do que alta.
     */
    private fun cutBitmapToSquare(bitmap: Bitmap): Bitmap {

        // Obtém em x1 e x2 as posições de inicio e final da largura da imagem, e em y1 e y2 as
        // posições de início e final da altura da imagem criada a partir do rastro do marcador.

        var x1 = -1; var x2 = -1; var y1 = -1; var y2 = -1

        // Localiza o ponto no eixo x da primeira coluna com um pixel preto.
        label@ for (x in 0 until bitmap.width) {

            for (y in 0 until bitmap.height) {

                if (bitmap[x, y] == Color.BLACK) {
                    x1 = x
                    break@label
                }

            }

        }

        // Se não localizou o valor de x1, lança uma exceção.
        if (x1 < 0 ) throw Exception("Não existe uma imagem desenhada na tela.")

        // Localiza o ponto no eixo x da última coluna com um pixel preto. Desta forma, delimita a
        // dimensão horizontal da imagem.
        label@ for (x in bitmap.width - 1 downTo   0) {

            for (y in 0 until  bitmap.height) {

                if (bitmap[x, y] == Color.BLACK) {
                    x2 = x
                    break@label
                }

            }

        }

        // Localiza o ponto no eixo y da primeira linha com um pixel preto.
        label@ for (y in 0 until bitmap.height) {

            for (x in 0 until bitmap.width) {

                if (bitmap[x, y] == Color.BLACK) {
                    y1 = y
                    break@label
                }

            }

        }

        // Localiza o ponto no eixo y da última linha com um pixel preto. Desta forma, delimita a
        // dimensão vertical da imagem.
        label@ for (y in bitmap.height - 1 downTo 0) {

            for (x in 0 until bitmap.width) {

                if (bitmap[x, y] == Color.BLACK) {
                    y2 = y
                    break@label
                }

            }

        }

        // Dimensão horizontal do caractere (largura em pixels).
        val w: Int = x2 - x1

        // Dimensão vertical do caractere (altura em pixels).
        val h: Int = y2 - y1

        // Dimensão da imagem que vai receber os contornos do caractere.
        // Vai ter como referência a dimensão que tem o maior número de pixels
        // (horizontal ou vertical).
        val d: Int = ((if (w > h) w else h) + 1)

        // Cria a imagem de cópia.
        val squareBitmap = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888, true)
        val o1: Int; val o2: Int; val m: Int

        // Encaixa a imagem criada, centralizando-a tanto na vertical quanto na horizontal.

        if (w > h) {

            // A imagem é mais larga do que alta...

            m = h / 2       // Metade da dimensão vertical da imagem
            o1 = y1 + m     // Localização do ponto o no eixo y em bitmap
            o2 = d / 2      // Localização do ponto o no eixo y em squareBitmap (centro do quadrado)

            var x3 = 0      // Controle de iterações no eixo x em squareBitmap

            // Copia a linha no ponto o de bitmap para o eixo o em squareBitmap
            for (x in x1 ..  x2) {
                squareBitmap[x3++, o2] = bitmap[x, o1]
            }

            // Vai iterar h/2 vezes (metade da dimensão vertical da imagem).
            for (i in 1 .. m) {

                x3 = 0

                for (x in x1 ..  x2) {

                    // Copia uma linha que está i pixels acima de o em bitmap para a mesma posição
                    // relativa em squareBitmap.
                    squareBitmap[x3, o2-i] = bitmap[x, o1-i]

                    // Copia uma linha que está i pixels abaixo de o em bitmap para a mesma posição
                    // relativa em squareBitmap.
                    squareBitmap[x3, o2+i] = bitmap[x, o1+i]

                    x3++

                }

            }

        } else {

            // A imagem é mais alta do que larga...

            m = w / 2       // Metade da dimensão horizontal da imagem
            o1 = x1 + m     // Localização do ponto o no eixo x em bitmap
            o2 = d / 2      // Localização do ponto o no eixo x em squareBitmap (centro do quadrado)

            var y3 = 0      // Controle de iterações no eixo y em squareBitmap

            // Copia a coluna no ponto o de bitmap para o eixo o em squareBitmap
            for (y in y1 .. y2) {
                squareBitmap[o2, y3++] = bitmap[o1, y]
            }

            // Vai iterar h/2 vezes (metade da dimensão horizontal da imagem).
            for (i in 1 .. m) {

                y3 = 0

                for (y in y1 ..  y2) {

                    // Copia uma coluna que está i pixels à esquerda de o em bitmap para a mesma
                    // posição relativa em squareBitmap.
                    squareBitmap[o2-i, y3] = bitmap[o1-i, y]

                    // Copia uma coluna que está i pixels à direita de o em bitmap para a mesma posição
                    // relativa em squareBitmap.
                    squareBitmap[o2+i, y3] = bitmap[o1+i, y]

                    y3++

                }

            }

        }

        return squareBitmap

    }


    /**
     * O fundo da imagem é transparente. Por convenção, o fundo deve ficar em cor branca. Neste
     * método o que se faz é colorir os pixels de fundo da imagem de branco, mantendo os contornos
     * da imagem do caracter em preto.
     *
     * @param bitmap bitmap que passará pela transformação.
     *
     * @return bitmap resultante da transformação.
     */
    private fun repaintBitmapBackground(bitmap: Bitmap): Bitmap {

        //Cada pixel que não é preto (transparente) passa a ser branco.
        for (x in 0 until bitmap.width) {

            for (y in 0 until bitmap.height) {

                if (bitmap[x, y] != Color.BLACK) {
                    bitmap[x, y] = Color.WHITE
                }

            }

        }

        return bitmap

    }


    /**
     * Redimensiona um bitmap.
     *
     * @param bitmap bitmap original, que será redimensionado.
     *
     * @param width nova largura do bitmap.
     *
     * @param height nova altura do bitmap.
     *
     * @return bitmap resultante da transformação.
     */
    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {

        return Bitmap.createScaledBitmap(
            bitmap,
            width,
            height,
            true
        )

    }


    /**
     * Obter uma imagem para amostragem no banco de dados ou para o processamento na rede neural
     * artifical. Neste método é feito a otimização da entrada, de tal modo que mantenha um custo
     * constante de processamento, independentemente de resolução de tela de dispositivo móvel.
     *
     * @param bitmap imagem original, obtida da entrada do usuário.
     *
     * @param scale tamanho em pixels da imagem de amostra. Como a amostra é uma imagem
     * normalizada, quadrada, o tamanho refe-se à dimensão dos lados deste quadrado.
     *
     * @return imagem normalizada para entrada na rede neural artificial ou amostragem no banco de
     * dados. Entenda-se como imagem normalizada, uma imagem com as dimensões informadas no parâmetro
     * [scale], com contornos em cor preta e fundo em cor branca.
     */
    fun createSampleBitmap(bitmap: Bitmap, scale: Int): Bitmap {

        // Na primeira etapa do processamento da imagem original reduz-se o seu tamanho de
        // tal forma que, o lado maior dela sempre tenha 400 pixels. O lado menor deve sofrer
        // uma redução proporcional, mantendo as características da imagem original.
        // Feito desta forma, o custo computacional de gerar uma amostra não se altera muito
        // de uma resolução de tela de dispositivo para outra menor ou maior.
        // Tal redução deve ser considerada pois o custo computacional de se processar uma
        // imagem pixel a pixel é uma relação de m x n, onde m é a largura da imagem e n a sua
        // altura.
        // Considerando-se como exemplo o dispositivo que foi feito os testes deste algoritmo,
        // a imagem desenhada tem resolução de de 2.490 x 1.440 pixels, logo, o número total de
        // pixels desta somam 3.585.600. Com a transformação, este número se reduz para 92.400
        // pixels, um fator de redução de 38,80, mantendo as características originais da imagem
        // obtida na entrada.

        val biggerSide = 400
        val minorSide: Int
        val bigger: Int = if (bitmap.width > bitmap.height) bitmap.width else bitmap.height
        val minor: Int = if (bigger == bitmap.width) bitmap.height else bitmap.width
        val reductionFactor: Double = (bigger.toDouble() / biggerSide.toDouble())

        minorSide = (minor / reductionFactor).toInt()

        val width: Int
        val height: Int

        if (bigger == bitmap.width) {
            width = biggerSide
            height = minorSide
        } else {
            height = biggerSide
            width = minorSide
        }

        val bitmap1 = resizeBitmap(bitmap, width, height)

        // Na segunda etapa do processamento da imagem faz-se o recorte, para que esta fique quadrada,
        // de acordo com a dimensão maior do desenho (horizontal ou vertical). Este recorte é necessário
        // para normalizar a imagem, e prepará-la para a terceira etapa, que faz a redução  da imagem.

        val bitmap2 = cutBitmapToSquare(bitmap1)

        // Na terceira etapa do processamento faz-se a redução para a escala que será utilizada como
        // entrada para o processamento pela rede neural (exemplo: 50x50).

        val bitmap3 = resizeBitmap(bitmap2, scale, scale)

        // Na quarta etapa do processamento da imagem faz-se com que os pixels de fundo fiquem brancos,
        // enquanto o contorno do desenho é mantido preto.

        return repaintBitmapBackground(bitmap3)

    }


    /**
     * Transforma o bitmap normalizado em uma string aonde caractere "1" representa pixel preto e
     * caractere "0" significa pixel branco. Mapeia linha por linha da matriz.
     *
     * @param bitmap bitmap a ser transformado.
     *
     * @return string representando o bitmap.
     */
    fun bitmapToString(bitmap: Bitmap): String {

        val sb = StringBuilder(bitmap.width * bitmap.height)

        for (x in 0 until bitmap.width) {

            for (y in 0 until bitmap.height) {

                if (bitmap[x, y] == Color.BLACK) {
                    sb.append("1")
                } else {
                    sb.append("0")
                }

            }

        }

        return sb.toString()

    }


    /**
     * Transforma o bitmap em um array de double onde 1.0 representa pixel preto e 0.0 pixel branco.
     * Mapeia linha por linha do bitmap.
     *
     * @param bitmap bitmap a ser transformado.
     *
     * @return array de double representando a imagem.
     */
    fun bitmapToFloatArray(bitmap: Bitmap): Array<Float> {

        val data = Array(bitmap.width * bitmap.height) { 0.0f }
        var index = 0

        for (x in 0 until bitmap.width) {

            for (y in 0 until bitmap.height) {

                if (bitmap[x, y] == Color.BLACK) {
                    data[index++] = 1.0f
                } else {
                    data[index++] = 0.0f
                }

            }

        }

        return data

    }


    /**
     * Transforma um array de bytes em um bitmap.
     *
     * @param imageStream array de bytes a ser transformado.
     *
     * @return bitmap correspondente.
     */
    fun imageStreamToBitmap(imageStream: ByteArray): Bitmap {

        return BitmapFactory.decodeByteArray(
            imageStream,
            0,
            imageStream.size
        )

    }


    /**
     * Transforma um bitmap em um array de byte.
     *
     * @param bitmap bitmap a ser transformado.
     *
     * @return array de bytes correspondente.
     */
    fun bitmapToImageStream(bitmap: Bitmap): ByteArray {

        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()

    }


}