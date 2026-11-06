# Flutter Admin Dashboard Screen (Honor/Huawei Theme Store)

This document contains the complete, production-ready Flutter/Dart implementation for:
1. `admin_dashboard_screen.dart`:
   - Admin authentication check against the `user_roles` table.
   - Access Denied view for non-admin users.
   - Upload Form for new `.hwt` themes with preview upload and metadata creation.
   - Themes Management list with delete buttons that remove records from the database and storage.

---

### `lib/screens/admin_dashboard_screen.dart`

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class AdminDashboardScreen extends StatefulWidget {
  const AdminDashboardScreen({super.key});

  @override
  State<AdminDashboardScreen> createState() => _AdminDashboardScreenState();
}

class _AdminDashboardScreenState extends State<AdminDashboardScreen> with SingleTickerProviderStateMixin {
  final SupabaseClient _supabase = Supabase.instance.client;

  bool _isCheckingRole = true;
  bool _isAdmin = false;
  late TabController _tabController;

  // Upload Form Controllers & State
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _creatorController = TextEditingController(text: 'Riode Studios');
  final _priceController = TextEditingController(text: 'Gratuit');
  final _sizeMbController = TextEditingController(text: '28.4 MB');

  String? _selectedCategoryId;
  List<Map<String, dynamic>> _categories = [];
  bool _isAnimated = false;
  bool _isPopular = false;
  bool _isEditorsChoice = false;

  PlatformFile? _pickedPreviewImage;
  PlatformFile? _pickedHwtFile;
  bool _isUploading = false;
  double _uploadProgress = 0.0;

  // Management List State
  List<Map<String, dynamic>> _uploadedThemes = [];
  bool _isLoadingThemes = false;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _verifyAdminRole();
  }

  @override
  void dispose() {
    _tabController.dispose();
    _titleController.dispose();
    _descriptionController.dispose();
    _creatorController.dispose();
    _priceController.dispose();
    _sizeMbController.dispose();
    super.dispose();
  }

  /// 1. Authentication & Role Check
  Future<void> _verifyAdminRole() async {
    setState(() => _isCheckingRole = true);

    final user = _supabase.auth.currentUser;
    if (user == null) {
      setState(() {
        _isCheckingRole = false;
        _isAdmin = false;
      });
      return;
    }

    try {
      final roleData = await _supabase
          .from('user_roles')
          .select('role')
          .eq('user_id', user.id)
          .eq('role', 'admin')
          .maybeSingle();

      if (roleData != null && roleData['role'] == 'admin') {
        setState(() {
          _isAdmin = true;
          _isCheckingRole = false;
        });
        _loadCategories();
        _loadUploadedThemes();
      } else {
        setState(() {
          _isAdmin = false;
          _isCheckingRole = false;
        });
      }
    } catch (e) {
      setState(() {
        _isAdmin = false;
        _isCheckingRole = false;
      });
    }
  }

  Future<void> _loadCategories() async {
    try {
      final response = await _supabase.from('categories').select('id, title');
      setState(() {
        _categories = List<Map<String, dynamic>>.from(response);
        if (_categories.isNotEmpty) {
          _selectedCategoryId = _categories.first['id'].toString();
        }
      });
    } catch (_) {}
  }

  Future<void> _loadUploadedThemes() async {
    setState(() => _isLoadingThemes = true);
    try {
      final response = await _supabase
          .from('themes')
          .select()
          .order('created_at', ascending: false);

      setState(() {
        _uploadedThemes = List<Map<String, dynamic>>.from(response);
        _isLoadingThemes = false;
      });
    } catch (_) {
      setState(() => _isLoadingThemes = false);
    }
  }

  /// Pick Preview Image
  Future<void> _pickPreviewImage() async {
    final result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['jpg', 'jpeg', 'png', 'webp'],
    );
    if (result != null && result.files.isNotEmpty) {
      setState(() => _pickedPreviewImage = result.files.first);
    }
  }

  /// Pick .HWT File
  Future<void> _pickHwtFile() async {
    final result = await FilePicker.platform.pickFiles(
      type: FileType.any,
    );
    if (result != null && result.files.isNotEmpty) {
      final file = result.files.first;
      if (file.name.toLowerCase().endsWith('.hwt') || file.name.toLowerCase().endsWith('.zip')) {
        setState(() {
          _pickedHwtFile = file;
          final sizeInMb = (file.size / (1024 * 1024)).toStringAsFixed(1);
          _sizeMbController.text = '$sizeInMb MB';
        });
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Veuillez sélectionner un fichier au format .hwt')),
        );
      }
    }
  }

  /// 2. Submit & Upload Theme to Storage and DB
  Future<void> _submitThemeUpload() async {
    if (!_formKey.currentState!.validate()) return;

    if (_pickedPreviewImage == null || _pickedPreviewImage!.path == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Veuillez sélectionner une image d\'aperçu')),
      );
      return;
    }

    if (_pickedHwtFile == null || _pickedHwtFile!.path == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Veuillez sélectionner le fichier .hwt du thème')),
      );
      return;
    }

    setState(() {
      _isUploading = true;
      _uploadProgress = 0.1;
    });

    try {
      final timestamp = DateTime.now().millisecondsSinceEpoch;
      final safeTitle = _titleController.text.trim().replaceAll(RegExp(r'\s+'), '_').toLowerCase();

      // 1. Upload Preview Image to Supabase Storage bucket 'theme_previews'
      final previewExt = _pickedPreviewImage!.extension ?? 'jpg';
      final previewStoragePath = 'previews/${safeTitle}_$timestamp.$previewExt';
      final previewFile = File(_pickedPreviewImage!.path!);

      await _supabase.storage.from('theme_previews').upload(
        previewStoragePath,
        previewFile,
        fileOptions: const FileOptions(cacheControl: '3600', upsert: true),
      );

      setState(() => _uploadProgress = 0.5);

      final previewPublicUrl = _supabase.storage.from('theme_previews').getPublicUrl(previewStoragePath);

      // 2. Upload .HWT File to Supabase Storage bucket 'themes_hwt'
      final hwtStoragePath = 'themes/${safeTitle}_$timestamp.hwt';
      final hwtFile = File(_pickedHwtFile!.path!);

      await _supabase.storage.from('themes_hwt').upload(
        hwtStoragePath,
        hwtFile,
        fileOptions: const FileOptions(cacheControl: '3600', upsert: true),
      );

      setState(() => _uploadProgress = 0.8);

      // 3. Insert record into Supabase Database `themes`
      final themePayload = {
        'title': _titleController.text.trim(),
        'description': _descriptionController.text.trim(),
        'creator': _creatorController.text.trim(),
        'price': _priceController.text.trim(),
        'size_mb': _sizeMbController.text.trim(),
        'category_id': _selectedCategoryId,
        'preview_image_url': previewPublicUrl,
        'hwt_storage_path': hwtStoragePath,
        'is_animated': _isAnimated,
        'is_popular': _isPopular,
        'is_editors_choice': _isEditorsChoice,
        'rating': 5.0,
        'reviews_count': 0,
        'downloads_count': 0,
        'compatible_versions': 'MagicOS 7/8/9 & EMUI 12/13/14',
      };

      await _supabase.from('themes').insert(themePayload);

      setState(() {
        _isUploading = false;
        _uploadProgress = 1.0;
        _pickedPreviewImage = null;
        _pickedHwtFile = null;
        _titleController.clear();
        _descriptionController.clear();
      });

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          backgroundColor: Color(0xFF10B981),
          content: Text('Thème publié avec succès dans la boutique !'),
        ),
      );

      _loadUploadedThemes();
      _tabController.animateTo(1); // Switch to Management list tab
    } catch (e) {
      setState(() => _isUploading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(backgroundColor: const Color(0xFFEF4444), content: Text('Erreur: $e')),
      );
    }
  }

  /// 3. Delete Theme from Database and Storage
  Future<void> _deleteTheme(Map<String, dynamic> theme) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: const Color(0xFF141129),
        title: const Text('Supprimer ce thème ?', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: Text('Voulez-vous supprimer définitivement "${theme['title']}" ? Cette action est irréversible.',
            style: const TextStyle(color: Colors.white70, fontSize: 13)),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Annuler', style: TextStyle(color: Colors.white54))),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFFEF4444)),
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Supprimer', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );

    if (confirm != true) return;

    try {
      final themeId = theme['id'].toString();
      final hwtPath = theme['hwt_storage_path']?.toString();

      // 1. Delete .hwt from storage
      if (hwtPath != null && hwtPath.isNotEmpty) {
        try {
          await _supabase.storage.from('themes_hwt').remove([hwtPath]);
        } catch (_) {}
      }

      // 2. Delete DB record
      await _supabase.from('themes').delete().eq('id', themeId);

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Thème supprimé de la base de données.')),
      );

      _loadUploadedThemes();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(backgroundColor: const Color(0xFFEF4444), content: Text('Erreur de suppression: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    const neonPurple = Color(0xFF8C52FF);
    const neonCyan = Color(0xFF00D2FF);

    if (_isCheckingRole) {
      return const Scaffold(
        backgroundColor: Color(0xFF07060E),
        body: Center(
          child: CircularProgressIndicator(color: neonCyan),
        ),
      );
    }

    // 1. Access Denied Screen
    if (!_isAdmin) {
      return Scaffold(
        backgroundColor: const Color(0xFF07060E),
        appBar: AppBar(
          backgroundColor: Colors.transparent,
          elevation: 0,
          leading: IconButton(
            icon: const Icon(Icons.arrow_back, color: Colors.white),
            onPressed: () => Navigator.pop(context),
          ),
        ),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(28),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  padding: const EdgeInsets.all(22),
                  decoration: BoxDecoration(
                    color: const Color(0xFFEF4444).withOpacity(0.12),
                    shape: BoxShape.circle,
                    border: Border.all(color: const Color(0xFFEF4444), width: 1.5),
                  ),
                  child: const Icon(Icons.lock, color: Color(0xFFEF4444), size: 48),
                ),
                const SizedBox(height: 24),
                const Text(
                  'Accès Refusé',
                  style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 10),
                const Text(
                  'Votre compte ne dispose pas des privilèges administrateur requis dans la table user_roles de Supabase.',
                  textAlign: TextAlign.center,
                  style: TextStyle(color: Colors.white60, fontSize: 13, height: 1.5),
                ),
                const SizedBox(height: 26),
                ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF1E153D),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20),
                      side: const BorderSide(color: neonPurple),
                    ),
                    padding: const EdgeInsets.symmetric(horizontal: 22, vertical: 12),
                  ),
                  onPressed: () => Navigator.pop(context),
                  icon: const Icon(Icons.arrow_back, color: Colors.white),
                  label: const Text('Retour à la boutique', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                ),
              ],
            ),
          ),
        ),
      );
    }

    // 2. Authorized Admin Dashboard
    return Scaffold(
      backgroundColor: const Color(0xFF07060E),
      appBar: AppBar(
        backgroundColor: const Color(0xFF110F20),
        elevation: 0,
        title: const Text('Tableau de bord Admin', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: neonCyan,
          labelColor: neonCyan,
          unselectedLabelColor: Colors.white60,
          tabs: const [
            Tab(icon: Icon(Icons.cloud_upload), text: 'Nouveau Thème'),
            Tab(icon: Icon(Icons.list_alt), text: 'Gestion Thèmes'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          // TAB 1: Upload Form
          _buildUploadForm(neonPurple, neonCyan),

          // TAB 2: Themes Management List
          _buildManagementList(neonPurple),
        ],
      ),
    );
  }

  Widget _buildUploadForm(Color neonPurple, Color neonCyan) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Publier un thème MagicOS / EMUI',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white)),
            const SizedBox(height: 16),

            // Title
            _inputField(controller: _titleController, label: 'Titre du thème', hint: 'Ex: [PHIN] Fall Sunset Lights'),
            const SizedBox(height: 14),

            // Creator & Price Row
            Row(
              children: [
                Expanded(child: _inputField(controller: _creatorController, label: 'Créateur', hint: 'Studio / Nom')),
                const SizedBox(width: 12),
                Expanded(child: _inputField(controller: _priceController, label: 'Prix', hint: 'Gratuit ou MAD 19,00')),
              ],
            ),
            const SizedBox(height: 14),

            // Category Dropdown
            const Text('Catégorie', style: TextStyle(color: Colors.white70, fontSize: 12)),
            const SizedBox(height: 6),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14),
              decoration: BoxDecoration(
                color: const Color(0xFF141129),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: neonPurple.withOpacity(0.3)),
              ),
              child: DropdownButtonHideUnderline(
                child: DropdownButton<String>(
                  value: _selectedCategoryId,
                  dropdownColor: const Color(0xFF1E173D),
                  isExpanded: true,
                  items: _categories.map((c) {
                    return DropdownMenuItem<String>(
                      value: c['id'].toString(),
                      child: Text(c['title'], style: const TextStyle(color: Colors.white)),
                    );
                  }).toList(),
                  onChanged: (val) => setState(() => _selectedCategoryId = val),
                ),
              ),
            ),
            const SizedBox(height: 14),

            // Description
            _inputField(controller: _descriptionController, label: 'Description', hint: 'Description du pack...', maxLines: 3),
            const SizedBox(height: 18),

            // File Pickers Section
            Row(
              children: [
                // Pick Preview Image
                Expanded(
                  child: _pickerButton(
                    icon: Icons.image,
                    label: _pickedPreviewImage != null ? _pickedPreviewImage!.name : 'Image d\'aperçu',
                    isPicked: _pickedPreviewImage != null,
                    onTap: _pickPreviewImage,
                  ),
                ),
                const SizedBox(width: 12),
                // Pick .HWT
                Expanded(
                  child: _pickerButton(
                    icon: Icons.archive,
                    label: _pickedHwtFile != null ? _pickedHwtFile!.name : 'Fichier .HWT',
                    isPicked: _pickedHwtFile != null,
                    onTap: _pickHwtFile,
                  ),
                ),
              ],
            ),

            const SizedBox(height: 16),

            // Toggles
            CheckboxListTile(
              contentPadding: EdgeInsets.zero,
              activeColor: neonPurple,
              title: const Text('Thème animé (Badge Animated)', style: TextStyle(color: Colors.white, fontSize: 13)),
              value: _isAnimated,
              onChanged: (v) => setState(() => _isAnimated = v ?? false),
            ),
            CheckboxListTile(
              contentPadding: EdgeInsets.zero,
              activeColor: neonPurple,
              title: const Text('Section Populaires (is_popular)', style: TextStyle(color: Colors.white, fontSize: 13)),
              value: _isPopular,
              onChanged: (v) => setState(() => _isPopular = v ?? false),
            ),

            const SizedBox(height: 20),

            if (_isUploading) ...[
              LinearProgressIndicator(value: _uploadProgress, color: neonCyan, backgroundColor: const Color(0xFF1C1738)),
              const SizedBox(height: 10),
              Center(child: Text('Téléversement vers Supabase... (${(_uploadProgress * 100).toInt()}%)', style: const TextStyle(color: neonCyan, fontSize: 12))),
              const SizedBox(height: 14),
            ],

            // Submit Button
            SizedBox(
              width: double.infinity,
              height: 52,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: neonPurple,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(26)),
                  elevation: 8,
                ),
                onPressed: _isUploading ? null : _submitThemeUpload,
                icon: const Icon(Icons.publish, color: Colors.white),
                label: const Text('Publier le thème', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15, color: Colors.white)),
              ),
            ),
            const SizedBox(height: 30),
          ],
        ),
      ),
    );
  }

  Widget _buildManagementList(Color neonPurple) {
    if (_isLoadingThemes) {
      return const Center(child: CircularProgressIndicator(color: Color(0xFF00D2FF)));
    }

    if (_uploadedThemes.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const [
            Icon(Icons.inbox, color: Colors.white30, size: 48),
            SizedBox(height: 10),
            Text('Aucun thème publié.', style: TextStyle(color: Colors.white54)),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadUploadedThemes,
      child: ListView.separated(
        padding: const EdgeInsets.all(20),
        itemCount: _uploadedThemes.length,
        separatorBuilder: (_, __) => const SizedBox(height: 12),
        itemBuilder: (context, index) {
          final theme = _uploadedThemes[index];
          return Container(
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              color: const Color(0xFF141129),
              borderRadius: BorderRadius.circular(20),
              border: Border.all(color: neonPurple.withOpacity(0.25)),
            ),
            child: Row(
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(12),
                  child: Container(
                    width: 50,
                    height: 50,
                    color: const Color(0xFF221C42),
                    child: theme['preview_image_url'] != null
                        ? Image.network(theme['preview_image_url'], fit: BoxFit.cover)
                        : const Icon(Icons.palette, color: Colors.white),
                  ),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(theme['title'] ?? 'Sans titre', style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white, fontSize: 14)),
                      Text('${theme['creator']} · ${theme['size_mb'] ?? '28 MB'}', style: const TextStyle(color: Colors.white54, fontSize: 11)),
                      Text('Téléchargements: ${theme['downloads_count'] ?? 0}', style: const TextStyle(color: Color(0xFF00D2FF), fontSize: 11)),
                    ],
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.delete_outline, color: Color(0xFFEF4444)),
                  onPressed: () => _deleteTheme(theme),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _inputField({required TextEditingController controller, required String label, required String hint, int maxLines = 1}) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: const TextStyle(color: Colors.white70, fontSize: 12)),
        const SizedBox(height: 6),
        TextFormField(
          controller: controller,
          maxLines: maxLines,
          style: const TextStyle(color: Colors.white, fontSize: 13),
          validator: (val) => val == null || val.trim().isEmpty ? 'Champ requis' : null,
          decoration: InputDecoration(
            hintText: hint,
            hintStyle: const TextStyle(color: Colors.white30, fontSize: 13),
            filled: true,
            fillColor: const Color(0xFF141129),
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: const BorderSide(color: Color(0x338C52FF))),
            enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: const BorderSide(color: Color(0x338C52FF))),
            focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: const BorderSide(color: Color(0xFF8C52FF))),
            contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
          ),
        ),
      ],
    );
  }

  Widget _pickerButton({required IconData icon, required String label, required bool isPicked, required VoidCallback onTap}) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        height: 70,
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: const Color(0xFF141129),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: isPicked ? const Color(0xFF10B981) : const Color(0x338C52FF)),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: isPicked ? const Color(0xFF10B981) : Colors.white70, size: 22),
            const SizedBox(height: 4),
            Text(
              label,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(color: isPicked ? const Color(0xFF10B981) : Colors.white60, fontSize: 11),
            ),
          ],
        ),
      ),
    );
  }
}
```
