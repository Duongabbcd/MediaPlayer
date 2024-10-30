package com.example.mediaplayer.fragment.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mediaplayer.adapter.SampleItemEffector
import com.example.mediaplayer.adapter.SampleScrollStateListener
import com.example.mediaplayer.adapter.TimePickerAdapter
import com.example.mediaplayer.databinding.FragmentTimePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TimePickerFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentTimePickerBinding

    private lateinit var adapter1: TimePickerAdapter
    private lateinit var adapter2: TimePickerAdapter
    private lateinit var adapter3: TimePickerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimePickerBinding.inflate(inflater, container, false)
        adapter1 = TimePickerAdapter()
        adapter2 = TimePickerAdapter()
        adapter3 = TimePickerAdapter()

        binding.wheelPicker1.adapter = adapter1
        binding.wheelPicker2.adapter = adapter2
        binding.wheelPicker3.adapter = adapter3

        binding.wheelPicker1.addItemEffector(
            SampleItemEffector(
                requireContext(),
                binding.wheelPicker1
            )
        )
        binding.wheelPicker2.addItemEffector(
            SampleItemEffector(
                requireContext(),
                binding.wheelPicker2
            )
        )
        binding.wheelPicker3.addItemEffector(
            SampleItemEffector(
                requireContext(),
                binding.wheelPicker3
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.wheelPicker1.addOnItemSelectedListener { _, position ->
            val n1 = adapter1.items[position]
            Toast.makeText(requireContext(), "Selected $n1", Toast.LENGTH_SHORT).show()
        }
    }
}