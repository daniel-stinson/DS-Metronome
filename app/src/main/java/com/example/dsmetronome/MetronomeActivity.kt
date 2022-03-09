package com.example.dsmetronome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_metronome.*
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_main.*

class MetronomeActivity : AppCompatActivity() {

    // These private variables help keep track of increasing/decreasing the BPM
    private var autoIncrement: Boolean = false
    private var autoDecrement: Boolean = false
    private var repeatUpdateHandler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metronome)

        class RepetitiveUpdater : Runnable {
            override fun run() {
                if (autoIncrement) {
                    updateBPM(true)
                    repeatUpdateHandler.postDelayed(RepetitiveUpdater(), 50L)
                } else if (autoDecrement) {
                    updateBPM(false)
                    repeatUpdateHandler.postDelayed(RepetitiveUpdater(), 50L)
                }
            }
        }

        currentBPM.setOnTouchListener {_, _ ->
            currentBPM.isCursorVisible = true

            false
        }

        currentBPM.setOnEditorActionListener { _, keyCode: Int?, _ ->
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                currentBPM.isCursorVisible = false
            }

            false
        }

        currentBPM.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val currentBpm = getCurrentBpm()
                showCurrentBpmError(currentBpm == null || !checkBpmBounds(currentBpm))
                updateBpmButtons()
            }
        })

        metronomeToggle.setOnCheckedChangeListener { _, isChecked ->
            updateMetronomeStatus(isChecked)
        }

        increaseBPM.setOnClickListener {
            updateBPM(true)
        }
        increaseBPM.setOnLongClickListener {
            autoIncrement = true
            repeatUpdateHandler.post(RepetitiveUpdater())
            false
        }
        increaseBPM.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP && autoIncrement) {
                autoIncrement = false
            }
            false
        }

        decreaseBPM.setOnClickListener {
            updateBPM(false)
        }
        decreaseBPM.setOnLongClickListener {
            autoDecrement = true
            repeatUpdateHandler.post(RepetitiveUpdater())
            false
        }
        decreaseBPM.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP && autoDecrement) {
                autoDecrement = false
            }
            false
        }

        // Initializes buttons with correct tags
        updateBpmButtons()

        SNameId.text=intent.getStringExtra("name")

    }


    private fun updateMetronomeStatus(turnOn: Boolean) {
        if (turnOn) {
            val currentBpm = getCurrentBpm()
            if (currentBpm != null && checkBpmBounds(currentBpm)) {
                Metronome.start(currentBpm)
            }
        } else {
            Metronome.stop()
        }

        updateBpmButtons()
    }

    private fun getCurrentBpm(): BPM? {
        val currentBpmEditText = findViewById<EditText>(R.id.currentBPM)

        return if (currentBpmEditText.text.isNotEmpty()) {
            currentBpmEditText.text.toString().toLong()
        } else {
            null
        }
    }

    private fun checkBpmBounds(bpm: BPM): Boolean {
        return (bpm in Metronome.BPM_LOWER_THRESHOLD..Metronome.BPM_UPPER_THRESHOLD)
    }


    private fun showCurrentBpmError(showError: Boolean) {
        currentBPM.error = if (showError) "BPM must be between ${Metronome.BPM_LOWER_THRESHOLD} and ${Metronome.BPM_UPPER_THRESHOLD}" else null
    }

    private fun enableIncreaseBpm() {
        increaseBPM.isEnabled = true
    }

    private fun enableDecreaseBpm() {
        decreaseBPM.isEnabled = true
    }

    private fun enableCurrentBpm() {
        currentBPM.isEnabled = true
    }

    private fun enableMetronomeToggle() {
        metronomeToggle.isEnabled = true
        if (Metronome.isOn()) {

        } else {

        }
    }


    private fun disableIncreaseBpm() {
        autoIncrement = false
        increaseBPM.isEnabled = false
    }

    private fun disableDecreaseBpm() {
        autoDecrement = false
        decreaseBPM.isEnabled = false
    }

    private fun disableCurrentBpm() {
        currentBPM.isEnabled = false
    }


    private fun disableMetronomeToggle() {
        metronomeToggle.isEnabled = false
    }


    private fun updateBpmButtons() {
        val currentBpm = getCurrentBpm()

        when {
            currentBpm == null -> {
                disableIncreaseBpm()
                disableDecreaseBpm()
                disableMetronomeToggle()
            }
            currentBpm < Metronome.BPM_LOWER_THRESHOLD -> {
                enableIncreaseBpm()

                disableDecreaseBpm()
                disableMetronomeToggle()
            }
            currentBpm == Metronome.BPM_LOWER_THRESHOLD -> {
                enableMetronomeToggle()
                if (Metronome.isOff()) {
                    enableIncreaseBpm()
                    disableDecreaseBpm()
                    enableCurrentBpm()
                } else {
                    disableIncreaseBpm()
                    disableDecreaseBpm()
                    disableCurrentBpm()
                }
            }
            currentBpm < Metronome.BPM_UPPER_THRESHOLD -> {
                enableMetronomeToggle()
                if (Metronome.isOff()) {
                    enableIncreaseBpm()
                    enableDecreaseBpm()
                    enableCurrentBpm()
                } else {
                    disableIncreaseBpm()
                    disableDecreaseBpm()
                    disableCurrentBpm()
                }
            }
            currentBpm == Metronome.BPM_UPPER_THRESHOLD -> {
                enableMetronomeToggle()
                if (Metronome.isOff()) {
                    disableIncreaseBpm()
                    enableDecreaseBpm()
                    enableCurrentBpm()
                } else {
                    disableIncreaseBpm()
                    disableDecreaseBpm()
                    disableCurrentBpm()
                }
            }
            else -> {
                disableIncreaseBpm()
                enableDecreaseBpm()
                disableMetronomeToggle()
            }
        }
    }

    private fun updateBPM(increase: Boolean) {
        val currentBpm = getCurrentBpm()

        if (currentBpm != null) {
            val newBpm = if (increase) currentBpm + 1 else currentBpm - 1
            val allowUpdate = if (increase) newBpm <= Metronome.BPM_UPPER_THRESHOLD else newBpm >= Metronome.BPM_LOWER_THRESHOLD

            if (allowUpdate) {
                currentBPM.setText(newBpm.toString())
                showCurrentBpmError(!checkBpmBounds(newBpm))
            }
        }
    }

    //Goes to the preferences page
    fun preferencesPage(view: View){
        var intent = Intent(this,PreferencesActivity::class.java)
        startActivity(intent)
    }
}
