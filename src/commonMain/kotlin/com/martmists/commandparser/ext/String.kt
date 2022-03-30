package com.martmists.commandparser.ext

internal fun String.strip(): String {
    if (isEmpty()) return this
    if (isBlank()) return ""

    var start = 0
    var end = this.lastIndex
    while (start != end && this[start].isWhitespace()) {
        start += 1
    }
    while (start != end && this[end].isWhitespace()) {
        end -= 1
    }
    return this.substring(start, end + 1)
}
