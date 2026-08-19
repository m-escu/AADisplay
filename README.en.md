# AADisplay

AADisplay is an LSPosed/Xposed module that lets Android Auto display regular Android applications through a nested `VirtualDisplay`.

## Requirements

- Rooted Android device
- LSPosed or compatible Xposed framework
- Android Auto

## Installation

1. Install module APK.
2. Enable module in LSPosed.
3. Select required target apps in LSPosed scope.
4. Open AADisplay settings.
5. Connect Android Auto.

## Settings

- Auto start. Start virtual display automatically after Android Auto connects.
- Full screen. Request full-screen output on car display.
- Picture-in-picture. Allow PiP mode.
- Display rotation. Set output rotation.
- Android Auto DPI. Override Android Auto density when required.
- Virtual display DPI. Override nested virtual-display density.
- Forced square corners. Disable rounded-corner treatment.
- Immersive mode. Hide system bars where supported.

## Compatibility

OEM ROM customizations can change application startup or display behavior. DPI correction may fail on affected ROMs, while core display behavior can remain functional.

## Safety

Operate only when parked. Follow local driving laws.

## License

GPL-3.0.
