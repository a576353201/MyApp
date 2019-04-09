package com.example.androidapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.flutter.facade.Flutter
import io.flutter.view.FlutterView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.FrameLayout


class MainActivity : AppCompatActivity() {

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
        val layout =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 800)
        flContainer.addView(flutterView, layout)
    }

    private fun createFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.flContainer, Flutter.createFragment("route1"))
            .commit()
    }
}
