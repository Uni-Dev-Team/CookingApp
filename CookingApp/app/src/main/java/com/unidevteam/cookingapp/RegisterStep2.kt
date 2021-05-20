package com.unidevteam.cookingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "RegisterFragment2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterStep2.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterStep2 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewOfLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater!!.inflate(R.layout.fragment_register_step2, container, false)
        /*viewOfLayout.findViewById<Button>(R.id.btn_next).setOnClickListener {
            val password1 = viewOfLayout.findViewById<TextView>(R.id.tf_password_1).text.toString()
            val password2 = viewOfLayout.findViewById<TextView>(R.id.tf_password_2).text.toString()
            if (password1.isNotEmpty() && password2.isNotEmpty()){
                Log.d(TAG, "Text filled")
                Log.d(TAG, "Password 1: $password1")
                Log.d(TAG, "Password 2: $password2")
            } else {
                Log.d(TAG, "Text not filled")
            }
        }
        viewOfLayout.findViewById<TextView>(R.id.lb_loginPage).setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }*/
        return viewOfLayout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterStep2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterStep2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}