package com.project.ta.presentation.ui.activities
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.project.ta.R
import com.project.ta.domain.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {

    private val mapViewModel : MapViewModel by viewModels()
//    private lateinit var fragment: Fragment
//    private lateinit var b: Bundle

//    private lateinit var nearestLocationDetails: List<NearestLocationDetails>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nav_host_fragment_container.findNavController().navigate(R.id.mainFragment)
//        fragment = MainFragment()
//        b = Bundle()
//        b.putString("hello","Hello");
//        changeFragment(fragment,b)
    }
//    fun changeFragment(fragment: Fragment, b: Bundle?) {
//        fragment.arguments = b
//        val fragmentManager: FragmentManager = supportFragmentManager
//        val transaction = fragmentManager.beginTransaction()
//        transaction.replace(R.id.flFragment, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//
//    }


}


