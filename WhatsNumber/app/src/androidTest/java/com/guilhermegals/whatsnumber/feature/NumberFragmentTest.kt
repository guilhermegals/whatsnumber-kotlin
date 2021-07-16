package com.guilhermegals.whatsnumber.feature

import android.Manifest
import android.content.Intent
import android.support.test.uiautomator.UiDevice
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.guilhermegals.whatsnumber.FakeNumberRepositoryImpl
import com.guilhermegals.whatsnumber.R
import com.guilhermegals.whatsnumber.core.di.RepositoryModule
import com.guilhermegals.whatsnumber.data.repository.contract.NumberRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
class NumberFragmentTest {

    // <editor-fold desc="[ Rules ]">

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            Manifest.permission.INTERNET
        )

    // </editor-fold>

    private lateinit var device: UiDevice

    private val fakeNumberRepositoryImpl = FakeNumberRepositoryImpl()

    @BindValue
    @JvmField
    var numberRepository: NumberRepository = fakeNumberRepositoryImpl

    // <editor-fold desc="[ Setup ]">

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    // </editor-fold>

    // <editor-fold desc="[ Tests ]">

    @Test
    fun numberFragment_SHOULD_FragmentContainer_WHEN_Opened() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.favorites_fragment_container))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_AllElements_WHEN_OpenedCorrectly() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.top_app_bar_text_size))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.top_app_bar_change_color))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_status))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_status_waiting)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_led_number))
            .check(ViewAssertions.matches(ViewMatchers.withText("0")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_attempts))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .check(ViewAssertions.matches(ViewMatchers.withHint(R.string.number_fragment_type_number)))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.send_label)))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_new_match))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun numberFragment_SHOULD_AllElements_WHEN_OpenedWithError() {
        fakeNumberRepositoryImpl.setReturnNumber(null)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.top_app_bar_text_size))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.top_app_bar_change_color))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_status))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_status_error)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_led_number))
            .check(ViewAssertions.matches(ViewMatchers.withText(FakeNumberRepositoryImpl.ERROR_CODE.toString())))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_attempts))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .check(ViewAssertions.matches(ViewMatchers.withHint(R.string.number_fragment_type_number)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.send_label)))
            .check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_new_match))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_fragment_new_match)))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_ShowColorPicker_WHEN_ClickInColorIcon() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.top_app_bar_change_color))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.select_color))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.confirm_label))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.back_label))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_CloseColorPicker_WHEN_ClickBackButton() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.top_app_bar_change_color))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.back_label))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.favorites_fragment_container))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_CloseColorPicker_WHEN_ClickBackOnDevice() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.top_app_bar_change_color))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.confirm_label))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.favorites_fragment_container))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_CloseColorPicker_WHEN_ClickConfirmButton() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.top_app_bar_change_color))
            .perform(ViewActions.click())

        device.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.favorites_fragment_container))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_ShowErrorMessageInTextInput_WHEN_TypeInvalidAttempt() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.number_fragment_input_empty_error))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_ShowStatusGreaterThan_WHEN_TypeNumberIsLessThanCurrentNumber() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .perform(ViewActions.typeText("1"))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_attempts))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_status))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_status_greater_than)))
    }

    @Test
    fun numberFragment_SHOULD_ShowStatusLessThan_WHEN_TypeNumberIsGreaterThanCurrentNumber() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .perform(ViewActions.typeText("20"))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_attempts))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_status))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_status_less_than)))
    }

    @Test
    fun numberFragment_SHOULD_ShowStatusWinAndButtonNewMatch_WHEN_TypeNumberIsTheCurrentNumber() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .perform(ViewActions.typeText("10"))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_status))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_status_win)))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_attempts))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_attempts_singular_message)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_new_match))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_fragment_new_match)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.send_label)))
            .check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_ShowStatusWinAndNumberOfAttempts_WHEN_WinWithTwoAttempts() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .perform(ViewActions.typeText("5"))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("10"))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_status))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_status_win)))


        val text = getResourceStringWithParam(R.string.number_attempts_plural_message, "2")
        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_attempts))
            .check(ViewAssertions.matches(ViewMatchers.withText(text)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_new_match))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_fragment_new_match)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.send_label)))
            .check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun numberFragment_SHOULD_ShowNewMatchScreen_WHEN_WinTheGameThanClickInNewMatchButton() {
        fakeNumberRepositoryImpl.setReturnNumber(10)
        launchActivity()
        delayTest()

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .perform(ViewActions.typeText("10"))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_new_match))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_status))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.number_status_waiting)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_led_number))
            .check(ViewAssertions.matches(ViewMatchers.withText("0")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_edit_attempt_number))
            .check(ViewAssertions.matches(ViewMatchers.withHint(R.string.number_fragment_type_number)))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_send))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.send_label)))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.number_fragment_new_match))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
    }

    // </editor-fold>

    // <editor-fold desc="[ Private Functions ]">

    private fun launchActivity(): ActivityScenario<MainActivity> {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        return ActivityScenario.launch(intent)
    }

    private fun delayTest(defaultTime: Long = 300) {
        Thread.sleep(defaultTime)
    }

    private fun getResourceStringWithParam(id: Int, param : String): String {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        return targetContext.resources.getString(id, param)
    }

    // </editor-fold>
}