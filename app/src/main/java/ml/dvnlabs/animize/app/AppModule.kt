/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.app

import ml.dvnlabs.animize.database.legacy.PackageStarDBHelper
import ml.dvnlabs.animize.database.legacy.RecentPlayDBHelper
import ml.dvnlabs.animize.database.notification.StarredNotificationDatabase
import ml.dvnlabs.animize.ui.viewmodel.CommonViewModel
import ml.dvnlabs.animize.ui.viewmodel.ListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module{
    single { RecentPlayDBHelper(androidContext()) }
    single { PackageStarDBHelper(androidContext()) }
    single { StarredNotificationDatabase.getDatabase(androidContext()) }
    viewModel { CommonViewModel() }
    viewModel { ListViewModel(androidApplication()) }

}