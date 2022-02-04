package com.unaluzdev.simpleencryption

import androidx.lifecycle.ViewModel

class CipherViewModel: ViewModel() {

    private val alphabet = ('A'..'Z').toList() + ('a'..'z').toList()

    /**
     * Ciphers the given 'message' using the given 'keyword and One-Time-Pad cipher method.
     * 'keyword' and 'message' must have the same length.
     * Accepts an optional argument 'decrypt', when true it subtracts instead of adding the char code.
     */
    fun oneTimePadCipher(
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
    fun caesarCipher(message: String, keyNumber: Int, decrypt: Boolean = false): String {
        val newMessage = message.map {
            (if (decrypt) it.code - keyNumber else it.code + keyNumber).asChar()
        }
        return newMessage.joinToString(separator = "")
    }

    /**
     * Ciphers the given 'message' using the given 'keyword' and the Simple Substitution method
     */
    fun simpleSubstitutionCipher(
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
