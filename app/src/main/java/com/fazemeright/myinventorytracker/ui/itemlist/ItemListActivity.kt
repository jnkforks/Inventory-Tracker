package com.fazemeright.myinventorytracker.ui.itemlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fazemeright.myinventorytracker.R
import com.fazemeright.myinventorytracker.database.InventoryDatabase
import com.fazemeright.myinventorytracker.databinding.ActivityItemListBinding
import com.fazemeright.myinventorytracker.ui.addbag.AddBagActivity
import com.fazemeright.myinventorytracker.ui.additem.AddItemActivity
import com.fazemeright.myinventorytracker.ui.itemdetail.ItemDetailActivity
import com.google.android.material.snackbar.Snackbar


class ItemListActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView

    val viewModel: ItemListViewModel by viewModels {
        BaseViewModelFactory(
            dataSource = InventoryDatabase.getInstance(application)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityItemListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_item_list)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        val manager = LinearLayoutManager(this)

        binding.itemList.layoutManager = manager

        val adapter = ItemListAdapter(ItemListAdapter.ItemListener(
            clickListener = { item ->
                viewModel.onItemClicked(item)
            },
            deleteClickListener = { itemId ->
                showConfirmationDialog(itemId)
//                TODO("Implement AlertDialog for confirmation before delete")
                viewModel.onDeleteItemClicked(itemId)
            }
        ))

        binding.itemList.adapter = adapter

        viewModel.items.observe(this, Observer {
            it?.let {
                adapter.updateList(it)
            }
            Log.i("ItemListActivity", "onCreate: $it")
        })

        /*viewModel.searchItems.observe(this, Observer {
            it?.let {
                adapter.updateList(it)
            }
        })*/

        viewModel.deletedItem.observe(this, Observer { deletedItem ->
            // Show a snack bar for undo option
            Snackbar.make(
                binding.root, // Parent view
                "Item deleted from database.", // Message to show
                Snackbar.LENGTH_LONG //
            ).setAction( // Set an action for snack bar
                "Undo" // Action button text
            ) {
                // Action button click listener
                // Do something when undo action button clicked
                viewModel.undoDeleteItem(deletedItem)
            }.show()
        })

        viewModel.navigateToAddItemActivity.observe(this, Observer { navigate ->
            if (navigate) {
                startActivity(Intent(this, AddItemActivity::class.java))
                viewModel.onNavigationToAddItemFinished()
            }
        })

        viewModel.navigateToItemDetailActivity.observe(this, Observer { itemInBag ->
            itemInBag?.let {
                val intent = Intent(this, ItemDetailActivity::class.java)
                    .apply { putExtra("itemInBag", it) }
                startActivity(intent)
                viewModel.onNavigationToItemDetailFinished()
            }
        })

        viewModel.bags.observe(this, Observer { bagList ->
            bagList?.let {
                adapter.updateBagList(bagList)
            }
        })
    }

    private fun showConfirmationDialog(itemId: Int) {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder
            // set title for alert dialog box
            .setTitle("Are you sure")
            .setMessage("Do you want to delete this entry?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onDeleteItemClicked(itemId)
            }
            // negative button text and action
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu!!.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "Search Inventory for Items"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onSearchClicked(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.onSearchClicked(query)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_bag -> startActivity(Intent(this, AddBagActivity::class.java))
        }
        return true
    }
}
