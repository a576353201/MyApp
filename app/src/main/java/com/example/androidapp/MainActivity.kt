package com.example.androidapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.flutter.facade.Flutter
import io.flutter.view.FlutterView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.FrameLayout
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    val CHANNEL_NAME = "com.example.androidapp.channel_second"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnFlutterCreateView.setOnClickListener {
            createView()
        }
        btnFlutterFragment.setOnClickListener {

            createFragment()
        }
    }

    private fun createView() {
        val flutterView: FlutterView = Flutter.createView(this, lifecycle, "route1")

        /**
         * 和flutter进行交互
         */
        MethodChannel(flutterView, CHANNEL_NAME).setMethodCallHandler(
                object : MethodChannel.MethodCallHandler {
                    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
                        Log.d(TAG, "onMethodCall: ")
                        methodCall(call, result)
                    }
                }
        )
        val layout =
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 800)
        flContainer.addView(flutterView, layout)

    }

    private fun createFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.flContainer, Flutter.createFragment(null))
                .commit()
    }

    private fun methodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "open_second_activity") {
            SecondActivity.launch(this)
            result.success(true)
        } else {
            result.notImplemented()
        }
    }

}
