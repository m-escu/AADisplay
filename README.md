# AADisplay

AADisplay is an LSPosed/Xposed module that lets Android Auto display regular Android applications through a nested `VirtualDisplay`.

> **Safety:** Use only while parked. Follow local laws and do not operate distracting applications while driving.

## Features

- Show supported Android applications on Android Auto.
- Run output through a nested virtual display.
- Configure Android Auto and virtual-display DPI independently.
- Support auto-start, full-screen mode, picture-in-picture, display rotation, immersive mode, and forced square corners.

## Requirements

- Rooted Android device.
- LSPosed or compatible Xposed framework.
- Android Auto.

## Installation

1. Download and install AADisplay APK.
2. Enable AADisplay in LSPosed.
3. Set required app scope in LSPosed.
4. Open AADisplay and configure display options.
5. Connect Android Auto.

## Settings

| Setting | Purpose |
| --- | --- |
| Auto start | Start virtual-display workflow after Android Auto connects. |
| Full screen | Request full-screen presentation on car display. |
| Picture-in-picture | Allow PiP when supported. |
| Display rotation | Set virtual-display orientation. |
| Android Auto DPI | Override Android Auto density when rendering is scaled incorrectly. |
| Virtual display DPI | Override nested display density independently. |
| Forced square corners | Disable rounded-corner treatment. |
| Immersive mode | Hide system bars where supported. |

## Compatibility

OEM ROM changes can alter application startup and display handling. On affected ROMs, DPI correction can fail while core display functionality remains available.

Android Auto updates can change internal hooks. Report failures with Android version, ROM, Android Auto version, LSPosed version, module version, and relevant logcat.

## Building

```bash
./gradlew :aa-display:assembleDebug
```

Debug APK output is under `aa-display/build/outputs/apk/debug/`.

## License

GPL-3.0. See [LICENSE](LICENSE).
