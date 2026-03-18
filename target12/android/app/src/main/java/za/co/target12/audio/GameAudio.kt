package za.co.target12.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class GameAudio {
    private val sampleRate = 22050
    private val fireBuffer: ShortArray
    private val boundaryBuffer: ShortArray
    private val bumpBuffer: ShortArray

    private var fireTrack: AudioTrack? = null
    private var boundaryTrack: AudioTrack? = null
    private var bumpTrack: AudioTrack? = null

    init {
        fireBuffer = generateFireSound()
        boundaryBuffer = generateTone(200f, 50, 0.15f)
        bumpBuffer = generateTone(50f, 30, 0.15f)
    }

    fun init() {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val format = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        fireTrack = createTrack(attrs, format, fireBuffer)
        boundaryTrack = createTrack(attrs, format, boundaryBuffer)
        bumpTrack = createTrack(attrs, format, bumpBuffer)
    }

    private fun createTrack(attrs: AudioAttributes, format: AudioFormat, buffer: ShortArray): AudioTrack {
        val track = AudioTrack.Builder()
            .setAudioAttributes(attrs)
            .setAudioFormat(format)
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(buffer, 0, buffer.size)
        return track
    }

    fun playFire() {
        playTrack(fireTrack, fireBuffer)
    }

    fun playBoundary() {
        playTrack(boundaryTrack, boundaryBuffer)
    }

    fun playBump() {
        playTrack(bumpTrack, bumpBuffer)
    }

    private fun playTrack(track: AudioTrack?, @Suppress("UNUSED_PARAMETER") buffer: ShortArray) {
        track?.let {
            try {
                it.stop()
            } catch (_: IllegalStateException) {}
            it.reloadStaticData()
            it.play()
        }
    }

    fun release() {
        fireTrack?.release()
        boundaryTrack?.release()
        bumpTrack?.release()
        fireTrack = null
        boundaryTrack = null
        bumpTrack = null
    }

    private fun generateFireSound(): ShortArray {
        // Layer 1: white noise burst 80ms, gain 0.4 → decay
        // Layer 2: sine sweep 150→50Hz over 200ms, gain 0.3 → decay
        val durationMs = 200
        val samples = sampleRate * durationMs / 1000
        val buffer = ShortArray(samples)
        val noiseSamples = sampleRate * 80 / 1000

        for (i in 0 until samples) {
            val t = i.toFloat() / sampleRate
            var sample = 0f

            // Noise layer (80ms)
            if (i < noiseSamples) {
                val noiseDecay = exp(-t / 0.02f) * 0.4f
                sample += (Random.nextFloat() * 2f - 1f) * noiseDecay
            }

            // Sine sweep layer (200ms)
            val sweepProgress = t / 0.2f
            val freq = 150f - 100f * sweepProgress // 150→50Hz
            val boomDecay = exp(-t / 0.06f) * 0.3f
            sample += sin(2f * PI.toFloat() * freq * t) * boomDecay

            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateTone(freq: Float, durationMs: Int, gain: Float): ShortArray {
        val samples = sampleRate * durationMs / 1000
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toFloat() / sampleRate
            val sample = sin(2f * PI.toFloat() * freq * t) * gain
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
