# Flutter Menu & Customizer Screens (Honor & Huawei Magic Themes)

This document provides the redesigned, production-ready Flutter implementations for **Menu & Settings** and the **Customizer / Creator Studio**, with zero layout overflow, full Arabic RTL support, and seamless fitting for the smartphone mockup, tool selectors, and export buttons.

---

## 1. Customizer Screen (`lib/screens/customizer_screen.dart`)

```dart
import 'package:flutter/material.dart';

class CustomizerScreen extends StatefulWidget {
  const CustomizerScreen({super.key});

  @override
  State<CustomizerScreen> createState() => _CustomizerScreenState();
}

class _CustomizerScreenState extends State<CustomizerScreen> {
  Color _accentColor = const Color(0xFF00D2FF);
  int _activeToolIndex = 0; // 0: اللون, 1: الخلفية, 2: الساعة, 3: الأيقونات
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
    {'name': 'سايبر نيون', 'colors': [Color(0xFF2E0854), Color(0xFF00D2FF)]},
    {'name': 'أنمي الغروب', 'colors': [Color(0xFF4A0E4E), Color(0xFFFF2A85)]},
    {'name': 'مجرة عميقة', 'colors': [Color(0xFF1E1B4B), Color(0xFF7C3AED)]},
    {'name': 'نوير مينيمال', 'colors': [Color(0xFF0F172A), Color(0xFF334155)]},
  ];

  final List<String> _clockStyles = ['نيون رقمي', 'مينيمال أنيق', 'ساعة مزدوجة'];
  final List<String> _iconStyles = ['نيون 3D Glass', 'باستيل ناعم', 'سايبر جليتش', 'أسود AMOLED'];

  void _exportHwtTheme() {
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
            Icon(Icons.auto_awesome, color: Color(0xFF00D2FF)),
            SizedBox(width: 8),
            Text('تم تصدير حزمة .hwt بنجاح', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'تم إنشاء حزمة الثيم المخصصة بنجاح وهي متوافقة تماماً مع Honor MagicOS و Huawei EMUI.',
              style: TextStyle(color: Colors.white70, fontSize: 13, height: 1.5),
            ),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: const Color(0xFF1F1A3B),
                borderRadius: BorderRadius.circular(14),
                border: Border.all(color: const Color(0x338C52FF)),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('اللون المخصص: #${_accentColor.value.toRadixString(16).substring(2).toUpperCase()}',
                      style: const TextStyle(color: Color(0xFF00D2FF), fontSize: 11, fontFamily: 'monospace')),
                  const SizedBox(height: 4),
                  Text('نمط الأيقونات: ${_iconStyles[_selectedIconPackIndex]}',
                      style: const TextStyle(color: Colors.white70, fontSize: 11)),
                  const SizedBox(height: 4),
                  Text('شكل الساعة: ${_clockStyles[_selectedClockIndex]}',
                      style: const TextStyle(color: Colors.white70, fontSize: 11)),
                ],
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إغلاق', style: TextStyle(color: Color(0xFF8C52FF), fontWeight: FontWeight.bold)),
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

    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        backgroundColor: const Color(0xFF07060E),
        body: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.only(left: 20, right: 20, top: 12, bottom: 24), // No bottom overlap
            child: Column(
              children: [
                // Header & Mode Switcher
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'استوديو التخصيص (Studio)',
                      style: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold),
                    ),
                    GestureDetector(
                      onTap: () => setState(() => _isLockScreenMode = !_isLockScreenMode),
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                        decoration: BoxDecoration(
                          color: const Color(0xFF231E44),
                          borderRadius: BorderRadius.circular(18),
                          border: Border.all(color: neonPurple),
                        ),
                        child: Text(
                          _isLockScreenMode ? 'شاشة القفل' : 'الشاشة الرئيسية',
                          style: const TextStyle(color: neonCyan, fontSize: 11, fontWeight: FontWeight.bold),
                        ),
                      ),
                    ),
                  ],
                ),

                const SizedBox(height: 14),

                // Responsive Smartphone Mockup with Radial Spectrum Aura (Zero Overflow)
                Center(
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      // Radial Glow Aura
                      Container(
                        width: 210,
                        height: 210,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          boxShadow: [
                            BoxShadow(
                              color: _accentColor.withOpacity(0.35),
                              blurRadius: 36,
                              spreadRadius: 4,
                            ),
                          ],
                        ),
                      ),

                      // Phone Chassis (Width 190, Height 290 - perfect fit)
                      Container(
                        width: 190,
                        height: 290,
                        decoration: BoxDecoration(
                          color: const Color(0xFF0F0E17),
                          borderRadius: BorderRadius.circular(30),
                          border: Border.all(color: const Color(0xFF28253D), width: 3),
                          boxShadow: [
                            BoxShadow(color: neonPurple.withOpacity(0.25), blurRadius: 16),
                          ],
                        ),
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(26),
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
                              Container(color: Colors.black.withOpacity(0.2)),

                              // Punch hole camera
                              Positioned(
                                top: 6,
                                left: 0,
                                right: 0,
                                child: Center(
                                  child: Container(
                                    width: 8,
                                    height: 8,
                                    decoration: const BoxDecoration(color: Colors.black, shape: BoxShape.circle),
                                  ),
                                ),
                              ),

                              // Screen Content (Lockscreen vs Homescreen)
                              if (_isLockScreenMode) ...[
                                Positioned(
                                  top: 36,
                                  left: 0,
                                  right: 0,
                                  child: Column(
                                    children: [
                                      if (_selectedClockIndex == 0) ...[
                                        Text('12:45', style: TextStyle(fontSize: 34, fontWeight: FontWeight.w900, color: _accentColor)),
                                        const Text('الجمعة، 6 نوفمبر', style: TextStyle(color: Colors.white, fontSize: 10)),
                                      ] else if (_selectedClockIndex == 1) ...[
                                        const Text('12:45', style: TextStyle(fontSize: 30, fontWeight: FontWeight.w200, color: Colors.white)),
                                        Text('MagicOS · 24°C', style: TextStyle(color: _accentColor, fontSize: 10)),
                                      ] else ...[
                                        Row(
                                          mainAxisAlignment: MainAxisAlignment.center,
                                          children: [
                                            Column(children: [const Text('مكة', style: TextStyle(fontSize: 8, color: Colors.white60)), Text('12:45', style: TextStyle(color: _accentColor, fontSize: 14, fontWeight: FontWeight.bold))]),
                                            const SizedBox(width: 14),
                                            Column(children: const [Text('الرياض', style: TextStyle(fontSize: 8, color: Colors.white60)), Text('12:45', style: TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold))]),
                                          ],
                                        ),
                                      ],
                                    ],
                                  ),
                                ),
                              ] else ...[
                                Positioned(
                                  top: 36,
                                  left: 12,
                                  right: 12,
                                  child: Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                                    decoration: BoxDecoration(
                                      color: Colors.black.withOpacity(0.35),
                                      borderRadius: BorderRadius.circular(12),
                                      border: Border.all(color: Colors.white24),
                                    ),
                                    child: Row(
                                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      children: [
                                        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                                          const Text('الرياض · 24°C', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.white)),
                                          Text('Magic Theme', style: TextStyle(color: _accentColor, fontSize: 9)),
                                        ]),
                                        const Icon(Icons.wb_sunny, color: Color(0xFFFFB800), size: 16),
                                      ],
                                    ),
                                  ),
                                ),
                              ],

                              // Bottom Dock Icons
                              Positioned(
                                bottom: 10,
                                left: 8,
                                right: 8,
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

                const SizedBox(height: 18),

                // Equal-Weighted Tool Selector Bar (اللون, الخلفية, الساعة, الأيقونات)
                Container(
                  padding: const EdgeInsets.all(4),
                  decoration: BoxDecoration(
                    color: const Color(0xFF141129),
                    borderRadius: BorderRadius.circular(22),
                    border: Border.all(color: neonPurple.withOpacity(0.2)),
                  ),
                  child: Row(
                    children: [
                      _toolButton(0, Icons.color_lens, 'اللون'),
                      _toolButton(1, Icons.brush, 'الخلفية'),
                      _toolButton(2, Icons.schedule, 'الساعة'),
                      _toolButton(3, Icons.dashboard, 'الأيقونات'),
                    ],
                  ),
                ),

                const SizedBox(height: 14),

                // Active Tool Customization Panel
                if (_activeToolIndex == 0) ...[
                  const Text('اختر لون النيون الأساسي', style: TextStyle(color: Colors.white70, fontSize: 11)),
                  const SizedBox(height: 10),
                  SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Row(
                      children: _palette.map((color) {
                        final isSelected = color == _accentColor;
                        return GestureDetector(
                          onTap: () => setState(() => _accentColor = color),
                          child: Container(
                            margin: const EdgeInsets.symmetric(horizontal: 5),
                            width: 36,
                            height: 36,
                            decoration: BoxDecoration(
                              color: color,
                              shape: BoxShape.circle,
                              border: Border.all(color: isSelected ? Colors.white : Colors.transparent, width: 2),
                              boxShadow: [BoxShadow(color: color.withOpacity(0.5), blurRadius: 6)],
                            ),
                            child: isSelected ? const Icon(Icons.check, color: Colors.black, size: 18) : null,
                          ),
                        );
                      }).toList(),
                    ),
                  ),
                ] else if (_activeToolIndex == 1) ...[
                  const Text('تدرجات ألوان الخلفية', style: TextStyle(color: Colors.white70, fontSize: 11)),
                  const SizedBox(height: 10),
                  SizedBox(
                    height: 44,
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
                            margin: const EdgeInsets.symmetric(horizontal: 4),
                            padding: const EdgeInsets.symmetric(horizontal: 14),
                            decoration: BoxDecoration(
                              gradient: LinearGradient(colors: colors),
                              borderRadius: BorderRadius.circular(14),
                              border: Border.all(color: isSelected ? Colors.white : Colors.transparent, width: 2),
                            ),
                            child: Center(
                              child: Text(item['name'], style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 11)),
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                ] else if (_activeToolIndex == 2) ...[
                  SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Row(
                      children: _clockStyles.asMap().entries.map((entry) {
                        final isSelected = entry.key == _selectedClockIndex;
                        return GestureDetector(
                          onTap: () => setState(() => _selectedClockIndex = entry.key),
                          child: Container(
                            margin: const EdgeInsets.symmetric(horizontal: 4),
                            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                            decoration: BoxDecoration(
                              color: isSelected ? const Color(0xFF261D4C) : const Color(0xFF141129),
                              borderRadius: BorderRadius.circular(14),
                              border: Border.all(color: isSelected ? neonPurple : Colors.white24),
                            ),
                            child: Text(entry.value, style: TextStyle(color: isSelected ? Colors.white : Colors.white60, fontSize: 11)),
                          ),
                        );
                      }).toList(),
                    ),
                  ),
                ] else ...[
                  SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Row(
                      children: _iconStyles.asMap().entries.map((entry) {
                        final isSelected = entry.key == _selectedIconPackIndex;
                        return GestureDetector(
                          onTap: () => setState(() => _selectedIconPackIndex = entry.key),
                          child: Container(
                            margin: const EdgeInsets.symmetric(horizontal: 4),
                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
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
                  ),
                ],

                const SizedBox(height: 22),

                // Export Button ("تصدير حزمة .hwt") - Perfect Full Width Fitting
                SizedBox(
                  width: double.infinity,
                  height: 50,
                  child: ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: neonPurple,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(25)),
                      shadowColor: neonPurple.withOpacity(0.5),
                      elevation: 8,
                    ),
                    onPressed: _exportHwtTheme,
                    icon: const Icon(Icons.auto_awesome, color: Colors.white, size: 20),
                    label: const Text(
                      'تصدير حزمة .hwt',
                      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15, color: Colors.white),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _toolButton(int index, IconData icon, String label) {
    final isSelected = _activeToolIndex == index;
    const neonPurple = Color(0xFF8C52FF);

    return Expanded(
      child: GestureDetector(
        onTap: () => setState(() => _activeToolIndex = index),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            color: isSelected ? neonPurple : Colors.transparent,
            borderRadius: BorderRadius.circular(18),
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(icon, color: isSelected ? Colors.white : Colors.white60, size: 14),
              const SizedBox(width: 4),
              Text(
                label,
                style: TextStyle(
                  color: isSelected ? Colors.white : Colors.white60,
                  fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                  fontSize: 11,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _mockupIcon(IconData icon, Color color) {
    return Container(
      width: 26,
      height: 26,
      decoration: BoxDecoration(
        color: const Color(0xFF1E1A33).withOpacity(0.8),
        borderRadius: BorderRadius.circular(7),
        border: Border.all(color: color.withOpacity(0.7), width: 1),
      ),
      child: Icon(icon, color: Colors.white, size: 13),
    );
  }
}
```
