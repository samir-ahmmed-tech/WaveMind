package com.example.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
    @Volatile private var masterEnabled = true
    @Volatile private var tapSoundEnabled = true
    @Volatile private var tapVolumeScale = 1.0f
    @Volatile private var victoryEnabled = true
    @Volatile private var buzzerEnabled = true
    @Volatile private var soundVolumeScale = 1.0f
    @Volatile private var soundPack = "Sci-Fi UI"

    // Background Music Engine
    @Volatile private var bgMusicEnabled = false
    @Volatile private var bgMusicVolumeScale = 0.7f
    @Volatile private var bgMusicTrack = "963Hz Solfeggio Cyber Zen"
    private var bgMusicJob: Job? = null

    private const val SAMPLE_RATE = 22050
    private val scope = CoroutineScope(Dispatchers.Default)
    private val audioQueue = Channel<ShortArray>(Channel.BUFFERED)

    @Volatile private var audioTrack: AudioTrack? = null
    @Volatile private var isTrackInitialized = false
    @Volatile private var audioTrackFailed = false

    init {
        scope.launch {
            try {
                initStreamingTrack()
                for (buffer in audioQueue) {
                    val track = audioTrack
                    if (track != null && track.state == AudioTrack.STATE_INITIALIZED) {
                        try {
                            track.write(buffer, 0, buffer.size)
                        } catch (_: Throwable) {}
                    }
                }
            } catch (_: Throwable) {
                audioTrackFailed = true
            }
        }
    }

    private fun initStreamingTrack() {
        if (isTrackInitialized) return
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(SAMPLE_RATE / 4)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            track.play()
            audioTrack = track
            isTrackInitialized = true
        } catch (_: Throwable) {
            audioTrackFailed = true
        }
    }

    fun updateConfig(
        master: Boolean,
        click: Boolean,
        victory: Boolean,
        buzzer: Boolean,
        volume: Float = 100f,
        pack: String = "Sci-Fi UI"
    ) {
        masterEnabled = master
        tapSoundEnabled = click
        victoryEnabled = victory
        buzzerEnabled = buzzer
        soundVolumeScale = (volume / 100f).coerceIn(0f, 1f)
        soundPack = pack
    }

    fun updateAdvancedAudio(
        master: Boolean,
        tapEnabled: Boolean,
        tapVolume: Float,
        soundVolume: Float,
        bgEnabled: Boolean,
        bgVolume: Float,
        bgTrack: String,
        pack: String
    ) {
        masterEnabled = master
        tapSoundEnabled = tapEnabled
        tapVolumeScale = (tapVolume / 100f).coerceIn(0f, 1f)
        soundVolumeScale = (soundVolume / 100f).coerceIn(0f, 1f)
        bgMusicEnabled = bgEnabled
        bgMusicVolumeScale = (bgVolume / 100f).coerceIn(0f, 1f)
        bgMusicTrack = bgTrack
        soundPack = pack

        syncBgMusic()
    }

    private fun syncBgMusic() {
        if (!masterEnabled || !bgMusicEnabled || bgMusicVolumeScale <= 0f) {
            bgMusicJob?.cancel()
            bgMusicJob = null
            return
        }

        if (bgMusicJob == null || !bgMusicJob!!.isActive) {
            bgMusicJob = scope.launch {
                var step = 0
                while (isActive && masterEnabled && bgMusicEnabled) {
                    when (bgMusicTrack) {
                        "963Hz Solfeggio Cyber Zen" -> {
                            // Soothing 963Hz crown chakra tone combined with 528Hz love frequency drone
                            val baseFreq = if (step % 4 == 0) 963f else 528f
                            enqueueAmbientChord(floatArrayOf(baseFreq, baseFreq * 1.5f), 1200, 0.12f * bgMusicVolumeScale)
                            delay(1250)
                        }
                        "Sports Rock Energizer" -> {
                            // Driving 130 BPM syncopated synth-bass power rhythm
                            val notes = floatArrayOf(110f, 130.81f, 164.81f, 146.83f, 110f, 164.81f, 196f, 146.83f)
                            val note = notes[step % notes.size]
                            enqueueBassPunch(note, 220, 0.22f * bgMusicVolumeScale)
                            delay(230)
                        }
                        "Sci-Fi UI Ambient" -> {
                            // Space exploration harmonic sweep
                            val pads = floatArrayOf(220f, 277.18f, 329.63f, 440f)
                            enqueueAmbientChord(pads, 1400, 0.10f * bgMusicVolumeScale)
                            delay(1500)
                        }
                        else -> { // Neo Synthwave Groove
                            val synthNotes = floatArrayOf(130.81f, 164.81f, 196f, 246.94f)
                            enqueueTone(synthNotes[step % synthNotes.size], 180, 0.18f * bgMusicVolumeScale)
                            delay(200)
                        }
                    }
                    step++
                }
            }
        }
    }

    fun playClick() = playTap()

    fun playTap() {
        if (!masterEnabled || !tapSoundEnabled || audioTrackFailed || tapVolumeScale <= 0f) return
        val amp = 0.26f * tapVolumeScale * soundVolumeScale
        when (soundPack) {
            "Sci-Fi UI", "universfield" -> {
                // High-tech sci-fi UI chirp & transient tick
                enqueueTone(1350f, durationMs = 12, amplitude = amp)
                enqueueTone(2200f, durationMs = 18, amplitude = amp * 0.7f)
            }
            "Soundshelf", "Cyber Click" -> {
                // Mechanical crisp tactile cyber click
                enqueueTone(880f, durationMs = 15, amplitude = amp)
                enqueueTone(440f, durationMs = 10, amplitude = amp * 0.5f)
            }
            "Neo Synthwave" -> {
                enqueueTone(720f, durationMs = 24, amplitude = amp)
            }
            "Crystal Zen" -> {
                enqueueTone(1480f, durationMs = 32, amplitude = amp * 0.8f)
            }
            else -> { // Cyber Arcade
                enqueueTone(960f, durationMs = 22, amplitude = amp)
            }
        }
    }

    fun playSciFiUiClick() = playTap()

    fun playSuccess() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.25f * soundVolumeScale
        enqueueTone(587.33f, durationMs = 38, amplitude = amp)
        enqueueTone(880f, durationMs = 50, amplitude = amp * 1.1f)
    }

    fun playRoundComplete() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.28f * soundVolumeScale
        enqueueTone(523.25f, durationMs = 40, amplitude = amp)
        enqueueTone(659.25f, durationMs = 40, amplitude = amp)
        enqueueTone(783.99f, durationMs = 40, amplitude = amp)
        enqueueTone(1046.50f, durationMs = 80, amplitude = amp * 1.2f)
    }

    fun playCombo(streak: Int = 1) {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val pitchMultiplier = (1.0f + (streak.coerceIn(1, 10) * 0.08f))
        val amp = 0.28f * soundVolumeScale
        enqueueTone(600f * pitchMultiplier, durationMs = 35, amplitude = amp)
        enqueueTone(900f * pitchMultiplier, durationMs = 60, amplitude = amp * 1.1f)
    }

    fun playPowerUp() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.26f * soundVolumeScale
        enqueueTone(440f, durationMs = 30, amplitude = amp)
        enqueueTone(659.25f, durationMs = 40, amplitude = amp)
        enqueueTone(987.77f, durationMs = 50, amplitude = amp)
        enqueueTone(1318.51f, durationMs = 80, amplitude = amp * 1.2f)
    }

    fun playCoin() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.28f * soundVolumeScale
        enqueueTone(987.77f, durationMs = 35, amplitude = amp)
        enqueueTone(1318.51f, durationMs = 60, amplitude = amp * 1.15f)
    }

    fun playStreak() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.24f * soundVolumeScale
        enqueueTone(523.25f, durationMs = 35, amplitude = amp)
        enqueueTone(659.25f, durationMs = 35, amplitude = amp)
        enqueueTone(1046.50f, durationMs = 70, amplitude = amp * 1.2f)
    }

    fun playLevelUp() {
        if (!masterEnabled || !victoryEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.30f * soundVolumeScale
        enqueueTone(440f, durationMs = 45, amplitude = amp)
        enqueueTone(554.37f, durationMs = 45, amplitude = amp)
        enqueueTone(659.25f, durationMs = 50, amplitude = amp)
        enqueueTone(880f, durationMs = 95, amplitude = amp * 1.25f)
    }

    fun playTargetCompleted() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.28f * soundVolumeScale
        enqueueTone(659.25f, durationMs = 45, amplitude = amp)
        enqueueTone(783.99f, durationMs = 45, amplitude = amp)
        enqueueTone(1046.50f, durationMs = 85, amplitude = amp * 1.2f)
    }

    fun playError() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        enqueueTone(240f, durationMs = 65, amplitude = 0.28f * soundVolumeScale)
    }

    fun playBuzzer() {
        if (!masterEnabled || !buzzerEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        enqueueTone(180f, durationMs = 95, amplitude = 0.34f * soundVolumeScale)
    }

    fun playGameOver() {
        if (!masterEnabled || !buzzerEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.32f * soundVolumeScale
        enqueueTone(330f, durationMs = 80, amplitude = amp)
        enqueueTone(260f, durationMs = 90, amplitude = amp)
        enqueueTone(190f, durationMs = 140, amplitude = amp * 1.1f)
    }

    fun playVictory() {
        if (!masterEnabled || !victoryEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        val amp = 0.28f * soundVolumeScale
        enqueueTone(523.25f, durationMs = 50, amplitude = amp)
        enqueueTone(659.25f, durationMs = 50, amplitude = amp)
        enqueueTone(783.99f, durationMs = 55, amplitude = amp)
        enqueueTone(1046.50f, durationMs = 110, amplitude = amp * 1.3f)
    }

    fun playTick() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        enqueueTone(1200f, durationMs = 14, amplitude = 0.15f * soundVolumeScale)
    }

    fun playSliderTick(fraction: Float = 0.5f) {
        if (!masterEnabled || !tapSoundEnabled || audioTrackFailed || tapVolumeScale <= 0f) return
        val pitch = 600f + (fraction.coerceIn(0f, 1f) * 1200f)
        enqueueTone(pitch, durationMs = 12, amplitude = 0.14f * tapVolumeScale * soundVolumeScale)
    }

    fun playWhoosh() {
        if (!masterEnabled || audioTrackFailed || soundVolumeScale <= 0f) return
        enqueueTone(420f, durationMs = 30, amplitude = 0.18f * soundVolumeScale)
        enqueueTone(840f, durationMs = 35, amplitude = 0.22f * soundVolumeScale)
    }

    fun triggerHaptic(context: Context, type: String = "light") {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type.lowercase()) {
                    "heavy" -> VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                    "medium" -> VibrationEffect.createOneShot(25, 140)
                    "double" -> VibrationEffect.createWaveform(longArrayOf(0, 20, 40, 25), -1)
                    else -> VibrationEffect.createOneShot(12, 90)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (_: Throwable) {}
    }

    private fun enqueueTone(frequency: Float, durationMs: Int, amplitude: Float = 0.3f) {
        if (audioTrackFailed) return
        val numSamples = (durationMs * SAMPLE_RATE) / 1000
        if (numSamples <= 0) return
        val buffer = ShortArray(numSamples)
        val useHarmonic = soundPack == "Neo Synthwave"

        for (i in 0 until numSamples) {
            val envelope = 1.0f - (i.toFloat() / numSamples)
            val primaryWave = sin(2.0 * Math.PI * i * frequency / SAMPLE_RATE)
            val wave = if (useHarmonic) {
                val overtone = sin(4.0 * Math.PI * i * frequency / SAMPLE_RATE) * 0.25
                primaryWave * 0.75 + overtone
            } else {
                primaryWave
            }
            val sample = wave * envelope * amplitude
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }

        audioQueue.trySend(buffer)
    }

    private fun enqueueBassPunch(frequency: Float, durationMs: Int, amplitude: Float = 0.25f) {
        if (audioTrackFailed) return
        val numSamples = (durationMs * SAMPLE_RATE) / 1000
        if (numSamples <= 0) return
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val pitchDecay = frequency * (1.0f + (1.0f - progress) * 0.8f)
            val envelope = (1.0f - progress) * (1.0f - progress)
            val wave = sin(2.0 * Math.PI * i * pitchDecay / SAMPLE_RATE)
            val sample = wave * envelope * amplitude
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }

        audioQueue.trySend(buffer)
    }

    private fun enqueueAmbientChord(frequencies: FloatArray, durationMs: Int, amplitude: Float = 0.12f) {
        if (audioTrackFailed) return
        val numSamples = (durationMs * SAMPLE_RATE) / 1000
        if (numSamples <= 0) return
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            // Smooth attack and release
            val envelope = if (progress < 0.2f) (progress / 0.2f) else (1.0f - (progress - 0.2f) / 0.8f)
            var mixed = 0.0
            for (f in frequencies) {
                mixed += sin(2.0 * Math.PI * i * f / SAMPLE_RATE)
            }
            mixed /= frequencies.size.coerceAtLeast(1)
            val sample = mixed * envelope * amplitude
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }

        audioQueue.trySend(buffer)
    }
}
