Aplicativo Kotlin para sistemas Android que implementa uma rede neural artificial de camada única do tipo perceptron. O objetivo do aplicativo é a demonstração de como este modelo de rede neural é implementado na prática.

Classe do Perceptron:

package tests.android.ocr.perceptron

import java.io.Serializable

class Perceptron(

    /**Número de entradas (features).*/
    private val inputSize: Int,

    /**Número de neurônios de saída.*/
    private val outputSize: Int

): Serializable {


    companion object {
        private const val serialVersionUID: Long = 1L
    }


    /**
     * Matriz de pesos com dimensões [[outputSize, inputSize]]. Cada peso é inicializado com um
     * valor aleatório entre -0.5 e 0.5.
     */
    private val weights: Array<FloatArray> = Array(outputSize) {
        FloatArray(inputSize) { (0..100).random() / 100f - 0.5f }
    }

    /**Vetor de viés (bias), um para cada saída, todos iniciados com 0.*/
    private val biases: FloatArray = FloatArray(outputSize) { 0f }


    /**
     * Função de ativação degrau binário.
     *
     * @param sum soma ponderada.
     *
     * @return Valor 1.0, se a soma ponderada for positiva ou zero. Valor 0.0, caso contrário.
     */
    private fun activationFunction(sum: Float): Float {

        return if (sum >= 0) 1.0f else 0.0f

    }


    /**
     * Função de treinamento. O código segue o algoritmo original de Rosenblatt. O treinamento é um
     * processo iterativo de ajuste de pesos e viés (bias) com base nos erros, até que o modelo
     * consiga classificar corretamente todas as amostras de treinamento.
     *
     * @param samples amostras obtidas do banco de dados.
     *
     * @param targets saídas esperadas.
     *
     * @param epochs número máximo de épocas para o treinamento.
     *
     * @param learningRate taxa de aprendizado usada para ajustar os pesos.
     *
     * @return número de épocas para convergir (erro = zero).
     */
    fun train(samples: Array<FloatArray>, targets: Array<FloatArray>, epochs: Int, learningRate: Float): Int  {

        // Contador de épocas.

        var iterations = 0

        // Loop principal de treinamento. Cada iteração desse loop é chamada de época. Uma época
        // representa uma passagem completa por todas as amostras de treinamento. Se a rede não
        // convergir (erro total = 0) ao final do número máximo de épocas, sai, para não entrar
        // possivelmente em um loop infinito.

        for (epoch in 1..epochs) {

            // Acumula o erro absoluto de todas as saídas.

            var totalError = 0f

            // Loop que itera sobre cada amostra.

            for (i in samples.indices) {

                // Vetor de entradas da amostra no índice i.

                val input = samples[i]

                // Vetor de saídas esperadas para a amostra no índice i.

                val target = targets[i]

                // Neurônios de saída.

                val outputs = FloatArray(outputSize)

                // Loop que itera sob cada neurônio de saída [outputs].

                for (t in 0 until outputSize) {

                    // Soma Ponderada (weightedSum): Para cada neurônio de saída, o Perceptron
                    // calcula a soma ponderada. Isso é feito multiplicando cada valor de
                    // entrada (input[j]) pelo seu peso correspondente (weights[t][j]) e somando
                    // o resultado com o viés do neurônio (biases[t]). A fórmula é:
                    //
                    //          inputSize
                    //   z[t] = ∑ (x[j] ⋅ w[t,j]) + b[t]
                    //          j=1
                    //
                    // Onde:
                    //
                    //   z[t]: é a soma ponderada para o neurônio de saída t.
                    //   x[j]: é o j-ésimo valor no vetor de entrada.
                    //   w[t,j]: é o peso que conecta a j-ésima entrada ao t-ésimo neurônio de saída.
                    //   b[t]: é o bias para o t-ésimo neurônio de saída.

                    val weightedSum = input.zip(weights[t]) { x, w -> x * w }.sum() + biases[t]

                    // Aplica a soma ponderada z[t] à função de ativação: A função de ativação é a
                    // degrau binário, que produz uma saída de 1 se a soma ponderada for não-negativa,
                    // e 0, caso contrário.
                    //
                    //          ┌
                    //          │ 1.0, se z[t] ≥ 0
                    //   y[t] = ┤
                    //          │ 0.0, se z[t] < 0
                    //          └
                    //
                    // Onde y[t] é a saída calculada para o neurônio de saída t.

                    outputs[t] = activationFunction(weightedSum)

                    // Calcula o erro. O erro é a diferença entre a saída esperada (target[t]) e a
                    // saída calculada pelo Perceptron (outputs[t]).

                    val error = target[t] - outputs[t]

                    // O totalError acumula o valor absoluto desse erro para todas as saídas de
                    // todas as amostras.

                    totalError += kotlin.math.abs(error)

                    // Loop que atualiza os pesos para cada entrada conectada ao neurônio t.

                    for (j in weights[t].indices) {

                        // O peso de cada conexão com o neurônio t é atualizado com base na regra de
                        // aprendizagem do Perceptron:
                        //
                        //     w(novo) = w(antigo) + taxa_de_aprendizado ⋅ erro ⋅ entrada
                        //
                        // Onde:
                        //
                        // * w(novo): Novo peso calculado.
                        //
                        // * w(antigo): Peso anterior.
                        //
                        // * taxa_de_aprendizado: Controla o tamanho do passo de ajuste. Um valor
                        // pequeno torna o aprendizado mais lento, mas mais estável; um valor grande
                        // pode levar a instabilidade e não convergência.
                        //
                        // * erro: A direção do ajuste depende do erro. Se o erro for positivo, o
                        // peso aumenta; se for negativo, diminui. Se o erro for zero, o peso não
                        // muda.
                        //
                        // * entrada: O ajuste é proporcional ao valor da entrada. Se a entrada for
                        // grande, o ajuste é maior.

                        weights[t][j] += learningRate * error * input[j]

                    }

                    // Ajuste do viés (bias) do neurônio t: O viés é ajustado de maneira semelhante
                    // aos pesos, mas sem a influência da entrada.
                    //
                    //     b(novo) = b(antigo) + taxa_de_aprendizado ⋅ erro
                    //
                    // Onde:
                    //
                    // * b(novo): Novo bias calculado.
                    //
                    // * b(antigo): Bias anterior.
                    //
                    // * taxa_de_aprendizado: A taxa de aprendizado controla o tamanho do passo para
                    // a atualização do bias. Ela age como um fator de escala que modera o impacto
                    // do erro. Uma taxa de aprendizado alta pode fazer o bias ser atualizado
                    // drasticamente, o que pode levar a um aprendizado instável e a um modelo que
                    // nunca converge para uma solução ideal. Uma taxa de aprendizado baixa, por outro
                    // lado, garante um aprendizado mais lento e estável, mas pode levar mais tempo
                    // para convergir.
                    //
                    // * erro: Um erro positivo significa que a saída do modelo foi menor que o valor
                    // alvo, então o bias deve ser aumentado. Um erro negativo significa que a saída
                    // do modelo foi maior que o valor alvo, então o bias deve ser diminuído. Quanto
                    // maior o valor absoluto do erro, maior será a correção.

                    biases[t] += learningRate * error

                }

            }

            // Se não houver erro, interrompe o treinamento e salva a época de convergência.

            if (totalError == 0f) {
                iterations = epoch
                break
            }

        }

        // Retorna o número de épocas até convergência (ou zero se não convergir).

        return iterations

    }


    /**
     * Função de inferência. Calcula as saídas do Perceptron.
     *
     * @param input vetor de entradas do padrão a ser classificado.
     *
     * @return saídas calculadas.
     */
    fun calculate(input: FloatArray): FloatArray {

        // Cria o vetor de neurônios de saída.

        val outputs = FloatArray(outputSize)

        // Calcula cada saída y[t].

        for (t in 0 until outputSize) {

            // Calcula a soma ponderada:
            //
            // A soma ponderada, que representa a entrada total para cada neurônio, é calculada
            // multiplicando cada valor de entrada pelo seu peso correspondente e somando todos
            // os resultados, e então somando o bias.
            //
            // Para cada neurônio de saída t, a soma ponderada é dada por:
            //
            //          inputSize
            //   z[t] = ∑ (x[j] ⋅ w[t,j]) + b[t]
            //          j=1
            //
            // Onde:
            //
            //   z[t]: é a soma ponderada para o neurônio de saída t.
            //   x[j]: é o j-ésimo valor no vetor de entrada.
            //   w[t,j]: é o peso que conecta a j-ésima entrada ao t-ésimo neurônio de saída.
            //   b[t]: é o bias para o t-ésimo neurônio de saída.

            val weightedSum = input.zip(weights[t]) { x, w -> x * w }.sum() + biases[t]

            // Aplica a soma ponderada z[t] à função de ativação:
            //
            // A função de ativação é a degrau binário, que produz uma saída de 1 se a soma ponderada
            // for não-negativa, e 0, caso contrário.
            //
            // A saída para o neurônio t é dada por:
            //
            //          ┌
            //          │ 1.0, se z[t] ≥ 0
            //   y[t] = ┤
            //          │ 0.0, se z[t] < 0
            //          └
            //
            // Onde y[t] é a saída calculada para o neurônio t.

            outputs[t] = activationFunction(weightedSum)

        }

        // Retorna o vetor de neurônios de saída. Neurônios ativados são representados por 1.0.
        // Neurônios desativados são representados por 0.0.

        return outputs

    }


}

Para mais informações sobre o modelo, consulte a página:

https://en.wikipedia.org/wiki/Perceptron


https://github.com/user-attachments/assets/bcd23024-daaa-4137-8f0b-50aa2bf9be98
