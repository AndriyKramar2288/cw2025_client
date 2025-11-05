package com.banew.cw2025_client.ui.grave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.banew.cw2025_client.R

class CreateCoursePlanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<View?>(R.id.back_button)
            .setOnClickListener(View.OnClickListener { v: View? ->
                NavHostFragment.findNavController(this).navigateUp()
            })

        val model =
            ViewModelProvider(this).get<CreateCoursePlanView>(CreateCoursePlanView::class.java)
    }
}