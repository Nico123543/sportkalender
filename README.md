# Sportkalender

Sportkalender ist ein Fußball-Spielplan-Projekt mit drei Frontends:

- Web/PWA (`index.html`)
- Native Android-App in Kotlin + Jetpack Compose (`android-native`)
- Native iOS-App in SwiftUI (`ios-native`)

Es zeigt Spiele (u. a. Bundesliga, DFB-Pokal, Champions League), Wochennavigation und TV-Zuordnungen.

## Projektstruktur

- `index.html`: Web-App (PWA-fähig)
- `manifest.json`, `sw.js`: PWA-Manifest/Service Worker
- `android-native/`: Native Android-App (Gradle/Compose)
- `ios-native/`: Native iOS-App (SwiftUI/Xcode)
- `README-PWA.md`: Hinweise zu PWA-Icons und Test
- `android-native/README.md`: Android-spezifische Build/Release-Hinweise
- `ios-native/README.md`: iOS-spezifische Build/Run-Hinweise

## Features

- Liga- und Saisonauswahl
- Wochenansicht mit Vorwoche/Heute/Nächste
- Spielpaarungen inkl. Anstoßzeit
- Ergebnisanzeige (falls verfügbar)
- TV-Mapping auf Basis gepflegter Regeln
- PDF-Export in der Web-App

## Web-App lokal starten

```bash
cd /Users/nico/Documents/Development/sportkalender
python3 -m http.server 8000
```

Dann im Browser öffnen:

- [http://localhost:8000/index.html](http://localhost:8000/index.html)

## Android-App bauen

```bash
cd /Users/nico/Documents/Development/sportkalender/android-native
./gradlew assembleDebug
```

Oder Release:

```bash
./gradlew assembleRelease
```

APK-Ausgaben:

- Debug: `android-native/app/build/outputs/apk/debug/`
- Release: `android-native/app/build/outputs/apk/release/`

## iOS-App öffnen und starten

```bash
cd /Users/nico/Documents/Development/sportkalender/ios-native
open SportkalenderiOS.xcodeproj
```

Dann in Xcode Scheme `SportkalenderiOS` auf Simulator oder iPhone starten.

## Android Release-Signing

- Für lokale Release-Performance-Tests kann ein installierbarer Release-Build mit Debug-Signing erzeugt werden.
- Für produktive Signierung:
  1. `android-native/keystore.properties.example` nach `android-native/keystore.properties` kopieren
  2. echte Keystore-Werte eintragen

`keystore.properties` ist absichtlich in `.gitignore`.

## Lizenz

Dieses Repository steht unter MIT:

- `LICENSE`

## Third-Party / Datenhinweise

Nicht alles in der Laufzeitumgebung fällt unter MIT. Details:

- `THIRD_PARTY_NOTICES.md`

Kurzfassung:

- Externe Libraries/Fonts haben eigene Lizenzen (z. B. jsPDF, Google Fonts)
- Externe Datenquellen (z. B. OpenLigaDB) unterliegen ihren jeweiligen Nutzungsbedingungen
- Marken-/Vereinsnamen verbleiben bei den jeweiligen Rechteinhabern
