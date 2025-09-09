
package com.setouta.assistant

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmokeTest {
    @get:Rule
    val mainRule = ActivityScenarioRule(MainActivity::class.java)

    @Test fun launchesMain() {
        // If we got here, MainActivity launched without crashing.
        assert(true)
    }
}
