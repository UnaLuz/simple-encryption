package com.unaluzdev.simpleencryption

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.unaluzdev.simpleencryption.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var encryptionMethods: Array<String>

    private val alphabet = ('A'..'Z').toList() + ('a'..'z').toList()

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
                simpleSubstitutionCipher(message, keyword, decrypt)
            getString(Methods.CAESAR.RID) -> {
                if (!keyword.isDigitsOnly()) {
                    digitError = true
                    null // newMessage will be null
                } else caesarCipher(message, keyword.toInt(), decrypt)
            }
            getString(Methods.ONE_TIME_PAD.RID) -> {
                if (keyword.length != message.length) {
                    lengthError = true
                    null
                } else oneTimePadCipher(message, keyword, decrypt)
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

    /**
     * Ciphers the given 'message' using the given 'keyword and One-Time-Pad cipher method.
     * 'keyword' and 'message' must have the same length.
     * Accepts an optional argument 'decrypt', when true it subtracts instead of adding the char code.
     */
    private fun oneTimePadCipher(
        message: String,
        keyword: String,
        decrypt: Boolean = false
    ): String {
        val newMessage = message.mapIndexed { index, char ->
            (if (decrypt) char.code - keyword[index].code else char.code + keyword[index].code).asChar()
        }
        return newMessage.joinToString(separator = "")
    }

    /**
     * Ciphers the given 'message' using the given 'keyNumber' and Caesar cipher method.
     * 'keyNumber' must be a positive integer (or natural number).
     * Accepts an optional argument 'decrypt', when true it subtracts instead of adding the key
     */
    private fun caesarCipher(message: String, keyNumber: Int, decrypt: Boolean = false): String {
        val newMessage = message.map {
            (if (decrypt) it.code - keyNumber else it.code + keyNumber).asChar()
        }
        return newMessage.joinToString(separator = "")
    }

    /**
     * Ciphers the given 'message' using the given 'keyword' and the Simple Substitution method
     */
    private fun simpleSubstitutionCipher(
        message: String,
        keyword: String,
        decrypt: Boolean = false
    ): String {
        val noSpacesKeyword = keyword.replace(regex = Regex("\\s"), "")
        val modifiedAlphabet =
            withoutDuplicates(withoutDuplicates(noSpacesKeyword.toList()) + alphabet)
        val newMessage = message.map {
            if (decrypt) newChar(alphabet, modifiedAlphabet, it)
            else newChar(modifiedAlphabet, alphabet, it)
        }
        return newMessage.joinToString(separator = "")
    }

    /**
     * Removes the duplicate characters of a given list
     */
    private fun withoutDuplicates(charList: List<Char>) = charList.toSet().toList()

    /**
     * Receives two Char lists, an original list to get the index from of the char,
     * and a new list to get the new char at that position/index.
     *
     * Returns the char that is in the same position as the given char but in the new list.
     * Returns the same char if the char is not in the original list.
     *
     * Raises an error if the index found is greater than the max index of the new list.
     */
    private fun newChar(newCharList: List<Char>, originalCharList: List<Char>, char: Char): Char {
        val index = originalCharList.indexOf(char)
        return if (index != -1) newCharList[index] else char
    }

    private fun Int.asChar(): Char = this.mod(Char.MAX_VALUE.code).toChar()
}
