package com.example.madtlab2;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** Local JVM tests: count expectations are explicit and deterministic. */
public final class TextMetricsCalculatorTest {
    private final TextMetricsCalculator calculator = new TextMetricsCalculator();

    private static void expect(TextMetrics actual, int sentences, int words,
                               int punctuation, int numbers) {
        assertEquals("sentences", sentences, actual.getSentenceCount());
        assertEquals("words", words, actual.getWordCount());
        assertEquals("punctuation", punctuation, actual.getPunctuationCount());
        assertEquals("numbers", numbers, actual.getNumberCount());
    }

    @Test public void nullHasNoMetrics() {
        expect(calculator.calculate(null), 0, 0, 0, 0);
    }

    @Test public void emptyHasNoMetrics() {
        expect(calculator.calculate(""), 0, 0, 0, 0);
    }

    @Test public void whitespaceHasNoMetrics() {
        expect(calculator.calculate("   \n\t "), 0, 0, 0, 0);
    }

    @Test public void basicTextCountsAllMetrics() {
        expect(calculator.calculate("Hello world. I have 2 apples!"), 2, 6, 2, 1);
    }

    @Test public void aSentenceDoesNotNeedFinalPeriod() {
        expect(calculator.calculate("Hello world"), 1, 2, 0, 0);
    }

    @Test public void punctuationOnlyHasNoSentenceOrWords() {
        expect(calculator.calculate("...?!!"), 0, 0, 6, 0);
    }

    @Test public void unicodeLettersCountAsWords() {
        expect(calculator.calculate("Привет, мир! Héllo?"), 2, 3, 3, 0);
    }

    @Test public void apostrophesStayInsideWords() {
        expect(calculator.calculate("It's John's book."), 1, 3, 3, 0);
    }

    @Test public void numericCharactersInsideWordsAreNotNumbers() {
        expect(calculator.calculate("RoomA23 has 7 seats."), 1, 4, 1, 1);
    }

    @Test public void decimalNumberIsOneWordAndOneNumber() {
        expect(calculator.calculate("The price is 12.50 EUR. Pay 100 EUR!"), 2, 8, 3, 2);
    }

    @Test public void clockTimeIsOneWordAndOneNumber() {
        expect(calculator.calculate("The class begins at 9:30. We have 3 exercises, 2 quizzes, and 1 project!"),
                2, 14, 5, 4);
    }

    @Test public void multipleNumericSeparatorsDoNotSplitToken() {
        expect(calculator.calculate("At 12:30:45, meet 1,000 people."), 1, 5, 5, 2);
    }

    @Test public void UnicodeEllipsisAndNewline() {
        expect(calculator.calculate("Hello…\nWorld!"), 2, 2, 2, 0);
    }

    @Test public void punctuationMarksHaveSeveralUnicodeCategories() {
        expect(calculator.calculate("(Hi)—‘ok’!"), 1, 2, 6, 0);
    }

    @Test public void emojiDoesNotCountAsWord() {
        expect(calculator.calculate("Hi 👋!"), 1, 1, 1, 0);
    }

    @Test public void multipleEndingMarksCountOneSentence() {
        expect(calculator.calculate("Really?! Yes!!!"), 2, 2, 5, 0);
    }

    @Test public void sentenceWithDecimalPeriodDoesNotSplitThere() {
        expect(calculator.calculate("Pay 12.50 now."), 1, 3, 2, 1);
    }

    @Test public void aSeparatedNumberIsNotTheDigitSuffixOfAWord() {
        expect(calculator.calculate("Code A12, version 12.50beta."), 1, 4, 3, 0);
    }

    @Test public void mixedScriptsAndClockTime() {
        expect(calculator.calculate("Привіт о 9:30. He said ‘hello’!"), 2, 6, 5, 1);
    }

    @Test public void oneLetterOrDigitWithoutPunctuationIsOneSentence() {
        expect(calculator.calculate("7"), 1, 1, 0, 1);
    }
}
