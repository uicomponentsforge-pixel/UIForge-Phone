import { storage } from './storage/storage.js';
import { weatherService } from './services/weatherService.js';
import { deviceService } from './services/deviceService.js';

export const Wallpapers = {
    ABSTRACT_SILK: 'uiforge_wallpaper.jpg',
    CYBER_EMERALD: 'uiforge_wallpaper_1791091277783.jpg',
    MIDNIGHT_COSMOS: 'linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #311042 100%)',
    SUNSET_NEON: 'linear-gradient(135deg, #431407 0%, #78350f 50%, #831843 100%)'
};

export class OSState {
    constructor() {
        this.listeners = new Set();

        this.currentApp = null; // null or AppId string e.g. 'phone'
        this.recentsList = []; // list of AppId strings
        this.isRecentsOpen = false;
        this.isAppDrawerOpen = false;
        this.isNotificationShadeOpen = false;
        this.isLocked = false;

        this.currentTime = "";
        this.currentDate = "";

        this.isWifiEnabled = storage.getItem('uiforge_wifi', true);
        this.isBluetoothEnabled = storage.getItem('uiforge_bt', true);
        this.isFlashlightOn = false;
        this.isAutoRotateEnabled = storage.getItem('uiforge_autorotate', true);
        this.isDarkMode = storage.getItem('uiforge_darkmode', true);
        this.wallpaper = storage.getItem('uiforge_wallpaper', Wallpapers.ABSTRACT_SILK);
        this.volumeRatio = storage.getItem('uiforge_volume', 0.8);
        this.brightnessRatio = storage.getItem('uiforge_brightness', 0.85);

        this.searchQuery = "";

        this.weather = {
            cityName: "San Francisco",
            temperature: 21.0,
            weatherDesc: "Clear Sky",
            weatherCode: 0,
            hourly: [],
            daily: []
        };
        this.isWeatherLoading = false;

        this.deviceInfo = {};

        this.notifications = storage.getItem('uiforge_notifications', [
            {
                id: "1",
                appName: "UIForge OS",
                title: "System Ready",
                message: "Welcome to UIForge Web Edition! HTML5/CSS3/Vanilla JS active.",
                time: "Now",
                accentColor: "#06B6D4",
                isDismissible: true
            },
            {
                id: "2",
                appName: "Weather",
                title: "Real-time Forecast",
                message: "Live weather data streaming from Open-Meteo API.",
                time: "5m ago",
                accentColor: "#3B82F6",
                isDismissible: true
            },
            {
                id: "3",
                appName: "Battery Guard",
                title: "Device Health",
                message: "Browser Battery API active. Optimal charge level.",
                time: "12m ago",
                accentColor: "#10B981",
                isDismissible: true
            }
        ]);

        this.initClock();
        this.refreshWeather();
        this.refreshDeviceInfo();
    }

    subscribe(listener) {
        this.listeners.add(listener);
        return () => this.listeners.delete(listener);
    }

    notify() {
        for (const listener of this.listeners) {
            listener(this);
        }
    }

    initClock() {
        const updateClock = () => {
            const now = new Date();
            let hours = now.getHours();
            const minutes = now.getMinutes().toString().padStart(2, '0');
            const ampm = hours >= 12 ? 'PM' : 'AM';
            hours = hours % 12 || 12;
            this.currentTime = `${hours}:${minutes}`;

            const options = { weekday: 'long', month: 'long', day: 'numeric' };
            this.currentDate = now.toLocaleDateString('en-US', options);
            this.notify();
        };
        updateClock();
        setInterval(updateClock, 1000);
    }

    async refreshWeather(cityIndex = 0) {
        this.isWeatherLoading = true;
        this.notify();
        this.weather = await weatherService.fetchWeather(weatherService.popularCities[cityIndex] || weatherService.popularCities[0]);
        this.isWeatherLoading = false;
        this.notify();
    }

    async refreshDeviceInfo() {
        this.deviceInfo = await deviceService.getDeviceInfo();
        this.notify();
    }

    openApp(appId) {
        this.currentApp = appId;
        this.isAppDrawerOpen = false;
        this.isRecentsOpen = false;
        this.isNotificationShadeOpen = false;

        this.recentsList = this.recentsList.filter(id => id !== appId);
        this.recentsList.unshift(appId);
        this.notify();
    }

    closeApp() {
        this.currentApp = null;
        this.notify();
    }

    pressHome() {
        this.currentApp = null;
        this.isRecentsOpen = false;
        this.isAppDrawerOpen = false;
        this.isNotificationShadeOpen = false;
        this.notify();
    }

    pressBack() {
        if (this.isNotificationShadeOpen) {
            this.isNotificationShadeOpen = false;
            this.notify();
            return true;
        }
        if (this.isRecentsOpen) {
            this.isRecentsOpen = false;
            this.notify();
            return true;
        }
        if (this.isAppDrawerOpen) {
            this.isAppDrawerOpen = false;
            this.notify();
            return true;
        }
        if (this.currentApp !== null) {
            this.currentApp = null;
            this.notify();
            return true;
        }
        return false;
    }

    toggleRecents() {
        this.isRecentsOpen = !this.isRecentsOpen;
        if (this.isRecentsOpen) {
            this.isAppDrawerOpen = false;
            this.isNotificationShadeOpen = false;
        }
        this.notify();
    }

    dismissRecentApp(appId) {
        this.recentsList = this.recentsList.filter(id => id !== appId);
        if (this.currentApp === appId) {
            this.currentApp = null;
        }
        this.notify();
    }

    clearAllRecents() {
        this.recentsList = [];
        this.currentApp = null;
        this.isRecentsOpen = false;
        this.notify();
    }

    toggleAppDrawer() {
        this.isAppDrawerOpen = !this.isAppDrawerOpen;
        if (this.isAppDrawerOpen) {
            this.isRecentsOpen = false;
            this.isNotificationShadeOpen = false;
        }
        this.notify();
    }

    toggleNotificationShade() {
        this.isNotificationShadeOpen = !this.isNotificationShadeOpen;
        if (this.isNotificationShadeOpen) {
            this.isRecentsOpen = false;
            this.isAppDrawerOpen = false;
        }
        this.notify();
    }

    lockDevice() {
        this.isLocked = true;
        this.isNotificationShadeOpen = false;
        this.isRecentsOpen = false;
        this.notify();
    }

    unlockDevice() {
        this.isLocked = false;
        this.notify();
    }

    setSearchQuery(q) {
        this.searchQuery = q;
        this.notify();
    }

    toggleWifi() {
        this.isWifiEnabled = !this.isWifiEnabled;
        storage.setItem('uiforge_wifi', this.isWifiEnabled);
        this.notify();
    }

    toggleBluetooth() {
        this.isBluetoothEnabled = !this.isBluetoothEnabled;
        storage.setItem('uiforge_bt', this.isBluetoothEnabled);
        this.notify();
    }

    async toggleFlashlight() {
        this.isFlashlightOn = !this.isFlashlightOn;
        if ('mediaDevices' in navigator && 'getUserMedia' in navigator.mediaDevices) {
            try {
                if (this.isFlashlightOn) {
                    const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } });
                    const track = stream.getVideoTracks()[0];
                    if (track && track.getCapabilities && track.getCapabilities().torch) {
                        await track.applyConstraints({ advanced: [{ torch: true }] });
                    }
                }
            } catch (e) {
                console.warn("Torch hardware control unavailable:", e);
            }
        }
        this.notify();
    }

    toggleAutoRotate() {
        this.isAutoRotateEnabled = !this.isAutoRotateEnabled;
        storage.setItem('uiforge_autorotate', this.isAutoRotateEnabled);
        this.notify();
    }

    toggleDarkMode() {
        this.isDarkMode = !this.isDarkMode;
        storage.setItem('uiforge_darkmode', this.isDarkMode);
        this.notify();
    }

    setWallpaper(wp) {
        this.wallpaper = wp;
        storage.setItem('uiforge_wallpaper', this.wallpaper);
        this.notify();
    }

    setVolume(ratio) {
        this.volumeRatio = ratio;
        storage.setItem('uiforge_volume', ratio);
        this.notify();
    }

    setBrightness(ratio) {
        this.brightnessRatio = ratio;
        storage.setItem('uiforge_brightness', ratio);
        this.notify();
    }

    addNotification(title, message, appName = "UIForge System") {
        const item = {
            id: Date.now().toString(),
            appName,
            title,
            message,
            time: "Just now",
            accentColor: "#06B6D4",
            isDismissible: true
        };
        this.notifications.unshift(item);
        storage.setItem('uiforge_notifications', this.notifications);
        this.notify();
    }

    dismissNotification(id) {
        this.notifications = this.notifications.filter(n => n.id !== id);
        storage.setItem('uiforge_notifications', this.notifications);
        this.notify();
    }

    clearAllNotifications() {
        this.notifications = [];
        storage.setItem('uiforge_notifications', this.notifications);
        this.notify();
    }
}

export const state = new OSState();
