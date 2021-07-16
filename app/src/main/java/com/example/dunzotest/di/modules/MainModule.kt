package com.example.dunzotest.di.modules

import androidx.lifecycle.ViewModel
import com.example.dunzotest.ui.photo.PhotoListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(PhotoListViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: PhotoListViewModel): ViewModel
}