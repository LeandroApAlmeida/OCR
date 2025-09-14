package tests.android.ocr.ui.recyclerview.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Adaptador padrão para uma RecyclerView. Permite tratar eventos de onClick e onLongClick nos
 * items da lista.
 */
abstract class DefaultAdapter<T>(resource: Int, showGridColor: Boolean = true): Adapter<T>(resource, showGridColor) {


    /**Tratador de evento de onClick. O método recebe o índice do item na lista.*/
    private var onClickHandler: ((position: Int)->Unit)? = null

    /**Tratador de evento de onLongClick. O método recebe o índice do item na lista.*/
    private var onLongClickHandler: ((position: Int)->Unit)? = null


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = (holder as Adapter<T>.ViewHolder)

        viewHolder.bind(dataList[position], position, true)

        //Atribui o tratador de evento de OnClick no item da RecyclerView.
        viewHolder.view.setOnClickListener {
            if (onClickHandler != null) {
                onClickHandler?.invoke(position)
            }
        }

        //Atribui o tratador de evento de OnLongClick no item da RecyclerView.
        viewHolder.view.setOnLongClickListener {
            if (onLongClickHandler != null) {
                onLongClickHandler?.invoke(position)
            }
            true
        }

    }


    /**
     * Atribui o tratador de evento de onClick para os itens da RecyclerView.
     *
     * @param onClickHandler tratador de evento de onClick.
     */
    fun setOnClickHandler(onClickHandler: (position: Int)->Unit) {

        this.onClickHandler = onClickHandler

    }


    /**
     * Atribui o tratador de evento de onLongClick para os itens da RecyclerView.
     *
     * @param onClickHandler tratador de evento de onLongClick.
     */
    fun setOnLongClickHandler(onLongClickHandler: (position: Int)->Unit) {

        this.onLongClickHandler = onLongClickHandler

    }


}