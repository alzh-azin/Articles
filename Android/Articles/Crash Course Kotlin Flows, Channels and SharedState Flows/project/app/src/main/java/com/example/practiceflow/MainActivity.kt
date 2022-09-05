package com.example.practiceflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private val myViewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        collection()

        myViewModel.emitNavigationState(5)
        myViewModel.emitNavigationEvent(14)
    }

    private fun collection() {
        lifecycleScope.launchWhenStarted {
            myViewModel.regularFlow.collect {
                Log.d("PracticeFlowLogs", "regular flow -> $it ")
            }

            myViewModel.navigationState.collect {
                Log.d("PracticeFlowLogs", "state flow -> destination $it ")
            }
        }

        lifecycleScope.launchWhenStarted {
            myViewModel.navigationEvent.collect{
                Log.d("PracticeFlowLogs", "shared flow -> destination $it ")
            }
        }


    }
}