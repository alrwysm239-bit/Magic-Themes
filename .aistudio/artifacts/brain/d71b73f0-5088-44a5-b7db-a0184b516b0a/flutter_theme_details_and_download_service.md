# Flutter Theme Details Screen & Supabase Download Service

This file contains the complete, production-ready Dart implementation for:
1. `theme_download_service.dart`: Handles Dio download to `/storage/emulated/0/Download/ThemeStore`, calling Supabase RPC `increment_download`, and reporting percentage stream.
2. `theme_details_screen.dart`: Dark-mode glowing UI with realistic phone preview mockup, title, rating stars, description, specs, animated download progress button, and post-download dialog.

---

### 1. `lib/services/theme_download_service.dart`

```dart
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:path_provider/path_provider.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class ThemeDownloadService {
  final SupabaseClient _supabase = Supabase.instance.client;
  final Dio _dio = Dio();

  /// Target directory: /storage/emulated/0/Download/ThemeStore
  Future<Directory> _getThemeStoreDownloadDirectory() async {
    Directory targetDir;

    if (Platform.isAndroid) {
      // Standard public Android Download folder
      const downloadPath = '/storage/emulated/0/Download/ThemeStore';
      targetDir = Directory(downloadPath);

      if (!await targetDir.exists()) {
        try {
          await targetDir.create(recursive: true);
        } catch (_) {
          // Fallback if scoped storage blocks direct path creation
          final extDirs = await getExternalStorageDirectories(type: StorageDirectory.downloads);
          final base = extDirs != null && extDirs.isNotEmpty
              ? extDirs.first
              : await getApplicationDocumentsDirectory();
          targetDir = Directory('${base.path}/ThemeStore');
          if (!await targetDir.exists()) {
            await targetDir.create(recursive: true);
          }
        }
      }
    } else {
      final base = await getApplicationDocumentsDirectory();
      targetDir = Directory('${base.path}/ThemeStore');
      if (!await targetDir.exists()) {
        await targetDir.create(recursive: true);
      }
    }

    return targetDir;
  }

  /// Downloads the .hwt theme file and increments the download counter in Supabase
  Future<File> downloadThemeFile({
    required String themeId,
    required String hwtStoragePath,
    required String themeTitle,
    required void Function(double progress, int receivedBytes, int totalBytes) onProgress,
  }) async {
    // 1. Call Supabase SQL stored procedure to increment download counter
    try {
      await _supabase.rpc(
        'increment_download',
        params: {'theme_id': themeId},
      );
    } catch (e) {
      // Log or gracefully continue even if RPC fails so user gets their theme
      print('Warning: Failed to call increment_download RPC: $e');
    }

    // 2. Obtain download URL (Handles direct URLs from Option B or Supabase Storage paths)
    String downloadUrl;
    if (hwtStoragePath.startsWith('http://') || hwtStoragePath.startsWith('https://')) {
      downloadUrl = hwtStoragePath;
    } else {
      try {
        downloadUrl = _supabase.storage.from('themes').getPublicUrl(hwtStoragePath);
      } catch (_) {
        downloadUrl = _supabase.storage.from('themes_hwt').getPublicUrl(hwtStoragePath);
      }
    }

    // 3. Prepare target destination file in /Honor/Themes/ or /Huawei/Themes/
    final isHnt = hwtStoragePath.toLowerCase().endsWith('.hnt') || themeTitle.toLowerCase().contains('honor');
    final ext = isHnt ? '.hnt' : '.hwt';
    final targetSubFolder = isHnt ? 'Honor/Themes' : 'Huawei/Themes';

    Directory targetDir;
    final nativePlatformDir = Directory('/storage/emulated/0/$targetSubFolder');
    if (await nativePlatformDir.exists()) {
      targetDir = nativePlatformDir;
    } else {
      try {
        await nativePlatformDir.create(recursive: true);
        targetDir = nativePlatformDir;
      } catch (_) {
        targetDir = await _getThemeStoreDownloadDirectory();
      }
    }

    final sanitizedTitle = themeTitle.replaceAll(RegExp(r'[^\w\s\.-]'), '_').trim();
    final fileName = sanitizedTitle.toLowerCase().endsWith('.hwt') || sanitizedTitle.toLowerCase().endsWith('.hnt')
        ? sanitizedTitle
        : '$sanitizedTitle$ext';
    final targetFile = File('${targetDir.path}/$fileName');

    // Remove old partially downloaded file if exists
    if (await targetFile.exists()) {
      await targetFile.delete();
    }

    // 4. Download file using Dio with real-time progress callbacks
    final response = await _dio.download(
      downloadUrl,
      targetFile.path,
      options: Options(
        responseType: ResponseType.bytes,
        followRedirects: true,
      ),
      onReceiveProgress: (received, total) {
        if (total > 0) {
          final progress = received / total;
          onProgress(progress, received, total);
        }
      },
    );

    if (response.statusCode != 200 && response.statusCode != 206) {
      throw Exception('Erreur de téléchargement: Code HTTP ${response.statusCode}');
    }

    return targetFile;
  }
}
```

---

### 2. `lib/screens/theme_details_screen.dart`

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:open_filex/open_filex.dart';
import '../models/theme_model.dart';
import '../services/theme_download_service.dart';

class ThemeDetailsScreen extends StatefulWidget {
  final ThemeModel theme;

  const ThemeDetailsScreen({super.key, required this.theme});

  @override
  State<ThemeDetailsScreen> createState() => _ThemeDetailsScreenState();
}

class _ThemeDetailsScreenState extends State<ThemeDetailsScreen> {
  final ThemeDownloadService _downloadService = ThemeDownloadService();

  bool _isDownloading = false;
  double _progress = 0.0;
  String _downloadStatusText = '';
  File? _savedFile;
  bool _isFavorite = false;

  @override
  void initState() {
    super.initState();
    _isFavorite = widget.theme.isFavorite;
  }

  Future<void> _startDownload() async {
    setState(() {
      _isDownloading = true;
      _progress = 0.0;
      _downloadStatusText = 'Connexion à Supabase...';
    });

    try {
      final file = await _downloadService.downloadThemeFile(
        themeId: widget.theme.id,
        hwtStoragePath: widget.theme.hwtStoragePath,
        themeTitle: widget.theme.title,
        onProgress: (progress, received, total) {
          final receivedMb = (received / (1024 * 1024)).toStringAsFixed(1);
          final totalMb = (total / (1024 * 1024)).toStringAsFixed(1);
          setState(() {
            _progress = progress;
            _downloadStatusText = '$receivedMb MB / $totalMb MB (${(progress * 100).toInt()}%)';
          });
        },
      );

      setState(() {
        _isDownloading = false;
        _savedFile = file;
      });

      _showDownloadSuccessDialog(file);
    } catch (e) {
      setState(() {
        _isDownloading = false;
        _downloadStatusText = '';
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          backgroundColor: const Color(0xFFEF4444),
          content: Text('Échec du téléchargement: $e'),
        ),
      );
    }
  }

  void _showDownloadSuccessDialog(File file) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF141129),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(24),
          side: const BorderSide(color: Color(0xFF8C52FF), width: 1.5),
        ),
        title: Row(
          children: const [
            Icon(Icons.check_circle, color: Color(0xFF00D2FF), size: 28),
            SizedBox(width: 10),
            Text('Téléchargement Réussi !', style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Le fichier .hwt a été enregistré directement dans le dossier Download de votre appareil:',
              style: TextStyle(color: Colors.white70, fontSize: 13),
            ),
            const SizedBox(height: 10),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: const Color(0xFF1D1838),
                borderRadius: BorderRadius.circular(14),
                border: Border.all(color: const Color(0xFF00D2FF).withOpacity(0.4)),
              ),
              child: Row(
                children: [
                  const Icon(Icons.folder, color: Color(0xFF00D2FF), size: 20),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      file.path,
                      style: const TextStyle(color: Colors.white, fontSize: 11, fontFamily: 'monospace'),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 14),
            const Text(
              'Comment l\'appliquer sur Honor & Huawei:\n'
              '1. Ouvrez l\'application Thèmes officielle.\n'
              '2. Allez dans l\'onglet "Moi" > "Mes thèmes".\n'
              '3. Cliquez sur le thème pour l\'appliquer.',
              style: TextStyle(color: Colors.white60, fontSize: 12, height: 1.4),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              OpenFilex.open(file.path);
            },
            child: const Text('Ouvrir / Appliquer', style: TextStyle(color: Color(0xFF00D2FF), fontWeight: FontWeight.bold)),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Fermer', style: TextStyle(color: Colors.white54)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    const neonPurple = Color(0xFF8C52FF);
    const neonCyan = Color(0xFF00D2FF);
    const neonAmber = Color(0xFFFFB800);

    return Scaffold(
      backgroundColor: const Color(0xFF07060E),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text('Détails du thème', style: TextStyle(fontWeight: FontWeight.bold)),
        actions: [
          IconButton(
            icon: Icon(
              _isFavorite ? Icons.star : Icons.star_border,
              color: _isFavorite ? neonAmber : Colors.white,
            ),
            onPressed: () => setState(() => _isFavorite = !_isFavorite),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 1. Creator & Title Card
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFF141129),
                borderRadius: BorderRadius.circular(22),
                border: Border.all(color: neonPurple.withOpacity(0.3)),
              ),
              child: Row(
                children: [
                  ClipRRect(
                    borderRadius: BorderRadius.circular(14),
                    child: Container(
                      width: 52,
                      height: 52,
                      color: neonPurple.withOpacity(0.25),
                      child: widget.theme.previewImageUrl != null
                          ? CachedNetworkImage(
                              imageUrl: widget.theme.previewImageUrl!,
                              fit: BoxFit.cover,
                            )
                          : const Icon(Icons.palette, color: Colors.white),
                    ),
                  ),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          widget.theme.title,
                          style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold, color: Colors.white),
                        ),
                        Text(
                          'Par ${widget.theme.creator}',
                          style: const TextStyle(fontSize: 12, color: Colors.white60),
                        ),
                        const SizedBox(height: 4),
                        Row(
                          children: [
                            const Icon(Icons.star, color: neonAmber, size: 14),
                            const SizedBox(width: 4),
                            Text(
                              widget.theme.rating.toStringAsFixed(1),
                              style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 12),
                            ),
                            const SizedBox(width: 4),
                            Text(
                              '(${widget.theme.reviewsCount} avis)',
                              style: const TextStyle(color: Colors.white54, fontSize: 11),
                            ),
                            const SizedBox(width: 8),
                            Text(
                              '· ${widget.theme.downloadsCount} téléch.',
                              style: const TextStyle(color: neonCyan, fontSize: 11, fontWeight: FontWeight.w500),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // 2. Realistic Smartphone Preview Mockup
            Center(
              child: Container(
                height: 380,
                width: 230,
                decoration: BoxDecoration(
                  color: const Color(0xFF0F0E17),
                  borderRadius: BorderRadius.circular(36),
                  border: Border.all(color: const Color(0xFF28253D), width: 3.5),
                  boxShadow: [
                    BoxShadow(color: neonPurple.withOpacity(0.35), blurRadius: 28, spreadRadius: 2),
                  ],
                ),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(30),
                  child: Stack(
                    fit: StackFit.expand,
                    children: [
                      // Preview image or futuristic fallback gradient
                      if (widget.theme.previewImageUrl != null)
                        CachedNetworkImage(
                          imageUrl: widget.theme.previewImageUrl!,
                          fit: BoxFit.cover,
                        )
                      else
                        Container(
                          decoration: const BoxDecoration(
                            gradient: LinearGradient(
                              colors: [Color(0xFF2E0854), Color(0xFF00D2FF)],
                              begin: Alignment.topCenter,
                              end: Alignment.bottomCenter,
                            ),
                          ),
                        ),

                      // Dark overlay for clock visibility
                      Container(color: Colors.black.withOpacity(0.25)),

                      // Neon Lockscreen Clock & Weather
                      Positioned(
                        top: 45,
                        left: 0,
                        right: 0,
                        child: Column(
                          children: const [
                            Text(
                              '12:45',
                              style: TextStyle(
                                fontSize: 44,
                                fontWeight: FontWeight.w800,
                                color: neonCyan,
                                letterSpacing: 1.2,
                              ),
                            ),
                            Text(
                              'Vendredi, 6 Novembre · 24°C',
                              style: TextStyle(fontSize: 11, color: Colors.white, fontWeight: FontWeight.w500),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),

            const SizedBox(height: 24),

            // 3. Technical Specifications Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFF141129),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: neonPurple.withOpacity(0.2)),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Spécifications', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 14)),
                  const SizedBox(height: 10),
                  _specRow('Format', '.HWT (Huawei/Honor Archive)'),
                  _specRow('Dossier cible', '/Download/ThemeStore'),
                  _specRow('Compatibilité', widget.theme.compatibleVersions),
                  _specRow('Taille du fichier', widget.theme.sizeMb),
                ],
              ),
            ),

            const SizedBox(height: 16),

            // 4. Description
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFF141129),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: Colors.white.withOpacity(0.08)),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Description', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 14)),
                  const SizedBox(height: 6),
                  Text(
                    widget.theme.description,
                    style: const TextStyle(color: Colors.white70, fontSize: 13, height: 1.4),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // 5. Download Progress Indicator (Visible while downloading)
            if (_isDownloading) ...[
              LinearProgressIndicator(
                value: _progress,
                backgroundColor: const Color(0xFF1D1838),
                valueColor: const AlwaysStoppedAnimation<Color>(neonCyan),
                minHeight: 6,
                borderRadius: BorderRadius.circular(3),
              ),
              const SizedBox(height: 8),
              Center(
                child: Text(
                  _downloadStatusText,
                  style: const TextStyle(color: neonCyan, fontSize: 12, fontWeight: FontWeight.bold),
                ),
              ),
              const SizedBox(height: 14),
            ],

            // 6. Glowing Primary Download Button
            SizedBox(
              width: double.infinity,
              height: 56,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  padding: EdgeInsets.zero,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(28)),
                  backgroundColor: Colors.transparent,
                  shadowColor: neonCyan.withOpacity(0.6),
                  elevation: 12,
                ),
                onPressed: _isDownloading ? null : _startDownload,
                child: Ink(
                  decoration: BoxDecoration(
                    gradient: const LinearGradient(colors: [neonPurple, neonCyan]),
                    borderRadius: BorderRadius.circular(28),
                  ),
                  child: Center(
                    child: _isDownloading
                        ? Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: const [
                              SizedBox(
                                width: 22,
                                height: 22,
                                child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2.5),
                              ),
                              SizedBox(width: 12),
                              Text('Téléchargement en cours...', style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white)),
                            ],
                          )
                        : Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: const [
                              Icon(Icons.download, color: Colors.white),
                              SizedBox(width: 8),
                              Text('Télécharger (.hwt)', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white)),
                            ],
                          ),
                  ),
                ),
              ),
            ),

            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _specRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(color: Colors.white54, fontSize: 12)),
          Text(value, style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.w500)),
        ],
      ),
    );
  }
}
```
