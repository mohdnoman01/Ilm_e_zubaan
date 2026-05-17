Large MP4 samples live here so they are not bundled into the Android APK.

Bundling these files under `app/src/main/res/raw` made the debug APK roughly
314 MB and caused `INSTALL_FAILED_INSUFFICIENT_STORAGE` on the emulator. Move a
video back into a packaged resource only after it has been compressed to a
mobile-sized asset, or prefer a remote/on-demand media URL.
