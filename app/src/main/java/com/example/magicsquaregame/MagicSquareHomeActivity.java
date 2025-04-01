package com.example.magicsquaregame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MagicSquareHomeActivity extends AppCompatActivity {
    private EditText editTextLevel;
    private Button buttonMagicSquare;
    private TextView textViewSuccessFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_square_home);

        textViewSuccessFail = findViewById(R.id.textViewSuccessFail);
        editTextLevel = findViewById(R.id.editTextLevel);
        buttonMagicSquare = findViewById(R.id.buttonMagicSquare);

        // Check if the "isWon" intent extra exists and set the text and color accordingly
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("isWon")) {
            boolean isWon = intent.getBooleanExtra("isWon", false);
            textViewSuccessFail.setText(isWon ? "Success" : "Fail");
            textViewSuccessFail.setTextColor(isWon ? Color.GREEN : Color.RED);
        } else {
            textViewSuccessFail.setText("Last game result here");
            textViewSuccessFail.setTextColor(Color.BLACK);
        }

        // Add TextWatcher to EditText for validation
        editTextLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        int value = Integer.parseInt(s.toString());
                        if (value >= 1 && value <= 9) {
                            setEditTextBorder(Color.GRAY); // Reset to normal
                        } else {
                            setEditTextBorder(Color.RED); // Highlight red
                        }
                    } catch (NumberFormatException e) {
                        setEditTextBorder(Color.RED); // Highlight red
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Button click event
        buttonMagicSquare.setOnClickListener(v -> {
            String levelText = editTextLevel.getText().toString();
            if (!levelText.isEmpty()) {

                    int level = Integer.parseInt(levelText);
                    if (level >= 1 && level <= 9) {
                        Intent gameIntent = new Intent(MagicSquareHomeActivity.this, MagicGame.class);
                        gameIntent.putExtra("level", level);
                        startActivity(gameIntent);
                    }

            }
        });
    }

    // Function to set EditText border color
    private void setEditTextBorder(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(5, color); // 5dp border
        drawable.setCornerRadius(10); // Rounded corners
        editTextLevel.setBackground(drawable);
    }
}
