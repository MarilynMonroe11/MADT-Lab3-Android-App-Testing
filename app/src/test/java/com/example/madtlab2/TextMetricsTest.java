package com.example.madtlab2;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** Covers the model constructor and all public accessors. */
public final class TextMetricsTest {
    @Test public void allGettersReturnConstructorValues() {
        TextMetrics metrics = new TextMetrics(1, 2, 3, 4);
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(2, metrics.getWordCount());
        assertEquals(3, metrics.getPunctuationCount());
        assertEquals(4, metrics.getNumberCount());
    }
}
