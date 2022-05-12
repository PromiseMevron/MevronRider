package com.mevron.rides.rider



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mevron.rides.rider.auth.AuthActivity

class IntroScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)
        val button = findViewById<Button>(R.id.start_button)
        button.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}