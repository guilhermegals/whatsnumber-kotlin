package com.guilhermegals.whatsnumber.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.guilhermegals.whatsnumber.CoroutineTestRule
import com.guilhermegals.whatsnumber.FakeNumberRepositoryImpl
import com.guilhermegals.whatsnumber.data.model.NumberStatus
import com.guilhermegals.whatsnumber.getOrAwaitValue
import com.guilhermegals.whatsnumber.observeForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class NumberViewModelTest {

    // <editor-fold desc="[ Rules ]">

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = CoroutineTestRule()

    // </editor-fold>

    // <editor-fold desc="[ ViewModel & Repositories ]">

    private lateinit var viewModel: NumberViewModel

    private lateinit var numberRepository: FakeNumberRepositoryImpl

    // </editor-fold>

    // <editor-fold desc="[ Setup ]">

    @Before
    fun setUp() {
        numberRepository = FakeNumberRepositoryImpl(dispatcher = Dispatchers.Main)
        viewModel =
            NumberViewModel(numberRepository = numberRepository, dispatcher = Dispatchers.Main)
    }

    // </editor-fold>

    // <editor-fold desc="[ Tests ]">

    @Test
    fun newMatch_SHOULD_ClearTextInputNumber() = mainCoroutineRule.runBlockingTest {
        viewModel.textInputNumber.observeForTesting {
            viewModel.newMatch()

            val result = viewModel.textInputNumber.getOrAwaitValue()

            assert(result == "")
        }
    }

    @Test
    fun newMatch_SHOULD_SetLedNumberAs0() = mainCoroutineRule.runBlockingTest {
        numberRepository.setReturnNumber(10)
        viewModel.ledNumber.observeForTesting {
            viewModel.newMatch()

            val result = viewModel.ledNumber.getOrAwaitValue()

            assert(result == "0")
        }
    }

    @Test
    fun newMatch_SHOULD_HasErrorCode_WHEN_OccursError() = mainCoroutineRule.runBlockingTest {
        numberRepository.setReturnNumber(null)
        viewModel.ledNumber.observeForTesting {
            viewModel.newMatch()

            val result = viewModel.ledNumber.getOrAwaitValue()

            assert(result == FakeNumberRepositoryImpl.ERROR_CODE.toString())
        }
    }

    @Test
    fun newMatch_SHOULD_DefineCurrentStatus() = mainCoroutineRule.runBlockingTest {
        numberRepository.setReturnNumber(10)
        viewModel.currentStatus.observeForTesting {
            viewModel.newMatch()

            val result = viewModel.currentStatus.getOrAwaitValue()

            assert(result == NumberStatus.NoStatus)
        }
    }

    @Test
    fun checkAttempt_SHOULD_ChangeCurrentStatusToUnknown_WHEN_TextInputNumberAndCurrentNumberAreEmpty() =
        mainCoroutineRule.runBlockingTest {
            viewModel.textInputNumber.value = "123"
            viewModel.currentStatus.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.currentStatus.getOrAwaitValue()

                assert(result == NumberStatus.Unknown)
            }
        }

    @Test
    fun checkAttempt_SHOULD_ChangeLedNumber() =
        mainCoroutineRule.runBlockingTest {

            numberRepository.setReturnNumber(321)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "123"

            viewModel.ledNumber.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.ledNumber.getOrAwaitValue()

                assert(result == "123")
            }
        }

    @Test
    fun checkAttempt_SHOULD_ChangeCurrentStatusToGreaterThan_WHEN_CurrentNumberIsGreaterThanInputNumber() =
        mainCoroutineRule.runBlockingTest {

            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "5"

            viewModel.currentStatus.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.currentStatus.getOrAwaitValue()

                assert(result == NumberStatus.GreaterThan)
            }
        }

    @Test
    fun checkAttempt_SHOULD_ChangeCurrentStatusToLessThan_WHEN_CurrentNumberIsLessThanInputNumber() =
        mainCoroutineRule.runBlockingTest {

            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "15"

            viewModel.currentStatus.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.currentStatus.getOrAwaitValue()

                assert(result == NumberStatus.LessThan)
            }
        }

    @Test
    fun checkAttempt_SHOULD_ChangeCurrentStatusToWin_WHEN_CurrentNumberIsEqualsToInputNumber() =
        mainCoroutineRule.runBlockingTest {

            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "10"

            viewModel.currentStatus.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.currentStatus.getOrAwaitValue()

                assert(result == NumberStatus.Win)
            }
        }

    @Test
    fun canNewMatch_SHOULD_BeTrue_WHEN_CurrentStatusIsWin() =
        mainCoroutineRule.runBlockingTest {

            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "10"

            viewModel.canNewMatch.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.canNewMatch.getOrAwaitValue()

                assert(result)
            }
        }

    @Test
    fun canNewMatch_SHOULD_BeTrue_WHEN_CurrentStatusIsError() =
        mainCoroutineRule.runBlockingTest {

            numberRepository.setReturnNumber(null)

            viewModel.canNewMatch.observeForTesting {
                viewModel.newMatch()

                val result = viewModel.canNewMatch.getOrAwaitValue()

                assert(result)
            }
        }

    @Test
    fun canNewMatch_SHOULD_BeTrue_WHEN_CurrentStatusIsUnknown() =
        mainCoroutineRule.runBlockingTest {
            viewModel.textInputNumber.value = "123"
            viewModel.canNewMatch.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.canNewMatch.getOrAwaitValue()

                assert(result)
            }
        }

    @Test
    fun canNewMatch_SHOULD_BeFalse_WHEN_CurrentStatusIsGreaterThan() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "5"

            viewModel.canNewMatch.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.canNewMatch.getOrAwaitValue()

                assert(!result)
            }
        }

    @Test
    fun canNewMatch_SHOULD_BeFalse_WHEN_CurrentStatusIsLessThan() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "15"

            viewModel.canNewMatch.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.canNewMatch.getOrAwaitValue()

                assert(!result)
            }
        }

    @Test
    fun canNewMatch_SHOULD_BeFalse_WHEN_CurrentStatusIsNoStatus() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)

            viewModel.canNewMatch.observeForTesting {
                viewModel.newMatch()

                val result = viewModel.canNewMatch.getOrAwaitValue()

                assert(!result)
            }
        }

    @Test
    fun textInputError_SHOULD_Be0_WHEN_CreateNewMatch() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)

            viewModel.textInputError.observeForTesting {
                viewModel.newMatch()

                val result = viewModel.textInputError.getOrAwaitValue()

                assert(result == 0)
            }
        }

    @Test
    fun textInputError_SHOULD_Be0_WHEN_AttemptIsValid() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "15"

            viewModel.textInputError.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.textInputError.getOrAwaitValue()

                assert(result == 0)
            }
        }

    @Test
    fun textInputError_SHOULD_HasValue_WHEN_AttemptIsInvalid() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = ""

            viewModel.textInputError.observeForTesting {
                viewModel.checkAttempt()

                val result = viewModel.textInputError.getOrAwaitValue()

                assert(result != 0)
            }
        }

    @Test
    fun showAttempts_SHOULD_Be0_WHEN_StatusIsNotWin() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)
            viewModel.newMatch()

            viewModel.showAttempts.observeForTesting {

                val result = viewModel.showAttempts.getOrAwaitValue()

                assert(result == 0)
            }
        }

    @Test
    fun showAttempts_SHOULD_Be0_WHEN_StatusIsGreaterThan() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "5"

            viewModel.showAttempts.observeForTesting {

                viewModel.checkAttempt()
                val result = viewModel.showAttempts.getOrAwaitValue()

                assert(result == 0)
            }
        }

    @Test
    fun showAttempts_SHOULD_Be0_WHEN_StatusIsLessThan() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "15"

            viewModel.showAttempts.observeForTesting {

                viewModel.checkAttempt()
                val result = viewModel.showAttempts.getOrAwaitValue()

                assert(result == 0)
            }
        }

    @Test
    fun showAttempts_SHOULD_Be2_WHEN_WinWith2Attempts() =
        mainCoroutineRule.runBlockingTest {
            numberRepository.setReturnNumber(10)
            viewModel.newMatch()
            viewModel.textInputNumber.value = "5"

            viewModel.showAttempts.observeForTesting {

                viewModel.checkAttempt()

                viewModel.textInputNumber.value = "10"
                viewModel.checkAttempt()
                val result = viewModel.showAttempts.getOrAwaitValue()

                assert(result == 2)
            }
        }

    // </editor-fold>
}