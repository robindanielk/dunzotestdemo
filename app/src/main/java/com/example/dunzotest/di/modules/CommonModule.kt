package com.example.dunzotest.di.modules

import com.example.dunzotest.model.photo.PhotoListRepository
import com.example.dunzotest.model.photo.PhotoListRepositoryImpl
import com.example.dunzotest.domain.photo.PhotoListInteractor
import com.example.dunzotest.domain.photo.PhotoListUseCases
import dagger.Binds
import dagger.Module


@Module
abstract class CommonModule {
    @Binds
    abstract fun bindApiService(userListRepository: PhotoListRepositoryImpl): PhotoListRepository

    @Binds
    abstract fun bindPhotoListUseCases(photoListInteractor: PhotoListInteractor): PhotoListUseCases

}