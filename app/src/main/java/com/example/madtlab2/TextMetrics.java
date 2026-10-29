package com.example.madtlab2;

/** Immutable result of a single text analysis. */
public final class TextMetrics {
    private final int sentenceCount;
    private final int wordCount;
    private final int punctuationCount;
    private final int numberCount;

    public TextMetrics(int sentenceCount, int wordCount, int punctuationCount, int numberCount) {
        this.sentenceCount = sentenceCount;
        this.wordCount = wordCount;
        this.punctuationCount = punctuationCount;
        this.numberCount = numberCount;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getPunctuationCount() {
        return punctuationCount;
    }

    public int getNumberCount() {
        return numberCount;
    }
}
