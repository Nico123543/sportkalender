# PWA Icons generieren

Die PWA benötigt Icons in den Größen 192x192 und 512x512 Pixel.

## Methode 1: Browser (empfohlen)

1. Öffne `generate-icons.html` im Browser
2. Klicke auf die Buttons "Icon 192x192" und "Icon 512x512"
3. Die Icons werden automatisch heruntergeladen
4. Speichere sie als `icon-192.png` und `icon-512.png` im gleichen Ordner wie index.html

## Methode 2: Eigene Icons

Du kannst auch eigene Icons verwenden:
- Erstelle zwei PNG-Dateien: `icon-192.png` (192x192px) und `icon-512.png` (512x512px)
- Ein simples Design mit blauem Hintergrund (#2563eb) und einem Fußball-Symbol reicht aus

## PWA Testen

Nachdem die Icons erstellt wurden:
1. Öffne die Seite im Browser (Chrome/Edge empfohlen)
2. Öffne die DevTools (F12)
3. Gehe zum "Application" Tab
4. Prüfe unter "Manifest" und "Service Workers"
5. Die App sollte installierbar sein (Symbol in der Adressleiste)

## Lizenz

Der Quellcode dieses Repositories steht unter MIT:
`/Users/nico/Documents/Development/sportkalender/LICENSE`

Drittanbieter-Lizenzen und externe Datenquellen:
`/Users/nico/Documents/Development/sportkalender/THIRD_PARTY_NOTICES.md`
