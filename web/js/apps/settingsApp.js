import { state, Wallpapers } from '../state.js';

export class SettingsApp {
    render(container) {
        this.container = container;
        this.renderSections();
    }

    renderSections() {
        const info = state.deviceInfo;
        this.container.innerHTML = `
            <div class="app-header">
                <span>⚙️ Settings</span>
            </div>
            <div class="app-content" style="padding: 12px;">
                <div style="font-size: 11px; color: var(--text-secondary); background: rgba(100, 116, 139, 0.15); padding: 8px 12px; border-radius: 8px; margin-bottom: 12px;">
                    ℹ️ Web Settings distinguishes browser APIs from Android system-only settings.
                </div>

                <div style="display: flex; flex-direction: column; gap: 8px;">
                    <!-- Network / Connectivity -->
                    <div class="settings-card" style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: 12px; padding: 12px;">
                        <div style="font-weight: 600; font-size: 14px; margin-bottom: 8px; color: var(--accent);">Network & Connections</div>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-size: 13px;">
                            <span>Wi-Fi (Web Network API)</span>
                            <input type="checkbox" id="setWifi" ${state.isWifiEnabled ? 'checked' : ''}>
                        </div>
                        <div style="display: flex; justify-content: space-between; align-items: center; font-size: 13px;">
                            <span>Bluetooth</span>
                            <input type="checkbox" id="setBt" ${state.isBluetoothEnabled ? 'checked' : ''}>
                        </div>
                    </div>

                    <!-- Display & Theme -->
                    <div class="settings-card" style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: 12px; padding: 12px;">
                        <div style="font-weight: 600; font-size: 14px; margin-bottom: 8px; color: var(--accent);">Display & Appearance</div>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-size: 13px;">
                            <span>Dark Mode</span>
                            <input type="checkbox" id="setDark" ${state.isDarkMode ? 'checked' : ''}>
                        </div>
                        <div style="font-size: 13px; margin-bottom: 6px;">Wallpaper</div>
                        <select id="setWpSelect" style="width: 100%; background: var(--bg-primary); color: var(--text-primary); border: 1px solid var(--border-color); padding: 6px; border-radius: 8px;">
                            <option value="${Wallpapers.ABSTRACT_SILK}" ${state.wallpaper === Wallpapers.ABSTRACT_SILK ? 'selected' : ''}>Abstract Silk (JPG)</option>
                            <option value="${Wallpapers.CYBER_EMERALD}" ${state.wallpaper === Wallpapers.CYBER_EMERALD ? 'selected' : ''}>Cyber Emerald (JPG)</option>
                            <option value="${Wallpapers.MIDNIGHT_COSMOS}" ${state.wallpaper === Wallpapers.MIDNIGHT_COSMOS ? 'selected' : ''}>Midnight Cosmos Gradient</option>
                            <option value="${Wallpapers.SUNSET_NEON}" ${state.wallpaper === Wallpapers.SUNSET_NEON ? 'selected' : ''}>Sunset Neon Gradient</option>
                        </select>
                    </div>

                    <!-- Battery & Storage -->
                    <div class="settings-card" style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: 12px; padding: 12px;">
                        <div style="font-weight: 600; font-size: 14px; margin-bottom: 8px; color: var(--accent);">Battery & Storage</div>
                        <div style="font-size: 13px; margin-bottom: 4px;">Battery Level: ${info.batteryPercent}% (${info.isCharging ? 'Charging' : 'Discharging'})</div>
                        <div style="font-size: 13px; margin-bottom: 4px;">RAM: ${info.ramInfo}</div>
                        <div style="font-size: 13px;">Browser Quota Storage: ${info.usedStorageGb} GB used / ${info.totalStorageGb} GB total</div>
                    </div>

                    <!-- About Phone -->
                    <div class="settings-card" style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: 12px; padding: 12px;">
                        <div style="font-weight: 600; font-size: 14px; margin-bottom: 8px; color: var(--accent);">About Phone</div>
                        <div style="font-size: 12px; color: var(--text-secondary); display: flex; flex-direction: column; gap: 4px;">
                            <div><strong>Model:</strong> UIForge Web Edition</div>
                            <div><strong>Platform:</strong> ${info.platform || 'Web Browser'}</div>
                            <div><strong>User Agent:</strong> <span style="word-break: break-all;">${info.userAgent || ''}</span></div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        this.container.querySelector('#setWifi').addEventListener('change', () => state.toggleWifi());
        this.container.querySelector('#setBt').addEventListener('change', () => state.toggleBluetooth());
        this.container.querySelector('#setDark').addEventListener('change', () => state.toggleDarkMode());
        this.container.querySelector('#setWpSelect').addEventListener('change', (e) => state.setWallpaper(e.target.value));
    }
}
