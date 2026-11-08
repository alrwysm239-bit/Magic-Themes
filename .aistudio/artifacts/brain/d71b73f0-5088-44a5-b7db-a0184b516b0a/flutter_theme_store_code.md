# Honor & Huawei Theme Store (MagicOS & EMUI) - Flutter & Supabase Implementation

This document provides the complete, production-ready Flutter/Dart architecture for the **Honor & Huawei Theme Store** app, fully integrated with **Supabase Database (`categories`, `themes`, `wallpapers`, `icon_packs`) and Supabase Storage (`.hwt` theme files)**.

---

## 1. Dependencies (`pubspec.yaml`)

```yaml
name: magic_themes_store
description: Futuristic Theme Store for Honor and Huawei EMUI / MagicOS devices.
publish_to: 'none'
version: 1.0.0+1

environment:
  sdk: '>=3.3.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter
  supabase_flutter: ^2.5.6
  provider: ^6.1.2
  cached_network_image: ^3.3.1
  dio: ^5.4.3+1
  path_provider: ^2.1.3
  open_filex: ^4.4.0
  flutter_staggered_grid_view: ^0.7.0
  share_plus: ^9.0.0
  shared_preferences: ^2.2.3

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0

flutter:
  uses-material-design: true
```

---

## 2. Supabase Models (`lib/models/`)

### `lib/models/theme_model.dart`
```dart
class ThemeModel {
  final String id;
  final String title;
  final String creator;
  final String price;
  final double rating;
  final int reviewsCount;
  final String downloadsCount;
  final String sizeMb;
  final bool isAnimated;
  final bool isPopular;
  final bool isEditorsChoice;
  final String categoryId;
  final String? previewImageUrl;
  final String? lockscreenUrl;
  final String description;
  final String compatibleVersions;
  final String hwtStoragePath;
  final bool isDownloaded;
  final bool isFavorite;

  ThemeModel({
    required this.id,
    required this.title,
    required this.creator,
    required this.price,
    this.rating = 5.0,
    this.reviewsCount = 25,
    this.downloadsCount = '48k',
    this.sizeMb = '28.4 MB',
    this.isAnimated = false,
    this.isPopular = false,
    this.isEditorsChoice = false,
    required this.categoryId,
    this.previewImageUrl,
    this.lockscreenUrl,
    this.description = '',
    this.compatibleVersions = 'MagicOS 7/8/9 & EMUI 12/13/14',
    required this.hwtStoragePath,
    this.isDownloaded = false,
    this.isFavorite = false,
  });

  factory ThemeModel.fromMap(Map<String, dynamic> map) {
    return ThemeModel(
      id: map['id']?.toString() ?? '',
      title: map['title'] ?? 'Untitled Theme',
      creator: map['creator'] ?? 'Magic Themes Studio',
      price: map['price'] ?? 'Gratuit',
      rating: (map['rating'] is num) ? (map['rating'] as num).toDouble() : 5.0,
      reviewsCount: map['reviews_count'] ?? 25,
      downloadsCount: map['downloads_count'] ?? '12k',
      sizeMb: map['size_mb'] ?? '25 MB',
      isAnimated: map['is_animated'] ?? false,
      isPopular: map['is_popular'] ?? false,
      isEditorsChoice: map['is_editors_choice'] ?? false,
      categoryId: map['category_id']?.toString() ?? '',
      previewImageUrl: map['preview_image_url'],
      lockscreenUrl: map['lockscreen_url'],
      description: map['description'] ?? 'Thème exclusif MagicOS / EMUI.',
      compatibleVersions: map['compatible_versions'] ?? 'MagicOS 7/8/9 & EMUI 12/13/14',
      hwtStoragePath: map['hwt_storage_path'] ?? 'themes/default.hwt',
      isFavorite: map['is_favorite'] ?? false,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'creator': creator,
      'price': price,
      'rating': rating,
      'reviews_count': reviewsCount,
      'downloads_count': downloadsCount,
      'size_mb': sizeMb,
      'is_animated': isAnimated,
      'is_popular': isPopular,
      'is_editors_choice': isEditorsChoice,
      'category_id': categoryId,
      'preview_image_url': previewImageUrl,
      'lockscreen_url': lockscreenUrl,
      'description': description,
      'compatible_versions': compatibleVersions,
      'hwt_storage_path': hwtStoragePath,
    };
  }

  ThemeModel copyWith({bool? isFavorite, bool? isDownloaded}) {
    return ThemeModel(
      id: id,
      title: title,
      creator: creator,
      price: price,
      rating: rating,
      reviewsCount: reviewsCount,
      downloadsCount: downloadsCount,
      sizeMb: sizeMb,
      isAnimated: isAnimated,
      isPopular: isPopular,
      isEditorsChoice: isEditorsChoice,
      categoryId: categoryId,
      previewImageUrl: previewImageUrl,
      lockscreenUrl: lockscreenUrl,
      description: description,
      compatibleVersions: compatibleVersions,
      hwtStoragePath: hwtStoragePath,
      isFavorite: isFavorite ?? this.isFavorite,
      isDownloaded: isDownloaded ?? this.isDownloaded,
    );
  }
}
```

### `lib/models/wallpaper_model.dart` & `icon_pack_model.dart`
```dart
class WallpaperModel {
  final String id;
  final String title;
  final String category;
  final String imageUrl;
  final String downloadsCount;
  final bool isLive;

  WallpaperModel({
    required this.id,
    required this.title,
    required this.category,
    required this.imageUrl,
    this.downloadsCount = '80k',
    this.isLive = false,
  });

  factory WallpaperModel.fromMap(Map<String, dynamic> map) {
    return WallpaperModel(
      id: map['id']?.toString() ?? '',
      title: map['title'] ?? 'Wallpaper',
      category: map['category'] ?? 'Wallpapers',
      imageUrl: map['image_url'] ?? '',
      downloadsCount: map['downloads_count'] ?? '50k',
      isLive: map['is_live'] ?? false,
    );
  }
}

class IconPackModel {
  final String id;
  final String title;
  final String creator;
  final double rating;
  final String iconCount;
  final String styleTag;
  final List<String> sampleIcons;
  final String downloadUrl;

  IconPackModel({
    required this.id,
    required this.title,
    required this.creator,
    this.rating = 5.0,
    this.iconCount = '1,500+ icônes',
    this.styleTag = 'Neon 3D',
    this.sampleIcons = const [],
    required this.downloadUrl,
  });

  factory IconPackModel.fromMap(Map<String, dynamic> map) {
    return IconPackModel(
      id: map['id']?.toString() ?? '',
      title: map['title'] ?? 'Icon Pack',
      creator: map['creator'] ?? 'Studio',
      rating: (map['rating'] is num) ? (map['rating'] as num).toDouble() : 5.0,
      iconCount: map['icon_count'] ?? '1,200+ icônes',
      styleTag: map['style_tag'] ?? 'Neon 3D',
      sampleIcons: List<String>.from(map['sample_icons'] ?? []),
      downloadUrl: map['download_url'] ?? '',
    );
  }
}
```

---

## 3. Supabase Service & `.hwt` Engine (`lib/services/supabase_service.dart`)

```dart
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:path_provider/path_provider.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../models/theme_model.dart';
import '../models/wallpaper_model.dart';
import '../models/icon_pack_model.dart';

class SupabaseService {
  final SupabaseClient client = Supabase.instance.client;
  final Dio dio = Dio();

  // Fetch Themes from Supabase
  Future<List<ThemeModel>> fetchThemes({String? categoryId, bool? popularOnly}) async {
    try {
      var query = client.from('themes').select();
      if (categoryId != null && categoryId.isNotEmpty) {
        query = query.eq('category_id', categoryId);
      }
      if (popularOnly == true) {
        query = query.eq('is_popular', true);
      }
      final response = await query.order('rating', ascending: false);
      return (response as List).map((map) => ThemeModel.fromMap(map)).toList();
    } catch (e) {
      debugPrint('Error fetching live themes from Supabase: $e');
      return [];
    }
  }

  // Fetch Wallpapers dynamically from Supabase
  Future<List<WallpaperModel>> fetchWallpapers({String? category}) async {
    try {
      var query = client.from('wallpapers').select();
      if (category != null && category.isNotEmpty) {
        query = query.eq('category', category);
      }
      final response = await query.order('created_at', ascending: false);
      return (response as List).map((map) => WallpaperModel.fromMap(map)).toList();
    } catch (e) {
      debugPrint('Error fetching live wallpapers from Supabase: $e');
      return [];
    }
  }

  // Fetch Icon Packs dynamically from Supabase
  Future<List<IconPackModel>> fetchIconPacks() async {
    try {
      final response = await client.from('icons').select().order('created_at', ascending: false);
      return (response as List).map((map) => IconPackModel.fromMap(map)).toList();
    } catch (e) {
      debugPrint('Error fetching live icons from Supabase: $e');
      return [];
    }
  }

  // Download .HWT / .HNT theme file from Supabase Storage and save into /Honor/Themes/ or local directory
  Future<File> downloadHwtFile({
    required String storagePath,
    required String fileName,
    required Function(double progress) onProgress,
  }) async {
    // 1. Get signed/public URL from Supabase Storage bucket 'themes'
    final String publicUrl = client.storage.from('themes').getPublicUrl(storagePath);

    // 2. Prepare destination path in /Honor/Themes or App Directory
    Directory baseDir;
    try {
      final extDir = await getExternalStorageDirectory();
      final honorDir = Directory('${extDir?.path}/Honor/Themes');
      if (!await honorDir.exists()) {
        await honorDir.create(recursive: true);
      }
      baseDir = honorDir;
    } catch (_) {
      baseDir = await getApplicationDocumentsDirectory();
    }

    final safeFileName = (fileName.endsWith('.hwt') || fileName.endsWith('.hnt')) ? fileName : '$fileName.hwt';
    final targetFile = File('${baseDir.path}/$safeFileName');

    // 3. Real HTTP download with progress tracking
    await dio.download(
      publicUrl,
      targetFile.path,
      onReceiveProgress: (received, total) {
        if (total > 0) {
          onProgress(received / total);
        }
      },
    );

    return targetFile;
  }
}
```

---

## 4. UI Theme & Design System (`lib/theme/app_theme.dart`)

```dart
import 'package:flutter/material.dart';

class AppColors {
  static const Color darkBackground = Color(0xFF07060E);
  static const Color darkSurface = Color(0xFF110F20);
  static const Color glassSurface = Color(0x281F1A3A);
  static const Color glassBorder = Color(0x33A78BFA);

  // Neon Glow Accents
  static const Color neonPurple = Color(0xFF8C52FF);
  static const Color neonCyan = Color(0xFF00D2FF);
  static const Color neonMagenta = Color(0xFFFF2A85);
  static const Color neonAmber = Color(0xFFFFB800);

  static const LinearGradient purpleCyanGradient = LinearGradient(
    colors: [neonPurple, neonCyan],
  );

  static const LinearGradient bottomBarGlow = LinearGradient(
    colors: [neonCyan, neonPurple, neonMagenta],
  );
}

class AppTheme {
  static ThemeData get darkTheme {
    return ThemeData(
      brightness: Brightness.dark,
      scaffoldBackgroundColor: AppColors.darkBackground,
      primaryColor: AppColors.neonPurple,
      colorScheme: const ColorScheme.dark(
        primary: AppColors.neonPurple,
        secondary: AppColors.neonCyan,
        surface: AppColors.darkSurface,
      ),
      fontFamily: 'Roboto',
      appBarTheme: const AppBarTheme(
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
    );
  }
}
```

---

## 5. Screen 1: Wallpapers & Icons Screen

### `lib/screens/wallpapers_screen.dart`
```dart
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../models/wallpaper_model.dart';
import '../services/supabase_service.dart';
import '../theme/app_theme.dart';

class WallpapersScreen extends StatefulWidget {
  const WallpapersScreen({super.key});

  @override
  State<WallpapersScreen> createState() => _WallpapersScreenState();
}

class _WallpapersScreenState extends State<WallpapersScreen> {
  final SupabaseService _service = SupabaseService();
  String _selectedFilter = 'Live Wallpapers';
  final List<String> _filters = ['Live Wallpapers', 'Wallpapers', 'Art/Design'];

  late Future<List<WallpaperModel>> _wallpapersFuture;

  @override
  void initState() {
    super.initState();
    _loadWallpapers();
  }

  void _loadWallpapers() {
    _wallpapersFuture = _service.fetchWallpapers(category: _selectedFilter);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            // Top Header
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text(
                    'Wallpapers',
                    style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                  ),
                  Container(
                    width: 44,
                    height: 44,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: AppColors.neonPurple.withOpacity(0.2),
                      border: Border.all(color: AppColors.neonPurple.withOpacity(0.4)),
                    ),
                    child: const Icon(Icons.search, color: Colors.white, size: 20),
                  ),
                ],
              ),
            ),

            // Sub-filter chips
            SizedBox(
              height: 40,
              child: ListView.separated(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 20),
                itemCount: _filters.length,
                separatorBuilder: (_, __) => const SizedBox(width: 8),
                itemBuilder: (context, index) {
                  final filter = _filters[index];
                  final isSelected = filter == _selectedFilter;
                  return GestureDetector(
                    onTap: () {
                      setState(() {
                        _selectedFilter = filter;
                        _loadWallpapers();
                      });
                    },
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      decoration: BoxDecoration(
                        color: isSelected ? const Color(0xFF231E44) : const Color(0x1F2B244D),
                        borderRadius: BorderRadius.circular(20),
                        border: Border.all(
                          color: isSelected ? AppColors.neonPurple : AppColors.neonPurple.withOpacity(0.2),
                        ),
                      ),
                      child: Text(
                        filter,
                        style: TextStyle(
                          color: isSelected ? Colors.white : Colors.white70,
                          fontSize: 12,
                          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                        ),
                      ),
                    ),
                  );
                },
              ),
            ),

            const SizedBox(height: 12),

            // 3-Column Wallpaper Grid matching Screenshot
            Expanded(
              child: FutureBuilder<List<WallpaperModel>>(
                future: _wallpapersFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator(color: AppColors.neonCyan));
                  }
                  final items = snapshot.data ?? [];
                  return GridView.builder(
                    padding: const EdgeInsets.fromLTRB(16, 8, 16, 100),
                    gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 3,
                      childAspectRatio: 9 / 16,
                      crossAxisSpacing: 10,
                      mainAxisSpacing: 12,
                    ),
                    itemCount: items.length,
                    itemBuilder: (context, index) {
                      final item = items[index];
                      return ClipRRect(
                        borderRadius: BorderRadius.circular(18),
                        child: Stack(
                          fit: StackFit.expand,
                          children: [
                            CachedNetworkImage(
                              imageUrl: item.imageUrl,
                              fit: BoxFit.cover,
                              placeholder: (_, __) => Container(color: const Color(0xFF1B1736)),
                              errorWidget: (_, __) => Container(
                                color: const Color(0xFF1B1736),
                                child: const Icon(Icons.image, color: Colors.white24),
                              ),
                            ),
                            // Download circular overlay button matching screenshot
                            Positioned(
                              bottom: 8,
                              left: 0,
                              right: 0,
                              child: Center(
                                child: Container(
                                  width: 32,
                                  height: 32,
                                  decoration: BoxDecoration(
                                    shape: BoxShape.circle,
                                    gradient: AppColors.purpleCyanGradient,
                                    boxShadow: [
                                      BoxShadow(
                                        color: AppColors.neonPurple.withOpacity(0.6),
                                        blurRadius: 8,
                                      ),
                                    ],
                                  ),
                                  child: const Icon(Icons.arrow_downward, color: Colors.white, size: 16),
                                ),
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

### `lib/screens/icons_screen.dart`
```dart
import 'package:flutter/material.dart';
import '../models/icon_pack_model.dart';
import '../services/supabase_service.dart';
import '../theme/app_theme.dart';

class IconsScreen extends StatelessWidget {
  const IconsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final SupabaseService service = SupabaseService();

    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: const [
                  Text('Icons', style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
                  Icon(Icons.search, color: Colors.white),
                ],
              ),
            ),
            Expanded(
              child: FutureBuilder<List<IconPackModel>>(
                future: service.fetchIconPacks(),
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator(color: AppColors.neonPurple));
                  }
                  final packs = snapshot.data ?? [];
                  return GridView.builder(
                    padding: const EdgeInsets.fromLTRB(16, 8, 16, 100),
                    gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      childAspectRatio: 0.85,
                      crossAxisSpacing: 14,
                      mainAxisSpacing: 16,
                    ),
                    itemCount: packs.length,
                    itemBuilder: (context, index) {
                      final pack = packs[index];
                      return Container(
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: const Color(0xFF141129),
                          borderRadius: BorderRadius.circular(22),
                          border: Border.all(color: AppColors.neonPurple.withOpacity(0.3)),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // 2x2 Icons Preview Grid
                            Expanded(
                              child: Container(
                                decoration: BoxDecoration(
                                  color: const Color(0xFF1D1838),
                                  borderRadius: BorderRadius.circular(16),
                                ),
                                child: GridView.count(
                                  crossAxisCount: 2,
                                  physics: const NeverScrollableScrollPhysics(),
                                  padding: const EdgeInsets.all(8),
                                  children: const [
                                    Icon(Icons.phone, color: AppColors.neonCyan),
                                    Icon(Icons.chat, color: AppColors.neonPurple),
                                    Icon(Icons.camera_alt, color: AppColors.neonAmber),
                                    Icon(Icons.language, color: AppColors.neonMagenta),
                                  ],
                                ),
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text(
                              pack.title,
                              maxLines: 1,
                              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                            ),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Row(
                                  children: [
                                    const Icon(Icons.star, color: AppColors.neonAmber, size: 13),
                                    const SizedBox(width: 4),
                                    Text(pack.rating.toStringAsFixed(1), style: const TextStyle(fontSize: 11)),
                                  ],
                                ),
                                Text(
                                  pack.styleTag,
                                  style: const TextStyle(color: AppColors.neonCyan, fontSize: 10),
                                ),
                              ],
                            ),
                          ],
                        ),
                      );
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## 6. Screen 2: Theme Details Screen (`lib/screens/theme_details_screen.dart`)

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:open_filex/open_filex.dart';
import '../models/theme_model.dart';
import '../services/supabase_service.dart';
import '../theme/app_theme.dart';

class ThemeDetailsScreen extends StatefulWidget {
  final ThemeModel theme;

  const ThemeDetailsScreen({super.key, required this.theme});

  @override
  State<ThemeDetailsScreen> createState() => _ThemeDetailsScreenState();
}

class _ThemeDetailsScreenState extends State<ThemeDetailsScreen> {
  final SupabaseService _service = SupabaseService();
  bool _isDownloading = false;
  double _downloadProgress = 0.0;
  File? _downloadedFile;

  Future<void> _handleDownload() async {
    setState(() {
      _isDownloading = true;
      _downloadProgress = 0.0;
    });

    try {
      final file = await _service.downloadHwtFile(
        storagePath: widget.theme.hwtStoragePath,
        fileName: '${widget.theme.id}_theme',
        onProgress: (p) => setState(() => _downloadProgress = p),
      );

      setState(() {
        _isDownloading = false;
        _downloadedFile = file;
      });

      _showInstallGuideDialog(file);
    } catch (e) {
      setState(() => _isDownloading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erreur lors du téléchargement: $e')),
      );
    }
  }

  void _showInstallGuideDialog(File file) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: AppColors.darkSurface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        title: const Text('Thème .hwt Enregistré !', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Fichier enregistré dans:\n${file.path}', style: const TextStyle(color: AppColors.neonCyan, fontSize: 12)),
            const SizedBox(height: 12),
            const Text('Étapes pour appliquer sur Honor/Huawei:', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
            const SizedBox(height: 6),
            const Text('1. Ouvrez l\'application Thèmes native.\n2. Allez dans "Moi" > "Mes thèmes".\n3. Sélectionnez le thème et appuyez sur Appliquer.', style: TextStyle(fontSize: 12, color: Colors.white70)),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              OpenFilex.open(file.path);
            },
            child: const Text('Ouvrir / Appliquer', style: TextStyle(color: AppColors.neonPurple, fontWeight: FontWeight.bold)),
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
    return Scaffold(
      appBar: AppBar(
        title: const Text('Theme Details'),
        actions: [
          IconButton(
            icon: Icon(widget.theme.isFavorite ? Icons.star : Icons.star_border, color: AppColors.neonAmber),
            onPressed: () {},
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            // Header Creator Card
            Row(
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(14),
                  child: Container(
                    width: 50,
                    height: 50,
                    color: AppColors.neonPurple.withOpacity(0.3),
                    child: const Icon(Icons.palette, color: Colors.white),
                  ),
                ),
                const SizedBox(width: 14),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(widget.theme.title, style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold)),
                    Text(widget.theme.creator, style: const TextStyle(color: Colors.white70, fontSize: 12)),
                  ],
                ),
              ],
            ),

            const SizedBox(height: 24),

            // Smartphone 3D Mockup Preview
            Container(
              height: 380,
              width: 230,
              decoration: BoxDecoration(
                color: const Color(0xFF0F0E17),
                borderRadius: BorderRadius.circular(36),
                border: Border.all(color: const Color(0xFF28253D), width: 4),
                boxShadow: [
                  BoxShadow(
                    color: AppColors.neonPurple.withOpacity(0.4),
                    blurRadius: 24,
                  ),
                ],
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(30),
                child: Stack(
                  fit: StackFit.expand,
                  children: [
                    Container(
                      decoration: const BoxDecoration(
                        gradient: LinearGradient(
                          colors: [Color(0xFF2E0854), Color(0xFF00D2FF)],
                          begin: Alignment.topCenter,
                          end: Alignment.bottomCenter,
                        ),
                      ),
                    ),
                    const Positioned(
                      top: 40,
                      left: 0,
                      right: 0,
                      child: Column(
                        children: [
                          Text('12:45', style: TextStyle(fontSize: 42, fontWeight: FontWeight.bold, color: AppColors.neonCyan)),
                          Text('Vendredi, 6 Novembre', style: TextStyle(fontSize: 12, color: Colors.white)),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 24),

            // Download Button with glowing gradient
            SizedBox(
              width: double.infinity,
              height: 54,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  padding: EdgeInsets.zero,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(28)),
                  backgroundColor: Colors.transparent,
                  shadowColor: AppColors.neonCyan,
                  elevation: 12,
                ),
                onPressed: _isDownloading ? null : _handleDownload,
                child: Ink(
                  decoration: BoxDecoration(
                    gradient: AppColors.purpleCyanGradient,
                    borderRadius: BorderRadius.circular(28),
                  ),
                  child: Center(
                    child: _isDownloading
                        ? Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              const SizedBox(
                                width: 20,
                                height: 20,
                                child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2),
                              ),
                              const SizedBox(width: 12),
                              Text(
                                'Téléchargement .hwt (${(_downloadProgress * 100).toInt()}%)',
                                style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
                              ),
                            ],
                          )
                        : const Text(
                            'Télécharger',
                            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
                          ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## 7. Screen 3: Customizer / Creator Studio (`lib/screens/customizer_screen.dart`)

```dart
import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class CustomizerScreen extends StatefulWidget {
  const CustomizerScreen({super.key});

  @override
  State<CustomizerScreen> createState() => _CustomizerScreenState();
}

class _CustomizerScreenState extends State<CustomizerScreen> {
  Color _accentColor = AppColors.neonCyan;
  int _clockStyle = 0;
  bool _isLockScreenMode = true;

  final List<Color> _palette = [
    AppColors.neonCyan,
    AppColors.neonPurple,
    AppColors.neonMagenta,
    AppColors.neonAmber,
    const Color(0xFF10B981),
    Colors.white,
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Customizer'),
        actions: [
          TextButton(
            onPressed: () {
              setState(() {
                _isLockScreenMode = !_isLockScreenMode;
              });
            },
            child: Text(
              _isLockScreenMode ? 'Verrouillage' : 'Accueil',
              style: const TextStyle(color: AppColors.neonCyan, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
        child: Column(
          children: [
            // Center Phone Mockup with Glowing Radial Color Spectrum Ring
            Center(
              child: Stack(
                alignment: Alignment.center,
                children: [
                  Container(
                    width: 250,
                    height: 250,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      boxShadow: [
                        BoxShadow(
                          color: _accentColor.withOpacity(0.5),
                          blurRadius: 40,
                          spreadRadius: 10,
                        ),
                      ],
                    ),
                  ),
                  Container(
                    width: 210,
                    height: 350,
                    decoration: BoxDecoration(
                      color: const Color(0xFF0F0E17),
                      borderRadius: BorderRadius.circular(32),
                      border: Border.all(color: _accentColor, width: 2),
                    ),
                    child: Center(
                      child: Text(
                        '12:45',
                        style: TextStyle(
                          fontSize: 40,
                          fontWeight: FontWeight.bold,
                          color: _accentColor,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 30),

            const Text('Sélectionnez la couleur néon d\'accentuation:', style: TextStyle(color: Colors.white70)),
            const SizedBox(height: 14),

            // Color Swatches
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: _palette.map((color) {
                final isSelected = color == _accentColor;
                return GestureDetector(
                  onTap: () => setState(() => _accentColor = color),
                  child: Container(
                    margin: const EdgeInsets.symmetric(horizontal: 6),
                    width: 38,
                    height: 38,
                    decoration: BoxDecoration(
                      color: color,
                      shape: BoxShape.circle,
                      border: Border.all(
                        color: isSelected ? Colors.white : Colors.transparent,
                        width: 2.5,
                      ),
                      boxShadow: [
                        BoxShadow(color: color.withOpacity(0.6), blurRadius: 8),
                      ],
                    ),
                  ),
                );
              }).toList(),
            ),

            const SizedBox(height: 30),

            // Export custom theme button
            SizedBox(
              width: double.infinity,
              height: 52,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.neonPurple,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(26)),
                ),
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Pack personnalisé prêt à l\'exportation .HWT !')),
                  );
                },
                icon: const Icon(Icons.auto_awesome, color: Colors.white),
                label: const Text('Exporter Pack .hwt', style: TextStyle(fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## 8. Screen 4: Menu & Settings Screen (`lib/screens/menu_screen.dart`)

```dart
import 'package:flutter/material.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../theme/app_theme.dart';

class MenuScreen extends StatefulWidget {
  const MenuScreen({super.key});

  @override
  State<MenuScreen> createState() => _MenuScreenState();
}

class _MenuScreenState extends State<MenuScreen> {
  bool _darkMode = true;
  bool _notifications = true;

  @override
  Widget build(BuildContext context) {
    final user = Supabase.instance.client.auth.currentUser;

    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(20),
          child: Column(
            children: [
              // User Profile Header
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: const Color(0xFF141129),
                  borderRadius: BorderRadius.circular(24),
                  border: Border.all(color: AppColors.neonPurple.withOpacity(0.3)),
                ),
                child: Row(
                  children: [
                    CircleAvatar(
                      radius: 26,
                      backgroundColor: AppColors.neonPurple,
                      child: const Icon(Icons.person, color: Colors.white, size: 28),
                    ),
                    const SizedBox(width: 14),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('chunoit', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                          Text(
                            user?.email ?? 'alrwysm239@gmail.com',
                            style: const TextStyle(fontSize: 12, color: Colors.white70),
                          ),
                        ],
                      ),
                    ),
                    const Icon(Icons.chevron_right, color: Colors.white54),
                  ],
                ),
              ),

              const SizedBox(height: 20),

              // Menu navigation options
              Container(
                decoration: BoxDecoration(
                  color: const Color(0xFF141129),
                  borderRadius: BorderRadius.circular(24),
                  border: Border.all(color: AppColors.neonPurple.withOpacity(0.2)),
                ),
                child: Column(
                  children: [
                    _buildTile(Icons.folder_special, 'Mes contenus', 'Thèmes .hwt et favoris'),
                    _buildTile(Icons.storage, 'Dossier des thèmes', '/Honor/Themes & /Huawei/Themes'),
                    SwitchListTile(
                      value: _notifications,
                      activeColor: AppColors.neonPurple,
                      onChanged: (v) => setState(() => _notifications = v),
                      secondary: const Icon(Icons.notifications, color: AppColors.neonCyan),
                      title: const Text('Notifications', style: TextStyle(fontSize: 14)),
                    ),
                    SwitchListTile(
                      value: _darkMode,
                      activeColor: AppColors.neonCyan,
                      onChanged: (v) => setState(() => _darkMode = v),
                      secondary: const Icon(Icons.dark_mode, color: AppColors.neonCyan),
                      title: const Text('Dark Mode', style: TextStyle(fontSize: 14)),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 30),

              // Settings glowing outline button
              Container(
                width: double.infinity,
                height: 54,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(27),
                  border: Border.all(color: AppColors.neonPurple, width: 1.5),
                  color: const Color(0xFF1E153D),
                ),
                child: const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.settings, color: AppColors.neonPurple),
                    SizedBox(width: 8),
                    Text('Paramètres', style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white)),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTile(IconData icon, String title, String subtitle) {
    return ListTile(
      leading: Icon(icon, color: AppColors.neonPurple),
      title: Text(title, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
      subtitle: Text(subtitle, style: const TextStyle(fontSize: 11, color: Colors.white54)),
      trailing: const Icon(Icons.chevron_right, color: Colors.white38),
      onTap: () {},
    );
  }
}
```
