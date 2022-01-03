package com.unaluzdev.simpleencryption

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var encryptionMethods: Array<String>
    private lateinit var messageEditTextField: EditText
    private lateinit var keywordEditTextField: EditText
    private lateinit var dropdownMenu: AutoCompleteTextView

    private val alphabet = ('A'..'Z').toList()

    enum class Methods(val RID: Int) {
        CAESAR(R.string.caesar),
        SIMPLE_SUBSTITUTION(R.string.simple_substitution),
        ONE_TIME_PAD(R.string.one_time_pad)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Late init vars
        encryptionMethods = resources.getStringArray(R.array.encryptionMethods)
        messageEditTextField = findViewById(R.id.messageEditTextField)
        keywordEditTextField = findViewById(R.id.keywordEditTextField)
        dropdownMenu = findViewById(R.id.menuAutoCompleteTextView)

        configureDropdownMenu()

        // Configure the encryption button
        val encryptButton = findViewById<Button>(R.id.encryptButton)
        encryptButton.setOnClickListener {
            cipher()
        }

        val decryptButton = findViewById<Button>(R.id.DecryptButton)
        decryptButton.setOnClickListener {
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
        with(dropdownMenu) {
            setAdapter(adapter)
            // Put the selected encryption method or simple_substitution by default
            setText(
                getString(Methods.SIMPLE_SUBSTITUTION.RID),
                false
            )
        }
    }

    private fun cipher(decrypt: Boolean = false): Boolean {
        val message = messageEditTextField.text.toString()
        val keyword = keywordEditTextField.text.toString()
        val method = dropdownMenu.text.toString()

        if (decrypt) {
            println("Decrypting the message '${message}' with the method '${method}'")
            when (method) {
                getString(Methods.SIMPLE_SUBSTITUTION.RID) -> decryptSS(message, keyword)
                getString(Methods.CAESAR.RID) -> decryptCaesar(message, keyword)
                getString(Methods.ONE_TIME_PAD.RID) -> decryptOTP(message, keyword)
            }
        } else {
            println("Encrypting the message '${message}' with the method '${method}'")
            when (method) {
                getString(Methods.SIMPLE_SUBSTITUTION.RID) -> encryptSS(message, keyword)
                getString(Methods.CAESAR.RID) -> encryptCaesar(message, keyword)
                getString(Methods.ONE_TIME_PAD.RID) -> encryptOTP(message, keyword)
            }
        }
        return decrypt
    }

    private fun encryptSS(message: String, keyword: String) {
        TODO("Think how to encrypt the message")
    }
}
