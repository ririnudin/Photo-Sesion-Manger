package com.example.data.model

data class AppSettings(
    val googleAccountName: String = "Fotografer Event Pro",
    val googleAccountEmail: String = "photographer@gmail.com",
    val isSignedIn: Boolean = true,
    val mainFolderName: String = "Photo Session Manager",
    val autoUploadEnabled: Boolean = true,
    val wifiOnly: Boolean = false,
    val watermarkEnabled: Boolean = false,
    val watermarkText: String = "© Photo Session Manager",
    val uploadQuality: String = "HIGH", // "ORIGINAL", "HIGH", "COMPRESSED"
    val appTheme: String = "LIGHT" // "LIGHT", "DARK", "SYSTEM"
)
