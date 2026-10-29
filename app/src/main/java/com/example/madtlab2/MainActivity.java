package com.example.madtlab2;

import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends Activity {
    private static final String HISTORY_TEXTS_KEY = "historyTexts";
    private static final String SAMPLE_INDEX_KEY = "sampleIndex";
    private static final int HISTORY_PREVIEW_CODEPOINTS = 65;

    private final TextMetricsCalculator calculator = new TextMetricsCalculator();
    private final List<HistoryEntry> history = new ArrayList<>();

    private EditText inputEditText;
    private TextView sentencesValue;
    private TextView wordsValue;
    private TextView punctuationValue;
    private TextView numbersValue;
    private LinearLayout historyContainer;
    private int sampleIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applySafeAreaInsets();

        inputEditText = findViewById(R.id.inputEditText);
        sentencesValue = findViewById(R.id.sentencesValue);
        wordsValue = findViewById(R.id.wordsValue);
        punctuationValue = findViewById(R.id.punctuationValue);
        numbersValue = findViewById(R.id.numbersValue);
        historyContainer = findViewById(R.id.historyContainer);

        Button calculateButton = findViewById(R.id.calculateButton);
        Button generateButton = findViewById(R.id.generateButton);
        Button clearButton = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(view -> calculateInput());
        generateButton.setOnClickListener(view -> generateSample());
        clearButton.setOnClickListener(view -> clearInput());

        if (savedInstanceState != null) {
            sampleIndex = savedInstanceState.getInt(SAMPLE_INDEX_KEY, 0);
            ArrayList<String> previousTexts = savedInstanceState.getStringArrayList(HISTORY_TEXTS_KEY);
            if (previousTexts != null) {
                for (String previousText : previousTexts) {
                    history.add(new HistoryEntry(previousText, calculator.calculate(previousText)));
                }
            }
        }
        renderHistory();
        if (!history.isEmpty()) {
            displayMetrics(history.get(0).metrics);
        }
    }

    /** Keep scrollable content outside system bars on Android 15 edge-to-edge. */
    private void applySafeAreaInsets() {
        ScrollView scrollView = findViewById(R.id.mainScrollView);
        final int originalLeft = scrollView.getPaddingLeft();
        final int originalTop = scrollView.getPaddingTop();
        final int originalRight = scrollView.getPaddingRight();
        final int originalBottom = scrollView.getPaddingBottom();

        scrollView.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            int left;
            int top;
            int right;
            int bottom;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Insets safe = windowInsets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                left = safe.left;
                top = safe.top;
                right = safe.right;
                bottom = safe.bottom;
            } else {
                left = windowInsets.getSystemWindowInsetLeft();
                top = windowInsets.getSystemWindowInsetTop();
                right = windowInsets.getSystemWindowInsetRight();
                bottom = windowInsets.getSystemWindowInsetBottom();
            }
            // Use original padding, not existing inset padding, to avoid stacking
            // offsets every time insets are redispatched (e.g. on rotation).
            view.setPadding(originalLeft + left, originalTop + top,
                    originalRight + right, originalBottom + bottom);
            return windowInsets;
        });
        scrollView.requestApplyInsets();
    }

    private void calculateInput() {
        String text = inputEditText.getText().toString();
        if (text.trim().isEmpty()) {
            Toast.makeText(this, R.string.empty_text_warning, Toast.LENGTH_SHORT).show();
            return;
        }

        TextMetrics metrics = calculator.calculate(text);
        displayMetrics(metrics);
        history.add(0, new HistoryEntry(text, metrics));
        renderHistory();
    }

    private void generateSample() {
        String[] samples = getResources().getStringArray(R.array.sample_texts);
        if (samples.length == 0) {
            return;
        }
        inputEditText.setText(samples[sampleIndex % samples.length]);
        inputEditText.setSelection(inputEditText.length());
        sampleIndex++;
    }

    private void clearInput() {
        inputEditText.setText(null);
        Toast.makeText(this, R.string.input_cleared, Toast.LENGTH_SHORT).show();
    }

    private void displayMetrics(TextMetrics metrics) {
        sentencesValue.setText(String.valueOf(metrics.getSentenceCount()));
        wordsValue.setText(String.valueOf(metrics.getWordCount()));
        punctuationValue.setText(String.valueOf(metrics.getPunctuationCount()));
        numbersValue.setText(String.valueOf(metrics.getNumberCount()));
    }

    private void renderHistory() {
        historyContainer.removeAllViews();
        if (history.isEmpty()) {
            TextView placeholder = new TextView(this);
            placeholder.setText(R.string.history_empty);
            placeholder.setTextColor(getColor(R.color.text_secondary));
            placeholder.setTextSize(14);
            placeholder.setPadding(dp(12), dp(16), dp(12), dp(16));
            historyContainer.addView(placeholder);
            return;
        }

        for (int index = 0; index < history.size(); index++) {
            HistoryEntry entry = history.get(index);
            TextMetrics metrics = entry.metrics;
            TextView historyRow = new TextView(this);
            historyRow.setText(getString(R.string.history_item_format,
                    history.size() - index,
                    makePreview(entry.text),
                    metrics.getSentenceCount(),
                    metrics.getWordCount(),
                    metrics.getPunctuationCount(),
                    metrics.getNumberCount()));
            historyRow.setTextColor(getColor(R.color.text_primary));
            historyRow.setTextSize(13);
            historyRow.setLineSpacing(dp(2), 1f);
            historyRow.setPadding(dp(14), dp(12), dp(14), dp(12));
            historyRow.setBackgroundResource(R.drawable.history_background);
            historyRow.setContentDescription(getString(R.string.history_item_accessibility));
            historyRow.setOnClickListener(view -> inputEditText.setText(entry.text));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = dp(9);
            historyContainer.addView(historyRow, params);
        }
    }

    private String makePreview(String input) {
        String normalized = input.replaceAll("\\s+", " ").trim();
        int length = normalized.codePointCount(0, normalized.length());
        if (length <= HISTORY_PREVIEW_CODEPOINTS) {
            return normalized;
        }
        int cutoff = normalized.offsetByCodePoints(0, HISTORY_PREVIEW_CODEPOINTS);
        return normalized.substring(0, cutoff) + getString(R.string.history_ellipsis);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<String> previousTexts = new ArrayList<>();
        for (HistoryEntry entry : history) {
            previousTexts.add(entry.text);
        }
        outState.putStringArrayList(HISTORY_TEXTS_KEY, previousTexts);
        outState.putInt(SAMPLE_INDEX_KEY, sampleIndex);
        super.onSaveInstanceState(outState);
    }

    private static final class HistoryEntry {
        private final String text;
        private final TextMetrics metrics;

        private HistoryEntry(String text, TextMetrics metrics) {
            this.text = text;
            this.metrics = metrics;
        }
    }
}
