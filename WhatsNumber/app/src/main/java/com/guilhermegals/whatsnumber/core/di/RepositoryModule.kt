package com.guilhermegals.whatsnumber.core.di

import com.guilhermegals.whatsnumber.data.repository.contract.NumberRepository
import com.guilhermegals.whatsnumber.data.repository.implementation.NumberRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @ViewModelScoped
    @Binds
    abstract fun bindNumberRepository(
        numberRepository: NumberRepositoryImpl
    ): NumberRepository
}