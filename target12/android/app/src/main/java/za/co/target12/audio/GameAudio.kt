package za.co.target12.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object GameAudio {
    private const val SAMPLE_RATE = 44100

    private fun playBuffer(samples: FloatArray) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 4)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)
        track.setNotificationMarkerPosition(samples.size)
        track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(t: AudioTrack) { t.release() }
            override fun onPeriodicNotification(t: AudioTrack) {}
        })
        track.play()
    }

    fun playFireSound() {
        Thread {
            val durationMs = 200
            val numSamples = SAMPLE_RATE * durationMs / 1000
            val samples = FloatArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val tMs = t * 1000f

                // Layer 1: crack (white noise, 80ms, gain 0.4 exp decay)
                var s = 0f
                if (tMs < 80f) {
                    val env = 0.4f * exp(-tMs / 15f)
                    s += (Random.nextFloat() * 2f - 1f) * env
                }

                // Layer 2: boom (sine sweep 150→50 Hz over 200ms)
                val freq = 150f - (150f - 50f) * (tMs / 200f).coerceIn(0f, 1f)
                val boomEnv = 0.3f * exp(-tMs / 40f)
                s += sin(2f * PI.toFloat() * freq * t) * boomEnv

                samples[i] = s.coerceIn(-1f, 1f)
            }
            playBuffer(samples)
        }.start()
    }

    fun playBoundarySound() {
        Thread {
            val numSamples = SAMPLE_RATE * 50 / 1000
            val samples = FloatArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                samples[i] = (sin(2f * PI.toFloat() * 200f * t) * 0.15f).coerceIn(-1f, 1f)
            }
            playBuffer(samples)
        }.start()
    }

    fun playBumpSound() {
        Thread {
            val numSamples = SAMPLE_RATE * 30 / 1000
            val samples = FloatArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                samples[i] = (sin(2f * PI.toFloat() * 50f * t) * 0.15f).coerceIn(-1f, 1f)
            }
            playBuffer(samples)
        }.start()
    }
}
