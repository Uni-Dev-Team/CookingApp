package com.unidevteam.cookingapp.activities.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.util.Conversion
import java.util.regex.Pattern

class ConversionFragment : Fragment() {
    private lateinit var viewOfLayout : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_conversion, container, false)

        val fromIngredientSpinner : Spinner = viewOfLayout.findViewById(R.id.fromIngredientSpinner)
        val fromUnitSpinner : Spinner = viewOfLayout.findViewById(R.id.fromUnitSpinner)

        val toIngredientSpinner : Spinner = viewOfLayout.findViewById(R.id.toIngredientSpinner)
        val toUnitSpinner : Spinner = viewOfLayout.findViewById(R.id.toUnitSpinner)

        val fromIngredientSpinnerValues : List<String> = listOf("Generale", "Burro", "Olio")
        val fromUnitSpinnerValues : List<String> = listOf("g", "cl", "tablespoon", "cups")

        val toIngredientSpinnerValues : List<String> = listOf("Generale", "Olio", "Burro")
        val toUnitSpinnerValues : List<String> = listOf("cl", "g", "tablespoon", "cups")

        val fromIngredientSpinnerAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fromIngredientSpinnerValues)
        val fromUnitSpinnerAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fromUnitSpinnerValues)
        val toIngredientSpinnerAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, toIngredientSpinnerValues)
        val toUnitSpinnerAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, toUnitSpinnerValues)

        fromIngredientSpinner.adapter = fromIngredientSpinnerAdapter
        fromUnitSpinner.adapter = fromUnitSpinnerAdapter
        toIngredientSpinner.adapter = toIngredientSpinnerAdapter
        toUnitSpinner.adapter = toUnitSpinnerAdapter

        viewOfLayout.findViewById<Button>(R.id.button2).setOnClickListener {
            val fromIngredientValue : String = fromIngredientSpinner.selectedItem.toString().toLowerCase()
            val fromUnitValue : String = fromUnitSpinner.selectedItem.toString().toLowerCase()
            val toIngredientValue : String = toIngredientSpinner.selectedItem.toString().toLowerCase()
            val toUnitValue : String = toUnitSpinner.selectedItem.toString().toLowerCase()

            // Controllo spinner
            if(fromIngredientValue == "burro" && fromUnitValue == "g"
                && toIngredientValue == "olio" && toUnitValue == "cl") {
                // Controllo valore
                val inputAmount : String = viewOfLayout.findViewById<EditText>(R.id.fromIngredientAmountValue).text.toString()

                if(Pattern.compile("[0-9.]*").matcher(inputAmount).matches()) {
                    val inputDouble : Double = inputAmount.toDouble()

                    val resValue : Double = (inputDouble * 5) / 4
                    viewOfLayout.findViewById<EditText>(R.id.toIngredientAmountValue).setText(resValue.toString())
                } else {
                    Toast.makeText(requireContext(), "Inserisci un valore numerico", Toast.LENGTH_SHORT).show()
                }
            }

            else if(fromIngredientValue == "olio" && fromUnitValue == "cl"
                && toIngredientValue == "burro" && toUnitValue == "g") {
                // Controllo valore
                val inputAmount : String = viewOfLayout.findViewById<EditText>(R.id.fromIngredientAmountValue).text.toString()

                if(Pattern.compile("[0-9.]*").matcher(inputAmount).matches()) {
                    val inputDouble : Double = inputAmount.toDouble()

                    val resValue : Double = (inputDouble * 4) / 5
                    viewOfLayout.findViewById<EditText>(R.id.toIngredientAmountValue).setText(resValue.toString())
                } else {
                    Toast.makeText(requireContext(), "Inserisci un valore numerico", Toast.LENGTH_SHORT).show()
                }
            }

            else if(fromIngredientValue == toIngredientValue && fromUnitValue == toUnitValue) {
                val inputAmount : String = viewOfLayout.findViewById<EditText>(R.id.fromIngredientAmountValue).text.toString()

                if(Pattern.compile("[0-9.]*").matcher(inputAmount).matches()) {
                    viewOfLayout.findViewById<EditText>(R.id.toIngredientAmountValue).setText(inputAmount)
                } else {
                    Toast.makeText(requireContext(), "Inserisci un valore numerico", Toast.LENGTH_SHORT).show()
                }
            }

            else if(fromIngredientValue == "generale" && toIngredientValue == "generale") {
                var result : String? = null

                val inputAmount : String = viewOfLayout.findViewById<EditText>(R.id.fromIngredientAmountValue).text.toString()

                if(Pattern.compile("[0-9.]*").matcher(inputAmount).matches()) {
                    when {
                        fromUnitValue == "g" && toUnitValue == "cl" -> {
                            result = String.format("%.2f", (Conversion.gramsToCl(inputAmount.toDouble())))
                        }
                        fromUnitValue == "cl" && toUnitValue == "g" -> {
                            result = String.format("%.2f", (Conversion.clToGrams(inputAmount.toDouble())))
                        }
                        fromUnitValue == "g" && toUnitValue == "tablespoon" -> {
                            result = String.format("%.2f", (Conversion.gramsToTablespoon(inputAmount.toDouble())))
                        }
                        fromUnitValue == "tablespoon" && toUnitValue == "g" -> {
                            result = String.format("%.2f", (Conversion.tablespoonsToGrams(inputAmount.toDouble())))
                        }
                        fromUnitValue == "cl" && toUnitValue == "tablespoon" -> {
                            result = String.format("%.2f", Conversion.clToTablespoon(inputAmount.toDouble()))
                        }
                        fromUnitValue == "tablespoon" && toUnitValue == "cl" -> {
                            result = String.format("%.2f", Conversion.tablespoonToCl(inputAmount.toDouble()))
                        }
                        fromUnitValue == "cups" && toUnitValue == "g" -> {
                            result = String.format("%.2f", (Conversion.cupsToGrams(inputAmount.toDouble())))
                        }
                        fromUnitValue == "g" && toUnitValue == "cups" -> {
                            result = String.format("%.2f", (Conversion.gramsToCups(inputAmount.toDouble())))
                        }
                        fromUnitValue == "cups" && toUnitValue == "cl" -> {
                            result = String.format("%.2f", Conversion.cupsToCl(inputAmount.toDouble()))
                        }
                        fromUnitValue == "cl" && toUnitValue == "cups" -> {
                            result = String.format("%.2f", Conversion.clToCups(inputAmount.toDouble()))
                        }
                        fromUnitValue == "cups" && toUnitValue == "tablespoon" -> {
                            result = String.format("%.2f", Conversion.cupsToTablespoon(inputAmount.toDouble()))
                        }
                        fromUnitValue == "tablespoon" && toUnitValue == "cups" -> {
                            result = String.format("%.2f", Conversion.tablespoonToCups(inputAmount.toDouble()))
                        }
                    }

                    if(result != null) viewOfLayout.findViewById<EditText>(R.id.toIngredientAmountValue).setText(result)
                } else {
                    Toast.makeText(requireContext(), "Inserisci un valore numerico", Toast.LENGTH_SHORT).show()
                }
            }

            else {
                Toast.makeText(requireContext(), "Conversione non valida", Toast.LENGTH_SHORT).show()
            }
        }

        return viewOfLayout
    }
}