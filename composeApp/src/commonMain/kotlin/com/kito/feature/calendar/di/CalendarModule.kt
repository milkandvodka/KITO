package com.kito.feature.calendar.di

import com.kito.feature.calendar.data.CalendarRepositoryImpl
import com.kito.feature.calendar.domain.repository.CalendarRepository
import com.kito.feature.calendar.domain.usecase.GetCalendarMonthUseCase
import com.kito.feature.calendar.domain.usecase.GetClassesForDateUseCase
import com.kito.feature.calendar.presentation.CalendarViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val calendarModule = module {
    single<CalendarRepositoryImpl>() bind CalendarRepository::class
    single<GetCalendarMonthUseCase>()
    single<GetClassesForDateUseCase>()
    single<CalendarViewModel>()
}
