package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.calculator.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt


enum class Sign {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE
}


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var sign: Sign? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainText.movementMethod = null
        binding.textUpper.movementMethod = null
        binding.sign.movementMethod = null
        binding.btnAC.setOnLongClickListener {
            onRemoveHold()
            true
        }
    }

    fun onDigitPressed(view: View?) {
        if (view == null)
            return
        if (binding.mainText.text.length == 11)
            return
        when (view.id) {
            binding.btn1.id -> appendDigit("1")
            binding.btn2.id -> appendDigit("2")
            binding.btn3.id -> appendDigit("3")
            binding.btn4.id -> appendDigit("4")
            binding.btn5.id -> appendDigit("5")
            binding.btn6.id -> appendDigit("6")
            binding.btn7.id -> appendDigit("7")
            binding.btn8.id -> appendDigit("8")
            binding.btn9.id -> appendDigit("9")
            binding.btn0.id -> appendDigit("0")
        }
    }

    fun onRemoveClick(view: View?) {
        val text = binding.mainText.text.toString()
        if (text == "0" && binding.textUpper.text.isNotEmpty()) {
            val upper = binding.textUpper.text.toString()
            binding.mainText.setText(upper)
            binding.textUpper.setText("")
        }
        else if (text.length == 1)
            binding.mainText.setText("0")
        else if (text.isNotEmpty())
            binding.mainText.setText(text.dropLast(1))
    }

    fun onPlusMinusClick(view: View?) {
        val number = binding.mainText.text
        if (number.toString() == "0")
            return
        if (number.startsWith('-'))
            binding.mainText.setText(number.removePrefix("-"))
        else
            binding.mainText.setText("-" + number)
    }

    fun onPercentClick(view: View?) {
        val number = binding.mainText.text.toString()
        if (binding.mainText.text.length == 11)
            return
        if (number == "0")
            return
        binding.mainText.setText(number + "%")
    }

    fun onCommaPressed(view: View?) {
        var text = binding.mainText.text.toString()
        if (text.contains(','))
            return

        val percentages = percentageRow(text)
        text = text.trimEnd('%')
        binding.mainText.setText(text + "," + percentages)
    }

    fun onOperationClick(view: View?) {
        if (view == null)
            return

        when (view.id) {
            binding.btnPlus.id -> {
                sign = Sign.PLUS
                binding.sign.setText("+")
            }
            binding.btnMinus.id -> {
                sign = Sign.MINUS
                binding.sign.setText("-")
            }
            binding.btnMultiply.id -> {
                sign = Sign.MULTIPLY
                binding.sign.setText("??")
            }
            binding.btnDivide.id -> {
                sign = Sign.DIVIDE
                binding.sign.setText("??")
            }
        }

        // ???? ???????????????? ?????????????? ??????????, ???? ???????????? ????????
        if (binding.textUpper.text.toString() != "")
            return

        binding.textUpper.setText(convertToProperNumber(binding.mainText.text.toString()))
        binding.mainText.setText("0")
    }

    fun onEqualsClick(view: View?) {
        var numFirst = BigDecimal("0.0")
        var numSecond = megaTextConverter(binding.mainText.text.toString())
        if (binding.textUpper.text.isNotEmpty())
            numFirst = megaTextConverter(binding.textUpper.text.toString())
        else {
            val result = convertToProperNumber(numSecond.toString().replace('.', ','))
            binding.mainText.setText(result)
            return
        }

        val result = when (sign) {
            Sign.PLUS -> numFirst.plus(numSecond)
            Sign.MINUS -> numFirst.minus(numSecond)
            Sign.DIVIDE -> {
                if (numSecond.compareTo(BigDecimal(0)) == 0) {
                    Toast.makeText(applicationContext, "???????????? ?????? ?????????????? ???? 0!", Toast.LENGTH_SHORT).show()
                    return
                }
                numFirst.divide(numSecond, 11, RoundingMode.HALF_EVEN)
            }
            Sign.MULTIPLY -> numFirst.multiply(numSecond)
            null -> throw UnknownError("?????????????? ???????????? ?????????? ?????? ?????????????????? ????????????????.")
        }

        binding.mainText.setText(convertToProperNumber(result.toString().replace('.', ',')))
        binding.textUpper.setText("")
        binding.sign.setText("")
    }

    private fun appendDigit(digit: String) {
        var text = binding.mainText.text.toString()
        if (text == "0")
            text = ""

        val percentages = percentageRow(text)
        text = text.trimEnd('%')
        binding.mainText.setText(text + digit + percentages)
    }

    private fun percentageRow(number: String): String {
        var percentages = ""
        for (char in number) {
            if (char == '%')
                percentages += "%"
        }
        return percentages
    }

    private fun megaTextConverter(text: String): BigDecimal {
        val percentCount = percentageRow(text).count()

        var number = BigDecimal(text.trimEnd('%').replace(',', '.'))
        for (i in 1..percentCount)
            number = number.divide(BigDecimal(100))
        return number
    }

    private fun convertToProperNumber(number: String): String {
        var numberParts = mutableListOf("", "")
        if (number.contains(',')){
            numberParts = number.split(',').toMutableList()
        }
        else {
            numberParts[0] = number
        }
        val percentages = percentageRow(numberParts[1])
        numberParts[1] = numberParts[1].trimEnd('%')
        numberParts[1] = numberParts[1].trimEnd('0')
        if (numberParts[1] == "")
            return numberParts[0] + percentages

        return "${numberParts[0]},${numberParts[1].take(6) + percentages}"
    }

    private fun onRemoveHold() {
        binding.mainText.setText("0")
        binding.textUpper.setText("")
        binding.sign.setText("")
    }
}