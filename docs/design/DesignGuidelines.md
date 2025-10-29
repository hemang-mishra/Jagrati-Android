Help me improve the design of the screen. You have complete freedom to entirely revamp styles.
Improve ui and ux without changing the implementation
make sure ui is adaptable for tablets and in landscape modes.

Avoid writing comments in code.
Here is the screen code:

Preview guidelines:
@Preview(showBackground = true,)  
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
use this annotation for any preiview function and use JagratiAndroidTheme {} always

Icons guidelines:
whenever you use any icons use only painter
like painter = painterResource(R.drawable.ic_icon)

Here is the design guidelines.
### Jagrati Brand Theme Guidelines for UI Development

## Brand Overview

Jagrati is an social intiative by IIITians to teach unprivileged of nearby villages. This app is meant mostly for volunteers who are students of IIITDMJ. with a vibrant, modern design system built around accessibility, contrast, and visual hierarchy. The brand emphasizes warmth, energy, and learning through its carefully curated color palette and typography.

## Color System & Usage

### Primary Brand Colors

The Jagrati color system follows a **60-30-10 color distribution rule**:

- **Primary (60%)**: Orange (`#F17E01`) - Main brand color, used for primary actions, logos, and key highlights
- **Secondary (30%)**: Purple (`#AA9FF8`) - High contrast complement, used for secondary actions and important elements
- **Accent Colors (10%)**: Yellow (`#FFD233`) and Turquoise (`#3FB8AF`) - Supporting colors for variety and batch differentiation

### Supporting Colors

- **Brown (`#A65700`)**: Warm neutral for cards and containers
- **Dark Brown (`#332118`)**: Dark theme surfaces and deep contrast elements
- **Light Gray (`#FAF7F2`)**: Light backgrounds and subtle containers
- **Black (`#121212`)**: Dark theme background and high contrast text

## **CRITICAL: Color Access Rules**

### ✅ CORRECT - Use Theme Colors

```kotlin
// Access Material 3 theme colors
MaterialTheme.colorScheme.primary        // Orange in light, adjusted orange in dark
MaterialTheme.colorScheme.secondary      // Purple variations
MaterialTheme.colorScheme.tertiary       // Yellow/Turquoise based on theme
MaterialTheme.colorScheme.surface        // Theme-appropriate surface colors
MaterialTheme.colorScheme.background     // Theme-appropriate backgrounds

// Access custom brand colors
JagratiThemeColors.orange               // Always brand orange
JagratiThemeColors.purple               // Always brand purple
JagratiThemeColors.yellow               // Always brand yellow
JagratiThemeColors.turquoise            // Always brand turquoise
JagratiThemeColors.brown                // Always brand brown

// Access batch colors (theme-aware)
JagratiThemeColors.batchColors          // Returns List<Color> based on current theme
```

### ❌ INCORRECT - Never Hardcode Colors

```kotlin
// NEVER DO THIS
Color(0xFFF17E01)                       // Hardcoded hex values
Color.Orange                            // Generic colors
"#F17E01"                              // String hex values
```

### Theme Wrapper

Always wrap your app content with:

```kotlin
JagratiAndroidTheme {
    // Your UI content here
}
```

## UI Component Guidelines

### Buttons

- **Primary Buttons**: Use `MaterialTheme.colorScheme.primary` (Orange)
- **Secondary Buttons**: Use `MaterialTheme.colorScheme.secondary` (Purple)
- **Outlined Buttons**: Use `MaterialTheme.colorScheme.secondary` for border and text

### Cards and Containers

- **Primary Cards**: Use `MaterialTheme.colorScheme.surface` with `MaterialTheme.colorScheme.primary` accents
- **Batch/Category Cards**: Use `JagratiThemeColors.batchColors[index]` for dynamic color assignment
- **Information Cards**: Alternate between orange and purple using theme colors

### Text Hierarchy

- **Headers**: Use `MaterialTheme.colorScheme.primary` for emphasis
- **Subheaders**: Use `MaterialTheme.colorScheme.secondary`
- **Body Text**: Use `MaterialTheme.colorScheme.onSurface`
- **Captions**: Use `MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)`

### Backgrounds

- **Main Background**: `MaterialTheme.colorScheme.background`
- **Card Backgrounds**: `MaterialTheme.colorScheme.surface`
- **Elevated Surfaces**: `MaterialTheme.colorScheme.surfaceVariant`

## Batch/Category Color Distribution

For dividing content into batches or categories, use the batch color system:

```kotlin
val batchColors = JagratiThemeColors.batchColors
val assignedColor = batchColors[itemIndex % batchColors.size]

// Use for:
// - Student batch identification
// - Course categories
// - Progress indicators
// - Status badges
// - Organizational elements
```

This ensures:

- **Visual variety** without monotony
- **Consistent cycling** through brand colors
- **Theme-appropriate contrast** in both light and dark modes

## Typography Usage

### Accessing Typography

```kotlin
// Material 3 typography (preferred)
MaterialTheme.typography.headlineLarge
MaterialTheme.typography.titleMedium
MaterialTheme.typography.bodyLarge

// Custom Jagrati typography for special cases
JagratiTypography.logoText              // For brand logos
JagratiTypography.batchTitle            // For batch/category titles
JagratiTypography.cardTitle             // For card headers
```

## Dark/Light Theme Behavior

The theme automatically adapts:

- **Light Theme**: Full brand colors with high contrast
- **Dark Theme**: Adjusted brand colors with appropriate opacity for readability
- **Batch Colors**: Separate arrays optimized for each theme
- **Surface Colors**: Material 3 compliant surface elevation system

## Design Principles

### Visual Hierarchy

1. **Orange** draws primary attention (CTAs, important actions)
2. **Purple** provides secondary focus (navigation, secondary actions)
3. **Yellow/Turquoise** add variety and support (badges, indicators, categories)
4. **Neutral colors** provide structure and readability

### Contrast & Accessibility

- All color combinations meet **WCAG AA contrast requirements**
- **Color is never the only indicator** - use icons, text, or patterns alongside color
- **Theme-aware colors** ensure readability in both light and dark modes

### Consistency Rules

- **Never mix hardcoded colors** with theme colors
- **Always use theme-aware color access methods**
- **Maintain 60-30-10 distribution** across major UI sections
- **Use batch colors systematically** for organizational elements

## Implementation Checklist

Before creating any UI component, ensure:

- [ ] All colors accessed through `MaterialTheme.colorScheme.*` or `JagratiThemeColors.*`
- [ ] No hardcoded hex values or `Color.*` constants used
- [ ] Batch colors used for multiple similar items (lists, categories, etc.)
- [ ] Component works in both light and dark themes
- [ ] Orange used for primary actions, Purple for secondary emphasis
- [ ] Typography accessed through `MaterialTheme.typography.*` or `JagratiTypography.*`
- [ ] Proper contrast maintained for accessibility

## Example Implementation

```kotlin
@Composable
fun JagratiBatchCard(
    batchName: String,
    studentCount: Int,
    batchIndex: Int,
    onClick: () -> Unit
) {
    val batchColors = JagratiThemeColors.batchColors
    val batchColor = batchColors[batchIndex % batchColors.size]
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(2.dp, batchColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = batchName,
                style = JagratiTypography.batchTitle,
                color = batchColor
            )
            Text(
                text = "$studentCount students",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
```

This comprehensive system ensures consistent, accessible, and beautiful UIs that properly represent the Jagrati brand across all components and themes.