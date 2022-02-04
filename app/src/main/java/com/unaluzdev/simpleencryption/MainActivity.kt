package com.unaluzdev.simpleencryption

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.unaluzdev.simpleencryption.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CipherViewModel by viewModels()
    private lateinit var encryptionMethods: Array<String>

    enum class Methods(val RID: Int) {
        CAESAR(R.string.caesar),
        SIMPLE_SUBSTITUTION(R.string.simple_substitution),
        ONE_TIME_PAD(R.string.one_time_pad)
    }

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
                getString(Methods.SIMPLE_SUBSTITUTION.RID),
                false
            )
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
        val method = binding.menuAutoCompleteTextView.text.toString()

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

        var digitError = false
        var lengthError = false
        val newMessage: String? = when (method) {
            getString(Methods.SIMPLE_SUBSTITUTION.RID) ->
                viewModel.simpleSubstitutionCipher(message, keyword, decrypt)
            getString(Methods.CAESAR.RID) -> {
                if (!keyword.isDigitsOnly()) {
                    digitError = true
                    null // newMessage will be null
                } else viewModel.caesarCipher(message, keyword.toInt(), decrypt)
            }
            getString(Methods.ONE_TIME_PAD.RID) -> {
                if (keyword.length != message.length) {
                    lengthError = true
                    null
                } else viewModel.oneTimePadCipher(message, keyword, decrypt)
            }
            else -> null
        }

        if (newMessage.isNullOrBlank()) {
            Toast.makeText(
                this,
                when {
                    digitError -> getString(R.string.error_keyword_not_natural_number)
                    lengthError -> getString(R.string.error_keyword_and_message_length_differ)
                    else -> getString(R.string.error_unknown_at_cipher)
                },
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // If everything went well and message was ciphered show ir
        binding.messageEditTextField.setText(newMessage)

        return true
    }
}
