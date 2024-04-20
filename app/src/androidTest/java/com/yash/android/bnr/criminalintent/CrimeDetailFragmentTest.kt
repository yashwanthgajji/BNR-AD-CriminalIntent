package com.yash.android.bnr.criminalintent

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrimeDetailFragmentTest {

    private lateinit var scenario: FragmentScenario<CrimeDetailFragment>

    @Before
    fun setUp() {
        scenario = FragmentScenario.Companion.launchInContainer(CrimeDetailFragment::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun crimeUpdateOnSolved() {
        onView(withId(R.id.crime_solved)).perform(click())
        scenario.onFragment { fragment ->
            assertTrue(fragment.crime.isSolved)
        }
    }

    @Test
    fun crimeUpdateOnTitleChange() {
        val titleExpected = "Test title"
        onView(withId(R.id.crime_title)).perform(typeText(titleExpected))
        scenario.onFragment{ fragment ->
            assertEquals(titleExpected, fragment.crime.title)
        }
    }
}