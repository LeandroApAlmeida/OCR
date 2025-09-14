package tests.android.ocr.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Classe que representa os parâmetros para o treinamento da rede neural perceptron.
 */
@Entity(

    tableName = "params"

)
data class Params(

    /**Identificador chave primária do registro.*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long,

    /**Número de entradas (exemplo, imagem 50x50 pixels, com 2500 entradas).*/
    @ColumnInfo("input_size")
    var inputSize: Int,

    /**Número de neurônios de saída.*/
    @ColumnInfo("target_size")
    var outputSize: Int,

    /**Taxa de aprendizado.*/
    @ColumnInfo("learning_rate")
    var learningRate: Float,

    /**Número máximo de épocas para o treinamento.*/
    @ColumnInfo("epochs")
    var epochs: Int

)