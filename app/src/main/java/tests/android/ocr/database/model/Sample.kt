package tests.android.ocr.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Classe de entidade que representa uma amostra para o treinamento da rede neural. Cada amostra
 * será obtida da interface gráfica do usuário do aplicativo, desenhada em um componente PaintView
 * que eu mesmo implementei, que captura o movimento do dedo do usuário na tela do dispositivo e
 * cria os contornos do símbolo, que após passar pela etapa de processamento de imagem e pela de
 * normalização, será inserido no banco de dados já no formato acabado.[<br><br>]
 *
 * Cada amostra deverá, obrigatóriamente, estar associada à sua respectiva saída-alvo, para que
 * o algoritmo entenda seu contexto quando for realizar o treinamento da rede neural.
 */
@Entity(

    tableName = "sample",

    foreignKeys = [
        ForeignKey(
            entity = Output::class,
            parentColumns = ["id"],
            childColumns = ["id_output"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]

)
data class Sample (


    /**Identificador chave primária do registro.*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long,

    /**Identificador chave estrangeira para a respectiva saída alvo da amostra.*/
    @ColumnInfo(name = "id_output")
    var idOutput: Long,

    /**Dados da amostra, que é uma imagem bitmap, pré processada e normalizada.*/
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.BLOB)
    var data: ByteArray?


) : Selectable {


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

        if (other !is Sample) return false

        return (other).id == this.id

    }


    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + idOutput.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + selected.hashCode()
        return result
    }


}