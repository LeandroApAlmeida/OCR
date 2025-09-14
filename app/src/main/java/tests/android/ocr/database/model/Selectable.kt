package tests.android.ocr.database.model

/**
 * Define um objeto que é selecionável.
 */
interface Selectable {

    /**
     * Define se o objeto está ou não selecionado.
     *
     * @param selected true, o objeto está selecionado, false, não está selecionado.
     */
    fun setSelected(selected: Boolean)

    /**
     * Retorna o status de selecionado de um objeto.
     *
     * @return true, o objeto está selecionado, false, o objeto não está selecionado.
     */
    fun isSelected(): Boolean

}