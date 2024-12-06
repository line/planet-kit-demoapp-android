package com.linecorp.planetkit.demo.uikit.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.linecorp.planetkit.demo.uikit.databinding.FragmentTwoButtonDialogBinding

class TwoButtonFragmentDialog(
    private val title: String,
    private val contents: String,
    private val leftButtonText: String,
    private val rightButtonText: String,
    private val onClickLeftButton: () -> Unit,
    private val onClickRightButton: () -> Unit
): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTwoButtonDialogBinding.inflate(inflater, container, false)

        binding.tvTitle.text = title
        binding.tvContent.text = contents
        binding.buttonLeft.text = leftButtonText
        binding.buttonRight.text = rightButtonText
        binding.buttonLeft.setOnClickListener {
            onClickLeftButton()
            dismiss()
        }
        binding.buttonRight.setOnClickListener {
            onClickRightButton()
            dismiss()
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}