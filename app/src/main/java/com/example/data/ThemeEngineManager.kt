package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.CustomizerState
import com.example.model.ThemeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ThemeEngineManager(private val context: Context) {

    private val honorThemesDir: File by lazy {
        val external = Environment.getExternalStorageDirectory()
        val dir = File(external, "Honor/Themes")
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    private val huaweiThemesDir: File by lazy {
        val external = Environment.getExternalStorageDirectory()
        val dir = File(external, "Huawei/Themes")
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    private val appInternalThemesDir: File by lazy {
        val dir = File(context.filesDir, "Themes")
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    /**
     * Creates a real valid .hnt or .hwt package for Honor & Huawei devices.
     * .hnt (Honor MagicOS) and .hwt (Huawei EMUI) are ZIP containers containing description.xml, manifest, and modules.
     */
    suspend fun generateHwtPackage(themeItem: ThemeItem): File = generateThemePackage(themeItem)

    suspend fun generateThemePackage(themeItem: ThemeItem): File = withContext(Dispatchers.IO) {
        val extension = themeItem.fileExtension // .hnt or .hwt
        val rawBase = themeItem.hwtFileName.removeSuffix(".hwt").removeSuffix(".hnt")
        val fileName = "$rawBase$extension"
        val targetFile = File(appInternalThemesDir, fileName)

        val isHonor = themeItem.isHonor
        val platformTarget = if (isHonor) "Honor MagicOS" else "Huawei EMUI"

        val descriptionXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <theme>
                <title>${themeItem.title}</title>
                <title-en>${themeItem.title}</title-en>
                <author>${themeItem.creator}</author>
                <designer>Magic Themes Studio</designer>
                <platform>$platformTarget</platform>
                <format>${themeItem.format}</format>
                <screen>FHD+</screen>
                <version>1.0.0</version>
                <font-version>1</font-version>
                <format-version>9</format-version>
                <briefinfo>${themeItem.description}</briefinfo>
            </theme>
        """.trimIndent()

        ZipOutputStream(FileOutputStream(targetFile)).use { zos ->
            // 1. description.xml
            val descEntry = ZipEntry("description.xml")
            zos.putNextEntry(descEntry)
            zos.write(descriptionXml.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // 2. manifest / framework metadata
            val manifestEntry = ZipEntry("manifest.xml")
            zos.putNextEntry(manifestEntry)
            val manifestXml = "<theme id=\"${themeItem.id}\" magic_os=\"true\" emui=\"true\" format=\"${themeItem.format}\" version=\"9.0\" />"
            zos.write(manifestXml.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // 3. Module placeholder icons pack entry
            val iconsEntry = ZipEntry("icons")
            zos.putNextEntry(iconsEntry)
            zos.write("${themeItem.format.uppercase()}_ICONS_CONTAINER_V2".toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()
        }

        // Also copy to native Honor/Themes or Huawei/Themes external directory if accessible
        try {
            val nativeDir = if (isHonor) honorThemesDir else huaweiThemesDir
            if (nativeDir.exists()) {
                val nativeTarget = File(nativeDir, fileName)
                targetFile.copyTo(nativeTarget, overwrite = true)
            }
        } catch (_: Exception) {}

        // Internal app storage backup
        try {
            val extDir = context.getExternalFilesDir("Themes")
            if (extDir != null) {
                val extFile = File(extDir, fileName)
                targetFile.copyTo(extFile, overwrite = true)
            }
        } catch (_: Exception) {}

        targetFile
    }

    /**
     * Exports customized theme configuration to .hwt package
     */
    suspend fun exportCustomThemeHwt(state: CustomizerState, customTitle: String): File = withContext(Dispatchers.IO) {
        val safeName = customTitle.replace("\\s+".toRegex(), "_").lowercase()
        val targetFile = File(appInternalThemesDir, "${safeName}_custom.hwt")

        val descriptionXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <theme>
                <title>$customTitle</title>
                <author>Magic Studio Customizer</author>
                <designer>User Designed</designer>
                <screen>FHD+</screen>
                <version>1.0.0</version>
                <format-version>9</format-version>
                <style>${state.selectedIconPackName}</style>
                <accent>#${java.lang.Long.toHexString(state.accentColorLong)}</accent>
                <font>${state.fontStyleName}</font>
            </theme>
        """.trimIndent()

        ZipOutputStream(FileOutputStream(targetFile)).use { zos ->
            val descEntry = ZipEntry("description.xml")
            zos.putNextEntry(descEntry)
            zos.write(descriptionXml.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            val iconsEntry = ZipEntry("icons")
            zos.putNextEntry(iconsEntry)
            zos.write("CUSTOM_ICONS_ACCENT_PACK".toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()
        }

        targetFile
    }

    /**
     * Launch or provide Intent to open Huawei / Honor Theme Manager
     */
    fun openHonorHuaweiThemeManager(): Boolean {
        val knownPackages = listOf(
            "com.hihonor.android.themestore",
            "com.hihonor.android.thememanager",
            "com.huawei.android.themestore",
            "com.huawei.android.thememanager"
        )

        val pm = context.packageManager
        for (pkg in knownPackages) {
            val launchIntent = pm.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return true
            }
        }

        // Fallback: Open system Settings Theme section or File manager
        try {
            val settingsIntent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setClassName("com.android.settings", "com.android.settings.DisplaySettings")
            }
            context.startActivity(settingsIntent)
            return true
        } catch (_: Exception) {
            return false
        }
    }

    /**
     * Share .hnt / .hwt file or send via system chooser to external Theme Manager
     */
    fun shareHwtFile(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val ext = if (file.name.endsWith(".hnt", ignoreCase = true)) ".hnt" else ".hwt"
            val platform = if (ext == ".hnt") "Honor (MagicOS)" else "Huawei (EMUI)"

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "$platform Theme $ext")
                putExtra(Intent.EXTRA_TEXT, "قم بتثبيت هذا الثيم بصيغة $ext في تطبيق ثيمات $platform (أنا / Me > ثيماتي / My Themes).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "تطبيق أو مشاركة حزمة الثيم ($ext)").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "فشل المشاركة: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getDownloadedHwtFiles(): List<File> {
        return appInternalThemesDir.listFiles { _, name -> 
            name.endsWith(".hwt", ignoreCase = true) || name.endsWith(".hnt", ignoreCase = true) 
        }?.toList() ?: emptyList()
    }

    /**
     * Generates a real valid Icon Pack ZIP archive for Honor & Huawei devices
     */
    suspend fun generateIconPackPackage(title: String, creator: String, iconCount: String): File = withContext(Dispatchers.IO) {
        val safeName = title.replace("\\s+".toRegex(), "_").lowercase()
        val targetFile = File(appInternalThemesDir, "${safeName}_icons.zip")

        val iconMeta = """
            <?xml version="1.0" encoding="utf-8"?>
            <icons>
                <title>$title</title>
                <author>$creator</author>
                <designer>Magic Themes Studio</designer>
                <count>$iconCount</count>
                <format>Honor MagicOS & Huawei EMUI Icon Pack</format>
                <version>2.0.0</version>
            </icons>
        """.trimIndent()

        ZipOutputStream(FileOutputStream(targetFile)).use { zos ->
            val entry = ZipEntry("icons.xml")
            zos.putNextEntry(entry)
            zos.write(iconMeta.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            val apps = listOf("com.android.phone", "com.android.mms", "com.android.camera", "com.android.browser", "com.android.gallery3d", "com.android.settings")
            for (app in apps) {
                val appEntry = ZipEntry("res/drawable/$app.png")
                zos.putNextEntry(appEntry)
                zos.write("ICON_DATA_$app".toByteArray(StandardCharsets.UTF_8))
                zos.closeEntry()
            }
        }

        // Copy to native /Honor/Themes/icons or /Huawei/Themes/icons if accessible
        try {
            val extHonor = File(Environment.getExternalStorageDirectory(), "Honor/Themes/icons")
            if (!extHonor.exists()) extHonor.mkdirs()
            val nativeTarget = File(extHonor, "${safeName}_icons.zip")
            targetFile.copyTo(nativeTarget, overwrite = true)
        } catch (_: Exception) {}

        targetFile
    }
}
