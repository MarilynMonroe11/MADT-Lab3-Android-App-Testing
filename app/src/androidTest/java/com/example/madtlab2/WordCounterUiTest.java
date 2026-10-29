package com.example.madtlab2;

import android.graphics.Insets;
import android.os.Build;
import android.view.WindowInsets;
import android.widget.ScrollView;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** Each test starts in an independent ActivityScenario on Android emulator/device. */
@RunWith(AndroidJUnit4.class)
public final class WordCounterUiTest {
    @Rule public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    private void typeAndCalculate(String input) {
        onView(withId(R.id.inputEditText)).perform(scrollTo(), replaceText(input), closeSoftKeyboard());
        onView(withId(R.id.calculateButton)).perform(scrollTo(), click());
    }

    private void checkMetrics(String sentences, String words, String punctuation, String numbers) {
        onView(withId(R.id.sentencesValue)).check(matches(withText(sentences)));
        onView(withId(R.id.wordsValue)).check(matches(withText(words)));
        onView(withId(R.id.punctuationValue)).check(matches(withText(punctuation)));
        onView(withId(R.id.numbersValue)).check(matches(withText(numbers)));
    }

    @Test public void scenario01_calculationShowsFourCorrectMetrics() {
        typeAndCalculate("Hello world. I have 2 apples!");
        checkMetrics("2", "6", "2", "1");
    }

    @Test public void scenario02_whitespaceDoesNotUpdateMetricsOrHistory() {
        typeAndCalculate("   \n ");
        checkMetrics("0", "0", "0", "0");
        onView(allOf(isDescendantOfA(withId(R.id.historyContainer)),
                withText(R.string.history_empty))).perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test public void scenario03_generateSampleAndCalculate() {
        onView(withId(R.id.generateButton)).perform(scrollTo(), click());
        onView(withId(R.id.inputEditText)).check(matches(withText(not(isEmptyString()))));
        onView(withId(R.id.calculateButton)).perform(scrollTo(), click());
        checkMetrics("3", "10", "4", "2");
        onView(allOf(isDescendantOfA(withId(R.id.historyContainer)),
                withText(containsString("Good morning!")))).perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test public void scenario04_clearKeepsHistory() {
        typeAndCalculate("Hello world.");
        onView(withId(R.id.clearButton)).perform(scrollTo(), click());
        onView(withId(R.id.inputEditText)).check(matches(withText("")));
        onView(allOf(isDescendantOfA(withId(R.id.historyContainer)),
                withText(containsString("Hello world.")))).perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test public void scenario05_historyEntryRestoresOriginalText() {
        typeAndCalculate("First text.");
        onView(withId(R.id.inputEditText)).perform(scrollTo(), replaceText("Another text"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.historyContainer)),
                withText(containsString("First text.")))).perform(scrollTo(), click());
        onView(withId(R.id.inputEditText)).check(matches(withText("First text.")));
    }

    @Test public void scenario06_rotationPreservesHistoryAndMetrics() {
        typeAndCalculate("Rotate me.");
        scenario.getScenario().recreate();
        checkMetrics("1", "2", "1", "0");
        onView(allOf(isDescendantOfA(withId(R.id.historyContainer)),
                withText(containsString("Rotate me.")))).perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    @Test public void scenario07_regressionTimeNineThirtyCountsAsOneToken() {
        typeAndCalculate("The class begins at 9:30. We have 3 exercises, 2 quizzes, and 1 project!");
        checkMetrics("2", "14", "5", "4");
    }

    @Test public void scenario08_repeatedGenerateMovesToSecondSample() {
        onView(withId(R.id.generateButton)).perform(scrollTo(), click());
        onView(withId(R.id.generateButton)).perform(scrollTo(), click());
        onView(withId(R.id.inputEditText)).check(matches(withText(containsString("9:30"))));
        onView(withId(R.id.calculateButton)).perform(scrollTo(), click());
        checkMetrics("2", "14", "5", "4");
    }

    @Test public void scenario09_longTextHistoryShowsPreviewAndRestoresCompleteText() {
        String longText = "This is a long piece of text to test the history preview truncation "
                + "without losing the original text when a saved result is selected.";
        typeAndCalculate(longText);
        onView(allOf(isDescendantOfA(withId(R.id.historyContainer)),
                withText(containsString("…")))).perform(scrollTo(), click());
        onView(withId(R.id.inputEditText)).check(matches(withText(longText)));
    }

    @Test public void scenario10_rotationPreservesSampleGeneratorProgress() {
        onView(withId(R.id.generateButton)).perform(scrollTo(), click());
        scenario.getScenario().recreate();
        onView(withId(R.id.generateButton)).perform(scrollTo(), click());
        onView(withId(R.id.inputEditText)).check(matches(withText(containsString("9:30"))));
    }

    @Test public void scenario11_safeAreaPaddingKeepsContentBelowSystemBars() {
        scenario.getScenario().onActivity(activity -> {
            ScrollView scrollView = activity.findViewById(R.id.mainScrollView);
            WindowInsets insets = scrollView.getRootWindowInsets();
            assertNotNull("Insets must have been dispatched", insets);
            int safeTop;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Insets bars = insets.getInsets(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout());
                safeTop = bars.top;
            } else {
                safeTop = insets.getSystemWindowInsetTop();
            }
            assertTrue("Top safe inset must be included in ScrollView padding",
                    scrollView.getPaddingTop() >= safeTop);
        });
    }
}
