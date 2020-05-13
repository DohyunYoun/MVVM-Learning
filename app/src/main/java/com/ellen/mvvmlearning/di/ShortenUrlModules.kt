package com.ellen.mvvmlearning.di

import com.ellen.mvvmlearning.model.NetworkRepositoryImpl
import com.ellen.mvvmlearning.model.Repository
import com.ellen.mvvmlearning.viewmodel.ShortenUrlViewModelFactory
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

val shortenUrlModules: Module = module {
    //API 통신
    factory {
        NetworkRepositoryImpl(get()) as Repository
    }

    //viewModel
    factory {
        ShortenUrlViewModelFactory(get())
    }
}