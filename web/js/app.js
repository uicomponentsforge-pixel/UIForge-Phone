import { state } from './state.js';
import { renderSystemShell } from './components/shell.js';

import { PhoneApp } from './apps/phoneApp.js';
import { MessagesApp } from './apps/messagesApp.js';
import { ContactsApp } from './apps/contactsApp.js';
import { CameraApp } from './apps/cameraApp.js';
import { GalleryApp } from './apps/galleryApp.js';
import { CalculatorApp } from './apps/calculatorApp.js';
import { CalendarApp } from './apps/calendarApp.js';
import { ClockApp } from './apps/clockApp.js';
import { MusicApp } from './apps/musicApp.js';
import { WeatherApp } from './apps/weatherApp.js';
import { SettingsApp } from './apps/settingsApp.js';
import { FilesApp } from './apps/filesApp.js';
import { BrowserApp } from './apps/browserApp.js';

class UIForgeApp {
    constructor() {
        this.appInstances = {
            phone: new PhoneApp(),
            messages: new MessagesApp(),
            contacts: new ContactsApp(),
            camera: new CameraApp(),
            gallery: new GalleryApp(),
            calculator: new CalculatorApp(),
            calendar: new CalendarApp(),
            clock: new ClockApp(),
            music: new MusicApp(),
            weather: new WeatherApp(),
            settings: new SettingsApp(),
            files: new FilesApp(),
            browser: new BrowserApp()
        };

        this.init();
    }

    init() {
        const root = document.getElementById('app');
        renderSystemShell(root);

        // Global interface for shell actions
        window.uiforge = {
            openApp: (id) => state.openApp(id),
            dismissNotification: (id) => state.dismissNotification(id)
        };

        this.setupAppWindowsContainer();
        state.subscribe(s => this.handleStateChange(s));
    }

    setupAppWindowsContainer() {
        const container = document.getElementById('appWindowsContainer');
        container.innerHTML = Object.keys(this.appInstances).map(id => `
            <div class="app-window" id="appWindow_${id}"></div>
        `).join('');
    }

    handleStateChange(s) {
        Object.keys(this.appInstances).forEach(id => {
            const win = document.getElementById(`appWindow_${id}`);
            if (!win) return;

            const isCurrent = s.currentApp === id;
            if (isCurrent && !win.classList.contains('active')) {
                win.classList.add('active');
                this.appInstances[id].render(win);
            } else if (!isCurrent && win.classList.contains('active')) {
                win.classList.remove('active');
                if (this.appInstances[id].close) {
                    this.appInstances[id].close();
                }
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new UIForgeApp();
});
