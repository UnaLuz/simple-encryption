package com.unaluzdev.simpleencryption

/**
 * Removes the duplicate characters of a given list
 */
fun withoutDuplicates(charList: List<Char>) = charList.toSet().toList()

/**
 * Receives two Char lists, an original list to get the index from of the char,
 * and a new list to get the new char at that position/index.
 *
 * Returns the char that is in the same position as the given char but in the new list.
 * Returns the same char if the char is not in the original list.
 *
 * Raises an error if the index found is greater than the max index of the new list.
 */
fun newChar(newCharList: List<Char>, originalCharList: List<Char>, char: Char): Char {
    val index = originalCharList.indexOf(char)
    return if (index != -1) newCharList[index] else char
}

fun Int.asChar(): Char = this.mod(Char.MAX_VALUE.code).toChar()