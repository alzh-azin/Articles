package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch


class SecondFragment : Fragment() {

    lateinit var binding : FragmentSecondBinding
    private val notificationViewModel by activityViewModels<NotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNotification()
        notificationViewModel.startSharedNotification()
    }

    private fun collectNotification() {

        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.sharedNotification.collect {
                Toast.makeText(requireContext(), "Notification $it", Toast.LENGTH_SHORT).show()
            }
        }
    }


}