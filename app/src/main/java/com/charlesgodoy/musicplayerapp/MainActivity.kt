package com.charlesgodoy.musicplayerapp

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    var startTime = 0.0
    var finalTime = 0.0
    var forwardTime = 10000
    var backwardTime = 10000
    var oneTimeOnly = 0

    var handler = Handler()

    var mediaPlayer = MediaPlayer()

    lateinit var songTitleTV : TextView
    lateinit var timeLeft : TextView
    lateinit var seekBar : SeekBar

    lateinit var backBtn : Button
    lateinit var playBtn : Button
    lateinit var pauseBtn : Button
    lateinit var fowardBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()

        mediaPlayer = MediaPlayer.create(this, R.raw.good_night)

        seekBar.isClickable = false

        // Setting the music title
        val songResourceName = resources.getResourceEntryName(R.raw.good_night)
        val songFileName = songResourceName.replace("_", " ").split(" ")
            .joinToString(" ") { it.capitalize() }
        songTitleTV.text = songFileName

        // All buttons on its own func
        playButton()
        pauseButton()
        forwardButton()
        backButton()

    }

    private fun pauseButton() {
        pauseBtn.setOnClickListener() {
            mediaPlayer.pause()
        }
    }

    private fun backButton() {
        backBtn.setOnClickListener() {
            val temp = startTime
            if ((temp - backwardTime) >= 0) {
                startTime = startTime - backwardTime
                mediaPlayer.seekTo(startTime.toInt())
            } else {
                Toast.makeText(this, "Can't Jump Backward", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun forwardButton() {
        fowardBtn.setOnClickListener() {
            val temp = startTime
            if ((temp + forwardTime) <= finalTime) {
                startTime = startTime + forwardTime
                mediaPlayer.seekTo(startTime.toInt())
            } else {
                Toast.makeText(this, "Can't Jump forward", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun playButton() {
        playBtn.setOnClickListener() {
            try {
                mediaPlayer.start()

                finalTime = mediaPlayer.duration.toDouble()
                startTime = mediaPlayer.currentPosition.toDouble()

                if (oneTimeOnly == 0) {
                    seekBar.max = finalTime.toInt()
                    oneTimeOnly = 1
                }

                timeLeft.text = startTime.toString()
                seekBar.progress = startTime.toInt()

                handler.postDelayed(UpdateSongTime, 100)
            } catch (e: Exception) {
                // Handle the exception here
                e.printStackTrace()
                Toast.makeText(this, "Error playing music", Toast.LENGTH_LONG).show()
            }
        }
    }


    // Initialize Views here
    private fun initializeViews() {
        songTitleTV = findViewById(R.id.song_title)
        timeLeft = findViewById(R.id.time_left_text)
        seekBar = findViewById(R.id.seek_bar)

        backBtn = findViewById(R.id.rewind_button)
        playBtn = findViewById(R.id.play_button)
        pauseBtn = findViewById(R.id.pause_button)
        fowardBtn = findViewById(R.id.forward_button)
    }

    // Creating Runnable
    val UpdateSongTime : Runnable = object : Runnable {
        override fun run() {
            startTime = mediaPlayer.currentPosition.toDouble()
            timeLeft.text = "" +
                    String.format(
                        "%d min , %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(startTime.toLong() - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                startTime.toLong()
                            )
                        ))
                    )

            seekBar.progress = startTime.toInt()
            handler.postDelayed(this, 100)

        }
    }
}