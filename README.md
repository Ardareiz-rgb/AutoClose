# AutoClose

Android app project for a manually started Roblox countdown with a compact ongoing notification.

## Important Android limitation
Without root, Shizuku/ADB, device-owner privileges, or a privileged system app, a normal Android app cannot force-stop another app or remove its process from Recents. This project therefore uses an optional built-in Accessibility Service to send the Android HOME action when the timer expires. That returns the phone to the launcher, but Roblox may remain in the background/Recents. It does **not** claim to kill Roblox.

The Accessibility Service is part of AutoClose (no separate app required), but Android requires the user to enable it manually in Accessibility settings. If it is not enabled, the timer ends and AutoClose posts a small completion notification instead.

## Build with GitHub Actions
1. Create a GitHub repository.
2. Upload/push all files in this project to the repository root.
3. Open **Actions**.
4. Run **Build AutoClose APK** (or push to `main`).
5. Download the `AutoClose-debug-apk` artifact.
6. Install `app-debug.apk` on the phone.

## Use
1. Open AutoClose and allow notifications.
2. Enter duration in minutes.
3. Tap **Başlat**.
4. Use the small ongoing notification to see the countdown or stop it.
5. For automatic return to Home at expiry, enable AutoClose under Android Accessibility settings.

Package: `com.roblox.client`
