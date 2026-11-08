# Flutter Wallpapers & Icons Screens (Honor/Huawei MagicOS & EMUI)

This document contains modular, production-ready Flutter/Dart implementations for:
1. `wallpapers_screen.dart`: Sub-filters (Live Wallpapers, Wallpapers, Art), 3-column vertical grid fetched from Supabase, quick download button with Dio, and wallpaper preview bottom sheet.
2. `icons_screen.dart`: EMUI/MagicOS compatible icon packs grid with developer names, star ratings, and 2x2 3D preview icon cells.
3. Reusable components: `WallpaperCard`, `IconPackCard`, and filter chips.

---

### 1. `lib/screens/wallpapers_screen.dart`

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:dio/dio.dart';
import 'package:path_provider/path_provider.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class WallpaperItem {
  final String id;
  final String title;
  final String category;
  final String imageUrl;
  final String downloadsCount;
  final bool isLive;

  WallpaperItem({
    required this.id,
    required this.title,
    required this.category,
    required this.imageUrl,
    this.downloadsCount = '80k',
    this.isLive = false,
  });

  factory WallpaperItem.fromMap(Map<String, dynamic> map) {
    return WallpaperItem(
      id: map['id']?.toString() ?? '',
      title: map['title'] ?? 'Wallpaper',
      category: map['category'] ?? 'Wallpapers',
      imageUrl: map['image_url'] ?? '',
      downloadsCount: map['downloads_count']?.toString() ?? '50k',
      isLive: map['is_live'] ?? false,
    );
  }
}

class WallpapersScreen extends StatefulWidget {
  const WallpapersScreen({super.key});

  @override
  State<WallpapersScreen> createState() => _WallpapersScreenState();
}

class _WallpapersScreenState extends State<WallpapersScreen> {
  final SupabaseClient _supabase = Supabase.instance.client;
  final Dio _dio = Dio();

  String _selectedFilter = 'Live Wallpapers';
  final List<String> _filters = ['Live Wallpapers', 'Wallpapers', 'Art'];
  late Future<List<WallpaperItem>> _wallpapersFuture;
  final Set<String> _downloadingIds = {};

  @override
  void initState() {
    super.initState();
    _fetchWallpapers();
  }

  void _fetchWallpapers() {
    setState(() {
      _wallpapersFuture = _loadFromSupabase(_selectedFilter);
    });
  }

  Future<List<WallpaperItem>> _loadFromSupabase(String category) async {
    try {
      final response = await _supabase
          .from('wallpapers')
          .select()
          .eq('category', category)
          .order('created_at', ascending: false);

      final list = (response as List).map((e) => WallpaperItem.fromMap(e)).toList();
      return list;
    } catch (e) {
      debugPrint('Error loading wallpapers from Supabase: $e');
      return [];
    }
  }

  Future<void> _quickDownloadWallpaper(WallpaperItem item) async {
    if (_downloadingIds.contains(item.id)) return;

    setState(() => _downloadingIds.add(item.id));

    try {
      Directory dir;
      if (Platform.isAndroid) {
        dir = Directory('/storage/emulated/0/Download/ThemeStore/Wallpapers');
        if (!await dir.exists()) {
          try {
            await dir.create(recursive: true);
          } catch (_) {
            final ext = await getExternalStorageDirectory();
            dir = Directory('${ext?.path}/Wallpapers');
            await dir.create(recursive: true);
          }
        }
      } else {
        dir = await getApplicationDocumentsDirectory();
      }

      final fileName = '${item.title.replaceAll(RegExp(r'\s+'), '_').toLowerCase()}_${item.id}.jpg';
      final savePath = '${dir.path}/$fileName';

      await _dio.download(item.imageUrl, savePath);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            backgroundColor: const Color(0xFF141129),
            behavior: SnackBarBehavior.floating,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
              side: const BorderSide(color: Color(0xFF00D2FF)),
            ),
            content: Row(
              children: [
                const Icon(Icons.check_circle, color: Color(0xFF00D2FF), size: 20),
                const SizedBox(width: 10),
                Expanded(
                  child: Text(
                    'Enregistré dans: /Download/ThemeStore/Wallpapers',
                    style: const TextStyle(color: Colors.white, fontSize: 12),
                  ),
                ),
              ],
            ),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            backgroundColor: const Color(0xFFEF4444),
            content: Text('Erreur: $e'),
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _downloadingIds.remove(item.id));
      }
    }
  }

  void _showWallpaperDetails(WallpaperItem item) {
    showModalBottomSheet(
      context: context,
      backgroundColor: const Color(0xFF110F20),
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(30)),
      ),
      builder: (context) => Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: Colors.white24,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            const SizedBox(height: 16),
            ClipRRect(
              borderRadius: BorderRadius.circular(22),
              child: SizedBox(
                height: 380,
                width: 220,
                child: CachedNetworkImage(
                  imageUrl: item.imageUrl,
                  fit: BoxFit.cover,
                ),
              ),
            ),
            const SizedBox(height: 18),
            Text(item.title, style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
            Text('${item.category} · Résolution QHD+', style: const TextStyle(color: Colors.white60, fontSize: 12)),
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              height: 52,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF8C52FF),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(26)),
                ),
                onPressed: () {
                  Navigator.pop(context);
                  _quickDownloadWallpaper(item);
                },
                icon: const Icon(Icons.download, color: Colors.white),
                label: const Text('Télécharger en Haute Définition', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.white)),
              ),
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    const neonPurple = Color(0xFF8C52FF);
    const neonCyan = Color(0xFF00D2FF);

    return Scaffold(
      backgroundColor: const Color(0xFF07060E),
      body: SafeArea(
        child: Column(
          children: [
            // Top Bar
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Wallpapers', style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold)),
                  Container(
                    width: 42,
                    height: 42,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: const Color(0xFF1E163B),
                      border: Border.all(color: neonPurple.withOpacity(0.4)),
                    ),
                    child: const Icon(Icons.search, color: Colors.white, size: 20),
                  ),
                ],
              ),
            ),

            // Sub-Filters Row (Live Wallpapers, Wallpapers, Art)
            SizedBox(
              height: 42,
              child: ListView.separated(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 20),
                itemCount: _filters.length,
                separatorBuilder: (_, __) => const SizedBox(width: 10),
                itemBuilder: (context, index) {
                  final filter = _filters[index];
                  final isSelected = filter == _selectedFilter;

                  return GestureDetector(
                    onTap: () {
                      if (_selectedFilter != filter) {
                        setState(() => _selectedFilter = filter);
                        _fetchWallpapers();
                      }
                    },
                    child: AnimatedContainer(
                      duration: const Duration(milliseconds: 200),
                      padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 8),
                      decoration: BoxDecoration(
                        color: isSelected ? const Color(0xFF261D4C) : const Color(0xFF141129),
                        borderRadius: BorderRadius.circular(22),
                        border: Border.all(
                          color: isSelected ? neonPurple : neonPurple.withOpacity(0.2),
                          width: isSelected ? 1.5 : 1,
                        ),
                        boxShadow: isSelected
                            ? [BoxShadow(color: neonPurple.withOpacity(0.3), blurRadius: 10)]
                            : null,
                      ),
                      child: Center(
                        child: Text(
                          filter,
                          style: TextStyle(
                            color: isSelected ? Colors.white : Colors.white60,
                            fontSize: 12,
                            fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
                          ),
                        ),
                      ),
                    ),
                  );
                },
              ),
            ),

            const SizedBox(height: 12),

            // 3-Column Wallpaper Cards Grid
            Expanded(
              child: FutureBuilder<List<WallpaperItem>>(
                future: _wallpapersFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator(color: neonCyan));
                  }
                  final items = snapshot.data ?? [];
                  return GridView.builder(
                    padding: const EdgeInsets.fromLTRB(16, 8, 16, 110),
                    gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 3,
                      childAspectRatio: 9 / 16,
                      crossAxisSpacing: 10,
                      mainAxisSpacing: 12,
                    ),
                    itemCount: items.length,
                    itemBuilder: (context, index) {
                      final item = items[index];
                      final isDownloading = _downloadingIds.contains(item.id);

                      return WallpaperCard(
                        wallpaper: item,
                        isDownloading: isDownloading,
                        onTap: () => _showWallpaperDetails(item),
                        onDownloadTap: () => _quickDownloadWallpaper(item),
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

class WallpaperCard extends StatelessWidget {
  final WallpaperItem wallpaper;
  final bool isDownloading;
  final VoidCallback onTap;
  final VoidCallback onDownloadTap;

  const WallpaperCard({
    super.key,
    required this.wallpaper,
    required this.isDownloading,
    required this.onTap,
    required this.onDownloadTap,
  });

  @override
  Widget build(BuildContext context) {
    const neonPurple = Color(0xFF8C52FF);
    const neonCyan = Color(0xFF00D2FF);

    return GestureDetector(
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(18),
          border: Border.all(color: neonPurple.withOpacity(0.3), width: 1),
          boxShadow: [
            BoxShadow(color: neonPurple.withOpacity(0.2), blurRadius: 8),
          ],
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(17),
          child: Stack(
            fit: StackFit.expand,
            children: [
              CachedNetworkImage(
                imageUrl: wallpaper.imageUrl,
                fit: BoxFit.cover,
                placeholder: (_, __) => Container(color: const Color(0xFF1B1736)),
                errorWidget: (_, __) => Container(
                  color: const Color(0xFF1B1736),
                  child: const Icon(Icons.image, color: Colors.white24),
                ),
              ),

              // Gradient shade for overlay button
              Positioned(
                bottom: 0,
                left: 0,
                right: 0,
                height: 60,
                child: Container(
                  decoration: const BoxDecoration(
                    gradient: LinearGradient(
                      colors: [Colors.transparent, Colors.black87],
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                    ),
                  ),
                ),
              ),

              // Glowing Circular Quick Download Overlay Button
              Positioned(
                bottom: 8,
                left: 0,
                right: 0,
                child: Center(
                  child: GestureDetector(
                    onTap: onDownloadTap,
                    child: Container(
                      width: 32,
                      height: 32,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        gradient: const LinearGradient(colors: [neonPurple, neonCyan]),
                        boxShadow: [
                          BoxShadow(color: neonPurple.withOpacity(0.6), blurRadius: 6),
                        ],
                      ),
                      child: Center(
                        child: isDownloading
                            ? const SizedBox(
                                width: 14,
                                height: 14,
                                child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2),
                              )
                            : const Icon(Icons.arrow_downward, color: Colors.white, size: 16),
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

---

### 2. `lib/screens/icons_screen.dart`

```dart
import 'package:flutter/material.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class IconPackItem {
  final String id;
  final String title;
  final String developer;
  final double rating;
  final String iconCount;
  final String styleTag;
  final int primaryColorHex;
  final String downloadUrl;

  IconPackItem({
    required this.id,
    required this.title,
    required this.developer,
    this.rating = 5.0,
    this.iconCount = '1,800+ icônes',
    this.styleTag = 'Neon 3D',
    this.primaryColorHex = 0xFF8C52FF,
    required this.downloadUrl,
  });

  factory IconPackItem.fromMap(Map<String, dynamic> map) {
    return IconPackItem(
      id: map['id']?.toString() ?? '',
      title: map['title'] ?? 'Icon Pack',
      developer: map['developer'] ?? map['creator'] ?? 'Honor Theme Studio',
      rating: (map['rating'] is num) ? (map['rating'] as num).toDouble() : 5.0,
      iconCount: map['icon_count'] ?? '1,500+ icônes',
      styleTag: map['style_tag'] ?? 'Neon 3D',
      primaryColorHex: map['color_hex'] != null
          ? int.tryParse(map['color_hex'].toString()) ?? 0xFF8C52FF
          : 0xFF8C52FF,
      downloadUrl: map['download_url'] ?? '',
    );
  }
}

class IconsScreen extends StatefulWidget {
  const IconsScreen({super.key});

  @override
  State<IconsScreen> createState() => _IconsScreenState();
}

class _IconsScreenState extends State<IconsScreen> {
  final SupabaseClient _supabase = Supabase.instance.client;
  String _selectedSubFilter = 'Icon Packs';
  final List<String> _subFilters = ['Icon Packs', 'Icons', 'Minimal'];

  late Future<List<IconPackItem>> _iconsFuture;

  @override
  void initState() {
    super.initState();
    _fetchIconPacks();
  }

  void _fetchIconPacks() {
    setState(() {
      _iconsFuture = _loadIconPacks();
    });
  }

  Future<List<IconPackItem>> _loadIconPacks() async {
    try {
      final response = await _supabase
          .from('icons')
          .select()
          .order('created_at', ascending: false);

      final list = (response as List).map((e) => IconPackItem.fromMap(e)).toList();
      return list;
    } catch (e) {
      debugPrint('Error loading icons from Supabase: $e');
      return [];
    }
  }

  void _showPackDetails(IconPackItem pack) {
    showModalBottomSheet(
      context: context,
      backgroundColor: const Color(0xFF141129),
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(28))),
      builder: (context) => Padding(
        padding: const EdgeInsets.all(22),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(pack.title, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Colors.white)),
            const SizedBox(height: 4),
            Text('Par ${pack.developer} · ${pack.iconCount} · MagicOS & EMUI Compatible',
                style: const TextStyle(fontSize: 12, color: Colors.white60)),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _previewIconBubble(Icons.phone, Color(pack.primaryColorHex)),
                _previewIconBubble(Icons.chat, const Color(0xFF00D2FF)),
                _previewIconBubble(Icons.camera_alt, const Color(0xFFFFB800)),
                _previewIconBubble(Icons.music_note, const Color(0xFFFF2A85)),
              ],
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              height: 52,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF8C52FF),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(26)),
                ),
                onPressed: () {
                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Pack d\'icônes ${pack.title} prêt pour MagicOS/EMUI')),
                  );
                },
                child: const Text('Appliquer les icônes', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.white)),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _previewIconBubble(IconData icon, Color color) {
    return Container(
      width: 52,
      height: 52,
      decoration: BoxDecoration(
        color: const Color(0xFF1E173D),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withOpacity(0.6)),
        boxShadow: [BoxShadow(color: color.withOpacity(0.3), blurRadius: 6)],
      ),
      child: Icon(icon, color: color, size: 26),
    );
  }

  @override
  Widget build(BuildContext context) {
    const neonPurple = Color(0xFF8C52FF);

    return Scaffold(
      backgroundColor: const Color(0xFF07060E),
      body: SafeArea(
        child: Column(
          children: [
            // Header
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Icons', style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold)),
                  Container(
                    width: 42,
                    height: 42,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: const Color(0xFF1E163B),
                      border: Border.all(color: neonPurple.withOpacity(0.4)),
                    ),
                    child: const Icon(Icons.search, color: Colors.white, size: 20),
                  ),
                ],
              ),
            ),

            // Sub-Filters Row
            SizedBox(
              height: 40,
              child: ListView.separated(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 20),
                itemCount: _subFilters.length,
                separatorBuilder: (_, __) => const SizedBox(width: 10),
                itemBuilder: (context, index) {
                  final filter = _subFilters[index];
                  final isSelected = filter == _selectedSubFilter;
                  return GestureDetector(
                    onTap: () => setState(() => _selectedSubFilter = filter),
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      decoration: BoxDecoration(
                        color: isSelected ? const Color(0xFF261D4C) : const Color(0xFF141129),
                        borderRadius: BorderRadius.circular(20),
                        border: Border.all(color: isSelected ? neonPurple : neonPurple.withOpacity(0.2)),
                      ),
                      child: Text(
                        filter,
                        style: TextStyle(
                          color: isSelected ? Colors.white : Colors.white60,
                          fontSize: 12,
                          fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
                        ),
                      ),
                    ),
                  );
                },
              ),
            ),

            const SizedBox(height: 12),

            // 2-Column Icon Packs Grid matching Screenshot
            Expanded(
              child: FutureBuilder<List<IconPackItem>>(
                future: _iconsFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator(color: neonPurple));
                  }
                  final packs = snapshot.data ?? [];
                  return GridView.builder(
                    padding: const EdgeInsets.fromLTRB(16, 8, 16, 110),
                    gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      childAspectRatio: 0.85,
                      crossAxisSpacing: 14,
                      mainAxisSpacing: 16,
                    ),
                    itemCount: packs.length,
                    itemBuilder: (context, index) {
                      final pack = packs[index];
                      return IconPackCard(
                        pack: pack,
                        onTap: () => _showPackDetails(pack),
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

class IconPackCard extends StatelessWidget {
  final IconPackItem pack;
  final VoidCallback onTap;

  const IconPackCard({super.key, required this.pack, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final packColor = Color(pack.primaryColorHex);

    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: const Color(0xFF141129),
          borderRadius: BorderRadius.circular(22),
          border: Border.all(color: packColor.withOpacity(0.35)),
          boxShadow: [
            BoxShadow(color: packColor.withOpacity(0.15), blurRadius: 10),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 2x2 Icon Grid Preview
            Expanded(
              child: Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: const Color(0xFF1C1738),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        _miniSquare(Icons.phone, packColor),
                        _miniSquare(Icons.chat, const Color(0xFF00D2FF)),
                      ],
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        _miniSquare(Icons.camera_alt, const Color(0xFFFFB800)),
                        _miniSquare(Icons.language, packColor),
                      ],
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 8),

            // Title & Developer
            Text(
              pack.title,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Colors.white),
            ),
            Text(
              pack.developer,
              maxLines: 1,
              style: const TextStyle(fontSize: 10, color: Colors.white54),
            ),

            const SizedBox(height: 4),

            // Rating Stars & Style Tag
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    const Icon(Icons.star, color: Color(0xFFFFB800), size: 12),
                    const SizedBox(width: 3),
                    Text(
                      pack.rating.toStringAsFixed(1),
                      style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.white),
                    ),
                  ],
                ),
                Text(
                  pack.styleTag,
                  style: TextStyle(color: packColor, fontSize: 10, fontWeight: FontWeight.bold),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _miniSquare(IconData icon, Color color) {
    return Container(
      width: 38,
      height: 38,
      decoration: BoxDecoration(
        color: const Color(0xFF251E49),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: color.withOpacity(0.5)),
      ),
      child: Icon(icon, color: color, size: 20),
    );
  }
}
```
