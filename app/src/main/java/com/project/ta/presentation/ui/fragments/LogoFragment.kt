package com.project.ta.presentation.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.ta.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LogoFragment: Fragment(R.layout.fragment_logo) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performDefault()
    }

    private fun performDefault(){
        lifecycleScope.launch {
            delay(2000)
            nav_host_fragment_container.findNavController().navigate(R.id.mainFragment)
        }

    }
}