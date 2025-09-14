package tests.android.ocr.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Classe de entidade que representa o aprendizado da rede neural.
 */
@Entity(

    tableName = "learning"

)
data class Learning(


    /**Identificador chave primária do registro.*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long,

    /**
     * Aprendizado da rede neural. Neste campo será gravado o objeto que representa o aprendizado da
     * rede neural.
     */
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.BLOB)
    var data: ByteArray?


) {


    override fun equals(other: Any?): Boolean {

        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as Learning

        if (id != other.id) return false

        if (data != null) {

            if (other.data == null) return false

            if (!data.contentEquals(other.data)) return false

        } else if (other.data != null) {

            return false

        }

        return true
    }


    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }


}
