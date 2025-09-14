package tests.android.ocr.ui.recyclerview.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter para um RecyclerView padrão do aplicativo. Uma RecyclerView padrão tem o estilo listrado
 * sendo que as linhas ímpares tem a cor de fundo numa tonalidade de cinza e as linhas pares tem a
 * cor de fundo branca.
 */
abstract class Adapter<T>(var resource: Int, var showGridColor: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    /**RecyclerView ao qual o Adapter foi atribuído.*/
    protected var recyclerView: RecyclerView? = null

    /**Lista de dados da RecyclerView.*/
    protected var dataList: MutableList<T> = mutableListOf()


    /**Lista de dados da RecyclerView (propriedade).*/
    var data: List<T> get() = dataList
    set(newDataList) {

        // No método set é feito a atualização da lista de dados que serão exibidos na RecyclerView.
        // A atualização da RecyclerView é feita de modo a obter uma otimização de memória e de
        // processador, evitando desperdício de recursos do sistema.
        val changedDataListSize = (dataList.size != newDataList.size)
        val diffCallback = DiffCallback(dataList, newDataList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataList.clear()
        dataList.addAll(newDataList)
        diffResult.dispatchUpdatesTo(this)

        // Reconstrói o efeito de listrado após o redimensionamento da lista.
        if (changedDataListSize) {

            if (showGridColor) {

                for (position in 0 until dataList.size) {

                    val view = recyclerView?.findViewHolderForAdapterPosition(position)?.itemView
                    view?.background = getDefaultBackground(position)
                    notifyItemChanged(position)

                }

            }

        }

        // Invoca os procedimentos adicionais nas classes filhas para tratar ações
        // pós atualização da lista de dados.
        onDataListChanged(changedDataListSize)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Infla o layout do ViewHolder.

        val view = LayoutInflater.from(parent.context).inflate(
            resource,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

        // Obtém a referência para a RecyclerView.

        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
        this.recyclerView?.setHasFixedSize(true)

    }


    override fun getItemCount(): Int = dataList.size


    /**
     * Método que é executado ao fazer a vinculação dos dados ao componente de exibição na RecyclerView.
     *
     * @param view view para exibição dos dados.
     *
     * @param item dado a ser exibido.
     *
     * @param position posição do item na lista.
     */
    abstract fun onBinding(view: View, item: T, position: Int)


    /**
     * Método invocado quando a lista de dados para a RecyclerView é modificada. Por padrão,
     * o método posiciona a visualização da RecyclerView no primeiro item da lista.
     *
     * @param changedDataListSize true, a dimensão da lista foi alterada, false a dimensão se
     * manteve.
     */
    protected open fun onDataListChanged(changedDataListSize: Boolean) {

        recyclerView!!.scrollToPosition(0)

    }


    /**
     * Comparar o conteúdo dos items de dados. Como para cada classe o critério é diferente, é
     * delegada a implementação para cada classe filha na herença, que conhecerá os critérios
     * específicos de comparação.
     *
     * @param oldItem item antigo na posição.
     *
     * @param newItem novo item na posição
     */
    abstract fun compareItemsContents(oldItem: T, newItem: T): Boolean


    /**
     * Obter o background padrão para um componente de exibição de uma RecyclerView.
     *
     * @param position posição do item na lista. Linhas ímpares são de cor cinza, linhas
     * pares são de cor branca.
     */
    protected fun getDefaultBackground(position: Int): GradientDrawable {

        val color = if ((position + 1) % 2 == 1) {
            Color.rgb(250, 250, 250) //Cor cinza levemente escuro.
        } else {
            Color.rgb(255, 255, 255) //Cor branca.
        }

        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 0f
            setColor(color)
        }

    }


    /**
     * Obter o background padrão para um componente de exibição de uma RecyclerView.
     *
     * @param position posição do item na lista. Linhas ímpares são de cor cinza, linhas
     * pares são de cor branca.
     */
    protected fun getDefaultBackground(): GradientDrawable {

        val color = Color.rgb(255, 255, 255)

        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 0f
            setColor(color)
        }

    }


    /**
     * Define os métodos de uma [RecyclerView.ViewHolder] para a vinculação dos dados ao componente
     * de exibição da RecyclerView.
     */
    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {


        /**
         * Atribui os dados ao componente de exibição da RecyclerView e reconstrói o efeito de listrado
         * após reciclagem dos componentes.
         *
         * @param item componente de exibição da RecyclerView.
         *
         * @param position posição do item na lista de dados.
         *
         * @param updateBackground true, atualiza o background automáticamente, false, delega esta
         * tarefa às classes filhas na herança.
         */
        fun bind(item: T, position: Int, updateBackground: Boolean) {

            onBinding(view, item, position)

            if (updateBackground) {
                if (showGridColor) {
                    view.background = getDefaultBackground(position)
                } else {
                    view.background = getDefaultBackground()
                }
            }

        }


    }


    /**
     * Calcula a diferença entre duas listas.
     *
     * @param oldList lista antiga.
     *
     * @param newList lista nova.
     *
     * @see DiffUtil.Callback
     */
    private inner class DiffCallback(val oldList: List<T>, val newList: List<T>): DiffUtil.Callback() {


        override fun getOldListSize(): Int = oldList.size


        override fun getNewListSize(): Int = newList.size


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldList[oldItemPosition] == newList[newItemPosition])
        }


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // O critério de igualdade de conteúdo dos dados para a RecyclerView é definido nas
            // classes filhas na hierarquia, pois variam de classe para classe.
            return compareItemsContents (
                oldList[oldItemPosition],
                newList[newItemPosition]
            )
        }


    }


}