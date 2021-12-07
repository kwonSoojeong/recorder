package com.crystal.mediarecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var recorder: MediaRecorder? = null
    private val fileName: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }
    private var player: MediaPlayer? = null

    private val recordButton:RecordButton by lazy {
        findViewById(R.id.recordButton)
    }
    private val resetButton: Button by lazy{
        findViewById(R.id.resetButton)
    }
    private var state = State.BEFORE_RECODING
        set(value){
            field = value
            resetButton.isEnabled = (value== State.AFTER_RECODING) || (value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }

    private val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAudioPermission()
        initView()
        bindViews()
        initVariables()
    }

    private fun initVariables() {
        state = State.BEFORE_RECODING
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        if(!audioRecordPermissionGranted){
            finish()
        }
    }
    private fun requestAudioPermission(){
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initView() {
        recordButton.updateIconWithState(state)

    }

    private fun bindViews(){
        resetButton.setOnClickListener {
            stopPlaying()
            state = State.BEFORE_RECODING
        }
        recordButton.setOnClickListener{
            when(state){
                State.BEFORE_RECODING -> {
                    startRecording()
                }
                State.ON_RECODING -> stopRecoding()
                State.AFTER_RECODING -> startPlaying()
                State.ON_PLAYING -> stopPlaying()
            }
        }
    }
    private fun startRecording(){
        recorder = MediaRecorder().apply{
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            prepare()
        }
        recorder?.start()
        state = State.ON_RECODING
    }

    private fun stopRecoding(){
        recorder?.run{
            stop()
            release()
        }
        recorder = null
        state = State.AFTER_RECODING
    }

    private fun startPlaying(){
        player = MediaPlayer().apply {
            setDataSource(fileName)
            prepare()//파일이 커지면 비동기로 어싱크 프리페어를 사용해서 ui 작업을 해줄필요가 있음.
        }
        player?.start()
        state = State.ON_PLAYING
    }
    private fun stopPlaying(){
        player?.release() //스탑이 불림
        player = null
        state = State.AFTER_RECODING
    }
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}