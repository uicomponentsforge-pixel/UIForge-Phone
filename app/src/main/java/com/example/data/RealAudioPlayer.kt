package com.example.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.model.SongItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class RealAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var synthTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSong = MutableStateFlow<SongItem?>(null)
    val currentSong: StateFlow<SongItem?> = _currentSong.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _totalDurationMs = MutableStateFlow(180_000L)
    val totalDurationMs: StateFlow<Long> = _totalDurationMs.asStateFlow()

    private var progressJob: Job? = null

    val fallbackSongs = listOf(
        SongItem(
            id = -1L,
            title = "UIForge Cyber Genesis",
            artist = "Forge Synth Wave",
            durationMs = 120_000L,
            isBuiltInSynth = true
        ),
        SongItem(
            id = -2L,
            title = "Atmospheric Neon Pulse",
            artist = "OS Ambient Lab",
            durationMs = 150_000L,
            isBuiltInSynth = true
        ),
        SongItem(
            id = -3L,
            title = "Midnight Silicon Drift",
            artist = "Echo Frequency",
            durationMs = 180_000L,
            isBuiltInSynth = true
        )
    )

    fun fetchDeviceSongs(): List<SongItem> {
        val songs = mutableListOf<SongItem>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                "${MediaStore.Audio.Media.IS_MUSIC} != 0",
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )

            cursor?.let {
                val idCol = it.getColumnIndex(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val durationCol = it.getColumnIndex(MediaStore.Audio.Media.DURATION)

                while (it.moveToNext()) {
                    val id = if (idCol != -1) it.getLong(idCol) else 0L
                    val title = if (titleCol != -1) it.getString(titleCol) ?: "Audio Track" else "Audio Track"
                    val artist = if (artistCol != -1) it.getString(artistCol) ?: "Unknown Artist" else "Unknown Artist"
                    val duration = if (durationCol != -1) it.getLong(durationCol) else 180000L
                    val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    songs.add(
                        SongItem(
                            id = id,
                            title = title,
                            artist = artist,
                            durationMs = if (duration > 0) duration else 180000L,
                            uriString = uri.toString(),
                            isBuiltInSynth = false
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("RealAudioPlayer", "Error loading MediaStore songs: ${e.message}")
        } finally {
            cursor?.close()
        }

        return if (songs.isNotEmpty()) songs else fallbackSongs
    }

    fun playSong(song: SongItem) {
        stop()
        _currentSong.value = song
        _totalDurationMs.value = song.durationMs

        if (song.isBuiltInSynth || song.uriString == null) {
            startRealSynthPlayback()
        } else {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, Uri.parse(song.uriString))
                    setOnPreparedListener {
                        it.start()
                        _isPlaying.value = true
                        _totalDurationMs.value = it.duration.toLong().coerceAtLeast(1000L)
                        startProgressTracker()
                    }
                    setOnCompletionListener {
                        _isPlaying.value = false
                        _currentPositionMs.value = 0L
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e("RealAudioPlayer", "MediaPlayer failed: ${e.message}, falling back to synth")
                startRealSynthPlayback()
            }
        }
    }

    private fun startRealSynthPlayback() {
        _isPlaying.value = true
        startProgressTracker()

        synthJob = scope.launch {
            val sampleRate = 44100
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            try {
                synthTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf)
                    .build()

                synthTrack?.play()

                // Generate a melodious musical synth chord progression
                val notes = doubleArrayOf(220.0, 261.63, 329.63, 392.0, 440.0, 523.25)
                var noteIdx = 0
                val buffer = ShortArray(sampleRate / 4) // 250ms chunks

                while (isActive && _isPlaying.value) {
                    val freq = notes[noteIdx % notes.size]
                    noteIdx++
                    for (i in buffer.indices) {
                        val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                        val harmonic = sin(angle) * 0.7 + sin(angle * 2.0) * 0.3
                        val envelope = (1.0 - (i.toDouble() / buffer.size)).coerceIn(0.1, 1.0)
                        buffer[i] = (harmonic * envelope * 8000.0).toInt().toShort()
                    }
                    synthTrack?.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                Log.e("RealAudioPlayer", "Synth error: ${e.message}")
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                if (mediaPlayer != null) {
                    try {
                        _currentPositionMs.value = mediaPlayer?.currentPosition?.toLong() ?: 0L
                    } catch (_: Exception) {}
                } else {
                    _currentPositionMs.value = (_currentPositionMs.value + 500L) % _totalDurationMs.value
                }
                delay(500)
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        mediaPlayer?.pause()
        synthJob?.cancel()
        synthTrack?.pause()
        progressJob?.cancel()
    }

    fun resume() {
        val song = _currentSong.value ?: return
        if (song.isBuiltInSynth || song.uriString == null) {
            startRealSynthPlayback()
        } else {
            mediaPlayer?.start()
            _isPlaying.value = true
            startProgressTracker()
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else resume()
    }

    fun seekTo(positionMs: Long) {
        _currentPositionMs.value = positionMs
        mediaPlayer?.seekTo(positionMs.toInt())
    }

    fun stop() {
        _isPlaying.value = false
        progressJob?.cancel()
        synthJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null

        try {
            synthTrack?.stop()
            synthTrack?.release()
        } catch (_: Exception) {}
        synthTrack = null
    }
}
