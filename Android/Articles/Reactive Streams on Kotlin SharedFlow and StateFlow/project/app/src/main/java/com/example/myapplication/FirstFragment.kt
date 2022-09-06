package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch

class FirstFragment : Fragment() {

    lateinit var binding: FragmentFirstBinding
    private val notificationViewModel by activityViewModels<NotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        collectNotification()
        notificationViewModel.startNotificationState()

    }

    private fun initView() {

        binding.btnNavigationSecond.setOnClickListener {
            findNavController().navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment())
        }

        binding.btnNavigationThird.setOnClickListener {
            findNavController().navigate(FirstFragmentDirections.actionFirstFragmentToThirdFragment())
        }
    }

    private fun collectNotification() {

        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.sharedNotification.collect {
                Toast.makeText(requireContext(), "Notification $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

}