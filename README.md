# Kitui South Water Finder - Developer Documentation

A Firebase-backed Android application for tracking water source availability in Kitui South, Kenya. Built with Kotlin, Jetpack Compose, and Firebase Firestore.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Key Files Explained](#key-files-explained)
- [Firebase Setup](#firebase-setup)
- [Dependencies](#dependencies)
- [Building & Running](#building--running)
- [Future Enhancements](#future-enhancements)

---

## 🎯 Project Overview

**Purpose**: Help residents of Kitui South find available water sources (boreholes, kiosks, dams) in real-time.

**Key Features**:
- Browse water sources with availability status
- View water source details and locations
- Open locations in Google Maps
- Submit issue reports (all users)
- Admin login with Firebase Authentication
- Add new water sources (admin only)
- Update water source status (admin only)

**Tech Stack**:
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase Firestore (database) + Firebase Authentication
- **Navigation**: Jetpack Navigation Compose
- **Minimum SDK**: API 24 (Android 7.0)

---

## 🏗️ Architecture

This app follows the **MVVM (Model-View-ViewModel)** pattern:

```
┌─────────────────────────────────────────────────────┐
│                     UI Layer                        │
│  (Composable Screens - what users see)             │
│  WaterSourceListScreen, DetailScreen, etc.         │
└──────────────────┬──────────────────────────────────┘
                   │ observes state
                   ▼
┌─────────────────────────────────────────────────────┐
│                 ViewModel Layer                     │
│  (Business Logic & State Management)                │
│  WaterSourceViewModel                               │
└──────────────────┬──────────────────────────────────┘
                   │ calls functions
                   ▼
┌─────────────────────────────────────────────────────┐
│                 Repository Layer                    │
│  (Data Access - talks to Firebase)                  │
│  FirebaseRepository                                 │
└──────────────────┬──────────────────────────────────┘
                   │ reads/writes
                   ▼
┌─────────────────────────────────────────────────────┐
│                  Data Layer                         │
│  (Data Models)                                      │
│  WaterSource, Report                                │
└─────────────────────────────────────────────────────┘
                   │
                   ▼
              Firebase Cloud
        (Firestore + Authentication)
```

**Benefits of MVVM**:
- **Separation of Concerns**: UI, business logic, and data access are separated
- **Testability**: Each layer can be tested independently
- **Lifecycle Awareness**: ViewModel survives configuration changes (screen rotation)
- **Reactive UI**: UI automatically updates when data changes

---

## 📁 Project Structure

```
app/src/main/java/com/example/kituiwaterfinder/
│
├── data/                           # Data layer
│   ├── WaterSource.kt             # Water source data model
│   ├── Report.kt                  # Report data model
│   ├── FirebaseRepository.kt      # Firebase operations
│   └── MapUtils.kt                # Google Maps helper functions
│
├── viewmodel/                      # ViewModel layer
│   └── WaterSourceViewModel.kt    # App state & business logic
│
├── ui/                            # UI layer
│   ├── screens/                   # Composable screens
│   │   ├── WaterSourceListScreen.kt      # Main list view
│   │   ├── WaterSourceDetailScreen.kt    # Detail view
│   │   ├── ReportScreen.kt               # Report issue form
│   │   ├── AdminLoginScreen.kt           # Admin login
│   │   ├── EditStatusScreen.kt           # Admin edit status
│   │   └── AddWaterSourceScreen.kt       # Admin add new source
│   │
│   ├── components/                # Reusable UI components
│   │   └── WaterSourceCard.kt    # List item card
│   │
│   └── theme/                     # App theming
│       ├── Color.kt              # Color definitions
│       └── Theme.kt              # Material theme setup
│
├── navigation/                    # Navigation layer
│   └── NavGraph.kt               # Navigation routes & graph
│
└── MainActivity.kt               # App entry point
```

---

## 🔑 Key Files Explained

### **Data Layer**

#### `WaterSource.kt`
**Purpose**: Data class representing a single water source.

```kotlin
data class WaterSource(
    val id: String,           // Firestore document ID
    val name: String,         // e.g., "Kilungu Borehole"
    val type: String,         // "Borehole", "Kiosk", or "Dam"
    val location: String,     // Location description
    val status: String,       // "Available" or "Not Available"
    val lastUpdated: Long,    // Timestamp in milliseconds
    val latitude: Double,     // GPS coordinate
    val longitude: Double     // GPS coordinate
)
```

**Why it exists**: Provides a type-safe way to work with water source data throughout the app.

---

#### `Report.kt`
**Purpose**: Data class representing a user-submitted issue report.

```kotlin
data class Report(
    val id: String,           // Firestore document ID
    val sourceName: String,   // Which water source is being reported
    val issue: String,        // Description of the problem
    val timestamp: Long       // When the report was submitted
)
```

**Why it exists**: Allows users to report issues which are stored in Firestore for admin review.

---

#### `FirebaseRepository.kt`
**Purpose**: Centralized access point for all Firebase operations. The "middleman" between the app and Firebase.

**Key Functions**:

| Function | What It Does | Who Can Use |
|----------|-------------|-------------|
| `getWaterSources()` | Fetches all water sources from Firestore | Everyone |
| `submitReport()` | Saves a new report to Firestore | Everyone |
| `updateWaterSourceStatus()` | Updates water source status | Admin only |
| `addWaterSource()` | Adds a new water source | Admin only |
| `loginAdmin()` | Authenticates admin user | Admin only |
| `logoutAdmin()` | Signs out admin user | Admin only |
| `getCurrentUser()` | Returns logged-in user (or null) | Admin only |

**Why it exists**: 
- Single source of truth for data operations
- Handles all Firebase complexity (error handling, data conversion)
- Easy to mock/test in isolation

**Pattern Used**: Callback pattern with `onSuccess` and `onFailure` functions.

```kotlin
repository.getWaterSources(
    onSuccess = { waterSources -> /* update UI */ },
    onFailure = { error -> /* show error */ }
)
```

---

#### `MapUtils.kt`
**Purpose**: Helper functions to open Google Maps app with locations.

**Key Functions**:

| Function | What It Does |
|----------|-------------|
| `openLocationInMaps()` | Opens Maps with location name search |
| `openCoordinatesInMaps()` | Opens Maps with exact GPS coordinates |

**How it works**: Uses Android's `Intent` system with `geo:` URIs. No API key needed!

```kotlin
// Creates a URI like: geo:0,0?q=Kilungu+Borehole,+Near+Market
val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(searchQuery)}")
```

**Fallback**: If Google Maps isn't installed, opens location in web browser.

---

### **ViewModel Layer**

#### `WaterSourceViewModel.kt`
**Purpose**: The "brain" of the app. Manages all app state and business logic.

**State Variables** (automatically trigger UI updates):

```kotlin
var waterSources: List<WaterSource>     // All water sources
var isLoading: Boolean                  // Show loading indicator?
var isAdminLoggedIn: Boolean            // Is admin logged in?
var adminEmail: String                  // Admin's email
var errorMessage: String                // Error to display
var successMessage: String              // Success message
var isSubmittingReport: Boolean         // Submitting report?
var isLoggingIn: Boolean                // Logging in?
```

**Key Functions**:

| Function | What It Does |
|----------|-------------|
| `loadWaterSources()` | Fetches water sources from repository |
| `submitReport()` | Submits issue report |
| `updateWaterSourceStatus()` | Updates a source's status (admin) |
| `addWaterSource()` | Adds new water source (admin) |
| `loginAdmin()` | Logs in admin user |
| `logoutAdmin()` | Logs out admin user |
| `getWaterSourceById()` | Finds specific water source by ID |
| `refresh()` | Reloads all water sources |

**Why it exists**:
- Survives configuration changes (screen rotation)
- Provides single source of truth for app state
- Separates business logic from UI
- All screens share the same ViewModel instance

**Lifecycle**: Created once when app starts, lives until app is closed.

---

### **UI Layer - Screens**

#### `WaterSourceListScreen.kt`
**Purpose**: Main screen showing all water sources in a scrollable list.

**Features**:
- Displays water sources in cards with color-coded status
- Shows admin badge when logged in
- Admin login link at bottom
- Floating Action Button (FAB) to add new sources (admin only)
- Logout dialog (admin only)

**UI Components Used**:
- `Scaffold` - Standard Material layout
- `TopAppBar` - App bar with title and actions
- `LazyColumn` - Efficient scrolling list
- `FloatingActionButton` - Circular + button
- `WaterSourceCard` - Custom reusable card component

---

#### `WaterSourceDetailScreen.kt`
**Purpose**: Shows detailed information about a selected water source.

**Features**:
- Displays all water source details
- "View on Map" button to open Google Maps
- "Report an Issue" button
- Edit icon in top bar (admin only)

**Navigation**:
- Receives water source via navigation argument
- Can navigate to Report screen or Edit screen

---

#### `ReportScreen.kt`
**Purpose**: Form for users to report issues with a water source.

**Features**:
- Shows which water source is being reported
- Text field for issue description
- Submit button (disabled if text is empty)
- Shows "Submitting..." while saving

**Form Validation**:
- Submit button disabled when:
  - Text field is blank
  - Report is being submitted

---

#### `AdminLoginScreen.kt`
**Purpose**: Login form for administrators.

**Features**:
- Email and password fields
- Password is masked (dots instead of text)
- Shows error messages for incorrect credentials
- Loading state while logging in

**Security**: Uses Firebase Authentication for secure login.

---

#### `EditStatusScreen.kt`
**Purpose**: Allows admin to update a water source's status.

**Features**:
- Shows current status with color badge
- Radio buttons to select new status
- Save button (disabled if status unchanged)
- Updates timestamp automatically when saved

**Admin-only**: Only accessible when logged in as admin.

---

#### `AddWaterSourceScreen.kt`
**Purpose**: Form for admins to add new water sources.

**Features**:
- Required fields: Name, Type, Location, Status
- Optional fields: Latitude, Longitude
- Type dropdown: Borehole, Kiosk, Dam
- Status dropdown: Available, Not Available
- Form validation with error messages
- Scrollable form for smaller screens

**Form Fields**:

| Field | Type | Required | Example |
|-------|------|----------|---------|
| Name | Text | Yes | "Kilungu Borehole 2" |
| Type | Dropdown | Yes | Borehole/Kiosk/Dam |
| Location | Text | Yes | "Near Market" |
| Status | Dropdown | Yes | Available/Not Available |
| Latitude | Number | No | -1.6833 |
| Longitude | Number | No | 38.0833 |

---

### **UI Layer - Components**

#### `WaterSourceCard.kt`
**Purpose**: Reusable card component for displaying a water source in the list.

**Features**:
- Color-coded status indicator (green/red dot)
- Shows name, type, location, and status
- Clickable (navigates to detail screen)
- Consistent styling across app

**Why it's separate**: 
- Used in multiple places (list screen)
- Easier to maintain and update
- Promotes code reuse

---

### **UI Layer - Theme**

#### `Color.kt`
**Purpose**: Defines all colors used in the app.

```kotlin
val Blue60 = Color(0xFF1976D2)      // Primary blue (water theme)
val Teal60 = Color(0xFF00897B)      // Secondary teal
val StatusGreen = Color(0xFF4CAF50) // Available status
val StatusRed = Color(0xFFF44336)   // Not available status
```

**Why it exists**: Central color definitions make it easy to change the app's look.

---

#### `Theme.kt`
**Purpose**: Sets up Material 3 theme with color scheme.

**What it does**:
- Defines light color scheme
- Applies colors to Material components
- Wraps entire app in theme

**Usage**: Wraps app content in `MainActivity.kt`:
```kotlin
KituiWaterFinderTheme {
    // App content here
}
```

---

### **Navigation Layer**

#### `NavGraph.kt`
**Purpose**: Defines all screens and navigation routes in the app.

**Routes Defined**:

| Route | Screen | Arguments |
|-------|--------|-----------|
| `"list"` | WaterSourceListScreen | None |
| `"detail/{sourceId}"` | WaterSourceDetailScreen | sourceId |
| `"report/{sourceName}"` | ReportScreen | sourceName |
| `"admin_login"` | AdminLoginScreen | None |
| `"edit_status/{sourceId}"` | EditStatusScreen | sourceId |
| `"add_water_source"` | AddWaterSourceScreen | None |

**How Navigation Works**:

```kotlin
// Navigate TO a screen
navController.navigate("detail/abc123")

// Navigate BACK
navController.popBackStack()

// Navigate back to specific screen
navController.popBackStack("list", inclusive = false)
```

**Arguments**: Data passed between screens (like water source ID)

```kotlin
// Sending
navController.navigate("detail/${waterSource.id}")

// Receiving
val sourceId = backStackEntry.arguments?.getString("sourceId")
```

---

### **Entry Point**

#### `MainActivity.kt`
**Purpose**: App's entry point. Sets up Compose and creates ViewModel.

**What it does**:
1. Creates the activity (Android's basic building block)
2. Sets up Jetpack Compose
3. Creates Material theme
4. Creates one shared ViewModel for entire app
5. Starts navigation with `NavGraph`

**Code Flow**:
```kotlin
onCreate() → setContent() → KituiWaterFinderTheme →
  ViewModel created → NavGraph starts → List screen shows
```

**Lifecycle**: Created when app launches, destroyed when app closes.

---

## 🔥 Firebase Setup

### Firestore Collections

#### `waterSources` Collection
Document structure:
```javascript
{
  name: "Kilungu Borehole",          // string
  type: "Borehole",                  // string
  location: "Near Kilungu Market",   // string
  status: "Available",               // string
  lastUpdated: 1739692800000,        // number (timestamp)
  latitude: -1.6833,                 // number
  longitude: 38.0833                 // number
}
```

#### `reports` Collection
Document structure:
```javascript
{
  sourceName: "Kilungu Borehole",    // string
  issue: "No water available",       // string
  timestamp: 1739692800000           // number
}
```

### Security Rules
Current rules (testing only):
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

⚠️ **For Production**: Implement proper security rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Anyone can read water sources
    match /waterSources/{sourceId} {
      allow read: if true;
      // Only authenticated admins can write
      allow write: if request.auth != null;
    }
    
    // Anyone can read and create reports
    match /reports/{reportId} {
      allow read, create: if true;
      // Only admins can update/delete
      allow update, delete: if request.auth != null;
    }
  }
}
```

### Authentication
- Method: Email/Password
- Admin account: `admin@kituiwater.com`
- No sign-up screen (admins created manually in Firebase Console)

---

## 📦 Dependencies

```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.13.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
implementation("androidx.activity:activity-compose:1.9.1")

// Jetpack Compose
implementation(platform("androidx.compose:compose-bom:2024.06.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-auth-ktx")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
```

### Key Libraries Explained

| Library | Purpose |
|---------|---------|
| `compose-bom` | Bill of Materials - ensures all Compose versions match |
| `firebase-bom` | Bill of Materials - ensures all Firebase versions match |
| `firebase-firestore-ktx` | Kotlin extensions for Firestore (cleaner syntax) |
| `firebase-auth-ktx` | Kotlin extensions for Authentication |
| `navigation-compose` | Navigation library for Compose |
| `lifecycle-viewmodel-compose` | ViewModel integration with Compose |

---

## 🔨 Building & Running

### Prerequisites
- Android Studio (latest version)
- JDK 8 or higher
- Android device or emulator (API 24+)
- Firebase project with Firestore and Authentication enabled

### Setup Steps

1. **Clone the repository**
```bash
git clone <repository-url>
cd KituiWaterFinder
```

2. **Open in Android Studio**
   - File → Open → Select project folder

3. **Add Firebase Configuration**
   - Download `google-services.json` from Firebase Console
   - Place in `app/` folder

4. **Create Admin Account**
   - Go to Firebase Console → Authentication
   - Add user with email/password

5. **Add Sample Data**
   - Go to Firebase Console → Firestore
   - Create `waterSources` collection
   - Add documents with required fields

6. **Sync Gradle**
   - Android Studio should auto-sync
   - Or: File → Sync Project with Gradle Files

7. **Run the App**
   - Click Run button (green play icon)
   - Select device/emulator
   - Wait for app to install and launch

### Build Variants
- **Debug**: For development and testing
- **Release**: For production (requires signing configuration)

---

## 🧪 Testing Checklist

### Manual Testing

**Regular User Flow**:
- [ ] App launches and loads water sources
- [ ] List displays with correct status colors
- [ ] Tap water source → Detail screen shows
- [ ] "View on Map" opens Google Maps
- [ ] "Report Issue" → Form appears
- [ ] Submit report → Returns to list
- [ ] Report saved in Firebase

**Admin Flow**:
- [ ] Tap "Admin Login" → Login screen shows
- [ ] Enter credentials → Login successful
- [ ] Admin badge appears
- [ ] FAB appears (+ button)
- [ ] Tap FAB → Add screen opens
- [ ] Fill form → Add water source
- [ ] New source appears in list
- [ ] Tap Edit icon → Edit screen shows
- [ ] Change status → Status updates
- [ ] Tap logout → Admin features hidden

**Edge Cases**:
- [ ] App works without internet? (Should show error)
- [ ] Empty list handled gracefully
- [ ] Invalid login credentials → Error shown
- [ ] Form validation works (empty fields)
- [ ] Navigation back button works on all screens

---

## 🚀 Future Enhancements

### Suggested Features

1. **Pull to Refresh**
   - Swipe down on list to reload data
   - Library: `accompanist-swiperefresh` or built-in `PullRefreshIndicator`

2. **Search & Filter**
   - Search bar at top of list
   - Filter by: Type, Status, Location
   - Local filtering (data already loaded)

3. **Offline Mode**
   - Cache water sources locally
   - Queue reports when offline, sync when online
   - Library: Firestore has built-in offline persistence

4. **Push Notifications**
   - Notify users when nearby water source status changes
   - Firebase Cloud Messaging (FCM)

5. **Analytics**
   - Track which water sources are most viewed
   - Monitor report patterns
   - Firebase Analytics

6. **Dark Mode**
   - Material 3 makes this easy
   - Already have `LightColorScheme`, add `DarkColorScheme`

7. **Multi-language Support**
   - English + Swahili
   - Use Android string resources

8. **Admin Dashboard**
   - View all reports in one place
   - Mark reports as resolved
   - User statistics

9. **Images**
   - Upload photos of water sources
   - Firebase Storage for image hosting

10. **Accessibility**
    - Screen reader support (TalkBack)
    - Content descriptions for icons
    - Larger text options

### Performance Optimizations

1. **Pagination**
   - Load water sources in batches
   - Firestore `limit()` and `startAfter()` queries

2. **Image Caching**
   - If adding images, use Coil library for caching

3. **Database Indexing**
   - Create Firestore indexes for faster queries
   - Especially if adding search/filter

---

## 📝 Code Conventions

### Naming
- **Files**: PascalCase (e.g., `WaterSourceListScreen.kt`)
- **Classes**: PascalCase (e.g., `WaterSourceViewModel`)
- **Functions**: camelCase (e.g., `loadWaterSources()`)
- **Variables**: camelCase (e.g., `waterSources`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RESULTS`)

### Composables
- Always start with capital letter (Compose requirement)
- Use descriptive names ending in "Screen" for full screens
- Preview functions for development:
```kotlin
@Preview
@Composable
fun WaterSourceCardPreview() {
    WaterSourceCard(...)
}
```

### Documentation
- Use KDoc for public functions
```kotlin
/**
 * Loads all water sources from Firebase.
 * Updates isLoading state during fetch.
 */
fun loadWaterSources() { ... }
```

---

## 🐛 Common Issues & Solutions

### Build Errors

**"google-services.json not found"**
- Download from Firebase Console
- Place in `app/` folder (NOT root folder)

**"Conflicting overloads"**
- Clean project: Build → Clean Project
- Invalidate caches: File → Invalidate Caches → Restart

**"Unresolved reference"**
- Sync Gradle: File → Sync Project with Gradle Files
- Check import statements

### Runtime Errors

**"App crashes on launch"**
- Check Logcat for stack trace
- Common causes:
  - Firebase not initialized (missing google-services.json)
  - Network permission missing in AndroidManifest.xml

**"No data showing"**
- Check internet connection
- Verify Firestore has data
- Check Firestore security rules
- Look for errors in Logcat

**"Login not working"**
- Verify Firebase Authentication is enabled
- Check admin account exists in Firebase Console
- Verify credentials are correct

---

## 📄 License

[Add your license here]

---

## 👥 Contributors

[Add contributor information]

---

## 📧 Contact

For questions or support:
- Create an issue in the repository
- Contact: [your contact info]

---

**Last Updated**: February 2026  
**Version**: 1.0.0  
**Minimum SDK**: 24 (Android 7.0)  
**Target SDK**: 34 (Android 14)
