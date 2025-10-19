# UI Components List

This document lists all reusable UI components in the Jagrati Android application.

---

## 📄 PersonCard.kt

### PersonCard
A reusable card component for displaying person information (students/volunteers) with profile avatar, title, subtitle, and extra information.

**Data Class:**
- `PersonCardData` - Data model containing:
  - `title: String` - Person's name
  - `subtitle: String` - Secondary info (village for students, roll number for volunteers)
  - `extra: String` - Additional info (group for students, batch for volunteers)
  - `profileImageUrl: String?` - Optional profile image URL

**Extension Functions:**
- `Student.toPersonCardData(villageName: String, groupName: String): PersonCardData` - Converts Student to card data
- `Volunteer.toPersonCardData(): PersonCardData` - Converts Volunteer to card data

**Parameters:**
- `data: PersonCardData` - Card data to display
- `onClick: () -> Unit` - Callback when card is clicked
- `modifier: Modifier` - Styling modifier (default: Modifier)

**Features:**
- Uses ProfileAvatar for profile pictures
- Supports text overflow with ellipsis
- Material 3 card styling with elevation
- Click interaction support
- Consistent layout for both students and volunteers
- Primary color accent for extra information
- Responsive design with proper spacing

---

## 📄 ScreenHeader.kt

### ScreenHeader
A customizable header component with back navigation and trailing content.

**Parameters:**
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `onBackPress: () -> Unit` - Callback for back button press
- `title: String` - Header title text
- `isTitleClickable: Boolean` - Whether the title is clickable (default: false)
- `onTitleClick: () -> Unit` - Callback when title is clicked (default: {})
- `trailingContent: @Composable () -> Unit` - Composable content for the trailing section

**Features:**
- Back button with icon
- Bold title text with optional click interaction
- Flexible trailing content slot
- Proper alignment and spacing

---

## 📄 StudentList.kt

### StudentListRow
A list item component for displaying student information with an image, heading, subheading, and side text.

**Parameters:**
- `image: Bitmap?` - Student profile image (nullable)
- `heading: String` - Primary text (e.g., student name)
- `subheading: String` - Secondary text (e.g., class or ID)
- `sideText: String` - Text displayed on the right side (e.g., status)
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `onClick: () -> Unit` - Callback when the row is clicked

**Features:**
- Displays student profile image or default account icon
- Rounded corner image styling
- Two-line text layout (heading and subheading)
- Side text for additional information
- Click interaction support
- Horizontal divider for list separation

---

## 📄 TextFields.kt

### 1. EmailInput
Email input field with validation support.

**Parameters:**
- `email: String` - Current email value
- `onEmailChange: (String) -> Unit` - Callback when email changes
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `isError: Boolean` - Error state flag (default: false)
- `errorMessage: String?` - Error message to display (default: null)
- `imeAction: ImeAction` - Keyboard IME action (default: ImeAction.Next)
- `onImeAction: () -> Unit` - Callback for IME action (default: {})

**Features:**
- Email icon leading indicator
- Email keyboard type
- Error state with custom message
- Single line input
- Keyboard action support

---

### 2. PasswordInput
Password input field with visibility toggle.

**Parameters:**
- `password: String` - Current password value
- `onPasswordChange: (String) -> Unit` - Callback when password changes
- `label: String` - Input field label (default: "Password")
- `isPasswordVisible: Boolean` - Password visibility state
- `onTogglePasswordVisibility: () -> Unit` - Toggle visibility callback
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `isError: Boolean` - Error state flag (default: false)
- `errorMessage: String?` - Error message to display (default: null)
- `imeAction: ImeAction` - Keyboard IME action (default: ImeAction.Done)
- `onImeAction: () -> Unit` - Callback for IME action (default: {})

**Features:**
- Lock icon leading indicator
- Show/Hide password toggle button
- Password masking/unmasking
- Error state with custom message
- Password keyboard type
- Single line input
- Keyboard action support

---

### 3. PrimaryButton
Primary action button with loading state.

**Parameters:**
- `text: String` - Button text
- `onClick: () -> Unit` - Click callback
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `isLoading: Boolean` - Loading state flag (default: false)
- `enabled: Boolean` - Enabled state flag (default: true)

**Features:**
- Full width by default
- Loading spinner indicator
- Disabled state when loading
- Material Design 3 styling

---

### 4. TextLinkButton
Text-style button for secondary actions.

**Parameters:**
- `text: String` - Button text
- `onClick: () -> Unit` - Click callback
- `modifier: Modifier` - Styling modifier (default: Modifier)

**Features:**
- Text-only button style
- Minimal visual weight
- Material Design 3 styling

---

### 5. GoogleSignInButton
Outlined button for Google Sign-In with loading state.

**Parameters:**
- `onClick: () -> Unit` - Click callback
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `isLoading: Boolean` - Loading state flag (default: false)
- `enabled: Boolean` - Enabled state flag (default: true)

**Features:**
- Google Sign-In branding
- Loading spinner indicator
- Outlined button style
- Light gray border
- Disabled state when loading
- Full width by default

---

## 📄 GreetingCard.kt

### 1. GreetingCard
A beautiful greeting card component with user profile, greeting message, and action buttons.

**Parameters:**
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `userName: String` - User's first name
- `userEmail: String?` - User's email address (nullable)
- `profileImageUrl: String?` - Optional profile image URL (default: null)
- `greeting: String` - Greeting message (default: "Hello")
- `showSettingsButton: Boolean` - Whether to show settings button (default: true)
- `showSignOutButton: Boolean` - Whether to show sign out button (default: true)
- `onSettingsClick: () -> Unit` - Callback for settings button (default: {})
- `onSignOutClick: () -> Unit` - Callback for sign out button (default: {})

**Features:**
- Horizontal gradient background (primary to primaryContainer)
- Profile avatar (image or letter fallback)
- Customizable greeting message
- User name and email display
- Optional settings and sign out buttons
- Elevated card with large shape
- Responsive layout

---

### 2. ProfileAvatar
Profile avatar component that displays image or first letter fallback.

**Parameters:**
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `userName: String` - User's name for fallback letter
- `profileImageUrl: String?` - Optional profile image URL (default: null)
- `size: Dp` - Size of the avatar (default: 60.dp)

**Features:**
- Circular avatar shape
- Profile image loading with Coil
- First letter fallback with dynamic font size
- Secondary container background
- Customizable size

---

## 📄 ActionCard.kt

### 1. ActionCard
Clickable card component for navigation actions.

**Parameters:**
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `title: String` - Card title
- `description: String` - Card description
- `icon: ImageVector?` - Optional leading icon (default: null)
- `iconTint: Color` - Icon tint color (default: MaterialTheme.colorScheme.primary)
- `onClick: () -> Unit` - Click callback

**Features:**
- Clickable card with ripple effect
- Optional leading icon with customizable tint
- Title and description layout
- Forward arrow indicator
- Elevation and surface styling
- Full width layout

---

### 2. InfoCard
Informational card component for displaying non-clickable information.

**Parameters:**
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `title: String` - Card title
- `description: String` - Card description
- `icon: ImageVector?` - Optional leading icon (default: null)
- `iconTint: Color` - Icon tint color (default: MaterialTheme.colorScheme.primary)
- `containerColor: Color` - Card background color (default: MaterialTheme.colorScheme.secondaryContainer)

**Features:**
- Non-clickable information display
- Optional leading icon
- Customizable background color
- Title and description layout
- Elevated card styling
- Full width layout

---

## 📄 DrawerComponents.kt

### 1. DrawerHeader
Navigation drawer header with user profile display.

**Parameters:**
- `userName: String` - User's name
- `userEmail: String?` - User's email address (nullable)
- `profileImageUrl: String?` - Optional profile image URL
- `modifier: Modifier` - Styling modifier (default: Modifier)

**Features:**
- Primary container background
- Large profile avatar (80dp)
- User name and email display
- Centered layout
- Material Design 3 styling

---

### 2. DrawerItem
Navigation drawer item component for menu options.

**Parameters:**
- `label: String` - Item label text
- `icon: Int` - Drawable resource ID for the icon (using @DrawableRes)
- `onClick: () -> Unit` - Click callback
- `modifier: Modifier` - Styling modifier (default: Modifier)
- `iconTint: Color` - Icon tint color (default: MaterialTheme.colorScheme.onSurface)

**Features:**
- Clickable menu item
- Icon and text layout
- Customizable icon tint (e.g., error color for logout)
- Proper spacing and padding
- Material Design 3 styling

---

### 3. DrawerDivider
Visual separator for drawer sections.

**Parameters:**
- `modifier: Modifier` - Styling modifier (default: Modifier)

**Features:**
- Horizontal divider
- Outline variant color
- Proper padding for visual separation

---

## 📊 Component Summary

| File | Component Count | Primary Use Case |
|------|----------------|------------------|
| PersonCard.kt | 1 | Person information card |
| ScreenHeader.kt | 1 | Navigation headers |
| StudentList.kt | 1 | List item display |
| TextFields.kt | 5 | Form inputs & actions |
| GreetingCard.kt | 2 | User greeting & profile |
| ActionCard.kt | 2 | Action & info cards |
| DrawerComponents.kt | 3 | Navigation drawer |
| **Total** | **15** | |

---

## 🎨 Preview Components

### TextFields.kt Previews:
- `EmailInputPreview()`
- `PasswordInputPreview()`
- `PrimaryButtonPreview()`
- `GoogleSignInButtonPreview()`
- `TextLinkButtonPreview()`

### GreetingCard.kt Previews:
- `GreetingCardPreview()`
- `ProfileAvatarPreview()`

### ActionCard.kt Previews:
- `ActionCardPreview()`
- `InfoCardPreview()`

### DrawerComponents.kt Previews:
- `DrawerHeaderPreview()`
- `DrawerItemPreview()`

---

## 📝 Notes

- All components follow Material Design 3 guidelines
- Components use Jetpack Compose
- Error handling is built into input fields
- Loading states are available for async operations
- Components are fully customizable via Modifier parameters
- Preview functions are available for UI development
- Gradient backgrounds used for enhanced visual appeal
- Coil library used for async image loading
- Drawer components use R.drawable for icons instead of Icons.Default

---

**Last Updated:** October 12, 2025
