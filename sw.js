const CACHE_NAME = 'sportkalender-v1';
const urlsToCache = [
  './',
  './index.html',
  './manifest.json'
];

// Service Worker installieren - Ressourcen cachen
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(urlsToCache))
  );
  self.skipWaiting();
});

// Service Worker aktivieren - alte Caches entfernen
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheName !== CACHE_NAME) {
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
  self.clients.claim();
});

// Fetch-Requests abfangen und aus Cache bedienen
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        // Cache hit - Rückgabe aus Cache
        if (response) {
          return response;
        }

        // Clone Request
        const fetchRequest = event.request.clone();

        return fetch(fetchRequest).then(response => {
          // Prüfen ob gültige Response
          if (!response || response.status !== 200 || response.type !== 'basic') {
            return response;
          }

          // Clone Response
          const responseToCache = response.clone();

          caches.open(CACHE_NAME)
            .then(cache => {
              cache.put(event.request, responseToCache);
            });

          return response;
        }).catch(() => {
          // Offline-Fallback
          return caches.match('./index.html');
        });
      })
  );
});
