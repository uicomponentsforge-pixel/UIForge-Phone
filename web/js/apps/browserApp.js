export class BrowserApp {
    render(container) {
        container.innerHTML = `
            <div class="app-header" style="flex-direction: column; height: auto; padding: 8px 12px; gap: 6px;">
                <div style="display: flex; justify-content: space-between; width: 100%; align-items: center;">
                    <span style="font-weight: 600;">🌐 Web Browser</span>
                    <span style="font-size: 11px; opacity: 0.7;">Embedded Frame</span>
                </div>
                <div style="display: flex; gap: 6px; width: 100%;">
                    <input type="text" id="browserUrl" class="input-field" value="https://wikipedia.org" style="flex: 1; padding: 6px 10px; font-size: 12px;">
                    <button id="browserGoBtn" class="btn-primary" style="padding: 6px 12px; font-size: 12px;">Go</button>
                </div>
            </div>
            <div class="app-content" style="padding: 0; position: relative;">
                <iframe id="browserFrame" src="https://wikipedia.org" style="width: 100%; height: 100%; border: none; background: white;"></iframe>
            </div>
        `;

        const urlInput = container.querySelector('#browserUrl');
        const frame = container.querySelector('#browserFrame');

        container.querySelector('#browserGoBtn').addEventListener('click', () => {
            let url = urlInput.value.trim();
            if (!url.startsWith('http://') && !url.startsWith('https://')) {
                url = 'https://' + url;
            }
            frame.src = url;
        });
    }
}
