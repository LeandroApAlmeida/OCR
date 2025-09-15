package tests.android.ocr.android

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

/**
 * Classe para gravar as configurações do app via API SharedPreferences.
 */
class Settings(context: Context) {


    private val PREFERENCES = "ocr_app"

    /**Leitor das configurações.*/
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    /**Editor das configurações.*/
    private val editor: SharedPreferences.Editor = context.getSharedPreferences(PREFERENCES, AppCompatActivity.MODE_PRIVATE).edit()


    /**
     * Obter um valor String de configuração.
     *
     * @param key chave da configuração.
     *
     * @param default valor default.
     *
     * @return valor String de configuração.
     */
    fun getString(key: String, default: String): String? = prefs.getString(key, default)


    /**
     * Escrever um valor String de configuração.
     *
     * @param key chave de configuração.
     *
     * @param value valor da configuração.
     *
     * @return true, escrito com sucesso, false, não foi escrito.
     */
    fun setString(key: String, value: String): Boolean = with (editor) {
        putString(key, value)
        commit()
    }


}