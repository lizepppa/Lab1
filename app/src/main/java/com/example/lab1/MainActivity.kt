package com.example.application

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var editTextHP: EditText
    private lateinit var editTextCP: EditText
    private lateinit var editTextSP: EditText
    private lateinit var editTextNP: EditText
    private lateinit var editTextOP: EditText
    private lateinit var editTextWP: EditText
    private lateinit var editTextAP: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var textViewResult: TextView

    private val decimalFormat = DecimalFormat("#.##") // Формат для округлення до двох знаків

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ініціалізація елементів інтерфейсу
        editTextHP = findViewById(R.id.editTextHP)
        editTextCP = findViewById(R.id.editTextCP)
        editTextSP = findViewById(R.id.editTextSP)
        editTextNP = findViewById(R.id.editTextNP)
        editTextOP = findViewById(R.id.editTextOP)
        editTextWP = findViewById(R.id.editTextWP)
        editTextAP = findViewById(R.id.editTextAP)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        textViewResult = findViewById(R.id.resultText)

        // Обробник події для кнопки
        buttonCalculate.setOnClickListener {
            calculateFuelComposition()
        }
    }

    private fun calculateFuelComposition() {
        // Отримання значень з полів введення
        val hp = editTextHP.text.toString().toDoubleOrNull() ?: 0.0
        val cp = editTextCP.text.toString().toDoubleOrNull() ?: 0.0
        val sp = editTextSP.text.toString().toDoubleOrNull() ?: 0.0
        val np = editTextNP.text.toString().toDoubleOrNull() ?: 0.0
        val op = editTextOP.text.toString().toDoubleOrNull() ?: 0.0
        val wp = editTextWP.text.toString().toDoubleOrNull() ?: 0.0
        val ap = editTextAP.text.toString().toDoubleOrNull() ?: 0.0

        // Розрахунок коефіцієнтів переходу
        val kRs = 100 / (100 - wp) // Коефіцієнт переходу від робочої до сухої маси
        val kRg = 100 / (100 - wp - ap) // Коефіцієнт переходу від робочої до горючої маси

        // Розрахунок складу сухої маси
        val hS = hp * kRs
        val cS = cp * kRs
        val sS = sp * kRs
        val nS = np * kRs
        val oS = op * kRs
        val aS = ap * kRs

        // Розрахунок складу горючої маси
        val hG = hp * kRg
        val cG = cp * kRg
        val sG = sp * kRg
        val nG = np * kRg
        val oG = op * kRg

        // Розрахунок нижчої теплоти згоряння
        val qR = calculateLowerHeatingValue(hp, cp, sp, np, op, wp, ap) / 1000 // Переведення в МДж
        val qD = calculateLowerHeatingValue(hS, cS, sS, nS, oS, 0.0, aS) / 1000 // Переведення в МДж
        val qG = calculateLowerHeatingValue(hG, cG, sG, nG, oG, 0.0, 1.0) / 1000 // Перевед ення в МДж

        // Формування результату з округленням
        val result = """
            Коефіцієнт переходу від робочої до сухої маси: ${decimalFormat.format(kRs)}
            Коефіцієнт переходу від робочої до горючої маси: ${decimalFormat.format(kRg)}
            Склад сухої маси:
            HС: ${decimalFormat.format(hS)}%
            CС: ${decimalFormat.format(cS)}%
            SС: ${decimalFormat.format(sS)}%
            NС: ${decimalFormat.format(nS)}%
            OС: ${decimalFormat.format(oS)}%
            AС: ${decimalFormat.format(aS)}%
            Склад горючої маси:
            HГ: ${decimalFormat.format(hG)}%
            CГ: ${decimalFormat.format(cG)}%
            SГ: ${decimalFormat.format(sG)}%
            NГ: ${decimalFormat.format(nG)}%
            OГ: ${decimalFormat.format(oG)}%
            Нижча теплота згоряння для робочої маси: ${decimalFormat.format(qR)} МДж
            Нижча теплота згоряння для сухої маси: ${decimalFormat.format(qD)} МДж
            Нижча теплота згоряння для горючої маси: ${decimalFormat.format(qG)} МДж
        """.trimIndent()

        // Виведення результату
        textViewResult.text = result
    }

    private fun calculateLowerHeatingValue(hp: Double, cp: Double, sp: Double, np: Double, op: Double, wp: Double, ap: Double): Double {
        return 339 * cp + 1030 * hp - 108.8 * (op - sp) - 25 * wp // Формула Мендєлєєва
    }
}