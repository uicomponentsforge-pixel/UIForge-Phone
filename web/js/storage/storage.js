// Storage utility using IndexedDB for photos/audio and localStorage for preferences/contacts/events.

class AppStorage {
    constructor() {
        this.db = null;
        this.initDB();
    }

    initDB() {
        return new Promise((resolve, reject) => {
            const request = indexedDB.open('UIForgeDB', 1);
            request.onupgradeneeded = (e) => {
                const db = e.target.result;
                if (!db.objectStoreNames.contains('photos')) {
                    db.createObjectStore('photos', { keyPath: 'id', autoIncrement: true });
                }
                if (!db.objectStoreNames.contains('tracks')) {
                    db.createObjectStore('tracks', { keyPath: 'id', autoIncrement: true });
                }
            };
            request.onsuccess = (e) => {
                this.db = e.target.result;
                resolve(this.db);
            };
            request.onerror = (e) => {
                console.error('IndexedDB error:', e);
                resolve(null);
            };
        });
    }

    async addPhoto(dataUrl, name) {
        if (!this.db) await this.initDB();
        return new Promise((resolve) => {
            if (!this.db) return resolve(null);
            const tx = this.db.transaction('photos', 'readwrite');
            const store = tx.objectStore('photos');
            const photo = { name, dataUrl, timestamp: Date.now() };
            const req = store.add(photo);
            req.onsuccess = () => resolve(req.result);
            req.onerror = () => resolve(null);
        });
    }

    async getPhotos() {
        if (!this.db) await this.initDB();
        return new Promise((resolve) => {
            if (!this.db) return resolve([]);
            const tx = this.db.transaction('photos', 'readonly');
            const store = tx.objectStore('photos');
            const req = store.getAll();
            req.onsuccess = () => resolve(req.result || []);
            req.onerror = () => resolve([]);
        });
    }

    async deletePhoto(id) {
        if (!this.db) await this.initDB();
        return new Promise((resolve) => {
            if (!this.db) return resolve(false);
            const tx = this.db.transaction('photos', 'readwrite');
            const store = tx.objectStore('photos');
            const req = store.delete(id);
            req.onsuccess = () => resolve(true);
            req.onerror = () => resolve(false);
        });
    }

    getItem(key, defaultValue) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : defaultValue;
        } catch (e) {
            return defaultValue;
        }
    }

    setItem(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (e) {
            console.error('LocalStorage error:', e);
        }
    }
}

export const storage = new AppStorage();
