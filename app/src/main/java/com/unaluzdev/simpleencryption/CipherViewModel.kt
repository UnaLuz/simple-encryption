package com.unaluzdev.simpleencryption

import android.util.Log
import androidx.annotation.StringRes
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel

class CipherViewModel : ViewModel() {

    sealed class CipherMethod(@StringRes val RID: Int) {
        object Caesar : CipherMethod(R.string.caesar)
        object SimpleSubstitution : CipherMethod(R.string.simple_substitution)
        object OneTimePad : CipherMethod(R.string.one_time_pad)

        companion object {
            fun stringResourceList(): List<Int> {
                return listOf(
                    Caesar.RID,
                    SimpleSubstitution.RID,
                    OneTimePad.RID
                )
            }
        }
    }

    private val alphabet = ('A'..'Z').toList() + ('a'..'z').toList()

    private val stringResourceList = CipherMethod.stringResourceList()

    private var _methodSelected: Int = R.string.simple_substitution
    val methodSelected get() = _methodSelected

    fun onMethodSelected(itemMethod: String, lambda: (Int) -> String) {
        // Map the string resource IDs of the cipher methods with their Strings
        val mappedCipherMethodRIDs = stringResourceList.associateBy(lambda)
        // Try to get the resource ID of the item selected
        mappedCipherMethodRIDs[itemMethod]?.let { _methodSelected = it }
            ?: run { Log.e("CipherViewModel", "Unknown encryption method $itemMethod") }
    }

    fun onTryCipher(message: String, keyword: String, decrypt: Boolean = false): String? {
        return when (_methodSelected) {
            CipherMethod.SimpleSubstitution.RID -> {
                simpleSubstitutionCipher(message, keyword, decrypt)
            }
            CipherMethod.Caesar.RID -> {
                if (keyword.isDigitsOnly())
                    caesarCipher(message, keyword.toInt(), decrypt)
                else null
            }
            CipherMethod.OneTimePad.RID ->
                if (message.length == keyword.length)
                    oneTimePadCipher(message, keyword, decrypt)
                else null
            else -> null
        }
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
            (if (decrypt) char.code - keyword[index].code
            else char.code + keyword[index].code).asChar()
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
}
