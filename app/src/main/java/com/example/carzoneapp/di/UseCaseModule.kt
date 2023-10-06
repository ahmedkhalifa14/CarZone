package com.example.carzoneapp.di

import com.example.domain.repo.MainRepo
import com.example.domain.usecase.AddToSavedItemsUseCase
import com.example.domain.usecase.AddVehicleAdUseCase
import com.example.domain.usecase.FetchRegionsInCountryUseCase
import com.example.domain.usecase.GetAllAdsByUserIdUseCase
import com.example.domain.usecase.GetAllAdsByVehicleTypeUseCase
import com.example.domain.usecase.GetAllAdsUseCase
import com.example.domain.usecase.GetAllVehiclesCategoriesUseCase
import com.example.domain.usecase.GetMessagesUseCase
import com.example.domain.usecase.GetSavedItemsByUserIdUseCase
import com.example.domain.usecase.GetUserChatListUseCase
import com.example.domain.usecase.GetUserInfoUseCase
import com.example.domain.usecase.IsFirstTimeLaunchUseCase
import com.example.domain.usecase.LoginWithEmailUseCase
import com.example.domain.usecase.RegisterUseCase
import com.example.domain.usecase.RemoveFromSavedItemsByUserIdUseCase
import com.example.domain.usecase.SaveFirstTimeLaunchUseCase
import com.example.domain.usecase.SaveUserChatListUseCase
import com.example.domain.usecase.SaveUserDataUseCase
import com.example.domain.usecase.SendMessageUseCase
import com.example.domain.usecase.SendVerificationCodeUseCase
import com.example.domain.usecase.SignInWithGoogleUseCase
import com.example.domain.usecase.UploadImagesUseCase
import com.example.domain.usecase.VerifyCodeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideRegisterUseCase(mainRepo: MainRepo): RegisterUseCase =
        RegisterUseCase(mainRepo)

    @Provides
    fun provideLoginWithEmailUseCase(mainRepo: MainRepo): LoginWithEmailUseCase =
        LoginWithEmailUseCase(mainRepo)

    @Provides
    fun provideSaveUserDataUseCase(mainRepo: MainRepo): SaveUserDataUseCase =
        SaveUserDataUseCase(mainRepo)

    @Provides
    fun provideGetAllVehiclesCategoriesUseCase(mainRepo: MainRepo): GetAllVehiclesCategoriesUseCase =
        GetAllVehiclesCategoriesUseCase(mainRepo)


    @Provides
    fun provideSaveFirstTimeLaunchUseCase(mainRepo: MainRepo): SaveFirstTimeLaunchUseCase =
        SaveFirstTimeLaunchUseCase(mainRepo)

    @Provides
    fun provideIsFirstTimeLaunchUseCase(mainRepo: MainRepo): IsFirstTimeLaunchUseCase =
        IsFirstTimeLaunchUseCase(mainRepo)

    @Provides
    fun provideAddVehicleAdsUseCase(mainRepo: MainRepo): AddVehicleAdUseCase =
        AddVehicleAdUseCase(mainRepo)

    @Provides
    fun provideSignInWithGoogleUseCase(mainRepo: MainRepo): SignInWithGoogleUseCase =
        SignInWithGoogleUseCase(mainRepo)

    @Provides
    fun provideSendVerificationCodeUseCase(mainRepo: MainRepo): SendVerificationCodeUseCase =
        SendVerificationCodeUseCase(mainRepo)

    @Provides
    fun provideVerifyCodeUseCase(mainRepo: MainRepo): VerifyCodeUseCase =
        VerifyCodeUseCase(mainRepo)

    @Provides
    fun provideUploadImagesUseCase(mainRepo: MainRepo): UploadImagesUseCase =
        UploadImagesUseCase(mainRepo)

    @Provides
    fun provideGetAllAdsUseCase(mainRepo: MainRepo): GetAllAdsUseCase =
        GetAllAdsUseCase(mainRepo)

    @Provides
    fun provideFetchRegionsInCountryUseCase(mainRepo: MainRepo): FetchRegionsInCountryUseCase =
        FetchRegionsInCountryUseCase(mainRepo)

    @Provides
    fun provideGetUserInfoUseCase(mainRepo: MainRepo): GetUserInfoUseCase =
        GetUserInfoUseCase(mainRepo)

    @Provides
    fun provideGetAllAdsByVehicleTypeUseCase(mainRepo: MainRepo): GetAllAdsByVehicleTypeUseCase =
        GetAllAdsByVehicleTypeUseCase(mainRepo)

    @Provides
    fun provideGetAllAdsByUserIdUseCase(mainRepo: MainRepo): GetAllAdsByUserIdUseCase =
        GetAllAdsByUserIdUseCase(mainRepo)


    @Provides
    fun provideSendMessageUseCase(mainRepo: MainRepo): SendMessageUseCase =
        SendMessageUseCase(mainRepo)

    @Provides
    fun provideGetMessagesUseCase(mainRepo: MainRepo): GetMessagesUseCase =
        GetMessagesUseCase(mainRepo)

    @Provides
    fun provideGetUserChatListUseCase(mainRepo: MainRepo): GetUserChatListUseCase =
        GetUserChatListUseCase(mainRepo)

    @Provides
    fun provideSaveUserChatListUseCase(mainRepo: MainRepo): SaveUserChatListUseCase =
        SaveUserChatListUseCase(mainRepo)

    @Provides
    fun provideRemoveFromSavedItemsByUserIdUseCase(mainRepo: MainRepo): RemoveFromSavedItemsByUserIdUseCase =
        RemoveFromSavedItemsByUserIdUseCase(mainRepo)

    @Provides
    fun provideAddToSavedItemsUseCase(mainRepo: MainRepo): AddToSavedItemsUseCase =
        AddToSavedItemsUseCase(mainRepo)

    @Provides
    fun provideGetSavedItemsByUserIdUseCase(mainRepo: MainRepo): GetSavedItemsByUserIdUseCase =
        GetSavedItemsByUserIdUseCase(mainRepo)

}