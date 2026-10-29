package com.example.madtlab2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pure Java text metrics component, independent of Android UI classes.
 *
 * A word is a Unicode letter/digit token, with apostrophes allowed inside
 * words. Numeric values containing separators (for example 9:30 or 12.50)
 * are treated as single word tokens. A number is a complete digit-only token,
 * optionally containing internal time, decimal, or grouping separators.
 * A sentence ends at !, ?, ., or an ellipsis, except decimal points between
 * digits do not end sentences.
 */
public final class TextMetricsCalculator {
    private static final Pattern WORD_PATTERN = Pattern.compile(
            "\\p{Nd}+(?:[.,:]\\p{Nd}+)+[\\p{L}\\p{N}]*"
                    + "|[\\p{L}\\p{N}]+(?:['’][\\p{L}\\p{N}]+)*");
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
            "\\p{Nd}+(?:[.,:]\\p{Nd}+)*");

    public TextMetrics calculate(String sourceText) {
        if (sourceText == null || sourceText.trim().isEmpty()) {
            return new TextMetrics(0, 0, 0, 0);
        }

        // Count both metrics from the same tokens. This prevents a time such
        // as "9:30" from being split into two words and two numbers.
        Matcher words = WORD_PATTERN.matcher(sourceText);
        int wordCount = 0;
        int numberCount = 0;
        while (words.find()) {
            wordCount++;
            if (NUMBER_PATTERN.matcher(words.group()).matches()) {
                numberCount++;
            }
        }

        return new TextMetrics(
                countSentences(sourceText),
                wordCount,
                countPunctuation(sourceText),
                numberCount);
    }

    private int countSentences(String text) {
        int count = 0;
        boolean hasContent = false;

        for (int index = 0; index < text.length(); ) {
            int current = text.codePointAt(index);
            int previous = index > 0 ? text.codePointBefore(index) : -1;
            int nextIndex = index + Character.charCount(current);
            int next = nextIndex < text.length() ? text.codePointAt(nextIndex) : -1;

            if (Character.isLetterOrDigit(current)) {
                hasContent = true;
            } else if (isSentenceTerminator(current)) {
                boolean isDecimalPoint = current == '.'
                        && previous >= 0 && next >= 0
                        && Character.isDigit(previous) && Character.isDigit(next);
                if (!isDecimalPoint && hasContent) {
                    count++;
                    hasContent = false;
                }
            }
            index = nextIndex;
        }

        return count + (hasContent ? 1 : 0);
    }

    private boolean isSentenceTerminator(int codePoint) {
        return codePoint == '.' || codePoint == '!'
                || codePoint == '?' || codePoint == '…';
    }

    private int countPunctuation(String text) {
        int count = 0;
        for (int index = 0; index < text.length(); ) {
            int codePoint = text.codePointAt(index);
            int type = Character.getType(codePoint);
            switch (type) {
                case Character.CONNECTOR_PUNCTUATION:
                case Character.DASH_PUNCTUATION:
                case Character.START_PUNCTUATION:
                case Character.END_PUNCTUATION:
                case Character.INITIAL_QUOTE_PUNCTUATION:
                case Character.FINAL_QUOTE_PUNCTUATION:
                case Character.OTHER_PUNCTUATION:
                    count++;
                    break;
                default:
                    break;
            }
            index += Character.charCount(codePoint);
        }
        return count;
    }
}
