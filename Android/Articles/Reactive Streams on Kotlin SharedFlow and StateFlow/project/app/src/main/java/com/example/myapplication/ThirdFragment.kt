package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentThirdBinding
import kotlinx.coroutines.launch


class ThirdFragment : Fragment() {

    lateinit var binding: FragmentThirdBinding
    val notificationViewModel by activityViewModels<NotificationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNotification()
    }

    private fun collectNotification() {

        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.notificationState.collect {
                binding.tvNotification.text = "Notification $it"
            }

        }
    }

}