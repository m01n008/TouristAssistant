package com.project.ta.presentation.ui.activities
import android.app.Activity
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.ta.R
import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.data.datasource.local.AttractionsDao
import com.project.ta.domain.MapViewModel
import com.project.ta.presentation.ui.LocationListAdapter
import com.project.ta.presentation.ui.fragments.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

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


