import { state } from '../state.js';

export const APPS = [
    { id: 'phone', title: 'Phone', icon: '📞', color: '#10B981', category: 'Communication' },
    { id: 'messages', title: 'Messages', icon: '💬', color: '#3B82F6', category: 'Communication' },
    { id: 'contacts', title: 'Contacts', icon: '👤', color: '#6366F1', category: 'Communication' },
    { id: 'camera', title: 'Camera', icon: '📷', color: '#EC4899', category: 'Media' },
    { id: 'gallery', title: 'Gallery', icon: '🖼️', color: '#8B5CF6', category: 'Media' },
    { id: 'calculator', title: 'Calculator', icon: '🧮', color: '#F59E0B', category: 'Utilities' },
    { id: 'calendar', title: 'Calendar', icon: '📅', color: '#EF4444', category: 'Utilities' },
    { id: 'clock', title: 'Clock', icon: '⏰', color: '#14B8A6', category: 'Utilities' },
    { id: 'music', title: 'Music', icon: '🎵', color: '#F43F5E', category: 'Media' },
    { id: 'weather', title: 'Weather', icon: '🌤️', color: '#06B6D4', category: 'Utilities' },
    { id: 'settings', title: 'Settings', icon: '⚙️', color: '#64748B', category: 'System' },
    { id: 'files', title: 'Files', icon: '📁', color: '#EAB308', category: 'Utilities' },
    { id: 'browser', title: 'Browser', icon: '🌐', color: '#3B82F6', category: 'Internet' }
];

export function renderSystemShell(rootEl) {
    rootEl.innerHTML = `
        <div class="device-container">
            <div class="brightness-overlay" id="brightnessOverlay"></div>
            <div class="phone-screen" id="phoneScreen">
                <div class="camera-notch"></div>

                <!-- Status Bar -->
                <div class="status-bar" id="statusBar">
                    <span id="statusClock">12:00</span>
                    <div class="status-bar-icons">
                        <span id="statusWifi">📶</span>
                        <span id="statusBt">📡</span>
                        <span id="statusBattery">🔋 85%</span>
                    </div>
                </div>

                <!-- Main Workspace Area -->
                <div class="workspace" id="workspace">
                    <!-- Home Screen -->
                    <div class="home-screen" id="homeScreen">
                        <div class="widgets-area">
                            <div class="time-widget">
                                <div class="clock-text" id="homeClock">12:00</div>
                                <div class="date-text" id="homeDate">Monday, January 1</div>
                            </div>

                            <div class="quick-weather-card" id="quickWeatherCard">
                                <div>
                                    <div style="font-weight: 600; font-size: 16px;" id="homeWeatherCity">San Francisco</div>
                                    <div style="font-size: 13px; opacity: 0.8;" id="homeWeatherDesc">Clear Sky</div>
                                </div>
                                <div style="font-size: 28px; font-weight: 700;" id="homeWeatherTemp">21°C</div>
                            </div>

                            <div class="quick-search-bar" id="quickSearchBar">
                                <span>🔍</span>
                                <input type="text" placeholder="Search apps, web..." id="homeSearchInput">
                            </div>

                            <div class="app-grid" id="homeAppGrid"></div>
                        </div>

                        <div class="home-dock" id="homeDock"></div>
                    </div>

                    <!-- App Drawer Sheet -->
                    <div class="app-drawer-sheet" id="appDrawerSheet">
                        <div class="drawer-header">
                            <input type="text" class="drawer-search-input" placeholder="Search all apps..." id="drawerSearchInput">
                        </div>
                        <div class="drawer-app-list" id="drawerAppList"></div>
                    </div>

                    <!-- Quick Settings & Notification Shade -->
                    <div class="shade-overlay" id="shadeOverlay">
                        <div class="shade-header">
                            <h3>Quick Settings</h3>
                            <button id="closeShadeBtn" class="btn-secondary" style="padding: 4px 12px;">Close</button>
                        </div>
                        <div class="qs-grid">
                            <div class="qs-tile" id="qsWifi">
                                <span class="qs-tile-icon">📶</span>
                                <div class="qs-tile-text">
                                    <span class="qs-tile-title">Wi-Fi</span>
                                    <span class="qs-tile-status" id="qsWifiStatus">On</span>
                                </div>
                            </div>
                            <div class="qs-tile" id="qsBt">
                                <span class="qs-tile-icon">📡</span>
                                <div class="qs-tile-text">
                                    <span class="qs-tile-title">Bluetooth</span>
                                    <span class="qs-tile-status" id="qsBtStatus">On</span>
                                </div>
                            </div>
                            <div class="qs-tile" id="qsFlash">
                                <span class="qs-tile-icon">🔦</span>
                                <div class="qs-tile-text">
                                    <span class="qs-tile-title">Flashlight</span>
                                    <span class="qs-tile-status" id="qsFlashStatus">Off</span>
                                </div>
                            </div>
                            <div class="qs-tile" id="qsDark">
                                <span class="qs-tile-icon">🌙</span>
                                <div class="qs-tile-text">
                                    <span class="qs-tile-title">Dark Mode</span>
                                    <span class="qs-tile-status" id="qsDarkStatus">On</span>
                                </div>
                            </div>
                        </div>

                        <div class="slider-container">
                            <span>☀️</span>
                            <input type="range" id="brightnessSlider" min="0.2" max="1" step="0.05" value="0.85">
                        </div>
                        <div class="slider-container">
                            <span>🔊</span>
                            <input type="range" id="volumeSlider" min="0" max="1" step="0.05" value="0.8">
                        </div>

                        <h4 style="margin-top: 16px; margin-bottom: 8px;">Notifications</h4>
                        <div class="notifications-list" id="notificationsList"></div>
                    </div>

                    <!-- Recents Carousel -->
                    <div class="recents-overlay" id="recentsOverlay">
                        <h3 style="color: white; margin-bottom: 12px;">Recent Apps</h3>
                        <div class="recents-carousel" id="recentsCarousel"></div>
                        <button class="clear-recents-btn" id="clearRecentsBtn">Clear All</button>
                    </div>

                    <!-- Lock Screen -->
                    <div class="lock-screen" id="lockScreen">
                        <div>
                            <div style="font-size: 52px; font-weight: 700;" id="lockClock">12:00</div>
                            <div style="font-size: 16px;" id="lockDate">Monday, Jan 1</div>
                        </div>
                        <div class="unlock-prompt" id="unlockPrompt">
                            <span style="font-size: 28px;">🔒</span>
                            <span>Swipe or Click to Unlock</span>
                        </div>
                    </div>

                    <!-- Containers for App Windows -->
                    <div id="appWindowsContainer"></div>
                </div>

                <!-- Navigation Bar -->
                <div class="nav-bar" id="navBar">
                    <button class="nav-btn" id="navBack" title="Back">◀</button>
                    <button class="nav-btn" id="navHome" title="Home">●</button>
                    <button class="nav-btn" id="navRecents" title="Recents">■</button>
                </div>
            </div>
        </div>
    `;

    bindEvents();
    subscribeState();
}

function bindEvents() {
    document.getElementById('statusBar').addEventListener('click', () => state.toggleNotificationShade());
    document.getElementById('closeShadeBtn').addEventListener('click', () => state.toggleNotificationShade());

    document.getElementById('qsWifi').addEventListener('click', () => state.toggleWifi());
    document.getElementById('qsBt').addEventListener('click', () => state.toggleBluetooth());
    document.getElementById('qsFlash').addEventListener('click', () => state.toggleFlashlight());
    document.getElementById('qsDark').addEventListener('click', () => state.toggleDarkMode());

    document.getElementById('brightnessSlider').addEventListener('input', (e) => state.setBrightness(parseFloat(e.target.value)));
    document.getElementById('volumeSlider').addEventListener('input', (e) => state.setVolume(parseFloat(e.target.value)));

    document.getElementById('quickWeatherCard').addEventListener('click', () => state.openApp('weather'));
    document.getElementById('quickSearchBar').addEventListener('click', () => state.toggleAppDrawer());

    document.getElementById('navBack').addEventListener('click', () => state.pressBack());
    document.getElementById('navHome').addEventListener('click', () => state.pressHome());
    document.getElementById('navRecents').addEventListener('click', () => state.toggleRecents());

    document.getElementById('clearRecentsBtn').addEventListener('click', () => state.clearAllRecents());
    document.getElementById('unlockPrompt').addEventListener('click', () => state.unlockDevice());

    document.getElementById('drawerSearchInput').addEventListener('input', (e) => {
        state.setSearchQuery(e.target.value);
    });
}

function subscribeState() {
    state.subscribe(s => {
        // Theme & Background
        document.body.setAttribute('data-theme', s.isDarkMode ? 'dark' : 'light');
        const phoneScreen = document.getElementById('phoneScreen');
        if (s.wallpaper.startsWith('linear-gradient') || s.wallpaper.startsWith('url')) {
            phoneScreen.style.background = s.wallpaper;
        } else {
            phoneScreen.style.backgroundImage = `url(assets/${s.wallpaper})`;
        }

        // Brightness Overlay
        const overlay = document.getElementById('brightnessOverlay');
        overlay.style.opacity = (1 - s.brightnessRatio) * 0.7;

        // Clock & Date
        document.getElementById('statusClock').textContent = s.currentTime;
        document.getElementById('homeClock').textContent = s.currentTime;
        document.getElementById('lockClock').textContent = s.currentTime;
        document.getElementById('homeDate').textContent = s.currentDate;
        document.getElementById('lockDate').textContent = s.currentDate;

        // Weather Widget
        document.getElementById('homeWeatherCity').textContent = s.weather.cityName;
        document.getElementById('homeWeatherDesc').textContent = s.weather.weatherDesc;
        document.getElementById('homeWeatherTemp').textContent = `${Math.round(s.weather.temperature)}°C`;

        // Quick Settings Status
        const wifiTile = document.getElementById('qsWifi');
        wifiTile.classList.toggle('active', s.isWifiEnabled);
        document.getElementById('qsWifiStatus').textContent = s.isWifiEnabled ? 'On' : 'Off';
        document.getElementById('statusWifi').style.opacity = s.isWifiEnabled ? '1' : '0.3';

        const btTile = document.getElementById('qsBt');
        btTile.classList.toggle('active', s.isBluetoothEnabled);
        document.getElementById('qsBtStatus').textContent = s.isBluetoothEnabled ? 'On' : 'Off';
        document.getElementById('statusBt').style.opacity = s.isBluetoothEnabled ? '1' : '0.3';

        const flashTile = document.getElementById('qsFlash');
        flashTile.classList.toggle('active', s.isFlashlightOn);
        document.getElementById('qsFlashStatus').textContent = s.isFlashlightOn ? 'On' : 'Off';

        const darkTile = document.getElementById('qsDark');
        darkTile.classList.toggle('active', s.isDarkMode);
        document.getElementById('qsDarkStatus').textContent = s.isDarkMode ? 'Dark' : 'Light';

        if (s.deviceInfo.batteryPercent !== undefined) {
            document.getElementById('statusBattery').textContent = `🔋 ${s.deviceInfo.batteryPercent}%`;
        }

        // Lock Screen
        const lockScreen = document.getElementById('lockScreen');
        lockScreen.classList.toggle('unlocked', !s.isLocked);

        // App Drawer Sheet
        const appDrawerSheet = document.getElementById('appDrawerSheet');
        appDrawerSheet.classList.toggle('open', s.isAppDrawerOpen);

        // Quick Settings / Notification Shade Overlay
        const shadeOverlay = document.getElementById('shadeOverlay');
        shadeOverlay.classList.toggle('open', s.isNotificationShadeOpen);

        // Recents Overlay
        const recentsOverlay = document.getElementById('recentsOverlay');
        recentsOverlay.classList.toggle('open', s.isRecentsOpen);

        // Render Grids & Recents
        renderAppGrids(s.searchQuery);
        renderNotifications(s.notifications);
        renderRecents(s.recentsList);
    });
}

function renderAppGrids(searchQuery) {
    const homeGrid = document.getElementById('homeAppGrid');
    const homeDock = document.getElementById('homeDock');
    const drawerList = document.getElementById('drawerAppList');

    const dockAppIds = ['phone', 'messages', 'browser', 'camera'];
    const homeApps = APPS.filter(a => !dockAppIds.includes(a.id)).slice(0, 8);
    const filteredApps = APPS.filter(a => a.title.toLowerCase().includes(searchQuery.toLowerCase()));

    // Home Grid
    homeGrid.innerHTML = homeApps.map(app => `
        <div class="app-icon-item" onclick="window.uiforge.openApp('${app.id}')">
            <div class="app-icon-box" style="background: ${app.color};">${app.icon}</div>
            <span class="app-icon-label">${app.title}</span>
        </div>
    `).join('');

    // Home Dock
    homeDock.innerHTML = dockAppIds.map(id => {
        const app = APPS.find(a => a.id === id);
        if (!app) return '';
        return `
            <div class="app-icon-item" onclick="window.uiforge.openApp('${app.id}')">
                <div class="app-icon-box" style="background: ${app.color};">${app.icon}</div>
            </div>
        `;
    }).join('');

    // Drawer List
    drawerList.innerHTML = filteredApps.map(app => `
        <div class="app-icon-item" onclick="window.uiforge.openApp('${app.id}')">
            <div class="app-icon-box" style="background: ${app.color};">${app.icon}</div>
            <span class="app-icon-label">${app.title}</span>
        </div>
    `).join('');
}

function renderNotifications(notifications) {
    const container = document.getElementById('notificationsList');
    if (notifications.length === 0) {
        container.innerHTML = '<div style="color: var(--text-secondary); text-align: center; padding: 12px;">No notifications</div>';
        return;
    }
    container.innerHTML = notifications.map(n => `
        <div class="notification-card">
            <div class="notification-header">
                <span>${n.appName}</span>
                <span>${n.time}</span>
            </div>
            <div class="notification-title">${n.title}</div>
            <div class="notification-body">${n.message}</div>
            ${n.isDismissible ? `<button class="dismiss-btn" onclick="window.uiforge.dismissNotification('${n.id}')">✕</button>` : ''}
        </div>
    `).join('');
}

function renderRecents(recentsList) {
    const container = document.getElementById('recentsCarousel');
    if (recentsList.length === 0) {
        container.innerHTML = '<div style="color: white; text-align: center; width: 100%;">No recent apps</div>';
        return;
    }
    container.innerHTML = recentsList.map(id => {
        const app = APPS.find(a => a.id === id);
        if (!app) return '';
        return `
            <div class="recent-card" onclick="window.uiforge.openApp('${app.id}')">
                <div class="recent-card-header">
                    <span>${app.icon}</span>
                    <span style="font-weight: 600;">${app.title}</span>
                </div>
                <div class="recent-card-body" style="background: ${app.color}15;">
                    <span>${app.icon}</span>
                </div>
            </div>
        `;
    }).join('');
}
