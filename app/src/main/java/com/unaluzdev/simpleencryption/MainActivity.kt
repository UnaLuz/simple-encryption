package com.unaluzdev.simpleencryption

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.unaluzdev.simpleencryption.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CipherViewModel by viewModels()
    private lateinit var encryptionMethods: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Late init vars
        encryptionMethods = resources.getStringArray(R.array.encryptionMethods)

        configureDropdownMenu()

        // Configure the encryption button
        binding.encryptButton.setOnClickListener {
            cipher()
        }

        binding.DecryptButton.setOnClickListener {
            cipher(decrypt = true)
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

    private fun configureDropdownMenu() {
        // Set the adapter
        val adapter = ArrayAdapter(
            this, R.layout.menu_list_item,
            encryptionMethods
        )
        with(binding.menuAutoCompleteTextView) {
            setAdapter(adapter)
            // Put the selected encryption method or simple_substitution by default
            setText(
                getString(viewModel.methodSelected),
                false
            )
            setOnItemClickListener { _, _, index, _ ->
                val item = adapter.getItem(index).orEmpty()
                val position = adapter.getItemId(index)
                Log.d("MainActivity", "Item: $item, position: $position, index: $index")

//                val itemMethod = encryptionMethods[item]
                viewModel.onMethodSelected(item) { getString(it) }
            }
        }
    }

    /**
     * Ciphers the message using the keyword according to the method selected
     * returns true if process was successful
     * returns false if message or keyword are blank
     */
    private fun cipher(decrypt: Boolean = false): Boolean {
        val message = binding.messageEditTextField.text.toString()
        val keyword = binding.keywordEditTextField.text.toString()

        if (message.isBlank()) {
            Toast.makeText(
                this,
                getString(R.string.error_message_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (keyword.isBlank()) {
            Toast.makeText(
                this,
                getString(R.string.error_keyword_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        val newMessage: String? = viewModel.onTryCipher(message, keyword, decrypt)

        if (newMessage.isNullOrBlank()) {
            Toast.makeText(
                this,
                getString(R.string.error_unknown_at_cipher),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // If everything went well and message was ciphered show ir
        binding.messageEditTextField.setText(newMessage)

        return true
    }
}
