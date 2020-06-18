package com.fazemeright.myinventorytracker.ui.itemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fazemeright.myinventorytracker.database.InventoryDatabase
import com.fazemeright.myinventorytracker.ui.addbag.AddBagViewModel
import com.fazemeright.myinventorytracker.ui.additem.AddItemViewModel

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the SleepDatabaseDao and context to the ViewModel.
 */
class BaseViewModelFactory(
    private val dataSource: InventoryDatabase
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ItemListViewModel::class.java) -> {
                ItemListViewModel(dataSource) as T
            }
            modelClass.isAssignableFrom(AddItemViewModel::class.java) -> {
                AddItemViewModel(dataSource) as T
            }
            modelClass.isAssignableFrom(AddBagViewModel::class.java) -> {
                AddBagViewModel(dataSource) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

