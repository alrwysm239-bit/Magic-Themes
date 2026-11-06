# Localization (i18n) Configuration for "Magic Themes"

This document provides a clean, native Flutter localization setup with:
- **Default & Fallback Language:** Arabic (`ar`) with full Right-to-Left (RTL) support.
- **Supported Languages:** Arabic (`ar`), English (`en`), French (`fr`), and Spanish (`es`).
- **Auto-detection:** Automatically selects the device's locale if supported, otherwise seamlessly defaults to Arabic (`ar`).

---

### 1. `pubspec.yaml` Configuration

Add `flutter_localizations` to your dependencies:

```yaml
dependencies:
  flutter:
    sdk: flutter
  flutter_localizations:
    sdk: flutter
```

---

### 2. Localization Class & Delegate (`lib/l10n/app_localizations.dart`)

```dart
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class AppLocalizations {
  final Locale locale;

  AppLocalizations(this.locale);

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations) ??
        AppLocalizations(const Locale('ar'));
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  static const List<Locale> supportedLocales = [
    Locale('ar'), // Arabic (Default & Fallback)
    Locale('en'), // English
    Locale('fr'), // French
    Locale('es'), // Spanish
  ];

  static final Map<String, Map<String, String>> _localizedValues = {
    // ----------------- ARABIC (DEFAULT / الأساسية) -----------------
    'ar': {
      'app_title': 'ثيمات ماجيك',
      'app_subtitle': 'متجر ثيمات هونر وهواوي',
      'tab_themes': 'الثيمات',
      'tab_wallpapers': 'الخلفيات',
      'tab_icons': 'الأيقونات',
      'tab_customizer': 'المخصص',
      'tab_menu': 'القائمة',
      'filter_featured': 'مميز',
      'filter_top': 'الأفضل',
      'search_hint': 'ابحث عن ثيمات، خلفيات، أيقونات...',
      'bubble_my_content': 'محتوايا',
      'bubble_dark': 'داكن',
      'bubble_video': 'فيديو',
      'bubble_best': 'الأفضل',
      'section_popular': 'ثيمات دائماً شائعة',
      'section_editors_choice': 'اختيارات المحرر',
      'section_categories': 'استكشف حسب التصنيف',
      'banner_headline': 'الثيمات التي يجربها الجميع',
      'banner_sub': 'لا تتردد، قم بالتحميل اليوم',
      'btn_download': 'تحميل (.hwt)',
      'btn_apply': 'تطبيق',
      'btn_update': 'تحديث',
      'btn_settings': 'الإعدادات',
      'btn_export_hwt': 'تصدير حزمة .hwt',
      'live_wallpapers': 'خلفيات حية',
      'wallpapers': 'خلفيات',
      'art': 'فن وتصميم',
      'icon_packs': 'حزم الأيقونات',
      'animated_badge': 'متحرك',
      'free': 'مجاني',
      'details_title': 'تفاصيل الثيم',
      'specifications': 'المواصفات',
      'format': 'الصيغة',
      'target_folder': 'المجلد المستهدف',
      'compatibility': 'التوافق',
      'file_size': 'حجم الملف',
      'description': 'الوصف',
      'dark_mode': 'الوضع الداكن',
      'notifications': 'الإشعارات',
      'guide_title': 'دليل التثبيت',
      'access_denied': 'تم رفض الوصول',
      'admin_dashboard': 'لوحة تحكم المشرف',
    },

    // ----------------- ENGLISH -----------------
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
      'search_hint': 'Search themes, wallpapers, icons...',
      'bubble_my_content': 'My Content',
      'bubble_dark': 'Dark',
      'bubble_video': 'Video',
      'bubble_best': 'Best',
      'section_popular': 'Always Popular Themes',
      'section_editors_choice': 'Editor\'s Choice',
      'section_categories': 'Browse Categories',
      'banner_headline': 'Themes Everyone is Trying',
      'banner_sub': 'Don\'t wait, download today',
      'btn_download': 'Download (.hwt)',
      'btn_apply': 'Apply',
      'btn_update': 'Update',
      'btn_settings': 'Settings',
      'btn_export_hwt': 'Export .hwt Pack',
      'live_wallpapers': 'Live Wallpapers',
      'wallpapers': 'Wallpapers',
      'art': 'Art & Design',
      'icon_packs': 'Icon Packs',
      'animated_badge': 'Animated',
      'free': 'Free',
      'details_title': 'Theme Details',
      'specifications': 'Specifications',
      'format': 'Format',
      'target_folder': 'Target Directory',
      'compatibility': 'Compatibility',
      'file_size': 'File Size',
      'description': 'Description',
      'dark_mode': 'Dark Mode',
      'notifications': 'Notifications',
      'guide_title': 'Installation Guide',
      'access_denied': 'Access Denied',
      'admin_dashboard': 'Admin Dashboard',
    },

    // ----------------- FRENCH -----------------
    'fr': {
      'app_title': 'Magic Themes',
      'app_subtitle': 'Boutique de thèmes Honor et Huawei',
      'tab_themes': 'Thèmes',
      'tab_wallpapers': 'Wallpapers',
      'tab_icons': 'Icons',
      'tab_customizer': 'Customizer',
      'tab_menu': 'Menu',
      'filter_featured': 'Featured',
      'filter_top': 'Top',
      'search_hint': 'Rechercher thèmes, fonds, icônes...',
      'bubble_my_content': 'Mes contenus',
      'bubble_dark': 'Noir',
      'bubble_video': 'Vidéo',
      'bubble_best': 'Meilleur',
      'section_popular': 'Thèmes toujours populaires',
      'section_editors_choice': 'Thèmes choisis par l\'éditeur',
      'section_categories': 'Explorer par catégories',
      'banner_headline': 'Les thèmes que tout le monde essaie',
      'banner_sub': 'Ne tardez pas, téléchargez dès aujourd\'hui',
      'btn_download': 'Télécharger (.hwt)',
      'btn_apply': 'Appliquer',
      'btn_update': 'Mettre à jour',
      'btn_settings': 'Paramètres',
      'btn_export_hwt': 'Exporter pack .hwt',
      'live_wallpapers': 'Live Wallpapers',
      'wallpapers': 'Wallpapers',
      'art': 'Art / Design',
      'icon_packs': 'Packs d\'icônes',
      'animated_badge': 'Animated',
      'free': 'Gratuit',
      'details_title': 'Détails du thème',
      'specifications': 'Spécifications',
      'format': 'Format',
      'target_folder': 'Dossier cible',
      'compatibility': 'Compatibilité',
      'file_size': 'Taille du fichier',
      'description': 'Description',
      'dark_mode': 'Mode sombre',
      'notifications': 'Notifications',
      'guide_title': 'Guide d\'installation',
      'access_denied': 'Accès refusé',
      'admin_dashboard': 'Tableau de bord Admin',
    },

    // ----------------- SPANISH -----------------
    'es': {
      'app_title': 'Magic Themes',
      'app_subtitle': 'Tienda de temas Honor y Huawei',
      'tab_themes': 'Temas',
      'tab_wallpapers': 'Fondos',
      'tab_icons': 'Iconos',
      'tab_customizer': 'Personalizar',
      'tab_menu': 'Menú',
      'filter_featured': 'Destacado',
      'filter_top': 'Top',
      'search_hint': 'Buscar temas, fondos, iconos...',
      'bubble_my_content': 'Mi contenido',
      'bubble_dark': 'Oscuro',
      'bubble_video': 'Vídeo',
      'bubble_best': 'Mejor',
      'section_popular': 'Temas siempre populares',
      'section_editors_choice': 'Elegidos por el editor',
      'section_categories': 'Explorar categorías',
      'banner_headline': 'Los temas que todos están probando',
      'banner_sub': 'No esperes más, descárgalos hoy',
      'btn_download': 'Descargar (.hwt)',
      'btn_apply': 'Aplicar',
      'btn_update': 'Actualizar',
      'btn_settings': 'Ajustes',
      'btn_export_hwt': 'Exportar paquete .hwt',
      'live_wallpapers': 'Fondos animados',
      'wallpapers': 'Fondos',
      'art': 'Arte y diseño',
      'icon_packs': 'Paquetes de iconos',
      'animated_badge': 'Animado',
      'free': 'Gratis',
      'details_title': 'Detalles del tema',
      'specifications': 'Especificaciones',
      'format': 'Formato',
      'target_folder': 'Carpeta de destino',
      'compatibility': 'Compatibilidad',
      'file_size': 'Tamaño',
      'description': 'Descripción',
      'dark_mode': 'Modo oscuro',
      'notifications': 'Notificaciones',
      'guide_title': 'Guía de instalación',
      'access_denied': 'Acceso denegado',
      'admin_dashboard': 'Panel de administración',
    },
  };

  String translate(String key) {
    return _localizedValues[locale.languageCode]?[key] ??
        _localizedValues['ar']?[key] ?? // Arabic fallback
        key;
  }
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  bool isSupported(Locale locale) {
    return ['ar', 'en', 'fr', 'es'].contains(locale.languageCode);
  }

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(AppLocalizations(locale));
  }

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

// Convenient Extension for easy access in Widgets
extension LocalizationExtension on BuildContext {
  String tr(String key) => AppLocalizations.of(this).translate(key);
}
```

---

### 3. Application Integration (`lib/main.dart`)

Configure `MaterialApp` with `supportedLocales` and `localeResolutionCallback` targeting Arabic as default:

```dart
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'l10n/app_localizations.dart';
import 'screens/main_navigation_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MagicThemesApp());
}

class MagicThemesApp extends StatelessWidget {
  const MagicThemesApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Magic Themes',
      debugShowCheckedModeBanner: false,

      // 1. Supported Locales
      supportedLocales: AppLocalizations.supportedLocales,

      // 2. Localization Delegates (Natively handles Arabic RTL text direction)
      localizationsDelegates: const [
        AppLocalizations.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],

      // 3. Locale Resolution: Match device language, default to Arabic ('ar')
      localeResolutionCallback: (deviceLocale, supportedLocales) {
        if (deviceLocale != null) {
          for (var locale in supportedLocales) {
            if (locale.languageCode == deviceLocale.languageCode) {
              return locale; // Matches phone language (e.g. en, fr, es)
            }
          }
        }
        // Fallback default: Arabic
        return const Locale('ar');
      },

      home: const MainNavigationScreen(),
    );
  }
}
```

---

### 4. Usage Example in Any Widget

Simply use `context.tr('key')`:

```dart
// Automatic translation with Arabic RTL support
Text(context.tr('tab_themes'));     // "الثيمات" in Arabic, "Themes" in English
Text(context.tr('btn_download'));   // "تحميل (.hwt)" in Arabic, "Download (.hwt)" in English
Text(context.tr('section_popular'));// "ثيمات دائماً شائعة" in Arabic
```
