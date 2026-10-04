import { storage } from '../storage/storage.js';

export class FilesApp {
    render(container) {
        this.container = container;
        this.renderFiles();
    }

    async renderFiles() {
        const photos = await storage.getPhotos();
        const contacts = storage.getItem('uiforge_contacts', []);

        this.container.innerHTML = `
            <div class="app-header">
                <span>📁 Files</span>
                <span style="font-size: 11px; opacity: 0.7;">Browser Storage Explorer</span>
            </div>
            <div class="app-content" style="padding: 12px;">
                <div style="font-size: 11px; color: var(--text-secondary); background: rgba(234, 179, 8, 0.1); padding: 8px 12px; border-radius: 8px; margin-bottom: 12px;">
                    ℹ️ Browsers restrict access to native Android storage. Displaying browser sandboxed files & IndexedDB records.
                </div>

                <h4 style="font-size: 13px; color: var(--text-secondary); margin-bottom: 8px;">Storage Folders</h4>

                <div style="display: flex; flex-direction: column; gap: 8px;">
                    <div style="padding: 12px; border-radius: 12px; background: var(--bg-surface); border: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                        <div style="display: flex; align-items: center; gap: 10px;">
                            <span style="font-size: 24px;">🖼️</span>
                            <div>
                                <div style="font-weight: 600; font-size: 14px;">Pictures & Gallery</div>
                                <div style="font-size: 12px; color: var(--text-secondary);">${photos.length} items (IndexedDB)</div>
                            </div>
                        </div>
                    </div>

                    <div style="padding: 12px; border-radius: 12px; background: var(--bg-surface); border: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                        <div style="display: flex; align-items: center; gap: 10px;">
                            <span style="font-size: 24px;">👤</span>
                            <div>
                                <div style="font-weight: 600; font-size: 14px;">Contacts Database</div>
                                <div style="font-size: 12px; color: var(--text-secondary);">${contacts.length} records (LocalStorage)</div>
                            </div>
                        </div>
                    </div>

                    <div style="padding: 12px; border-radius: 12px; background: var(--bg-surface); border: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                        <div style="display: flex; align-items: center; gap: 10px;">
                            <span style="font-size: 24px;">⚙️</span>
                            <div>
                                <div style="font-weight: 600; font-size: 14px;">Preferences & Cache</div>
                                <div style="font-size: 12px; color: var(--text-secondary);">System config state</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
}
