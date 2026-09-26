---
name: WakeBrain
colors:
  surface: '#121316'
  surface-dim: '#121316'
  surface-bright: '#38393c'
  surface-container-lowest: '#0d0e11'
  surface-container-low: '#1b1b1f'
  surface-container: '#1f1f23'
  surface-container-high: '#292a2d'
  surface-container-highest: '#343538'
  on-surface: '#e3e2e6'
  on-surface-variant: '#cbc3d7'
  inverse-surface: '#e3e2e6'
  inverse-on-surface: '#2f3034'
  outline: '#958ea0'
  outline-variant: '#494454'
  surface-tint: '#d0bcff'
  primary: '#d0bcff'
  on-primary: '#3c0091'
  primary-container: '#a078ff'
  on-primary-container: '#340080'
  inverse-primary: '#6d3bd7'
  secondary: '#c0c1ff'
  on-secondary: '#1000a9'
  secondary-container: '#3131c0'
  on-secondary-container: '#b0b2ff'
  tertiary: '#cebdff'
  on-tertiary: '#381385'
  tertiary-container: '#9b7fed'
  on-tertiary-container: '#31057e'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e9ddff'
  primary-fixed-dim: '#d0bcff'
  on-primary-fixed: '#23005c'
  on-primary-fixed-variant: '#5516be'
  secondary-fixed: '#e1e0ff'
  secondary-fixed-dim: '#c0c1ff'
  on-secondary-fixed: '#07006c'
  on-secondary-fixed-variant: '#2f2ebe'
  tertiary-fixed: '#e8ddff'
  tertiary-fixed-dim: '#cebdff'
  on-tertiary-fixed: '#21005e'
  on-tertiary-fixed-variant: '#4f319c'
  background: '#121316'
  on-background: '#e3e2e6'
  surface-variant: '#343538'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 64px
    fontWeight: '700'
    lineHeight: 72px
    letterSpacing: -0.03em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 52px
    fontWeight: '700'
    lineHeight: 60px
    letterSpacing: -0.03em
  display-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 44px
    fontWeight: '700'
    lineHeight: 52px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.03em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system establishes a high-focus, clarity-first dark environment engineered specifically for sleep inertia and morning grogginess. The experience centers around decisive cognitive activation: visual noise is stripped away to provide immediate readability, high contrast, and frictionless interaction under low-light and early-morning conditions.

The visual style blends **Material Design 3 (M3)** mechanics with a refined, tactile minimalism. Surfaces rely on disciplined tonal containment rather than flashy effects. Interaction elements utilize punchy, vibrant accents against deep charcoal foundations to direct cognitive attention without blinding the user in the dark. It evokes discipline, competence, and reliable utility—empowering users to wake up smoothly and conquer their morning routines.

## Colors

The palette operates on pure Material You dark-mode surface role mechanics, prioritizing accessible contrast ratios (WCAG AAA for clock displays and active dismiss paths).

### Surfaces & Foundations
- **Background / Canvas**: `#121316` (deep neutral charcoal, minimizing glare in dark environments)
- **Surface Dim / Baseline**: `#0E0F12`
- **Surface Container Low**: `#1A1B20` (list items, inactive rows)
- **Surface Container**: `#22232A` (standard cards, module backings)
- **Surface Container High**: `#2A2C35` (elevated dialogs, modal bottom sheets, challenge containers)
- **Surface Container Highest**: `#323540` (hover/pressed surface states and toggle tracks)

### Accents & Brands
- **Primary (`#8B5CF6`)**: Electric violet, designated for primary wake actions, critical FABs, and active toggle thumbs.
- **On-Primary (`#FFFFFF`)**: High-contrast pure white for text/icons on primary fills.
- **Primary Container (`#3B1B7A`)**: Deep plum tone for tonal button fills and active state indicators.
- **On-Primary Container (`#DDD6FE`)**: Soft lilac text sitting on primary containers.
- **Secondary (`#6366F1`)**: Indigo/lavender accent for secondary metrics, time tags, and streak badges.
- **Tertiary (`#A78BFA`)**: Subtle soft lavender for auxiliary badges, icon tints, and step completions.

### Semantic & Feedback
- **Success (`#10B981`)**: Vivid mint emerald used for successful mission/challenge completion and armed status confirmation.
- **Success Container (`#064E3B`)**: Background for cleared alarm banners.
- **Error (`#EF4444`)**: Crisp high-legibility red used exclusively for missed alarms, failed verification challenges, and critical alerts.
- **Error Container (`#7F1D1D`)**: Background containment for urgent alerts.

### Content & Text
- **On-Surface (Text Primary)`: `#F9FAFB` (optical clarity for critical alarm digits and primary headers)
- **On-Surface Variant (Text Secondary)`: `#9CA3AF` (labels, snooze limits, mission rules)
- **Outline / Border**: `#374151` (subtle framing without introducing visual friction)
- **Outline Variant**: `#262933` (subtle divider lines between schedule rows)

## Typography

The type scale is calibrated for instant recognition under blurred or squinted vision. `Plus Jakarta Sans` provides geometric legibility with humanist balance, featuring tall x-heights and open apertures that prevent character confusion.

### Typographic Hierarchy & Behavior
- **Clock Display (`display-lg`, `display-lg-mobile`)**: Used for the central alarm clock readout. Numbers must render with tabular figures (`font-variant-numeric: tabular-nums`) to prevent horizontal jitter during countdowns and time updates.
- **Challenge Readouts (`display-md`, `headline-lg`)**: Utilized for anti-snooze math problems, memory sequence digits, and captcha puzzles to ensure zero ambiguity during cognitive effort.
- **Labels & Badges (`label-lg`, `label-md`)**: Rendered in medium and semi-bold weights with slight letter-spacing to provide clear visual cues for snooze parameters, AM/PM indicators, and sensor triggers.

## Layout & Spacing

The layout utilizes an 8-point spatial rhythm strictly mapped to handheld ergonomics. The primary screen is treated as an intentional single-viewport canvas optimized for thumbs, prioritizing the bottom two-thirds of the screen.

### Grid & Margins
- **Mobile Handheld (up to 599dp)**: 4-column layout with `16px` (`margin`) outer screen padding and `16px` (`gutter`) column spacing. All dismiss and snooze touch targets must span a minimum touch dimension of 48dp vertically.
- **Tablet / Landscape Foldable (600dp+)**: 8-column layout with `24px` margins. Alarms stack into a responsive dual-column grid separating the next scheduled ring from routine lists.

### Vertical Stacking Rules
- Space between independent alarm cards: `12px` (`space-sm` + `space-xs`).
- Padding inside standard alarm cards: `16px` (`space-md`).
- Bottom Action Floating Container padding: `24px` (`space-lg`) above the Android navigation bar.

## Elevation & Depth

This design system avoids blurry skeuomorphic drop shadows and glowing neon halogens. Instead, depth is articulated through **M3 Tonal Surface Elevation**, where light surfaces represent closer proximity to the user.

1. **Level 0 (Base / Canvas - `#121316`)**: Pure flat foundation for the application backdrop and empty list states.
2. **Level 1 (Card Default - `#1A1B20`)**: Inactive alarm modules and historical logs. Outlined with a razor-thin border (`1px solid #262933`).
3. **Level 2 (Card Active / Expanded - `#22232A`)**: Armed alarms, active sound pickers, and day-of-week matrices. 
4. **Level 3 (Modal Sheets & Floating Overlays - `#2A2C35`)**: Challenge sheets, dismiss puzzles, and bottom configuration sheets. Elevated using a sharp ambient scrim (`rgba(0, 0, 0, 0.6)`) and a subtle boundary line (`1px solid #374151`) on top edges.
5. **Level 4 (Critical Active Triggering - `#2A2C35`)**: The full-screen alarm ringing mode. Backed by solid opaque containment with zero ambient blur to eliminate GPU latency during wake execution.

## Shapes

The design system embraces Material Design 3 rounded curvature, utilizing a structured corner hierarchy:

- **Large Surfaces (Alarm Cards, Sheets, Dialogs)**: `16px` to `20px` border radius (`rounded-lg` / `rounded-xl`). Creates approachable, non-hostile contours that feel comfortable to interact with upon waking.
- **Interactive Controls (Buttons, Inputs, Mission Tiles)**: `12px` to `16px` border radius (`rounded-md` / `rounded-lg`).
- **Small Utilities & Status Badges (Chips, AM/PM Switchers, Weekday Circles)**: Full pill radius (`9999px`) to maintain semantic separation from structural content cards.

## Components

### Buttons
- **Filled Primary (Dismiss / Main Action)**: Background `#8B5CF6`, text `#FFFFFF`, height `56px`, corner radius `16px`. Active interaction states trigger `#7C3AED`. Touch target reaches full width in ringing states.
- **Tonal (Snooze / Secondary Challenge)**: Background `#2A2C35`, text `#DDD6FE`, border `1px solid #374151`, height `48px`, corner radius `14px`.
- **Text Button (Cancel / Skip)**: Transparent background, text `#9CA3AF`, hover state `#1A1B20`.

### Cards (Alarm Modules)
- Structured using M3 Surface Containers (`#1A1B20` inactive, `#22232A` active).
- Padding: `16px`.
- Layout: Large tabular time readout on the left, primary quick-toggle M3 switch on the right. Sub-labels (e.g., "Math Challenge • 3 steps", "Mon, Tue, Wed") render in `body-md` under the time display.
- Border: `1px solid #262933`. Active alarms gain an accent highlight via an inner border or primary indicator tick.

### Switches (M3 Style)
- Track: Width `52px`, height `32px`, corner radius `16px`.
- Inactive state: Track `#2A2C35`, outline `#374151`, thumb `#9CA3AF` (16px diameter).
- Active state: Track `#8B5CF6`, outline transparent, thumb `#FFFFFF` (24px diameter, containing a subtle `#7C3AED` checkmark icon).

### Chips & Day Pickers
- **Day Selector Circles**: 36px circular pill targets. Inactive: `#1A1B20` background with `#9CA3AF` label. Active: `#8B5CF6` background with `#FFFFFF` label.
- **Challenge Metadata Chips**: Height `28px`, padding `0 10px`, radius `8px`. Surface `#2A2C35`, text `#A78BFA`, font `label-sm`.

### Input Fields & Keypads (Challenge Interactions)
- **Math / Code Display**: Container `#0E0F12`, border `2px solid #8B5CF6`, typography `display-md` centered.
- **Numpad Buttons**: Square-profile cards with `16px` radius, background `#22232A`, active press state `#2A2C35`, label `headline-md` `#F9FAFB`. Zero haptic lag.

### Selection Controls (Radio / Checkbox)
- Checkbox: 20x20dp with 4dp corner radius. Checked background `#8B5CF6` with white checkmark vector.
- Radio: Concentric circles with active center dot in `#8B5CF6`.