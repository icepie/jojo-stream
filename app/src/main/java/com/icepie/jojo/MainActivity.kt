package com.icepie.jojo


import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera1
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.rtmp.RtmpStream


class MainActivity : AppCompatActivity(),ConnectCheckerRtmp,SurfaceHolder.Callback,View.OnClickListener {

    lateinit var  startStream:Button
    lateinit var  endStream:Button
    lateinit var rtmpCamera1: RtmpCamera1

    private var rtmpCamera: RtmpStream? = null

    val rtmpLink2="rtmp://test.com:2017/live/rfBd56ti2SMtYvSgD5xAV0YU99zampta7Z7S575KLkIZ9PYk"
    //    val rtmpLink = "rtmp://229cc9ff3650.global-contribute.live-video.net" +"/app/" + "sk_eu-west-1_Rq7qRVgIZ2Mb_l5gVxA1LfAACWsJmwmEtr3xcG6VXk9"
    val TAG = "mainActivivty"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startStream = findViewById(R.id.start_stream)
        endStream = findViewById(R.id.end_stream)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Log.i(TAG, "onCreate: "+rtmpLink2)

        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        rtmpCamera1 = RtmpCamera1(surfaceView, this@MainActivity)

        var bitrate = 128
        var sampleRate=48000
        var isStereo = true
        var echoCanceler = true
        var noiseSuppressor =true
        var width = 200
        var height = 200
        var fps = 24
        var rotation=180

        rtmpCamera1.switchCamera(1)


        if (rtmpCamera1.prepareAudio(bitrate, sampleRate, isStereo,  echoCanceler,
                noiseSuppressor) && rtmpCamera1.prepareVideo(width,  height,  fps,  bitrate,  rotation)) {

        }

        startStream.setOnClickListener(View.OnClickListener {

            //start stream

            if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
                rtmpCamera1.startStream(rtmpLink2)
            } else {
                /**This device cant init encoders, this could be for 2 reasons: The encoder selected doesnt support any configuration setted or your device hasnt a H264 or AAC encoder (in this case you can see log error valid encoder not found) */
            }
        })

        //stop stream
        endStream.setOnClickListener {
            if (rtmpCamera1.isStreaming) {
                rtmpCamera1.stopStream()
            } else {
                Toast.makeText(
                    this@MainActivity, "stop streaming in process",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    override fun onAuthErrorRtmp() {
        runOnUiThread {
            Toast.makeText(this@MainActivity, "Auth error", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onAuthSuccessRtmp() {
        runOnUiThread {
            Toast.makeText(this@MainActivity, "Auth success", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onConnectionFailedRtmp(reason: String) {
        runOnUiThread {
            if (rtmpCamera1.reTry(5000, reason)) {
                Toast.makeText(this@MainActivity, "Retry", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Connection failed. $reason",
                    Toast.LENGTH_SHORT
                )
                    .show()
                rtmpCamera1.stopStream()
            }
        }
    }

    override fun onConnectionStartedRtmp(rtmpUrl: String) {
        Toast.makeText(
            this@MainActivity,
            "Connection started",
            Toast.LENGTH_SHORT
        ).show()
    }


    override fun onConnectionSuccessRtmp() {
        runOnUiThread {
            Toast.makeText(
                this@MainActivity,
                "Connection success",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDisconnectRtmp() {
        runOnUiThread {
            Toast.makeText(this@MainActivity, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewBitrateRtmp(bitrate: Long) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        rtmpCamera1.startPreview();
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
        }
        rtmpCamera1.stopPreview();
    }

    override fun onClick(v: View?) {
//        when(v?.id){
//            R.id.start_stream -> {
//                if(!rtmpCamera1.isStreaming){
//                    rtmpCamera1.startStream(rtmpLink2);
//                }
//                else {
//                    Toast.makeText(this@MainActivity, "Already streaming",
//                        Toast.LENGTH_SHORT).show();
//                }
//            }
//            R.id.end_stream -> {
//                if(rtmpCamera1.isStreaming){
//                    rtmpCamera1.stopStream()
//                }
//                else {
//                    Toast.makeText(this@MainActivity, "Already streaming",
//                        Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        }
    }


}
