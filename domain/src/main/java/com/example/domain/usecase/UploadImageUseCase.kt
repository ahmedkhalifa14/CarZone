package com.example.domain.usecase

import android.net.Uri
import com.example.domain.repo.MainRepo

class UploadImagesUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(imageUris: List<Uri>): List<String> =
        mainRepo.uploadImages(imageUris)
}