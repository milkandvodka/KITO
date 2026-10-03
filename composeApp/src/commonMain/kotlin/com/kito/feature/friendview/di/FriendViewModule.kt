package com.kito.feature.friendview.di

import com.kito.feature.friendview.data.FriendViewRepositoryImpl
import com.kito.feature.friendview.domain.repository.FriendViewRepository
import com.kito.feature.friendview.presentation.FriendViewViewmodel
import com.kito.feature.friendview.presentation.list.FriendListViewModel
import com.kito.feature.friendview.presentation.schedule.FriendScheduleViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

import com.kito.feature.friendview.domain.FriendViewSyncGuard

val friendViewModule = module {
    single<FriendViewSyncGuard>()
    single<FriendViewRepositoryImpl>() bind FriendViewRepository::class
    single<FriendViewViewmodel>()
    single<FriendListViewModel>()
    single<FriendScheduleViewModel>()
}

