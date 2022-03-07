package com.interview.notes

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.interview.notes.kotlin.ui.MainActivity
import com.interview.notes.kotlin.ui.NoteDetailsFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testDetailsScreenOpens() {
        Intents.init()
        onView(ViewMatchers.withId(R.id.btn_add_note)).perform(ViewActions.click())
        intended(hasComponent(NoteDetailsFragment::class.java.name))
    }
}
