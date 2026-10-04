import { storage } from '../storage/storage.js';

export class CameraApp {
    constructor() {
        this.stream = null;
    }

    render(container) {
        this.container = container;
        container.innerHTML = `
            <div class="app-header">
                <span>📷 Camera</span>
                <span style="font-size: 11px; opacity: 0.7;">getUserMedia API</span>
            </div>
            <div class="app-content" style="padding: 12px; display: flex; flex-direction: column; justify-content: space-between; align-items: center; background: black;">
                <div style="position: relative; width: 100%; height: 380px; background: #111; border-radius: 16px; overflow: hidden; display: flex; justify-content: center; align-items: center;">
                    <video id="cameraVideo" autoplay playsinline style="width: 100%; height: 100%; object-fit: cover;"></video>
                    <canvas id="cameraCanvas" style="display: none;"></canvas>
                    <div id="cameraFallback" style="display: none; position: absolute; text-align: center; color: white; padding: 20px;">
                        <p style="font-size: 14px; margin-bottom: 12px;">Camera access unavailable or denied.</p>
                        <button id="retryCamBtn" class="btn-primary">Grant Permission</button>
                    </div>
                </div>

                <div style="display: flex; justify-content: center; align-items: center; width: 100%; padding: 16px;">
                    <button id="snapPhotoBtn" style="width: 64px; height: 64px; border-radius: 50%; border: 4px solid white; background: #EC4899; cursor: pointer; box-shadow: 0 4px 12px rgba(0,0,0,0.4);"></button>
                </div>
            </div>
        `;

        this.startCamera();
        container.querySelector('#snapPhotoBtn').addEventListener('click', () => this.takePhoto());
        container.querySelector('#retryCamBtn').addEventListener('click', () => this.startCamera());
    }

    async startCamera() {
        const video = this.container.querySelector('#cameraVideo');
        const fallback = this.container.querySelector('#cameraFallback');
        try {
            if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
                this.stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'user' }, audio: false });
                video.srcObject = this.stream;
                video.style.display = 'block';
                fallback.style.display = 'none';
            } else {
                throw new Error("getUserMedia not supported");
            }
        } catch (e) {
            console.warn("Camera access failed:", e);
            video.style.display = 'none';
            fallback.style.display = 'block';
        }
    }

    async takePhoto() {
        const video = this.container.querySelector('#cameraVideo');
        const canvas = this.container.querySelector('#cameraCanvas');
        if (video && video.srcObject) {
            canvas.width = video.videoWidth || 640;
            canvas.height = video.videoHeight || 480;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
            const dataUrl = canvas.toDataURL('image/jpeg');
            await storage.addPhoto(dataUrl, `Photo_${Date.now()}`);
            alert("Photo saved to Gallery!");
        } else {
            // Mock snap if camera isn't active
            const dummyCanvas = document.createElement('canvas');
            dummyCanvas.width = 400;
            dummyCanvas.height = 400;
            const ctx = dummyCanvas.getContext('2d');
            ctx.fillStyle = '#EC4899';
            ctx.fillRect(0, 0, 400, 400);
            ctx.fillStyle = 'white';
            ctx.font = '20px sans-serif';
            ctx.fillText('UIForge Web Photo', 100, 200);
            await storage.addPhoto(dummyCanvas.toDataURL(), `Photo_${Date.now()}`);
            alert("Sample photo saved to Gallery!");
        }
    }

    close() {
        if (this.stream) {
            this.stream.getTracks().forEach(track => track.stop());
            this.stream = null;
        }
    }
}
