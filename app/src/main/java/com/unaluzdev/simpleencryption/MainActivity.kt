package com.unaluzdev.simpleencryption

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the strings for the menu and the dropdown view
        val encryptionMethods = resources.getStringArray(R.array.encryptionMethods)
        val dropdownMenu = findViewById<AutoCompleteTextView>(R.id.menuAutoCompleteTextView)
        // Set the adapter
        val adapter = ArrayAdapter(
            this, R.layout.menu_list_item,
            encryptionMethods
        )
        with(dropdownMenu){
            setAdapter(adapter)
            setText(getString(R.string.simple_substitution), false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.layout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.info -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.disclaimer))
                    .setMessage(resources.getString(R.string.app_encryption_info))
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
            else -> false
        }
    }
}