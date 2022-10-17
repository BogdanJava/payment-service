package by.bahdan.paymentplatform.utils

import java.util.stream.Collectors

fun String.containsNonDigits(): Boolean {
    return chars().boxed().map { Char(it) }.filter { !it.isDigit() }.collect(Collectors.toList()).size != 0
}