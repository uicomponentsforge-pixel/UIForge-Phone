import { audioService } from '../services/audioService.js';

export class MusicApp {
    constructor() {
        this.tracks = [
            { id: 1, title: "Synthwave Pulse", artist: "UIForge Synth Engine", isSynth: true },
            { id: 2, title: "Ambient Cosmos", artist: "Web Audio Demo", isSynth: true }
        ];
        this.currentTrack = this.tracks[0];
    }

    render(container) {
        this.container = container;
        container.innerHTML = `
            <div class="app-header">
                <span>🎵 Music Player</span>
                <label class="btn-primary" style="padding: 4px 10px; font-size: 12px; cursor: pointer;">
                    + Open Track
                    <input type="file" id="uploadAudioInput" accept="audio/*" style="display: none;">
                </label>
            </div>
            <div class="app-content" style="padding: 16px; display: flex; flex-direction: column; justify-content: space-between; align-items: center;">
                <div style="text-align: center; margin-top: 20px;">
                    <div style="width: 140px; height: 140px; border-radius: 20px; background: linear-gradient(135deg, #F43F5E 0%, #8B5CF6 100%); display: flex; justify-content: center; align-items: center; font-size: 64px; color: white; margin: 0 auto; box-shadow: 0 10px 25px rgba(244, 63, 94, 0.4);">
                        🎵
                    </div>
                    <div id="trackTitle" style="font-weight: 700; font-size: 18px; margin-top: 16px;">${this.currentTrack.title}</div>
                    <div id="trackArtist" style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">${this.currentTrack.artist}</div>
                </div>

                <div style="display: flex; align-items: center; gap: 24px; margin-bottom: 20px;">
                    <button id="prevTrackBtn" class="btn-secondary" style="border-radius: 50%; width: 48px; height: 48px; padding: 0; font-size: 18px;">⏮</button>
                    <button id="playPauseBtn" style="background: #F43F5E; color: white; border: none; border-radius: 50%; width: 64px; height: 64px; font-size: 24px; cursor: pointer; display: flex; justify-content: center; align-items: center; box-shadow: 0 4px 12px rgba(244, 63, 94, 0.5);">▶</button>
                    <button id="nextTrackBtn" class="btn-secondary" style="border-radius: 50%; width: 48px; height: 48px; padding: 0; font-size: 18px;">⏭</button>
                </div>

                <div style="width: 100%; max-height: 160px; overflow-y: auto;" id="playlistContainer">
                    <h4 style="font-size: 13px; color: var(--text-secondary); margin-bottom: 8px;">Playlist</h4>
                    ${this.tracks.map(t => `
                        <div class="playlist-item" data-id="${t.id}" style="padding: 10px; border-radius: 10px; background: var(--bg-surface); border: 1px solid var(--border-color); margin-bottom: 6px; cursor: pointer; display: flex; justify-content: space-between; font-size: 13px;">
                            <span>${t.title} - ${t.artist}</span>
                            <span>${t.isSynth ? 'Synth' : 'Audio File'}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;

        const playBtn = container.querySelector('#playPauseBtn');
        playBtn.addEventListener('click', () => {
            if (audioService.isPlaying) {
                audioService.pause();
                playBtn.textContent = '▶';
            } else {
                if (this.currentTrack.isSynth) {
                    audioService.playSynthTone();
                } else if (this.currentTrack.url) {
                    audioService.playFile(this.currentTrack.url);
                }
                playBtn.textContent = '⏸';
            }
        });

        container.querySelector('#uploadAudioInput').addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const url = URL.createObjectURL(file);
                const newTrack = {
                    id: Date.now(),
                    title: file.name,
                    artist: 'Local File',
                    url,
                    isSynth: false
                };
                this.tracks.push(newTrack);
                this.currentTrack = newTrack;
                this.render(container);
            }
        });
    }
}
