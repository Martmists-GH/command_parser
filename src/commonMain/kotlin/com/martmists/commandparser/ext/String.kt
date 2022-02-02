package com.martmists.commandparser.ext

internal fun String.strip() : String {
    var start = 0
    var end = this.lastIndex
    while (start != end && this[start].isWhitespace()) {
        start += 1
    }
    while (start != end && this[end].isWhitespace()) {
        end -= 1
    }
    return this.substring(start, end+1)
}
