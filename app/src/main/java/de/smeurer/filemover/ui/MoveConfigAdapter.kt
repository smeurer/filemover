package de.smeurer.filemover.ui

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import de.smeurer.filemover.R
import de.smeurer.filemover.data.MoveConfig
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Simon Meurer on 16.04.18.
 */
abstract class MoveConfigAdapter(private var moveConfigs: MutableList<MoveConfig>) :
        RecyclerView.Adapter<MoveConfigAdapter.ViewHolder>() {

    class ViewHolder(val cardView: CardView, val textViewFrom: TextView, val textViewTo: TextView, val textViewMode: TextView, val textViewExtensions: TextView, val button: Button) : RecyclerView.ViewHolder(cardView)


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MoveConfigAdapter.ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_move_config, parent, false) as CardView
        val textViewFrom = cardView.findViewById(R.id.card_textView_from) as TextView
        val textViewTo = cardView.findViewById(R.id.card_textView_to) as TextView
        val textViewMode = cardView.findViewById(R.id.card_textView_mode) as TextView
        val textViewExtensions = cardView.findViewById(R.id.card_textView_extensions) as TextView
        val button = cardView.findViewById(R.id.card_button) as Button

        return ViewHolder(cardView, textViewFrom, textViewTo, textViewMode, textViewExtensions, button)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moveConfig = moveConfigs[position]
        holder.textViewFrom.text = moveConfig.fromPath
        holder.textViewTo.text = moveConfig.toPath
        holder.textViewExtensions.text = moveConfig.extensionsString
        holder.textViewMode.text = moveConfig.mode.name
        holder.button.onClick { onRunClicked(moveConfig) }
        holder.cardView.onClick { onCardClicked(moveConfig) }
    }

    fun removeItem(position: Int) {
        try {
            moveConfigs.removeAt(position)
        } catch (e: Exception) {

        }
    }

    fun getItem(position: Int): MoveConfig? {
        return try {
            moveConfigs[position]
        } catch (e: Exception) {
            null
        }
    }

    override fun getItemCount() = moveConfigs.size

    abstract fun onCardClicked(moveConfig: MoveConfig)
    abstract fun onRunClicked(moveConfig: MoveConfig)
}