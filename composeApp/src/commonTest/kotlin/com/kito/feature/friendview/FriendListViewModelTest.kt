package com.kito.feature.friendview

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.kito.core.datastore.data.PrefsRepositoryImpl
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.friendview.presentation.list.FriendListEvent
import com.kito.feature.friendview.presentation.list.FriendListViewModel
import com.kito.testing.FakeFriendViewRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FriendListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val tempPath = "friendlist_prefs_test.preferences_pb".toPath()
    private lateinit var prefsRepository: PrefsRepository
    private lateinit var datastoreScope: CoroutineScope

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        datastoreScope = CoroutineScope(testDispatcher + SupervisorJob())
        prefsRepository = PrefsRepositoryImpl(
            PreferenceDataStoreFactory.createWithPath(
                scope = datastoreScope,
                produceFile = { tempPath }
            )
        )
    }

    @AfterTest
    fun teardown() {
        datastoreScope.cancel()
        Dispatchers.resetMain()
        try {
            FileSystem.SYSTEM.delete(tempPath)
        } catch (_: Exception) {
            // ignore
        }
    }

    @Test
    fun friendsSummary_initiallyEmpty() = runTest(testDispatcher) {
        val vm = FriendListViewModel(FakeFriendViewRepository(), prefsRepository, dispatcher = testDispatcher)
        val job = launch { vm.friendsSummary.collect {} }
        advanceUntilIdle()

        assertTrue(vm.friendsSummary.value.isEmpty())
        job.cancel()
    }

    @Test
    fun onEvent_addFriend_updatesListAndFetchesSummary() = runTest(testDispatcher) {
        val summaries = mapOf(
            "2205001" to FriendSummary(roll = "2205001", section = "CSE-48", elective1 = "AI", elective2 = "ML")
        )
        val vm = FriendListViewModel(FakeFriendViewRepository(summaryMap = summaries), prefsRepository, dispatcher = testDispatcher)
        val rollsJob = launch { vm.friendRolls.collect {} }
        val summariesJob = launch { vm.friendsSummary.collect {} }

        vm.onEvent(FriendListEvent.AddFriend("2205001"))
        advanceUntilIdle()

        assertEquals(listOf("2205001"), vm.friendRolls.value)
        assertEquals(1, vm.friendsSummary.value.size)
        assertEquals("2205001", vm.friendsSummary.value[0].roll)
        assertEquals("CSE-48", vm.friendsSummary.value[0].section)
        assertEquals("CSE-48 • AI, ML", vm.friendsSummary.value[0].subtitleText)

        rollsJob.cancel()
        summariesJob.cancel()
    }

    @Test
    fun onEvent_addFriendByRoll_withName_savesCustomName() = runTest(testDispatcher) {
        val summaries = mapOf(
            "24155152" to FriendSummary(roll = "24155152", section = "CSE-33", batch = "batch_3", elective1 = "IPA-11", elective2 = "SVP-10")
        )
        val vm = FriendListViewModel(FakeFriendViewRepository(summaryMap = summaries), prefsRepository, dispatcher = testDispatcher)
        val rollsJob = launch { vm.friendRolls.collect {} }
        val summariesJob = launch { vm.friendsSummary.collect {} }

        vm.onEvent(FriendListEvent.AddFriendByRoll(name = "Adrish Paul", roll = "24155152"))
        advanceUntilIdle()

        assertEquals(listOf("24155152"), vm.friendRolls.value)
        assertEquals(1, vm.friendsSummary.value.size)
        val friend = vm.friendsSummary.value[0]
        assertEquals("24155152", friend.roll)
        assertEquals("Adrish Paul", friend.name)
        assertEquals("Adrish Paul", friend.displayName)
        assertEquals("AP", friend.monogram)
        assertEquals("CSE-33 • IPA-11, SVP-10", friend.subtitleText)
        assertNull(vm.addFriendError.value)

        rollsJob.cancel()
        summariesJob.cancel()
    }

    @Test
    fun onEvent_addFriendByRoll_whenRollNotFound_setsError() = runTest(testDispatcher) {
        val vm = FriendListViewModel(FakeFriendViewRepository(defaultFound = false), prefsRepository, dispatcher = testDispatcher)
        val rollsJob = launch { vm.friendRolls.collect {} }

        vm.onEvent(FriendListEvent.AddFriendByRoll(name = "John", roll = "99999999"))
        advanceUntilIdle()

        assertTrue(vm.friendRolls.value.isEmpty())
        assertEquals("Roll no not available, recheck and retry", vm.addFriendError.value)

        rollsJob.cancel()
    }

    @Test
    fun onEvent_addFriendByRoll_whenNetworkFails_setsNetworkError() = runTest(testDispatcher) {
        val networkEx = Exception("Unable to resolve host: supabase.co")
        val vm = FriendListViewModel(FakeFriendViewRepository(remoteError = networkEx), prefsRepository, dispatcher = testDispatcher)
        val rollsJob = launch { vm.friendRolls.collect {} }

        vm.onEvent(FriendListEvent.AddFriendByRoll(name = "John", roll = "24155152"))
        advanceUntilIdle()

        assertTrue(vm.friendRolls.value.isEmpty())
        assertEquals("No internet connection. Please check your network and retry.", vm.addFriendError.value)

        rollsJob.cancel()
    }

    @Test
    fun onEvent_addFriendBySection_addsFriendWithSectionAndElectives() = runTest(testDispatcher) {
        val vm = FriendListViewModel(FakeFriendViewRepository(), prefsRepository, dispatcher = testDispatcher)
        val rollsJob = launch { vm.friendRolls.collect {} }
        val summariesJob = launch { vm.friendsSummary.collect {} }

        vm.onEvent(
            FriendListEvent.AddFriendBySection(
                name = "Rahul Sharma",
                batch = "batch_3",
                branch = "CSE",
                section = "CSE-33",
                elective1 = "IPA-11",
                elective2 = "SVP-10"
            )
        )
        advanceUntilIdle()

        assertEquals(1, vm.friendRolls.value.size)
        assertTrue(vm.friendRolls.value[0].startsWith("SEC:CSE-33:"))
        assertEquals(1, vm.friendsSummary.value.size)
        val friend = vm.friendsSummary.value[0]
        assertEquals("Rahul Sharma", friend.name)
        assertEquals("Rahul Sharma", friend.displayName)
        assertEquals("RS", friend.monogram)
        assertEquals("CSE-33", friend.section)
        assertEquals("CSE-33 • IPA-11, SVP-10", friend.subtitleText)

        rollsJob.cancel()
        summariesJob.cancel()
    }

    @Test
    fun onEvent_removeFriend_removesFromList() = runTest(testDispatcher) {
        val vm = FriendListViewModel(FakeFriendViewRepository(), prefsRepository, dispatcher = testDispatcher)
        val rollsJob = launch { vm.friendRolls.collect {} }
        val summariesJob = launch { vm.friendsSummary.collect {} }

        vm.onEvent(FriendListEvent.AddFriend("2205001"))
        vm.onEvent(FriendListEvent.AddFriend("2205002"))
        advanceUntilIdle()

        assertEquals(2, vm.friendRolls.value.size)

        vm.onEvent(FriendListEvent.RemoveFriend("2205001"))
        advanceUntilIdle()

        assertEquals(listOf("2205002"), vm.friendRolls.value)

        rollsJob.cancel()
        summariesJob.cancel()
    }

    @Test
    fun onEvent_showAddDialog_togglesState() = runTest(testDispatcher) {
        val vm = FriendListViewModel(FakeFriendViewRepository(), prefsRepository, dispatcher = testDispatcher)
        assertEquals(false, vm.showAddDialog.value)

        vm.onEvent(FriendListEvent.ShowAddDialog(true))
        assertEquals(true, vm.showAddDialog.value)

        vm.onEvent(FriendListEvent.ShowAddDialog(false))
        assertEquals(false, vm.showAddDialog.value)
    }

    @Test
    fun onEvent_syncOnOpen_emitsSuccess() = runTest(testDispatcher) {
        val vm = FriendListViewModel(FakeFriendViewRepository(), prefsRepository, syncGuard = com.kito.feature.friendview.domain.FriendViewSyncGuard(), dispatcher = testDispatcher)
        val rollsJob = launch { vm.friendRolls.collect {} }
        val events = mutableListOf<com.kito.core.ui.state.SyncUiState>()
        val job = launch { vm.syncEvents.collect { events.add(it) } }

        vm.onEvent(FriendListEvent.AddFriend("2205001"))
        advanceUntilIdle()

        vm.onEvent(FriendListEvent.SyncOnOpen)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertTrue(events[0] is com.kito.core.ui.state.SyncUiState.Success)

        rollsJob.cancel()
        job.cancel()
    }

    @Test
    fun onEvent_syncOnOpen_onlySyncsOncePerSession() = runTest(testDispatcher) {
        val sharedGuard = com.kito.feature.friendview.domain.FriendViewSyncGuard()
        val vm = FriendListViewModel(FakeFriendViewRepository(), prefsRepository, syncGuard = sharedGuard, dispatcher = testDispatcher)
        val rollsJob = launch { vm.friendRolls.collect {} }
        val events = mutableListOf<com.kito.core.ui.state.SyncUiState>()
        val job = launch { vm.syncEvents.collect { events.add(it) } }

        vm.onEvent(FriendListEvent.AddFriend("2205001"))
        advanceUntilIdle()

        // First open -> triggers sync
        vm.onEvent(FriendListEvent.SyncOnOpen)
        advanceUntilIdle()
        assertEquals(1, events.size)

        // Second open during same session -> ignored
        vm.onEvent(FriendListEvent.SyncOnOpen)
        advanceUntilIdle()
        assertEquals(1, events.size)

        rollsJob.cancel()
        job.cancel()
    }
}
