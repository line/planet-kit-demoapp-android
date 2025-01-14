package com.linecorp.planetkit.demo.uikit.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.linecorp.planetkit.demo.uikit.databinding.FragmentSingleButtonDialogBinding

class SingleButtonFragmentDialog(
    private val title: String,
    private val contents: String,
    private val buttonText: String,
    private val onClosed: () -> Unit
): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSingleButtonDialogBinding.inflate(inflater, container, false)

        binding.tvTitle.text = title
        binding.tvContent.text = contents
        binding.button.text = buttonText
        binding.button.setOnClickListener {
            dismiss()
            onClosed()
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}