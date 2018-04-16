package de.smeurer.filemover.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import de.smeurer.filemover.R
import de.smeurer.filemover.data.MoveConfig


class MainActivity : AbstractBaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MoveConfigAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        viewManager = LinearLayoutManager(this)
        val moveConfigs = getMoveConfigs().toMutableList()
        moveConfigs.sortWith(Comparator({ m1, m2 -> m1.createdAt.compareTo(m2.createdAt) }))
        viewAdapter = object : MoveConfigAdapter(moveConfigs) {
            override fun onCardClicked(moveConfig: MoveConfig) {
                startConfigDetails(moveConfig)
            }

            override fun onRunClicked(moveConfig: MoveConfig) {
                runMoveConfig(moveConfig)
            }

        }

        recyclerView = findViewById<RecyclerView>(R.id.main_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP) {

                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    //Remove swiped item from list and notify the RecyclerView
                    val position = viewHolder.adapterPosition

                    val moveConfig = viewAdapter.getItem(position)

                    deleteMoveConfig(moveConfig)
                    viewAdapter.removeItem(position)

                    viewAdapter.notifyDataSetChanged()
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(this)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

    }


    fun startConfigDetails(moveConfig: MoveConfig) {
        val intent = Intent(this, MoveConfigDetailActivity::class.java)
        intent.putExtra("move_config", moveConfig)
        startActivity(intent)
    }

    fun onFabClicked(v: View) {
        val intent = Intent(this, MoveConfigDetailActivity::class.java)
        startActivity(intent)
    }

}
