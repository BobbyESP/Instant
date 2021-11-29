package com.ebf.instant.di

import com.ebf.instant.data.database.AppDatabase
import com.ebf.instant.data.network.auth.ObserveUserAuthStateUseCase
import com.ebf.instant.data.network.auth.datasources.AuthStateUserDataSource
import com.ebf.instant.data.network.auth.datasources.FirebaseAuthStateUserDataSource
import com.ebf.instant.data.network.auth.datasources.FirestoreRegisteredUserDataSource
import com.ebf.instant.data.network.auth.datasources.RegisteredUserDataSource
import com.ebf.instant.data.network.comment.FunctionsCommentDataSource
import com.ebf.instant.data.network.fcm.FcmTokenUpdater
import com.ebf.instant.data.network.post.FirestorePostDataSource
import com.ebf.instant.data.network.post.FunctionsPostDataSource
import com.ebf.instant.data.network.post.StoragePostDataSource
import com.ebf.instant.data.network.user.FunctionsUserDataSource
import com.ebf.instant.data.repository.AuthRepository
import com.ebf.instant.data.repository.CommentRepository
import com.ebf.instant.data.repository.PostRepository
import com.ebf.instant.data.repository.UserRepository
import com.ebf.instant.ui.InstantAppViewModel
import com.ebf.instant.ui.camera.CameraScreenViewModel
import com.ebf.instant.ui.comment.CommentScreenViewModel
import com.ebf.instant.ui.create.CreateAccountViewModel
import com.ebf.instant.ui.feed.FeedViewModel
import com.ebf.instant.ui.login.LoginScreenViewModel
import com.ebf.instant.ui.signin.FirebaseSignInViewModelDelegate
import com.ebf.instant.ui.signin.SignInViewModelDelegate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase
    single { Firebase.firestore }
    single { Firebase.auth }
    single { Firebase.storage }
    single { Firebase.functions("europe-west1") }

    // Data sources
    single { FirestorePostDataSource(get()) }
    single { FunctionsPostDataSource(get()) }
    single { FunctionsUserDataSource(get()) }
    single { FunctionsCommentDataSource(get()) }
    single { StoragePostDataSource(get(), get()) }

    // Repositories
    single { UserRepository(get(), get()) }
    single { AuthRepository(get(), get()) }
    single { PostRepository(get(), get(), get(), get(), get(), get(), get()) }
    single { CommentRepository(get(), get()) }

    // Room db
    single { AppDatabase.init(androidContext()) }
    factory { get<AppDatabase>().postDao() }
    factory { get<AppDatabase>().userDao() }
    factory { get<AppDatabase>().commentDao() }
    factory { get<AppDatabase>().likeDao() }

    // Scope
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    // ViewModels
    viewModel { InstantAppViewModel(get()) }
    viewModel { CreateAccountViewModel(get(), get(), get()) }
    viewModel { LoginScreenViewModel(get()) }
    viewModel { FeedViewModel(get(), get(), get()) }
    viewModel { CameraScreenViewModel(get(), get()) }
    viewModel { params -> CommentScreenViewModel(get(), get(), get(), postId = params.get()) }

    factory<RegisteredUserDataSource> { FirestoreRegisteredUserDataSource(get()) }
    single<AuthStateUserDataSource> { FirebaseAuthStateUserDataSource(get(), get(), get()) }
    factory { FcmTokenUpdater(get(), get()) }

    single { ObserveUserAuthStateUseCase(get(), get(), get()) }
    single<SignInViewModelDelegate> { FirebaseSignInViewModelDelegate(get(), get()) }
}