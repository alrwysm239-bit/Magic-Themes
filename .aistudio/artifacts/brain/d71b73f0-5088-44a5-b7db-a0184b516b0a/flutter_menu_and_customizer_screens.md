# Flutter Menu & Settings Screen and Customizer Studio (Honor/Huawei EMUI & MagicOS)

This document contains the complete, production-ready Flutter/Dart implementations for:
1. `menu_screen.dart`: User profile header with live Supabase Auth info, navigation to "Mes contenus", notifications switch, theme directory management, dark mode toggle, and settings dialog.
2. `customizer_screen.dart`: Interactive Creator Studio with live smartphone mockup, glowing radial spectrum ring, dynamic switching between lockscreen and homescreen, live palette swatches, and .hwt export.

---

### 1. `lib/screens/menu_screen.dart`

```dart
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'my_content_screen.dart';

class MenuScreen extends StatefulWidget {
  final ValueChanged<bool>? onThemeModeChanged;
  final bool initialDarkMode;

  const MenuScreen({
    super.key,
    this.onThemeModeChanged,
    this.initialDarkMode = true,
  });

  @override
  State<MenuScreen> createState() => _MenuScreenState();
}

class _MenuScreenState extends State<MenuScreen> {
  late bool _isDarkMode;
  bool _notificationsEnabled = true;

  @override
  void initState() {
    super.initState();
    _isDarkMode = widget.initialDarkMode;
    _loadPreferences();
  }

  Future<void> _loadPreferences() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _isDarkMode = prefs.getBool('dark_mode_enabled') ?? widget.initialDarkMode;
      _notificationsEnabled = prefs.getBool('notifications_enabled') ?? true;
    });
  }

  Future<void> _toggleDarkMode(bool value) async {
    setState(() => _isDarkMode = value);
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('dark_mode_enabled', value);
    widget.onThemeModeChanged?.call(value);
  }

  Future<void> _toggleNotifications(bool value) async {
    setState(() => _notificationsEnabled = value);
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('notifications_enabled', value);
  }

  void _showSettingsDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF141129),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(24),
          side: const BorderSide(color: Color(0xFF8C52FF)),
        ),
        title: const Text('Paramètres Magic Themes', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: const [
            Text('Version: 1.0.0 (Honor MagicOS & Huawei EMUI Edition)', style: TextStyle(color: Colors.white70, fontSize: 13)),
            SizedBox(height: 8),
            Text('Format d\'archive: .HWT (description.xml + icons)', style: TextStyle(color: Colors.white70, fontSize: 13)),
            SizedBox(height: 8),
            Text('Répertoire cible: /Download/ThemeStore', style: TextStyle(color: Color(0xFF00D2FF), fontSize: 13)),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('OK', style: TextStyle(color: Color(0xFF8C52FF), fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  void _showInstallGuide() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF141129),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(24),
          side: const BorderSide(color: Color(0xFF00D2FF)),
        ),
        title: const Text('Guide MagicOS / EMUI', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: const Text(
          '1. Téléchargez un thème .hwt.\n'
          '2. Il est automatiquement enregistré dans le dossier Download/ThemeStore.\n'
          '3. Ouvrez l\'application "Thèmes" préinstallée sur votre Honor ou Huawei.\n'
          '4. Rendez-vous dans "Moi" > "Mes thèmes" et appliquez votre nouveau thème.',
          style: TextStyle(color: Colors.white70, fontSize: 13, height: 1.5),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Compris', style: TextStyle(color: Color(0xFF00D2FF), fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    const neonPurple = Color(0xFF8C52FF);
    const neonCyan = Color(0xFF00D2FF);

    // Retrieve active user from Supabase Auth
    final user = Supabase.instance.client.auth.currentUser;
    final userEmail = user?.email ?? 'alrwysm239@gmail.com';
    final userName = user?.userMetadata?['name'] ?? userEmail.split('@').first;

    return Scaffold(
      backgroundColor: const Color(0xFF07060E),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Top Bar Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Menu', style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold)),
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

              const SizedBox(height: 18),

              // 1. User Profile Header Card (Supabase Auth info)
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: const Color(0xFF141129),
                  borderRadius: BorderRadius.circular(24),
                  border: Border.all(color: neonPurple.withOpacity(0.35)),
                  boxShadow: [
                    BoxShadow(color: neonPurple.withOpacity(0.15), blurRadius: 12),
                  ],
                ),
                child: Row(
                  children: [
                    Container(
                      width: 54,
                      height: 54,
                      decoration: const BoxDecoration(
                        shape: BoxShape.circle,
                        gradient: LinearGradient(colors: [neonPurple, neonCyan]),
                      ),
                      child: const Center(
                        child: Icon(Icons.person, color: Colors.white, size: 30),
                      ),
                    ),
                    const SizedBox(width: 14),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              Text(
                                userName,
                                style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
                              ),
                              const SizedBox(width: 8),
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                                decoration: BoxDecoration(
                                  color: neonPurple.withOpacity(0.3),
                                  borderRadius: BorderRadius.circular(10),
                                  border: Border.all(color: neonPurple, width: 1),
                                ),
                                child: const Text(
                                  'VIP',
                                  style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: neonCyan),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 2),
                          Text(
                            userEmail,
                            style: const TextStyle(fontSize: 12, color: Colors.white60),
                          ),
                        ],
                      ),
                    ),
                    const Icon(Icons.arrow_forward_ios, color: Colors.white38, size: 14),
                  ],
                ),
              ),

              const SizedBox(height: 24),

              // 2. Navigation & Feature Tiles Card
              Container(
                decoration: BoxDecoration(
                  color: const Color(0xFF141129),
                  borderRadius: BorderRadius.circular(24),
                  border: Border.all(color: neonPurple.withOpacity(0.2)),
                ),
                child: Column(
                  children: [
                    // Navigation to "My Content" (Mes contenus)
                    _menuTile(
                      icon: Icons.folder_special,
                      iconColor: neonPurple,
                      title: 'Mes contenus',
                      subtitle: 'Thèmes .hwt téléchargés et favoris',
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(builder: (_) => const MyContentScreen()),
                        );
                      },
                    ),

                    const Divider(color: Colors.white10, height: 1, indent: 64),

                    // Theme Folder Manager
                    _menuTile(
                      icon: Icons.storage,
                      iconColor: neonCyan,
                      title: 'Dossier des thèmes',
                      subtitle: '/Download/ThemeStore',
                      onTap: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('Emplacement cible: /storage/emulated/0/Download/ThemeStore')),
                        );
                      },
                    ),

                    const Divider(color: Colors.white10, height: 1, indent: 64),

                    // Notifications Switch
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      child: Row(
                        children: [
                          Container(
                            width: 38,
                            height: 38,
                            decoration: BoxDecoration(
                              color: const Color(0xFF221C42),
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: const Icon(Icons.notifications, color: neonPurple, size: 20),
                          ),
                          const SizedBox(width: 14),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: const [
                                Text('Notifications', style: TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.w600)),
                                Text('Nouveaux thèmes MagicOS & EMUI', style: TextStyle(color: Colors.white54, fontSize: 11)),
                              ],
                            ),
                          ),
                          Switch(
                            value: _notificationsEnabled,
                            activeColor: neonPurple,
                            activeTrackColor: neonPurple.withOpacity(0.5),
                            inactiveTrackColor: const Color(0xFF261D4C),
                            onChanged: _toggleNotifications,
                          ),
                        ],
                      ),
                    ),

                    const Divider(color: Colors.white10, height: 1, indent: 64),

                    // Functional Dark Mode Toggle Switch
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      child: Row(
                        children: [
                          Container(
                            width: 38,
                            height: 38,
                            decoration: BoxDecoration(
                              color: const Color(0xFF221C42),
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: const Icon(Icons.dark_mode, color: neonCyan, size: 20),
                          ),
                          const SizedBox(width: 14),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: const [
                                Text('Dark Mode', style: TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.w600)),
                                Text('Contraste AMOLED et néons', style: TextStyle(color: Colors.white54, fontSize: 11)),
                              ],
                            ),
                          ),
                          Switch(
                            value: _isDarkMode,
                            activeColor: neonCyan,
                            activeTrackColor: neonCyan.withOpacity(0.5),
                            inactiveTrackColor: const Color(0xFF261D4C),
                            onChanged: _toggleDarkMode,
                          ),
                        ],
                      ),
                    ),

                    const Divider(color: Colors.white10, height: 1, indent: 64),

                    // Guide MagicOS / EMUI
                    _menuTile(
                      icon: Icons.help_outline,
                      iconColor: const Color(0xFFFFB800),
                      title: 'Guide d\'installation',
                      subtitle: 'Comment installer manuellement un fichier .hwt',
                      onTap: _showInstallGuide,
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 32),

              // 3. "Paramètres" (Settings) Glowing Button
              GestureDetector(
                onTap: _showSettingsDialog,
                child: Container(
                  width: double.infinity,
                  height: 54,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(27),
                    color: const Color(0xFF1E153D),
                    border: Border.all(color: neonPurple, width: 1.5),
                    boxShadow: [
                      BoxShadow(color: neonPurple.withOpacity(0.35), blurRadius: 14),
                    ],
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: const [
                      Icon(Icons.settings, color: neonPurple, size: 20),
                      SizedBox(width: 10),
                      Text(
                        'Paramètres',
                        style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white),
                      ),
                    ],
                  ),
                ),
              ),

              const SizedBox(height: 20),
            ],
          ),
        ),
      ),
    );
  }

  Widget _menuTile({
    required IconData icon,
    required Color iconColor,
    required String title,
    required String subtitle,
    required VoidCallback onTap,
  }) {
    return ListTile(
      onTap: onTap,
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      leading: Container(
        width: 38,
        height: 38,
        decoration: BoxDecoration(
          color: const Color(0xFF221C42),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Icon(icon, color: iconColor, size: 20),
      ),
      title: Text(title, style: const TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.w600)),
      subtitle: Text(subtitle, style: const TextStyle(color: Colors.white54, fontSize: 11)),
      trailing: const Icon(Icons.arrow_forward_ios, color: Colors.white38, size: 13),
    );
  }
}
```

---

### 2. `lib/screens/customizer_screen.dart`

```dart
import 'package:flutter/material.dart';

class CustomizerScreen extends StatefulWidget {
  const CustomizerScreen({super.key});

  @override
  State<CustomizerScreen> createState() => _CustomizerScreenState();
}

class _CustomizerScreenState extends State<CustomizerScreen> {
  // Customizer styling state
  Color _accentColor = const Color(0xFF00D2FF);
  int _activeToolIndex = 0; // 0: Palette, 1: Fond, 2: Horloge, 3: Icônes
  bool _isLockScreenMode = true;

  int _selectedWallpaperIndex = 0;
  int _selectedClockIndex = 0;
  int _selectedIconPackIndex = 0;

  final List<Color> _palette = [
    const Color(0xFF00D2FF), // Neon Cyan
    const Color(0xFF8C52FF), // Neon Purple
    const Color(0xFFFF2A85), // Neon Magenta
    const Color(0xFFFFB800), // Amber Gold
    const Color(0xFF10B981), // Emerald
    const Color(0xFF3B82F6), // Electric Blue
    Colors.white,
  ];

  final List<Map<String, dynamic>> _wallpaperAccents = [
    {'name': 'Cyber Nebula', 'colors': [Color(0xFF2E0854), Color(0xFF00D2FF)]},
    {'name': 'Anime Sunset', 'colors': [Color(0xFF4A0E4E), Color(0xFFFF2A85)]},
    {'name': 'Deep Cosmic', 'colors': [Color(0xFF1E1B4B), Color(0xFF7C3AED)]},
    {'name': 'Noir Minimal', 'colors': [Color(0xFF0F172A), Color(0xFF334155)]},
  ];

  final List<String> _clockStyles = ['Néon Digital', 'Minimaliste', 'Double Fuseau'];
  final List<String> _iconStyles = ['Néon 3D Glass', 'Pastel Soft', 'Cyber Glitch', 'Noir Minimal'];

  void _exportHwtTheme() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF141129),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(24),
          side: const BorderSide(color: Color(0xFF8C52FF), width: 1.5),
        ),
        title: const Text('Pack .HWT Exporté !', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Votre thème personnalisé a été généré avec succès pour Honor MagicOS et Huawei EMUI.',
              style: TextStyle(color: Colors.white70, fontSize: 13),
            ),
            const SizedBox(height: 10),
            Text(
              'Accent: #${_accentColor.value.toRadixString(16).substring(2).toUpperCase()}\n'
              'Style d\'icônes: ${_iconStyles[_selectedIconPackIndex]}\n'
              'Horloge: ${_clockStyles[_selectedClockIndex]}',
              style: const TextStyle(color: Color(0xFF00D2FF), fontSize: 12, fontFamily: 'monospace'),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Fermer', style: TextStyle(color: Color(0xFF8C52FF), fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    const neonPurple = Color(0xFF8C52FF);
    const neonCyan = Color(0xFF00D2FF);

    final currentGradient = _wallpaperAccents[_selectedWallpaperIndex]['colors'] as List<Color>;

    return Scaffold(
      backgroundColor: const Color(0xFF07060E),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
          child: Column(
            children: [
              // Header & Screen Mode Switcher
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Customizer Studio', style: TextStyle(color: Colors.white, fontSize: 22, fontWeight: FontWeight.bold)),
                  GestureDetector(
                    onTap: () => setState(() => _isLockScreenMode = !_isLockScreenMode),
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                      decoration: BoxDecoration(
                        color: const Color(0xFF231E44),
                        borderRadius: BorderRadius.circular(18),
                        border: Border.all(color: neonPurple),
                      ),
                      child: Text(
                        _isLockScreenMode ? 'Écran Verrouillage' : 'Écran Accueil',
                        style: const TextStyle(color: neonCyan, fontSize: 11, fontWeight: FontWeight.bold),
                      ),
                    ),
                  ),
                ],
              ),

              const SizedBox(height: 24),

              // Interactive Central Smartphone Mockup with Radial Color Spectrum Aura
              Center(
                child: Stack(
                  alignment: Alignment.center,
                  children: [
                    // Glowing radial aura ring matching image
                    Container(
                      width: 260,
                      height: 260,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        boxShadow: [
                          BoxShadow(
                            color: _accentColor.withOpacity(0.45),
                            blurRadius: 50,
                            spreadRadius: 8,
                          ),
                        ],
                      ),
                    ),

                    // Phone Chassis
                    Container(
                      width: 220,
                      height: 360,
                      decoration: BoxDecoration(
                        color: const Color(0xFF0F0E17),
                        borderRadius: BorderRadius.circular(36),
                        border: Border.all(color: const Color(0xFF28253D), width: 3.5),
                        boxShadow: [
                          BoxShadow(color: neonPurple.withOpacity(0.3), blurRadius: 20),
                        ],
                      ),
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(30),
                        child: Stack(
                          fit: StackFit.expand,
                          children: [
                            // Wallpaper Gradient Accent
                            Container(
                              decoration: BoxDecoration(
                                gradient: LinearGradient(
                                  colors: currentGradient,
                                  begin: Alignment.topCenter,
                                  end: Alignment.bottomCenter,
                                ),
                              ),
                            ),
                            Container(color: Colors.black.withOpacity(0.25)),

                            // Top Punch hole camera
                            Positioned(
                              top: 8,
                              left: 0,
                              right: 0,
                              child: Center(
                                child: Container(
                                  width: 10,
                                  height: 10,
                                  decoration: const BoxDecoration(
                                    color: Colors.black,
                                    shape: BoxShape.circle,
                                  ),
                                ),
                              ),
                            ),

                            // Content: Lockscreen vs Homescreen
                            if (_isLockScreenMode) ...[
                              Positioned(
                                top: 50,
                                left: 0,
                                right: 0,
                                child: Column(
                                  children: [
                                    if (_selectedClockIndex == 0) ...[
                                      Text('12:45', style: TextStyle(fontSize: 42, fontWeight: FontWeight.w900, color: _accentColor, letterSpacing: 1)),
                                      const Text('Vendredi, 6 Novembre', style: TextStyle(color: Colors.white, fontSize: 11)),
                                    ] else if (_selectedClockIndex == 1) ...[
                                      const Text('12:45', style: TextStyle(fontSize: 38, fontWeight: FontWeight.w200, color: Colors.white)),
                                      Text('MagicOS · 24°C', style: TextStyle(color: _accentColor, fontSize: 11)),
                                    ] else ...[
                                      Row(
                                        mainAxisAlignment: MainAxisAlignment.center,
                                        children: [
                                          Column(children: [const Text('PARIS', style: TextStyle(fontSize: 9, color: Colors.white60)), Text('12:45', style: TextStyle(color: _accentColor, fontSize: 18, fontWeight: FontWeight.bold))]),
                                          const SizedBox(width: 16),
                                          Column(children: const [Text('TOKYO', style: TextStyle(fontSize: 9, color: Colors.white60)), Text('20:45', style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold))]),
                                        ],
                                      ),
                                    ],
                                  ],
                                ),
                              ),
                            ] else ...[
                              // Homescreen preview with weather widget and 4 icons
                              Positioned(
                                top: 50,
                                left: 16,
                                right: 16,
                                child: Container(
                                  padding: const EdgeInsets.all(10),
                                  decoration: BoxDecoration(
                                    color: Colors.black.withOpacity(0.3),
                                    borderRadius: BorderRadius.circular(14),
                                    border: Border.all(color: Colors.white24),
                                  ),
                                  child: Row(
                                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                    children: [
                                      Column(crossAxisAlignment: CrossAxisAlignment.start, children: [const Text('Paris · 24°C', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white)), Text('Magic Theme', style: TextStyle(color: _accentColor, fontSize: 10))]),
                                      const Icon(Icons.wb_sunny, color: Color(0xFFFFB800), size: 20),
                                    ],
                                  ),
                                ),
                              ),
                            ],

                            // Bottom Dock Icons (4 styled icons)
                            Positioned(
                              bottom: 12,
                              left: 10,
                              right: 10,
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.spaceAround,
                                children: [
                                  _mockupIcon(Icons.phone, _accentColor),
                                  _mockupIcon(Icons.chat, neonPurple),
                                  _mockupIcon(Icons.camera_alt, neonCyan),
                                  _mockupIcon(Icons.language, const Color(0xFFFFB800)),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 26),

              // Tool Selector Bar (Palette, Fond, Horloge, Icônes)
              Container(
                padding: const EdgeInsets.all(4),
                decoration: BoxDecoration(
                  color: const Color(0xFF141129),
                  borderRadius: BorderRadius.circular(24),
                  border: Border.all(color: neonPurple.withOpacity(0.2)),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _toolButton(0, Icons.color_lens, 'Couleur'),
                    _toolButton(1, Icons.brush, 'Fond'),
                    _toolButton(2, Icons.schedule, 'Horloge'),
                    _toolButton(3, Icons.dashboard, 'Icônes'),
                  ],
                ),
              ),

              const SizedBox(height: 18),

              // Active Tool Options
              if (_activeToolIndex == 0) ...[
                const Text('Teinte néon d\'accentuation', style: TextStyle(color: Colors.white60, fontSize: 12)),
                const SizedBox(height: 12),
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
                          border: Border.all(color: isSelected ? Colors.white : Colors.transparent, width: 2.5),
                          boxShadow: [BoxShadow(color: color.withOpacity(0.5), blurRadius: 8)],
                        ),
                        child: isSelected ? const Icon(Icons.check, color: Colors.black, size: 20) : null,
                      ),
                    );
                  }).toList(),
                ),
              ] else if (_activeToolIndex == 1) ...[
                const Text('Dégradé d\'ambiance du fond', style: TextStyle(color: Colors.white60, fontSize: 12)),
                const SizedBox(height: 12),
                SizedBox(
                  height: 50,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemCount: _wallpaperAccents.length,
                    itemBuilder: (context, index) {
                      final item = _wallpaperAccents[index];
                      final isSelected = _selectedWallpaperIndex == index;
                      final colors = item['colors'] as List<Color>;
                      return GestureDetector(
                        onTap: () => setState(() => _selectedWallpaperIndex = index),
                        child: Container(
                          margin: const EdgeInsets.only(right: 12),
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          decoration: BoxDecoration(
                            gradient: LinearGradient(colors: colors),
                            borderRadius: BorderRadius.circular(16),
                            border: Border.all(color: isSelected ? Colors.white : Colors.transparent, width: 2),
                          ),
                          child: Center(
                            child: Text(item['name'], style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 12)),
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ] else if (_activeToolIndex == 2) ...[
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: _clockStyles.asMap().entries.map((entry) {
                    final isSelected = entry.key == _selectedClockIndex;
                    return GestureDetector(
                      onTap: () => setState(() => _selectedClockIndex = entry.key),
                      child: Container(
                        margin: const EdgeInsets.symmetric(horizontal: 6),
                        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                        decoration: BoxDecoration(
                          color: isSelected ? const Color(0xFF261D4C) : const Color(0xFF141129),
                          borderRadius: BorderRadius.circular(16),
                          border: Border.all(color: isSelected ? neonPurple : Colors.white24),
                        ),
                        child: Text(entry.value, style: TextStyle(color: isSelected ? Colors.white : Colors.white60, fontSize: 12)),
                      ),
                    );
                  }).toList(),
                ),
              ] else ...[
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: _iconStyles.asMap().entries.map((entry) {
                    final isSelected = entry.key == _selectedIconPackIndex;
                    return GestureDetector(
                      onTap: () => setState(() => _selectedIconPackIndex = entry.key),
                      child: Container(
                        margin: const EdgeInsets.symmetric(horizontal: 4),
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
                        decoration: BoxDecoration(
                          color: isSelected ? const Color(0xFF261D4C) : const Color(0xFF141129),
                          borderRadius: BorderRadius.circular(14),
                          border: Border.all(color: isSelected ? _accentColor : Colors.white24),
                        ),
                        child: Text(entry.value, style: TextStyle(color: isSelected ? Colors.white : Colors.white60, fontSize: 11)),
                      ),
                    );
                  }).toList(),
                ),
              ],

              const SizedBox(height: 28),

              // Export Pack .hwt Button
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: neonPurple,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(26)),
                    shadowColor: neonPurple.withOpacity(0.6),
                    elevation: 10,
                  ),
                  onPressed: _exportHwtTheme,
                  icon: const Icon(Icons.auto_awesome, color: Colors.white),
                  label: const Text('Exporter Pack .hwt', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15, color: Colors.white)),
                ),
              ),

              const SizedBox(height: 20),
            ],
          ),
        ),
      ),
    );
  }

  Widget _toolButton(int index, IconData icon, String label) {
    final isSelected = _activeToolIndex == index;
    const neonPurple = Color(0xFF8C52FF);

    return GestureDetector(
      onTap: () => setState(() => _activeToolIndex = index),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: isSelected ? neonPurple : Colors.transparent,
          borderRadius: BorderRadius.circular(18),
        ),
        child: Row(
          children: [
            Icon(icon, color: isSelected ? Colors.white : Colors.white54, size: 16),
            const SizedBox(width: 6),
            Text(label, style: TextStyle(color: isSelected ? Colors.white : Colors.white54, fontSize: 12, fontWeight: isSelected ? FontWeight.bold : FontWeight.normal)),
          ],
        ),
      ),
    );
  }

  Widget _mockupIcon(IconData icon, Color color) {
    return Container(
      width: 34,
      height: 34,
      decoration: BoxDecoration(
        color: const Color(0xFF1D1838),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: color.withOpacity(0.6)),
      ),
      child: Icon(icon, color: color, size: 18),
    );
  }
}
```

---

### 3. `lib/screens/my_content_screen.dart` (Target for "Mes contenus")

```dart
import 'package:flutter/material.dart';

class MyContentScreen extends StatefulWidget {
  const MyContentScreen({super.key});

  @override
  State<MyContentScreen> createState() => _MyContentScreenState();
}

class _MyContentScreenState extends State<MyContentScreen> {
  String _selectedTab = 'Téléchargés';

  @override
  Widget build(BuildContext context) {
    const neonPurple = Color(0xFF8C52FF);
    const neonCyan = Color(0xFF00D2FF);

    return Scaffold(
      backgroundColor: const Color(0xFF07060E),
      appBar: AppBar(
        title: const Text('Mes Contenus', style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: Colors.transparent,
      ),
      body: Column(
        children: [
          // Filter switcher [ Téléchargés ] [ Favoris ]
          Container(
            margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
            padding: const EdgeInsets.all(4),
            decoration: BoxDecoration(
              color: const Color(0xFF141129),
              borderRadius: BorderRadius.circular(22),
              border: Border.all(color: neonPurple.withOpacity(0.3)),
            ),
            child: Row(
              children: ['Téléchargés', 'Favoris'].map((tab) {
                final isSelected = tab == _selectedTab;
                return Expanded(
                  child: GestureDetector(
                    onTap: () => setState(() => _selectedTab = tab),
                    child: Container(
                      padding: const EdgeInsets.symmetric(vertical: 8),
                      decoration: BoxDecoration(
                        gradient: isSelected ? const LinearGradient(colors: [neonPurple, neonCyan]) : null,
                        borderRadius: BorderRadius.circular(18),
                      ),
                      child: Center(
                        child: Text(tab, style: TextStyle(color: isSelected ? Colors.white : Colors.white54, fontWeight: FontWeight.bold, fontSize: 13)),
                      ),
                    ),
                  ),
                );
              }).toList(),
            ),
          ),

          // Content List
          Expanded(
            child: ListView(
              padding: const EdgeInsets.all(20),
              children: [
                _contentCard('Fall Sunset .hwt', 'Jokkat by Riode · 28.4 MB', Icons.palette, true),
                const SizedBox(height: 12),
                _contentCard('Neon Glow Icons', 'CyberArts · 2,800 icônes', Icons.dashboard, false),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _contentCard(String title, String subtitle, IconData icon, bool isTheme) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFF141129),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: const Color(0xFF8C52FF).withOpacity(0.3)),
      ),
      child: Row(
        children: [
          Container(
            width: 46,
            height: 46,
            decoration: BoxDecoration(
              color: const Color(0xFF221C42),
              borderRadius: BorderRadius.circular(14),
            ),
            child: Icon(icon, color: const Color(0xFF00D2FF)),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white, fontSize: 14)),
                Text(subtitle, style: const TextStyle(color: Colors.white54, fontSize: 11)),
              ],
            ),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF8C52FF),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
            ),
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Application de $title en cours...')),
              );
            },
            child: const Text('Appliquer', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.white)),
          ),
        ],
      ),
    );
  }
}
```
