package com.crystal.mediarecorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private val recordButton:RecordButton by lazy {
        findViewById(R.id.recordButton)
    }
    private var state = State.BEFORE_RECODING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        recordButton.updateIconWithState(state);
    }

}