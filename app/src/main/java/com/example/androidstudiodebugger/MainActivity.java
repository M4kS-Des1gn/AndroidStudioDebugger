package com.example.androidstudiodebugger;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.math.BigDecimal;
import java.math.MathContext;

public class MainActivity extends AppCompatActivity {
    private EditText firstNumber, secondNumber;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        firstNumber = findViewById(R.id.firstNumber);
        secondNumber = findViewById(R.id.secondNumber);
        result = findViewById(R.id.result);
        findViewById(R.id.buttonAdd).setOnClickListener(v -> calculate('+'));
        findViewById(R.id.buttonSub).setOnClickListener(v -> calculate('-'));
        findViewById(R.id.buttonDiv).setOnClickListener(v -> calculate('/'));
        findViewById(R.id.buttonMul).setOnClickListener(v -> calculate('*'));
        if (savedInstanceState != null) result.setText(savedInstanceState.getCharSequence("result", ""));
    }

    private BigDecimal readNumber(EditText input) {
        String text = input.getText().toString().trim().replace(',', '.');
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException ex) {
            input.setError(getString(R.string.invalid_number));
            input.requestFocus();
            return null;
        }
    }

    private void calculate(char operation) {
        result.setText("");
        firstNumber.setError(null);
        secondNumber.setError(null);
        BigDecimal a = readNumber(firstNumber);
        if (a == null) return;
        BigDecimal b = readNumber(secondNumber);
        if (b == null) return;
        BigDecimal answer;
        switch (operation) {
            case '+': answer = a.add(b); break;
            case '-': answer = a.subtract(b); break;
            case '*': answer = a.multiply(b); break;
            case '/':
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    secondNumber.setError(getString(R.string.division_by_zero));
                    secondNumber.requestFocus();
                    return;
                }
                answer = a.divide(b, MathContext.DECIMAL64);
                break;
            default: return;
        }
        result.setText(getString(R.string.result_value, answer.stripTrailingZeros().toPlainString()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("result", result.getText());
        super.onSaveInstanceState(outState);
    }
}
