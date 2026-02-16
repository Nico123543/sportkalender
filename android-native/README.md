# Sportkalender Native (Android)

Native Android-App in Kotlin + Jetpack Compose, basierend auf deiner Webversion.

## Enthaltene Funktionen

- Liga-Auswahl (BL1, BL2, BL3, DFB, UCL)
- Saison-Auswahl (2025/26, 2024/25, 2023/24)
- Wochen-Navigation (Vorwoche, Heute, Nächste)
- Tagesweise Darstellung der Spiele
- Ergebnisanzeige (wenn vorhanden)
- TV-Mapping nach Wochentag/Uhrzeit (vereinfachte Rechte-Regeln)
- Live-Daten von OpenLigaDB

## Starten

1. Android Studio öffnen.
2. Projektordner auswählen: `android-native`.
3. Gradle Sync abwarten.
4. App auf Emulator oder Gerät starten.

## Release Builds

### Lokaler Release-Test (installierbar)

Ohne `keystore.properties` wird der `release` Build automatisch mit der Debug-Signatur erstellt.
Das ist praktisch für lokale Performance-Tests auf Geräten.

Befehl:

```bash
./gradlew assembleRelease
```

### Produktive Release-Signatur

1. Lege eine Keystore-Datei an (oder nutze eine vorhandene).
2. Kopiere `keystore.properties.example` nach `keystore.properties`.
3. Trage echte Werte ein:
   - `storeFile`
   - `storePassword`
   - `keyAlias`
   - `keyPassword`

Dann nutzt der `release` Build automatisch diese Signatur.

## Wichtiger Hinweis zu Lint

In dieser Umgebung ist `lint` für Release aktuell deaktiviert (`checkReleaseBuilds=false`),
da es mit der lokalen JDK-/AGP-Kombination zu einem bekannten Fehler kommen kann.

Für CI/Store-Deployment solltest du Lint wieder aktivieren, sobald Toolchain-Versionen sauber abgestimmt sind.

## Lizenz

Der Quellcode steht unter MIT:
`/Users/nico/Documents/Development/sportkalender/LICENSE`

Hinweise zu Drittanbieter-Lizenzen und externen Datenquellen:
`/Users/nico/Documents/Development/sportkalender/THIRD_PARTY_NOTICES.md`
