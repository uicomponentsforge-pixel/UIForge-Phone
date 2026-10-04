import { storage } from '../storage/storage.js';
import { state, Wallpapers } from '../state.js';

export class GalleryApp {
    render(container) {
        this.container = container;
        this.loadPhotos();
    }

    async loadPhotos() {
        const storedPhotos = await storage.getPhotos();
        const defaultPhotos = [
            { id: 'def1', name: 'UIForge Silk', dataUrl: 'assets/uiforge_wallpaper.jpg' },
            { id: 'def2', name: 'Cyber Emerald', dataUrl: 'assets/uiforge_wallpaper_1791091277783.jpg' },
            { id: 'def3', name: 'Logo Asset', dataUrl: 'assets/ic_uiforge_logo.jpg' }
        ];

        const allPhotos = [...defaultPhotos, ...storedPhotos];

        this.container.innerHTML = `
            <div class="app-header">
                <span>🖼️ Gallery</span>
                <label class="btn-primary" style="padding: 4px 10px; font-size: 12px; cursor: pointer;">
                    + Upload
                    <input type="file" id="uploadPhotoInput" accept="image/*" style="display: none;">
                </label>
            </div>
            <div class="app-content" style="padding: 12px;">
                <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px;" id="photoGrid">
                    ${allPhotos.map(p => `
                        <div class="photo-card" style="position: relative; height: 140px; border-radius: 12px; overflow: hidden; border: 1px solid var(--border-color); cursor: pointer; background: black;">
                            <img src="${p.dataUrl}" style="width: 100%; height: 100%; object-fit: cover;">
                            <div style="position: absolute; bottom: 0; left: 0; right: 0; background: rgba(0,0,0,0.6); color: white; padding: 4px 8px; font-size: 11px; display: flex; justify-content: space-between; align-items: center;">
                                <span style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 80px;">${p.name}</span>
                                <button class="set-wp-btn btn-secondary" data-url="${p.dataUrl}" style="padding: 2px 6px; font-size: 10px;">Wallpaper</button>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;

        this.container.querySelector('#uploadPhotoInput').addEventListener('change', async (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = async (evt) => {
                    await storage.addPhoto(evt.target.result, file.name);
                    this.loadPhotos();
                };
                reader.readAsDataURL(file);
            }
        });

        this.container.querySelectorAll('.set-wp-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                const url = btn.getAttribute('data-url');
                state.setWallpaper(`url(${url})`);
                alert("Wallpaper updated!");
            });
        });
    }
}
