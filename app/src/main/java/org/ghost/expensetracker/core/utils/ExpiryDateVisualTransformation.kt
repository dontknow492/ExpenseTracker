package org.ghost.expensetracker.core.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // 1. Clean the input to only have digits, max 4.
        val originalText = text.text
        val digitsOnly = originalText.filter { it.isDigit() }.take(4)

        // 2. Build the formatted output string with a slash.
        val out = buildString {
            digitsOnly.forEachIndexed { index, char ->
                append(char)
                if (index == 1) { // Add slash after the second digit
                    append('/')
                }
            }
        }

        // 3. Create a smart OffsetMapping to handle cursor positioning.
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Count how many digits are in the original text up to the cursor's position.
                val digitsBeforeCursor = originalText.take(offset).count { it.isDigit() }
                // The new cursor position is the number of digits, plus 1 if we've passed the slash position.
                return when {
                    digitsBeforeCursor <= 1 -> digitsBeforeCursor
                    // The slash is at index 2, so cursor position is num of digits + 1
                    digitsBeforeCursor <= 4 -> digitsBeforeCursor + 1
                    else -> 5 // Cap at the end of "MM/YY"
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Count digits in the formatted text up to the cursor.
                val digitsInTransformed = out.take(offset).count { it.isDigit() }

                // Find the index of the Nth digit in the original, unfiltered text.
                var digitIndex = -1
                var digitsFound = 0
                for (i in originalText.indices) {
                    if (originalText[i].isDigit()) {
                        digitsFound++
                        if (digitsFound == digitsInTransformed) {
                            digitIndex = i
                            break
                        }
                    }
                }
                return digitIndex + 1
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}