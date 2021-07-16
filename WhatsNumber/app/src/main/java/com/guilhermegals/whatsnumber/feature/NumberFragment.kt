package com.guilhermegals.whatsnumber.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.guilhermegals.whatsnumber.R
import com.guilhermegals.whatsnumber.databinding.NumberFragmentBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NumberFragment : Fragment() {

    // <editor-fold desc="[ Properties ]">

    /** Binding do Fragment */
    private var _binding: NumberFragmentBinding? = null
    private val binding get() = _binding!!

    /** ViewModel */
    private val viewModel: NumberViewModel by viewModels()

    // </editor-fold>

    // <editor-fold desc="[ Actions ]">

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NumberFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Cria uma nova partida
        viewModel.newMatch()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.top_app_bar_text_size -> {
                openTextSlider()
                true
            }
            R.id.top_app_bar_change_color -> {
                openColorPickerDialog()
                true
            }
            else -> false
        }
    }

    // </editor-fold>

    // <editor-fold desc="[ Private Functions ]">

    private fun openTextSlider() {
        Toast.makeText(this.context, R.string.feature_not_developed, Toast.LENGTH_LONG).show()
        //TODO: Implementar o Slider
    }

    private fun openColorPickerDialog() {
        this.context?.let {
            // Biblioteca utilizada https://github.com/skydoves/ColorPickerView
            ColorPickerDialog.Builder(it)
                .setTitle(getString(R.string.select_color))
                .setPositiveButton(getString(R.string.confirm_label),
                    ColorEnvelopeListener { envelope, _ ->
                        // Clicando no botão "Confirmar" é alterado a fonte do Led
                        changeLedColor(envelope.color)
                    })
                .setNegativeButton(
                    getString(R.string.back_label)
                ) { dialogInterface, _ ->
                    // Clicando no botão "Voltar" o diálogo é fechado
                    dialogInterface.dismiss()
                }
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(false)
                .setBottomSpace(12)
                .show()
        }
    }

    private fun changeLedColor(color : Int) {
        binding.numberFragmentLedNumber.setTextColor(color)
    }

    // </editor-fold>
}