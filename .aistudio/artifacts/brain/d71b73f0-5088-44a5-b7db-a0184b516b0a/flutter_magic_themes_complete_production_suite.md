# Magic Themes - Complete Production Flutter & Supabase Suite

This document contains the complete, production-grade Flutter source code and Supabase backend architecture for **Magic Themes**, designed for Honor and Huawei devices with full support for **.hwt** (EMUI) and **.hnt** (MagicOS) formats, **Arabic (RTL)** as the default language with dynamic English switching, an authorized **Admin Dashboard**, and high-performance **Dio download service**.

---

## 1. Project Dependencies (`pubspec.yaml`)

```yaml
name: magic_themes
description: "Honor and Huawei Theme Store application compatible with .hwt and .hnt formats"
publish_to: 'none'
version: 1.0.0+1

environment:
  sdk: '>=3.2.0 <4.0.0'
  flutter: ">=3.16.0"

dependencies:
  flutter:
    sdk: flutter
  flutter_localizations:
    sdk: flutter
  intl: ^0.19.0

  # Supabase Backend (Auth, Database, Storage)
  supabase_flutter: ^2.8.0

  # Networking & File Management
  dio: ^5.4.0
  path_provider: ^2.1.2
  path: ^1.9.0
  permission_handler: ^11.3.0
  file_picker: ^8.0.0

  # State Management & Preferences
  provider: ^6.1.2
  shared_preferences: ^2.2.2

  # UI & Media
  cached_network_image: ^3.3.1
  google_fonts: ^6.1.0

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0

flutter:
  uses-material-design: true
  assets:
    - assets/images/
```

---

## 2. Supabase SQL Backend Schema & Security Setup

Run the following SQL in your Supabase SQL Editor to configure all tables, RLS policies, the `is_admin()` check function, and Storage buckets:

```sql
-- 1. Create Categories Table
CREATE TABLE IF NOT EXISTS public.categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(100) NOT NULL,
    subtitle VARCHAR(150),
    image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Create Themes Table (supporting both .hwt and .hnt formats)
CREATE TABLE IF NOT EXISTS public.themes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(150) NOT NULL,
    creator VARCHAR(100) DEFAULT 'Magic Studio',
    category VARCHAR(50) DEFAULT 'Popular',
    format VARCHAR(10) DEFAULT 'hwt' CHECK (format IN ('hwt', 'hnt')),
    rating NUMERIC(2, 1) DEFAULT 5.0,
    downloads_count INTEGER DEFAULT 0,
    reviews_count INTEGER DEFAULT 12,
    size_mb VARCHAR(20) DEFAULT '14.2 MB',
    compatible_versions VARCHAR(100) DEFAULT 'MagicOS 7/8/9 & EMUI 12/13/14',
    preview_url TEXT NOT NULL,
    theme_file_url TEXT NOT NULL,
    description TEXT,
    is_popular BOOLEAN DEFAULT false,
    is_editors_choice BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 3. Create User Roles Table
CREATE TABLE IF NOT EXISTS public.user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL UNIQUE,
    role VARCHAR(50) DEFAULT 'user' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 4. Create is_admin() Function (Dual Check: user_roles table OR email whitelist)
CREATE OR REPLACE FUNCTION public.is_admin()
RETURNS BOOLEAN AS $$
BEGIN
  RETURN EXISTS (
    SELECT 1 FROM public.user_roles
    WHERE user_id = auth.uid() AND role = 'admin'
  ) OR (
    SELECT (auth.jwt() ->> 'email') IN ('zaim9002@gmail.com', 'ogames226@gmail.com', 'alrwysm239@gmail.com')
  );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 5. Create increment_download RPC function
CREATE OR REPLACE FUNCTION public.increment_download(theme_id UUID)
RETURNS VOID AS $$
BEGIN
  UPDATE public.themes
  SET downloads_count = downloads_count + 1
  WHERE id = theme_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 6. Enable Row Level Security (RLS)
ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.themes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_roles ENABLE ROW LEVEL SECURITY;

-- Read policies (Public)
CREATE POLICY "Public can view categories" ON public.categories FOR SELECT USING (true);
CREATE POLICY "Public can view themes" ON public.themes FOR SELECT USING (true);
CREATE POLICY "Users can view their own role" ON public.user_roles FOR SELECT USING (auth.uid() = user_id);

-- Write policies (Admin Only)
CREATE POLICY "Admins can insert themes" ON public.themes FOR INSERT WITH CHECK (public.is_admin());
CREATE POLICY "Admins can update themes" ON public.themes FOR UPDATE USING (public.is_admin());
CREATE POLICY "Admins can delete themes" ON public.themes FOR DELETE USING (public.is_admin());

-- 7. Create Wallpapers Table
CREATE TABLE IF NOT EXISTS public.wallpapers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(150) NOT NULL,
    category VARCHAR(50) DEFAULT 'Static' CHECK (category IN ('Static', 'Live Wallpapers', 'Wallpapers', 'Art/Design')),
    resolution VARCHAR(50) DEFAULT '1440 x 3200 (QHD+)',
    image_url TEXT NOT NULL,
    tags TEXT[] DEFAULT ARRAY['AMOLED', 'Neon'],
    downloads_count INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 8. Create Icons Table
CREATE TABLE IF NOT EXISTS public.icons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(150) NOT NULL,
    developer VARCHAR(100) DEFAULT 'Magic Studio',
    category VARCHAR(50) DEFAULT 'Minimal',
    rating NUMERIC(2, 1) DEFAULT 4.9,
    icons_count INTEGER DEFAULT 1600,
    preview_url TEXT NOT NULL,
    package_file_url TEXT NOT NULL,
    compatible_versions VARCHAR(100) DEFAULT 'MagicOS & EMUI',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- RLS & Policies for Wallpapers and Icons
ALTER TABLE public.wallpapers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.icons ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public can view wallpapers" ON public.wallpapers FOR SELECT USING (true);
CREATE POLICY "Admins can insert wallpapers" ON public.wallpapers FOR INSERT WITH CHECK (public.is_admin());
CREATE POLICY "Admins can update wallpapers" ON public.wallpapers FOR UPDATE USING (public.is_admin());
CREATE POLICY "Admins can delete wallpapers" ON public.wallpapers FOR DELETE USING (public.is_admin());

CREATE POLICY "Public can view icons" ON public.icons FOR SELECT USING (true);
CREATE POLICY "Admins can insert icons" ON public.icons FOR INSERT WITH CHECK (public.is_admin());
CREATE POLICY "Admins can update icons" ON public.icons FOR UPDATE USING (public.is_admin());
CREATE POLICY "Admins can delete icons" ON public.icons FOR DELETE USING (public.is_admin());

-- 9. Supabase Storage Buckets
INSERT INTO storage.buckets (id, name, public) VALUES ('themes', 'themes', true) ON CONFLICT DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('previews', 'previews', true) ON CONFLICT DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('wallpapers', 'wallpapers', true) ON CONFLICT DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('icons', 'icons', true) ON CONFLICT DO NOTHING;

-- Storage Policies
CREATE POLICY "Public read storage themes" ON storage.objects FOR SELECT USING (bucket_id = 'themes');
CREATE POLICY "Public read storage previews" ON storage.objects FOR SELECT USING (bucket_id = 'previews');
CREATE POLICY "Public read storage wallpapers" ON storage.objects FOR SELECT USING (bucket_id = 'wallpapers');
CREATE POLICY "Public read storage icons" ON storage.objects FOR SELECT USING (bucket_id = 'icons');

CREATE POLICY "Admin upload storage themes" ON storage.objects FOR INSERT WITH CHECK (bucket_id = 'themes' AND public.is_admin());
CREATE POLICY "Admin upload storage previews" ON storage.objects FOR INSERT WITH CHECK (bucket_id = 'previews' AND public.is_admin());
CREATE POLICY "Admin upload storage wallpapers" ON storage.objects FOR INSERT WITH CHECK (bucket_id = 'wallpapers' AND public.is_admin());
CREATE POLICY "Admin upload storage icons" ON storage.objects FOR INSERT WITH CHECK (bucket_id = 'icons' AND public.is_admin());

CREATE POLICY "Admin delete storage themes" ON storage.objects FOR DELETE USING (bucket_id = 'themes' AND public.is_admin());
CREATE POLICY "Admin delete storage wallpapers" ON storage.objects FOR DELETE USING (bucket_id = 'wallpapers' AND public.is_admin());
CREATE POLICY "Admin delete storage icons" ON storage.objects FOR DELETE USING (bucket_id = 'icons' AND public.is_admin());
```

---

## 3. Localization System (`lib/l10n/app_localizations.dart`)

```dart
import 'package:flutter/material.dart';

class AppLocalizations {
  final Locale locale;
  AppLocalizations(this.locale);

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations)!;
  }

  static const LocalizationsDelegate<AppLocalizations> delegate = _AppLocalizationsDelegate();

  static final Map<String, Map<String, String>> _localizedValues = {
    'ar': {
      'app_title': 'ثيمات ماجيك',
      'app_subtitle': 'متجر ثيمات هونر وهواوي',
      'tab_themes': 'الثيمات',
      'tab_wallpapers': 'الخلفيات',
      'tab_icons': 'الأيقونات',
      'tab_customizer': 'المخصص',
      'tab_menu': 'القائمة',
      'filter_featured': 'المميزة',
      'filter_top': 'الأفضل',
      'bubble_my_content': 'محتواي',
      'bubble_dark': 'داكن',
      'bubble_video': 'فيديو',
      'bubble_best': 'الأفضل',
      'popular_themes': 'الثيمات الأكثر شعبية',
      'editors_choice': 'اختيارات المحرر',
      'explore_categories': 'استكشف حسب التصنيف',
      'banner_title': 'الثيمات التي يجربها الجميع',
      'banner_sub': 'لا تتردد، قم بالتحميل اليوم',
      'search_hint': 'ابحث عن ثيم، مصمم...',
      'download_hwt': 'تحميل (.hwt)',
      'download_hnt': 'تحميل (.hnt)',
      'download_theme': 'تحميل الثيم',
      'downloading': 'جارٍ التحميل...',
      'apply': 'تطبيق',
      'update': 'تحديث',
      'admin_dashboard': 'لوحة تحكم المسؤول',
      'upload_theme': 'رفع ثيم جديد',
      'theme_title': 'عنوان الثيم',
      'theme_desc': 'وصف الثيم',
      'select_format': 'صيغة الثيم',
      'format_hwt': 'Huawei EMUI (.hwt)',
      'format_hnt': 'Honor MagicOS (.hnt)',
      'select_preview': 'اختر صورة المعاينة',
      'select_file': 'اختر ملف الثيم (.hwt/.hnt)',
      'submit_theme': 'نشر وحفظ الثيم',
      'delete': 'حذف',
      'access_denied': 'تم رفض الوصول. هذه الصفحة مخصصة للمسؤولين فقط.',
      'customizer_title': 'استوديو التخصيص',
      'export_pack': 'تصدير الحزمة',
      'dark_mode': 'الوضع الداكن',
      'language': 'لغة التطبيق',
      'arabic': 'العربية',
      'english': 'English',
    },
    'en': {
      'app_title': 'Magic Themes',
      'app_subtitle': 'Honor & Huawei Theme Store',
      'tab_themes': 'Themes',
      'tab_wallpapers': 'Wallpapers',
      'tab_icons': 'Icons',
      'tab_customizer': 'Customizer',
      'tab_menu': 'Menu',
      'filter_featured': 'Featured',
      'filter_top': 'Top',
      'bubble_my_content': 'My Content',
      'bubble_dark': 'Dark',
      'bubble_video': 'Video',
      'bubble_best': 'Best',
      'popular_themes': 'Always Popular Themes',
      'editors_choice': "Editor's Choice",
      'explore_categories': 'Browse Categories',
      'banner_title': 'Themes Everyone is Trying',
      'banner_sub': "Don't wait, download today",
      'search_hint': 'Search themes, creators...',
      'download_hwt': 'Download (.hwt)',
      'download_hnt': 'Download (.hnt)',
      'download_theme': 'Download Theme',
      'downloading': 'Downloading...',
      'apply': 'Apply',
      'update': 'Update',
      'admin_dashboard': 'Admin Dashboard',
      'upload_theme': 'Upload New Theme',
      'theme_title': 'Theme Title',
      'theme_desc': 'Description',
      'select_format': 'Theme Format',
      'format_hwt': 'Huawei EMUI (.hwt)',
      'format_hnt': 'Honor MagicOS (.hnt)',
      'select_preview': 'Select Preview Image',
      'select_file': 'Select Theme File (.hwt/.hnt)',
      'submit_theme': 'Publish & Save Theme',
      'delete': 'Delete',
      'access_denied': 'Access Denied. Admins only.',
      'customizer_title': 'Customizer Studio',
      'export_pack': 'Export Pack',
      'dark_mode': 'Dark Mode',
      'language': 'Language',
      'arabic': 'العربية',
      'english': 'English',
    }
  };

  String translate(String key) {
    return _localizedValues[locale.languageCode]?[key] ?? key;
  }
}

class _AppLocalizationsDelegate extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();
  @override
  bool isSupported(Locale locale) => ['ar', 'en'].contains(locale.languageCode);

  @override
  Future<AppLocalizations> load(Locale locale) async {
    return AppLocalizations(locale);
  }

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}
```

---

## 4. Theme Model Supporting `.hwt` and `.hnt` (`lib/models/theme_model.dart`)

```dart
enum ThemeFormat { hwt, hnt }

class ThemeItem {
  final String id;
  final String title;
  final String creator;
  final String category;
  final ThemeFormat format;
  final double rating;
  final int downloadsCount;
  final int reviewsCount;
  final String sizeMb;
  final String compatibleVersions;
  final String previewUrl;
  final String themeFileUrl;
  final String description;
  final bool isPopular;
  final bool isEditorsChoice;

  ThemeItem({
    required this.id,
    required this.title,
    required this.creator,
    required this.category,
    required this.format,
    required this.rating,
    required this.downloadsCount,
    required this.reviewsCount,
    required this.sizeMb,
    required this.compatibleVersions,
    required this.previewUrl,
    required this.themeFileUrl,
    required this.description,
    this.isPopular = false,
    this.isEditorsChoice = false,
  });

  factory ThemeItem.fromMap(Map<String, dynamic> map) {
    final formatStr = (map['format'] ?? 'hwt').toString().toLowerCase();
    return ThemeItem(
      id: map['id'] ?? '',
      title: map['title'] ?? 'Unknown Theme',
      creator: map['creator'] ?? 'Magic Creator',
      category: map['category'] ?? 'Popular',
      format: formatStr == 'hnt' ? ThemeFormat.hnt : ThemeFormat.hwt,
      rating: (map['rating'] as num?)?.toDouble() ?? 5.0,
      downloadsCount: map['downloads_count'] ?? 0,
      reviewsCount: map['reviews_count'] ?? 0,
      sizeMb: map['size_mb'] ?? '15 MB',
      compatibleVersions: map['compatible_versions'] ?? (formatStr == 'hnt' ? 'Honor MagicOS 7/8/9' : 'Huawei EMUI 12/13/14'),
      previewUrl: map['preview_url'] ?? '',
      themeFileUrl: map['theme_file_url'] ?? '',
      description: map['description'] ?? '',
      isPopular: map['is_popular'] ?? false,
      isEditorsChoice: map['is_editors_choice'] ?? false,
    );
  }

  String get fileExtension => format == ThemeFormat.hnt ? '.hnt' : '.hwt';
  String get targetFolderName => format == ThemeFormat.hnt ? 'Honor/Themes' : 'Huawei/Themes';
  bool get isHonor => format == ThemeFormat.hnt;
  bool get isHuawei => format == ThemeFormat.hwt;
  String get platformBadgeText => isHonor ? 'Honor · .hnt' : 'Huawei · .hwt';
}
```

---

## 5. Dual-Format Theme Download Service (`lib/services/theme_download_service.dart`)

```dart
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:path_provider/path_provider.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../models/theme_model.dart';

class ThemeDownloadService {
  final Dio _dio = Dio();
  final SupabaseClient _supabase = Supabase.instance.client;

  Future<File?> downloadThemePackage({
    required ThemeItem theme,
    required Function(double progress) onProgress,
  }) async {
    try {
      // 1. Call SQL function to increment counter
      await _supabase.rpc('increment_download', params: {'theme_id': theme.id});

      // 2. Determine target save directory
      Directory? targetDir;
      if (Platform.isAndroid) {
        // Primary directory: Honor or Huawei native folder based on format
        final folder = theme.format == ThemeFormat.hnt ? 'Honor/Themes' : 'Huawei/Themes';
        final baseDir = Directory('/storage/emulated/0/$folder');
        if (await baseDir.exists()) {
          targetDir = baseDir;
        } else {
          // Fallback to Download/ThemeStore
          final downloadDir = Directory('/storage/emulated/0/Download/ThemeStore');
          if (!await downloadDir.exists()) {
            await downloadDir.create(recursive: true);
          }
          targetDir = downloadDir;
        }
      } else {
        targetDir = await getApplicationDocumentsDirectory();
      }

      final sanitizedTitle = theme.title.replaceAll(RegExp(r'[^a-zA-Z0-9_]'), '_');
      final fileName = '$sanitizedTitle${theme.fileExtension}';
      final savePath = '${targetDir.path}/$fileName';

      // 3. Download using Dio with Progress callback
      await _dio.download(
        theme.themeFileUrl,
        savePath,
        onReceiveProgress: (received, total) {
          if (total > 0) {
            onProgress(received / total);
          }
        },
      );

      return File(savePath);
    } catch (e) {
      debugPrint('Download error: $e');
      return null;
    }
  }
}
```

---

## 6. Admin Dashboard Screen (`lib/screens/admin_dashboard_screen.dart`)

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../l10n/app_localizations.dart';
import '../models/theme_model.dart';

class AdminDashboardScreen extends StatefulWidget {
  const AdminDashboardScreen({super.key});

  @override
  State<AdminDashboardScreen> createState() => _AdminDashboardScreenState();
}

class _AdminDashboardScreenState extends State<AdminDashboardScreen> {
  final _supabase = Supabase.instance.client;
  bool _isLoading = true;
  bool _isAdmin = false;
  List<ThemeItem> _themes = [];

  // Form Fields
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descController = TextEditingController();
  final _categoryController = TextEditingController(text: 'Cyberpunk');
  ThemeFormat _selectedFormat = ThemeFormat.hwt;
  File? _pickedPreviewImage;
  File? _pickedThemeFile;
  bool _isUploading = false;

  @override
  void initState() {
    super.initState();
    _verifyAdminAndLoad();
  }

  Future<void> _verifyAdminAndLoad() async {
    setState(() => _isLoading = true);
    try {
      final res = await _supabase.rpc('is_admin');
      _isAdmin = res == true;
      if (_isAdmin) {
        await _fetchThemes();
      }
    } catch (e) {
      _isAdmin = false;
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Future<void> _fetchThemes() async {
    final data = await _supabase.from('themes').select().order('created_at', ascending: false);
    setState(() {
      _themes = (data as List).map((e) => ThemeItem.fromMap(e)).toList();
    });
  }

  Future<void> _pickPreviewImage() async {
    final result = await FilePicker.platform.pickFiles(type: FileType.image);
    if (result != null && result.files.single.path != null) {
      setState(() => _pickedPreviewImage = File(result.files.single.path!));
    }
  }

  Future<void> _pickThemeFile() async {
    final result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['hwt', 'hnt', 'zip'],
    );
    if (result != null && result.files.single.path != null) {
      final path = result.files.single.path!;
      setState(() {
        _pickedThemeFile = File(path);
        if (path.endsWith('.hnt')) {
          _selectedFormat = ThemeFormat.hnt;
        } else if (path.endsWith('.hwt')) {
          _selectedFormat = ThemeFormat.hwt;
        }
      });
    }
  }

  Future<void> _uploadTheme() async {
    if (!_formKey.currentState!.validate() || _pickedPreviewImage == null || _pickedThemeFile == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select both a preview image and theme file (.hwt/.hnt)')),
      );
      return;
    }

    setState(() => _isUploading = true);
    try {
      final timestamp = DateTime.now().millisecondsSinceEpoch;
      final previewExt = _pickedPreviewImage!.path.split('.').last;
      final themeExt = _selectedFormat == ThemeFormat.hnt ? 'hnt' : 'hwt';

      // 1. Upload preview to storage
      final previewPath = 'previews/$timestamp.$previewExt';
      await _supabase.storage.from('previews').upload(previewPath, _pickedPreviewImage!);
      final previewUrl = _supabase.storage.from('previews').getPublicUrl(previewPath);

      // 2. Upload theme package to storage
      final themeFilePath = 'themes/$timestamp.$themeExt';
      await _supabase.storage.from('themes').upload(themeFilePath, _pickedThemeFile!);
      final themeUrl = _supabase.storage.from('themes').getPublicUrl(themeFilePath);

      // 3. Insert record into database
      await _supabase.from('themes').insert({
        'title': _titleController.text.trim(),
        'description': _descController.text.trim(),
        'category': _categoryController.text.trim(),
        'format': _selectedFormat == ThemeFormat.hnt ? 'hnt' : 'hwt',
        'preview_url': previewUrl,
        'theme_file_url': themeUrl,
        'size_mb': '${(_pickedThemeFile!.lengthSync() / (1024 * 1024)).toStringAsFixed(1)} MB',
        'compatible_versions': _selectedFormat == ThemeFormat.hnt ? 'Honor MagicOS 7/8/9' : 'Huawei EMUI 12/13/14',
      });

      _titleController.clear();
      _descController.clear();
      setState(() {
        _pickedPreviewImage = null;
        _pickedThemeFile = null;
      });

      await _fetchThemes();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Theme uploaded successfully!')));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Upload failed: $e')));
      }
    } finally {
      if (mounted) setState(() => _isUploading = false);
    }
  }

  Future<void> _deleteTheme(ThemeItem theme) async {
    try {
      await _supabase.from('themes').delete().eq('id', theme.id);
      await _fetchThemes();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Theme deleted')));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Delete failed: $e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);

    if (_isLoading) {
      return const Scaffold(
        backgroundColor: Color(0xFF0A0818),
        body: Center(child: CircularProgressIndicator(color: Color(0xFF00D2FF))),
      );
    }

    if (!_isAdmin) {
      return Scaffold(
        backgroundColor: const Color(0xFF0A0818),
        appBar: AppBar(title: Text(l10n.translate('admin_dashboard')), backgroundColor: Colors.transparent),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Text(
              l10n.translate('access_denied'),
              style: const TextStyle(color: Colors.redAccent, fontSize: 16),
              textAlign: TextAlign.center,
            ),
          ),
        ),
      );
    }

    return Scaffold(
      backgroundColor: const Color(0xFF0A0818),
      appBar: AppBar(
        title: Text(l10n.translate('admin_dashboard'), style: const TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: const Color(0xFF141129),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Prominent Quick File Manager Upload Button for Admin
            Container(
              margin: const EdgeInsets.only(bottom: 16),
              decoration: BoxDecoration(
                gradient: const LinearGradient(colors: [Color(0xFF8C52FF), Color(0xFF00D2FF)]),
                borderRadius: BorderRadius.circular(18),
              ),
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  minimumSize: const Size.fromHeight(52),
                  backgroundColor: Colors.transparent,
                  shadowColor: Colors.transparent,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
                ),
                onPressed: () {
                  showModalBottomSheet(
                    context: context,
                    isScrollControlled: true,
                    backgroundColor: Colors.transparent,
                    builder: (ctx) => const MemberUploadBottomSheet(),
                  );
                },
                icon: const Icon(Icons.cloud_upload, color: Colors.white),
                label: const Text(
                  'استيراد ورفع من مدير الملفات (Themes, Wallpapers, Icons)',
                  style: TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.bold),
                ),
              ),
            ),

            // Upload Form Card
            Container(
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                color: const Color(0xFF141129),
                borderRadius: BorderRadius.circular(24),
                border: Border.all(color: const Color(0x338C52FF)),
              ),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(l10n.translate('upload_theme'),
                        style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 14),
                    TextFormField(
                      controller: _titleController,
                      style: const TextStyle(color: Colors.white),
                      decoration: InputDecoration(
                        labelText: l10n.translate('theme_title'),
                        labelStyle: const TextStyle(color: Colors.white70),
                        filled: true,
                        fillColor: const Color(0xFF1F1A3B),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(16)),
                      ),
                      validator: (v) => v == null || v.isEmpty ? 'Required' : null,
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: _descController,
                      style: const TextStyle(color: Colors.white),
                      maxLines: 2,
                      decoration: InputDecoration(
                        labelText: l10n.translate('theme_desc'),
                        labelStyle: const TextStyle(color: Colors.white70),
                        filled: true,
                        fillColor: const Color(0xFF1F1A3B),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(16)),
                      ),
                    ),
                    const SizedBox(height: 14),
                    // Format selection (.hwt vs .hnt)
                    Row(
                      children: [
                        Text(l10n.translate('select_format'), style: const TextStyle(color: Colors.white70)),
                        const Spacer(),
                        ChoiceChip(
                          label: const Text('EMUI (.hwt)'),
                          selected: _selectedFormat == ThemeFormat.hwt,
                          onSelected: (s) => setState(() => _selectedFormat = ThemeFormat.hwt),
                        ),
                        const SizedBox(width: 8),
                        ChoiceChip(
                          label: const Text('MagicOS (.hnt)'),
                          selected: _selectedFormat == ThemeFormat.hnt,
                          onSelected: (s) => setState(() => _selectedFormat = ThemeFormat.hnt),
                        ),
                      ],
                    ),
                    const SizedBox(height: 14),
                    // File Pickers
                    Row(
                      children: [
                        Expanded(
                          child: OutlinedButton.icon(
                            onPressed: _pickPreviewImage,
                            icon: const Icon(Icons.image, color: Color(0xFF00D2FF)),
                            label: Text(_pickedPreviewImage != null ? 'Image Selected' : l10n.translate('select_preview'),
                                maxLines: 1),
                          ),
                        ),
                        const SizedBox(width: 10),
                        Expanded(
                          child: OutlinedButton.icon(
                            onPressed: _pickThemeFile,
                            icon: const Icon(Icons.archive, color: Color(0xFF8C52FF)),
                            label: Text(_pickedThemeFile != null ? 'File Selected' : l10n.translate('select_file'),
                                maxLines: 1),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 18),
                    SizedBox(
                      width: double.infinity,
                      height: 50,
                      child: ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF8C52FF),
                          shape: RoundedCornerShape(25),
                        ),
                        onPressed: _isUploading ? null : _uploadTheme,
                        child: _isUploading
                            ? const CircularProgressIndicator(color: Colors.white)
                            : Text(l10n.translate('submit_theme'),
                                style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                      ),
                    )
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            // Existing Themes List
            const Text('Manage Uploaded Themes',
                style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: _themes.length,
              itemBuilder: (context, index) {
                final theme = _themes[index];
                return Container(
                  margin: const EdgeInsets.only(bottom: 10),
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: const Color(0xFF141129),
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: ListTile(
                    leading: ClipRRect(
                      borderRadius: BorderRadius.circular(10),
                      child: Image.network(theme.previewUrl, width: 45, height: 65, fit: BoxFit.cover),
                    ),
                    title: Text(theme.title, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                    subtitle: Text('${theme.format.name.toUpperCase()} · ${theme.sizeMb}',
                        style: const TextStyle(color: Color(0xFF00D2FF), fontSize: 12)),
                    trailing: IconButton(
                      icon: const Icon(Icons.delete, color: Colors.redAccent),
                      onPressed: () => _deleteTheme(theme),
                    ),
                  ),
                );
              },
            )
          ],
        ),
      ),
    );
  }
}
```

---

## 7. App Entrypoint (`lib/main.dart`)

```dart
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'l10n/app_localizations.dart';
import 'screens/home_screen.dart';
import 'screens/wallpapers_screen.dart';
import 'screens/icons_screen.dart';
import 'screens/customizer_screen.dart';
import 'screens/menu_screen.dart';

// Language State Provider
class LocaleProvider extends ChangeNotifier {
  Locale _locale = const Locale('ar'); // Arabic by default
  Locale get locale => _locale;

  void setLocale(Locale newLocale) {
    _locale = newLocale;
    notifyListeners();
  }
}

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Supabase.initialize(
    url: 'https://vpp5xeco7dcrmnlm4hao.supabase.co',
    anonKey: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...', // Configured via backend
  );

  runApp(
    ChangeNotifierProvider(
      create: (_) => LocaleProvider(),
      child: const MagicThemesApp(),
    ),
  );
}

class MagicThemesApp extends StatelessWidget {
  const MagicThemesApp({super.key});

  @override
  Widget build(BuildContext context) {
    final localeProvider = Provider.of<LocaleProvider>(context);

    return MaterialApp(
      title: 'Magic Themes',
      debugShowCheckedModeBanner: false,
      locale: localeProvider.locale,
      supportedLocales: const [
        Locale('ar'), // Arabic Default
        Locale('en'), // English
      ],
      localizationsDelegates: const [
        AppLocalizations.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      theme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: const Color(0xFF0B091A),
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF8C52FF),
          secondary: Color(0xFF00D2FF),
          surface: Color(0xFF141129),
        ),
      ),
      home: const MainTabContainer(),
    );
  }
}

class MainTabContainer extends StatefulWidget {
  const MainTabContainer({super.key});

  @override
  State<MainTabContainer> createState() => _MainTabContainerState();
}

class _MainTabContainerState extends State<MainTabContainer> {
  int _currentIndex = 0;
  bool _isAdmin = false;

  final List<Widget> _screens = const [
    HomeScreen(),
    WallpapersScreen(),
    IconsScreen(),
    CustomizerScreen(),
    MenuScreen(),
  ];

  @override
  void initState() {
    super.initState();
    _checkAdminStatus();
  }

  Future<void> _checkAdminStatus() async {
    final isAdmin = await AuthService().isAdmin();
    if (mounted) setState(() => _isAdmin = isAdmin);
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);

    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _screens,
      ),
      // Strictly visible only for authenticated Admins. Completely hidden from regular members!
      floatingActionButton: (_isAdmin && _currentIndex != 4)
          ? FloatingActionButton.extended(
              backgroundColor: const Color(0xFF8C52FF),
              icon: const Icon(Icons.cloud_upload, color: Color(0xFF00D2FF)),
              label: const Text(
                'رفع ملفات (إدارة)',
                style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 13),
              ),
              onPressed: () {
                showModalBottomSheet(
                  context: context,
                  isScrollControlled: true,
                  backgroundColor: Colors.transparent,
                  builder: (ctx) => const MemberUploadBottomSheet(),
                );
              },
            )
          : null,
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: const Color(0xE60E0B22),
          border: const Border(top: BorderSide(color: Color(0x338C52FF), width: 1)),
          boxShadow: [
            BoxShadow(color: const Color(0xFF8C52FF).withOpacity(0.15), blurRadius: 20, spreadRadius: 2),
          ],
        ),
        child: BottomNavigationBar(
          currentIndex: _currentIndex,
          onTap: (index) => setState(() => _currentIndex = index),
          backgroundColor: Colors.transparent,
          elevation: 0,
          selectedItemColor: const Color(0xFF00D2FF),
          unselectedItemColor: Colors.white54,
          type: BottomNavigationBarType.fixed,
          items: [
            BottomNavigationBarItem(icon: const Icon(Icons.style), label: l10n.translate('tab_themes')),
            BottomNavigationBarItem(icon: const Icon(Icons.wallpaper), label: l10n.translate('tab_wallpapers')),
            BottomNavigationBarItem(icon: const Icon(Icons.grid_view_rounded), label: l10n.translate('tab_icons')),
            BottomNavigationBarItem(icon: const Icon(Icons.auto_awesome), label: l10n.translate('tab_customizer')),
            BottomNavigationBarItem(icon: const Icon(Icons.menu), label: l10n.translate('tab_menu')),
          ],
        ),
      ),
    );
  }
}

// ==========================================
// Member Upload Bottom Sheet (File Manager)
// ==========================================
class MemberUploadBottomSheet extends StatefulWidget {
  const MemberUploadBottomSheet({super.key});

  @override
  State<MemberUploadBottomSheet> createState() => _MemberUploadBottomSheetState();
}

class _MemberUploadBottomSheetState extends State<MemberUploadBottomSheet> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final SupabaseClient _supabase = Supabase.instance.client;
  bool _isUploading = false;

  final _titleController = TextEditingController();
  final _creatorController = TextEditingController(text: 'عضو Magic Themes');
  final _descController = TextEditingController();

  PlatformFile? _pickedFile;
  PlatformFile? _pickedPreviewImage;
  String _themeFormat = 'hwt';
  String _wallpaperCategory = 'Live Wallpapers';

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _titleController.dispose();
    _creatorController.dispose();
    _descController.dispose();
    super.dispose();
  }

  Future<void> _pickFromFileManager() async {
    final int index = _tabController.index;
    FilePickerResult? result;

    if (index == 0) {
      // Themes: .hwt, .hnt, .zip
      result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['hwt', 'hnt', 'zip'],
        withData: true,
      );
    } else if (index == 1) {
      // Wallpapers: images
      result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['jpg', 'jpeg', 'png', 'webp'],
        withData: true,
      );
    } else {
      // Icons: .zip, .hwt, .hnt
      result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['zip', 'hwt', 'hnt'],
        withData: true,
      );
    }

    if (result != null && result.files.isNotEmpty) {
      setState(() {
        _pickedFile = result!.files.single;
        if (_pickedFile!.name.toLowerCase().endsWith('.hnt')) {
          _themeFormat = 'hnt';
        } else if (_pickedFile!.name.toLowerCase().endsWith('.hwt')) {
          _themeFormat = 'hwt';
        }
        if (_titleController.text.isEmpty) {
          _titleController.text = _pickedFile!.name.split('.').first;
        }
      });
    }
  }

  Future<void> _pickPreviewImage() async {
    final result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['jpg', 'jpeg', 'png', 'webp'],
      withData: true,
    );
    if (result != null && result.files.isNotEmpty) {
      setState(() => _pickedPreviewImage = result.files.single);
    }
  }

  Future<void> _submitUpload() async {
    if (_pickedFile == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('يرجى اختيار الملف أولاً من مدير الملفات')),
      );
      return;
    }

    setState(() => _isUploading = true);

    try {
      final ts = DateTime.now().millisecondsSinceEpoch;
      final bytes = _pickedFile!.bytes ?? (await File(_pickedFile!.path!).readAsBytes());
      final index = _tabController.index;

      if (index == 0) {
        // Upload Theme (.hwt / .hnt)
        final filePath = 'themes/$ts.${_themeFormat}';
        await _supabase.storage.from('themes').uploadBinary(
          filePath,
          bytes,
          fileOptions: const FileOptions(upsert: true, contentType: 'application/octet-stream'),
        );
        final fileUrl = _supabase.storage.from('themes').getPublicUrl(filePath);

        String previewUrl = 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80';
        if (_pickedPreviewImage != null) {
          final prevBytes = _pickedPreviewImage!.bytes ?? (await File(_pickedPreviewImage!.path!).readAsBytes());
          final prevExt = _pickedPreviewImage!.extension ?? 'jpg';
          final prevPath = 'previews/$ts.$prevExt';
          await _supabase.storage.from('previews').uploadBinary(prevPath, prevBytes);
          previewUrl = _supabase.storage.from('previews').getPublicUrl(prevPath);
        }

        await _supabase.from('themes').insert({
          'title': _titleController.text.trim().isEmpty ? 'ثيم جديد' : _titleController.text.trim(),
          'description': _descController.text.trim(),
          'creator': _creatorController.text.trim(),
          'category': 'Community',
          'format': _themeFormat,
          'preview_url': previewUrl,
          'theme_file_url': fileUrl,
          'size_mb': '${(_pickedFile!.size / (1024 * 1024)).toStringAsFixed(1)} MB',
          'compatible_versions': _themeFormat == 'hnt' ? 'Honor MagicOS' : 'Huawei EMUI',
          'rating': 5.0,
        });
      } else if (index == 1) {
        // Upload Wallpaper
        final ext = _pickedFile!.extension ?? 'jpg';
        final path = 'wallpapers/$ts.$ext';
        await _supabase.storage.from('wallpapers').uploadBinary(
          path,
          bytes,
          fileOptions: FileOptions(upsert: true, contentType: 'image/$ext'),
        );
        final url = _supabase.storage.from('wallpapers').getPublicUrl(path);

        await _supabase.from('wallpapers').insert({
          'title': _titleController.text.trim().isEmpty ? 'خلفية جديدة' : _titleController.text.trim(),
          'category': _wallpaperCategory,
          'resolution': '1440 x 3200 (QHD+)',
          'image_url': url,
          'tags': ['Community', 'Honor', 'Huawei'],
        });
      } else {
        // Upload Icons Pack
        final ext = _pickedFile!.extension ?? 'zip';
        final path = 'icons/pack_$ts.$ext';
        await _supabase.storage.from('icons').uploadBinary(
          path,
          bytes,
          fileOptions: const FileOptions(upsert: true, contentType: 'application/zip'),
        );
        final url = _supabase.storage.from('icons').getPublicUrl(path);

        await _supabase.from('icons').insert({
          'title': _titleController.text.trim().isEmpty ? 'حزمة أيقونات' : _titleController.text.trim(),
          'developer': _creatorController.text.trim(),
          'category': 'Neon Style',
          'icons_count': 1600,
          'preview_url': 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=400&q=80',
          'package_file_url': url,
          'compatible_versions': 'MagicOS & EMUI',
          'rating': 5.0,
        });
      }

      if (mounted) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('تم رفع وإضافة الملف بنجاح! وظهر الآن للأعضاء.')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('فشل الرفع: $e')));
      }
    } finally {
      if (mounted) setState(() => _isUploading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.only(
        top: 20,
        left: 20,
        right: 20,
        bottom: MediaQuery.of(context).viewInsets.bottom + 20,
      ),
      decoration: const BoxDecoration(
        color: Color(0xFF141129),
        borderRadius: BorderRadius.vertical(top: Radius.circular(30)),
        border: Border(top: BorderSide(color: Color(0xFF8C52FF), width: 1.5)),
      ),
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'رفع من مدير الملفات للأعضاء',
                  style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                ),
                IconButton(
                  onPressed: () => Navigator.pop(context),
                  icon: const Icon(Icons.close, color: Colors.white70),
                ),
              ],
            ),
            const SizedBox(height: 12),
            TabBar(
              controller: _tabController,
              indicatorColor: const Color(0xFF00D2FF),
              labelColor: const Color(0xFF00D2FF),
              unselectedLabelColor: Colors.white60,
              tabs: const [
                Tab(icon: Icon(Icons.style, size: 20), text: 'ثيمات'),
                Tab(icon: Icon(Icons.wallpaper, size: 20), text: 'خلفيات'),
                Tab(icon: Icon(Icons.palette, size: 20), text: 'أيقونات'),
              ],
            ),
            const SizedBox(height: 18),
            // Select from device button
            OutlinedButton.icon(
              style: OutlinedButton.styleFrom(
                minimumSize: const Size.fromHeight(50),
                side: const BorderSide(color: Color(0xFF00D2FF), width: 1.5),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              ),
              onPressed: _pickFromFileManager,
              icon: const Icon(Icons.folder_open, color: Color(0xFF00D2FF)),
              label: Text(
                _pickedFile != null
                    ? 'تم اختيار: ${_pickedFile!.name} (${(_pickedFile!.size / (1024 * 1024)).toStringAsFixed(1)} MB)'
                    : 'اضغط لفتح مدير الملفات في هاتفك',
                style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: _titleController,
              style: const TextStyle(color: Colors.white),
              decoration: InputDecoration(
                labelText: 'العنوان / الاسم',
                labelStyle: const TextStyle(color: Colors.white70),
                filled: true,
                fillColor: const Color(0xFF1E193C),
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(14)),
              ),
            ),
            const SizedBox(height: 10),
            TextField(
              controller: _creatorController,
              style: const TextStyle(color: Colors.white),
              decoration: InputDecoration(
                labelText: 'اسم المصمم / العضو',
                labelStyle: const TextStyle(color: Colors.white70),
                filled: true,
                fillColor: const Color(0xFF1E193C),
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(14)),
              ),
            ),
            const SizedBox(height: 18),
            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF8C52FF),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(25)),
                ),
                onPressed: _isUploading ? null : _submitUpload,
                child: _isUploading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text(
                        'تأكيد الرفع والإضافة الفورية',
                        style: TextStyle(color: Colors.white, fontSize: 15, fontWeight: FontWeight.bold),
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

## 8. GitHub Actions Build Workflow (`.github/workflows/build-flutter-apk.yml`)

This workflow guarantees clean, conflict-free building of the Flutter APK on GitHub Actions:

```yaml
name: Build Magic Themes APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Set up Flutter
        uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.19.6'
          channel: 'stable'
          cache: true

      - name: Install dependencies
        run: flutter pub get

      - name: Build APK (Release/Debug)
        run: flutter build apk --debug

      - name: Upload APK Artifact
        uses: actions/upload-artifact@v4
        with:
          name: magic-themes-debug-apk
          path: build/app/outputs/flutter-apk/app-debug.apk
```
