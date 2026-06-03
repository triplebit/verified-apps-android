package org.privacyguides.verifiedapps.ui

import android.app.Application
import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import org.privacyguides.verifiedapps.BuildConfig
import org.privacyguides.verifiedapps.Source
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus
import org.privacyguides.verifiedapps.data.SubmissionUiState
import org.privacyguides.verifiedapps.data.VerificationInfo
import org.privacyguides.verifiedapps.data.VerifyAppUiState
import org.privacyguides.verifiedapps.internalVerificationInfoDatabase
import org.privacyguides.verifiedapps.submission.AppSubmissionClient
import org.privacyguides.verifiedapps.submission.AppSubmissionPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.security.MessageDigest

class VerifyAppViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VerifyAppUiState())
    val uiState: StateFlow<VerifyAppUiState> = _uiState.asStateFlow()

    private val _submissionState = MutableStateFlow<SubmissionUiState>(SubmissionUiState.Idle)
    val submissionState: StateFlow<SubmissionUiState> = _submissionState.asStateFlow()

    fun setAppVerificationInfo(
        name: String,
        packageName: String,
        hashes: Hashes,
        internalDatabaseInfo: InternalDatabaseInfo,
    ) {
        _uiState.value.name.value = name
        _uiState.value.packageName.value = packageName
        _uiState.value.hashes.value = hashes
        _uiState.value.internalDatabaseInfo.value = internalDatabaseInfo
        resetSubmissionState()
    }

    fun setAppIcon(icon: Drawable) {
        _uiState.value.icon.value = icon
    }

    fun setSearchQuery(query: String): Unit {
        uiState.value.searchQuery.value = query
    }

    fun resetSubmissionState() {
        _submissionState.value = SubmissionUiState.Idle
    }

    fun clearSubmissionMessage() {
        when (_submissionState.value) {
            is SubmissionUiState.Success, is SubmissionUiState.Error -> {
                _submissionState.value = SubmissionUiState.Idle
            }
            else -> Unit
        }
    }

    fun submitAppForDatabaseInclusion() {
        val url = BuildConfig.APP_SUBMISSION_URL
        if (url.isBlank()) {
            _submissionState.value = SubmissionUiState.Error("Submission is not configured for this build.")
            return
        }

        val json = AppSubmissionPayload.toJson(
            packageName = _uiState.value.packageName.value,
            appLabel = _uiState.value.name.value,
            hashes = _uiState.value.hashes.value,
            applicationId = BuildConfig.APPLICATION_ID,
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE,
        )

        _submissionState.value = SubmissionUiState.Submitting
        viewModelScope.launch {
            AppSubmissionClient.submit(url, json, allowHttp = BuildConfig.DEBUG)
                .onSuccess {
                    _submissionState.value = SubmissionUiState.Success
                }
                .onFailure { error ->
                    _submissionState.value = SubmissionUiState.Error(
                        error.message ?: "Submission failed."
                    )
                }
        }
    }

    fun getHashesFromPackageInfo(packageInfo: PackageInfo): Hashes {
        val signingInfo = packageInfo.signingInfo
        val hasMultipleSigners = signingInfo!!.hasMultipleSigners()

        val signatures = if (hasMultipleSigners) {
            signingInfo.apkContentsSigners
                .map { signature ->
                    MessageDigest
                        .getInstance("SHA-256")
                        .digest(signature.toByteArray())
                        .joinToString(":") {
                            "%02x".format(it)
                        }
                        .uppercase()
                }
        } else {
            signingInfo.signingCertificateHistory
                .map { signature ->
                    MessageDigest
                        .getInstance("SHA-256")
                        .digest(signature.toByteArray())
                        .joinToString(":") {
                            "%02x".format(it)
                        }
                        .uppercase()
                }
        }

        return Hashes(listOf(Source.NONE), signatures, hasMultipleSigners)
    }

    fun setApkFailedToParse(b: Boolean) {
        _uiState.value.apkFailedToParse.value = b
    }

    fun getInternalDatabaseInfoFromVerificationInfo(verificationInfo: VerificationInfo): InternalDatabaseInfo {
        return internalVerificationInfoDatabase.run {
            val packageNameMatchedInternalDatabaseVerificationInfo = try {
                this.first {
                    it.packageName == verificationInfo.packageName
                }
            } catch (e: NoSuchElementException) {
                return@run InternalDatabaseInfo(InternalDatabaseStatus.NOT_FOUND, listOf(Source.NONE))
            }

            val maybeMatchedHashes = packageNameMatchedInternalDatabaseVerificationInfo.hashesList.find {
                it.matchesSigningFingerprints(verificationInfo.hashes)
            }
            if (maybeMatchedHashes != null) {
                InternalDatabaseInfo(InternalDatabaseStatus.MATCH, maybeMatchedHashes.sources)
            } else {
                InternalDatabaseInfo(InternalDatabaseStatus.NOMATCH, listOf(Source.NONE))
            }
        }
    }

    fun setApkVerificationInfoAndInternalDatabaseStatusFromUri(
        contentResolver: ContentResolver,
        uri: Uri,
        packageManager: PackageManager,
    ) {
        contentResolver.openInputStream(uri).use { inputStream ->
            val tempFile = File.createTempFile("temp", null, getApplication<Application>().cacheDir)

            tempFile.outputStream().use { fileOut ->
                inputStream.use { it!!.copyTo(fileOut) }
            }

            val packageInfo = packageManager.getPackageArchiveInfo(
                tempFile.path,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            val applicationInfo = packageInfo?.applicationInfo ?: ApplicationInfo()

            if (packageInfo == null) {
                setApkFailedToParse(true)

                val isFileDeleted = tempFile.delete()

                if (!isFileDeleted) {
                    throw IOException(
                        "Temporary APK file couldn't be deleted! Report this bug please with instructions " +
                                "on how to reproduce!"
                    )
                }

                return
            }

            applicationInfo.sourceDir = tempFile.path
            applicationInfo.publicSourceDir = tempFile.path

            val packageName = packageInfo.packageName

            val hashes = getHashesFromPackageInfo(packageInfo)

            setAppVerificationInfo(
                packageManager.getApplicationLabel(applicationInfo).toString(),
                packageName,
                hashes,
                getInternalDatabaseInfoFromVerificationInfo(VerificationInfo(packageName, hashes)),
            )
            setAppIcon(packageManager.getApplicationIcon(applicationInfo))

            val isFileDeleted = tempFile.delete()

            if (!isFileDeleted) {
                throw IOException(
                    "Temporary APK file couldn't be deleted! Report this bug please with instructions " +
                            "on how to reproduce!"
                )
            }
        }
    }

    fun clearUiState() {
        _uiState.value = VerifyAppUiState()
        resetSubmissionState()
    }
}
