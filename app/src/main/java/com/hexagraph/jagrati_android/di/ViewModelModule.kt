package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.ui.screens.addStudent.AddStudentViewModel
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.ForgotPasswordViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.LoginViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.SignUpViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory<AuthViewModel> { AuthViewModel(get()) }
    factory<LoginViewModel> { LoginViewModel(get()) }
    factory<SignUpViewModel> { SignUpViewModel(get()) }
    factory<ForgotPasswordViewModel>{ ForgotPasswordViewModel(get()) }
    factory<OmniScanViewModel> { OmniScanViewModel(get(), get(), get()) }
    factory<AddStudentViewModel> { AddStudentViewModel(get()) }
}