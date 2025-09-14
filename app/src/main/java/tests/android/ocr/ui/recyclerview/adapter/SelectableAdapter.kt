package tests.android.ocr.ui.recyclerview.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import tests.android.ocr.database.model.Selectable

/**
 * Adaptador para uma RecyclerView com seleção de itens. Permite tratar evento de onSelectItem nos
 * items da lista. O evento onSelectItem é disparado quando o item é selecionado ou desselecionado
 * na lista permitindo executar ações relacionadas.[<br><br>]
 * Os items que preenchem esta lista devem, obrigatóriamente, implementar a interface [Selectable]
 */
abstract class SelectableAdapter<T: Selectable>(resource: Int, showGridColor: Boolean = true): Adapter<T>(resource, showGridColor) {


    /**Tratador de evento de seleção de itens na list view.*/
    private var onSelectItemHandler: ((position: Int, selected: Boolean)-> Unit)? = null

    /**Flag para controle de seleção de itens.*/
    private var activatedSelection = false


    /**Lista de items selecionados na RecyclerView.*/
    val selectedItems: List<T> get() = dataList.filter { it.isSelected() }


    override fun onDataListChanged(changedDataListSize: Boolean) {

        super.onDataListChanged(changedDataListSize)

        activatedSelection = false

        for (position in 0 until dataList.size) {

            val view = recyclerView?.findViewHolderForAdapterPosition(position)?.itemView
            val background1 = view?.background

            val background2 = if (showGridColor) {
                getDefaultBackground(position)
            } else {
                getDefaultBackground()
            }

            if (background1 != background2) {
                view?.background = background2
                notifyItemChanged(position)
            }

        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = (holder as Adapter<T>.ViewHolder)

        viewHolder.bind(dataList[position], position, false)

        if (dataList[position].isSelected()) {

            viewHolder.view.background = getSelectedBackground()

        } else {

            if (showGridColor) {
                viewHolder.view.background = getDefaultBackground(position)
            } else {
                viewHolder.view.background = getDefaultBackground()
            }

        }

        //Atribui tratador de evento de 0nClick ao item da RecyclerView.
        viewHolder.view.setOnClickListener {
            if (activatedSelection) {
                toggleSelection(position)
            }
        }

        //Atribui tratador de evento de OnLongClick ao item da RecyclerView.
        viewHolder.view.setOnLongClickListener {

            activatedSelection = !activatedSelection

            if (activatedSelection) {

                //Modo de seleção foi ativado.
                toggleSelection(position)

            } else {

                //Modo de seleção foi desativado.
                for (i in 0 until  dataList.size) {
                    val item = dataList[i]
                    item.setSelected(false)
                    notifyItemChanged(i)
                }

                if (onSelectItemHandler != null) {
                    onSelectItemHandler!!.invoke(position, false)
                }

            }

            true

        }

    }


    /**
     * Alterna o estado de selecionado/desselecionado na RecyclerView.
     *
     * @param position posição do clique no item da RecyclerView.
     */
    private fun toggleSelection(position: Int) {

        dataList[position].setSelected(!dataList[position].isSelected())

        if (onSelectItemHandler != null) {
            onSelectItemHandler!!.invoke(
                position,
                dataList[position].isSelected()
            )
        }

        notifyItemChanged(position)

    }


    /**
     * Obter o background padrão para uma linha selecionada de uma RecyclerView.
     */
    private fun getSelectedBackground(): GradientDrawable {

        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 0f
            setColor(Color.rgb(232, 240, 254))
        }

    }


    /**
     * Atribuir o tratador de evento de selecionar/desselecionar itens da RecyclerView.
     *
     * @param onSelectItemHandler método para tratar o evento de selecionar item da RecyclerView.
     */
    fun setOnSelectItemHandler(onSelectItemHandler: (position: Int, selected: Boolean)-> Unit) {

        this.onSelectItemHandler = onSelectItemHandler

    }


}