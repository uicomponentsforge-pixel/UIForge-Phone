// Audio Service matching RealAudioPlayer.kt (Web Audio Synth / HTML5 Audio)

export class AudioService {
    constructor() {
        this.audioCtx = null;
        this.oscillator = null;
        this.gainNode = null;
        this.audioElement = new Audio();
        this.isPlaying = false;
        this.currentTrack = null;
        this.synthInterval = null;
    }

    initCtx() {
        if (!this.audioCtx) {
            const AudioContext = window.AudioContext || window.webkitAudioContext;
            if (AudioContext) {
                this.audioCtx = new AudioContext();
            }
        }
        if (this.audioCtx && this.audioCtx.state === 'suspended') {
            this.audioCtx.resume();
        }
    }

    playSynthTone() {
        this.initCtx();
        if (!this.audioCtx) return;
        this.stop();

        let noteIdx = 0;
        const scale = [261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25]; // C4 major

        this.isPlaying = true;
        this.synthInterval = setInterval(() => {
            if (!this.isPlaying) return;
            try {
                const osc = this.audioCtx.createOscillator();
                const gain = this.audioCtx.createGain();
                osc.type = 'sine';
                osc.frequency.setValueAtTime(scale[noteIdx % scale.length], this.audioCtx.currentTime);
                gain.gain.setValueAtTime(0.1, this.audioCtx.currentTime);
                gain.gain.exponentialRampToValueAtTime(0.001, this.audioCtx.currentTime + 0.4);
                osc.connect(gain);
                gain.connect(this.audioCtx.destination);
                osc.start();
                osc.stop(this.audioCtx.currentTime + 0.4);
                noteIdx++;
            } catch (e) {
                console.error('Synth playback error:', e);
            }
        }, 500);
    }

    playFile(url) {
        this.stop();
        this.audioElement.src = url;
        this.audioElement.play().catch(e => console.warn('Audio playback error:', e));
        this.isPlaying = true;
    }

    pause() {
        if (this.synthInterval) {
            clearInterval(this.synthInterval);
            this.synthInterval = null;
        }
        this.audioElement.pause();
        this.isPlaying = false;
    }

    stop() {
        this.pause();
        this.audioElement.currentTime = 0;
    }

    setVolume(level) { // level: 0.0 to 1.0
        this.audioElement.volume = level;
    }
}

export const audioService = new AudioService();
