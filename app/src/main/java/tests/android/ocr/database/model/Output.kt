package tests.android.ocr.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Classe de entidade que representa uma saída da rede neural. Uma saída, representa uma classe
 * que será reconhecida pelo Perceptron. Por exemplo, se o padrão a ser reconhecido é de caracteres
 * do alfabeto, o resultado do processo será um caractere 'a', 'b', 'c', etc.
 *
 * Por exemplo:
 *
 * Sejam nossas classes para o modelo as consoantes do alfabeto a, e, i, o, u. As saídas alvo podem
 * ser definidas como 1 para neurônio ativado e 0 para neurônio desativado para indicar a posição
 * da consoante no alfabeto, desta forma:
 *
 * [<pre>]
 * Consoante  Saídas Alvo [<br>]
 * a          1-0-0-0-0 [<br>]
 * e          0-1-0-0-0 [<br>]
 * i          0-0-1-0-0 [<br>]
 * o          0-0-0-1-0 [<br>]
 * u          0-0-0-0-1 [<br>]
 * [</pre] [<br>]
 *
 * Na forma como está modelada, esta rede neural terá 5 neurônios na sua camada de saída, cada um
 * representando a consoante em sua respectiva posição no alfabeto. Para 'a', que é a primeira consoante,
 * a função de ativação será 1-0-0-0-0, significando que quando um 'a' é reconhecido, o primeiro
 * neurônio deve produzir como saída 1 (ativada), enquanto os demais devem produzir como saída
 * 0 (desativada). Da mesma forma, se um 'e' é reconhecido, então o segundo neurônio deve produzir
 * como saída 1 (ativada), enquanto os demais devem produzir como saída 0 (desativada), e assim
 * sucessivamente, até a consoante 'u', que será 0-0-0-0-1, indicando que o último neurônio terá a
 * saída ativada (1) e os demais terão a saída desativada (0).
 *
 * É um padrão bem simples e direto de representação, perfeito para o algoritmo de Perceptron
 * de Camada Única que vamos implementar neste projeto.
 */
@Entity(

    tableName = "output",

    indices = [
        Index(value = ["symbol"], unique = true),
        Index(value = ["target"], unique = true)
    ]

)
data class Output (


    /**Identificador chave primária do registro.*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long,

    /**Símbolo a ser associado à saída alvo. Cada símbolo representa uma classe a ser reconhecida
     * pela rede neural. No caso de exemplo, que os símbolos são as consoantes a, e, i, o, u, cada
     * consoante é um símbolo, e para cada símbolo, devemos associar uma saída alvo exclusiva,
     * representando a ativação ou não dos neurônios da camada de saída.*/
    @ColumnInfo(name = "symbol")
    var symbol: String,

    /**
     * Saídas alvo para o símbolo definido. No caso, use o padrão 1 para neurônio com a saída ativada
     * e 0 para neurônio com a saída desativada. Pode usar também o caracter "-" para separar cada
     * saída. Para cada símbolo, as saídas-alvo devem ter um padrão exclusivo, não podendo a mesma
     * saída ser associada a dois ous mais símbolos distintos.
     */
    @ColumnInfo(name = "target")
    var target: String,

    /**
     * Pronúncia da classe. Como estamos usando a API do sintetizador de fala do sistema
     * Android, então é parte do sistema a pronúncia daquela classe. Exemplo: "Consoante a".
     */
    @ColumnInfo(name = "pronunciation")
    var pronunciation: String


): Selectable {


    /**Status de objeto selecionado.*/
    @Ignore
    private var selected: Boolean = false


    override fun setSelected(selected: Boolean) {
        this.selected = selected
    }


    override fun isSelected(): Boolean {
        return selected
    }


    override fun equals(other: Any?): Boolean {

        if (other == null) return false

        if (other !is Output) return false

        return (other).id == this.id

    }


    override fun toString(): String {
        return symbol
    }


}