package com.guilhermegals.whatsnumber.feature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.guilhermegals.whatsnumber.R
import com.guilhermegals.whatsnumber.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // <editor-fold desc="[ Properties ]">

    /** Binding da Activity */
    private lateinit var binding : ActivityMainBinding

    // </editor-fold>

    // <editor-fold desc="[ Actions ]">

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflando o Layout da Activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Habilitando o Menu da ActionBar
        if (savedInstanceState == null) {
            setSupportActionBar(binding.activityMainTopToolbar)
        }

        // Abrindo o NumberFragment
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.activity_main_container,
                NumberFragment()
            ).commitNow()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setSupportActionBar(binding.activityMainTopToolbar)
    }

    // </editor-fold>
}