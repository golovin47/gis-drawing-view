package com.gis.gisdrawingboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gis.featuredrawingboard.presentation.ui.drawingscreen.DrawingFragment

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (savedInstanceState == null)
      supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DrawingFragment()).commit()
  }
}
