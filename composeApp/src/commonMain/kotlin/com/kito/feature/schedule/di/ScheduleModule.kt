package com.kito.feature.schedule.di

import com.kito.feature.schedule.data.ManualScheduleRepositoryImpl
import com.kito.feature.schedule.data.ScheduleRepositoryImpl
import com.kito.feature.schedule.domain.repository.ManualScheduleRepository
import com.kito.feature.schedule.domain.repository.ScheduleRepository
import com.kito.feature.schedule.domain.usecase.ClearManualScheduleUseCase
import com.kito.feature.schedule.domain.usecase.GetAvailableSectionsUseCase
import com.kito.feature.schedule.domain.usecase.GetScheduleLookupStateUseCase
import com.kito.feature.schedule.domain.usecase.SaveManualScheduleUseCase
import com.kito.feature.schedule.presentation.ScheduleScreenViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val scheduleModule = module {
    single<ScheduleRepositoryImpl>() bind ScheduleRepository::class
    single<ManualScheduleRepositoryImpl>() bind ManualScheduleRepository::class
    single<GetAvailableSectionsUseCase>()
    single<SaveManualScheduleUseCase>()
    single<ClearManualScheduleUseCase>()
    single<GetScheduleLookupStateUseCase>()
    single<ScheduleScreenViewModel>()
}
