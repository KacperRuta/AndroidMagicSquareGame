package com.example.magicsquaregame;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MagicGame extends AppCompatActivity {
    private int level;
    private final int GRID_SIZE = 9;
    private boolean isWon = false;
    private List<Integer> numbers;
    private EditText[] editTexts = new EditText[GRID_SIZE];
    private EditText[] outcomeTexts = new EditText[6];
    private Button buttonSubmit, buttonExitToMenu, buttonRulesLink;
    private TextView resultText;
    private Set<Integer> autoFilledNumbers = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_game);

        level = getIntent().getIntExtra("level", 1);
        Log.d("aa", "Level: " + level);

        initializeViews();
        generateRandomNumbers();
        assignNumbersToEditTexts();
        computeAndSetOutcomes();
        setupEditTextsForLevel();

        buttonSubmit.setOnClickListener(v -> validateSolution());
        buttonExitToMenu.setOnClickListener(v -> exitToMenu());
        buttonRulesLink.setOnClickListener(v -> openRulesPage());
    }

    private void initializeViews() {
        for (int i = 0; i < GRID_SIZE; i++) {
            int resId = getResources().getIdentifier("edit" + (i + 1), "id", getPackageName());
            editTexts[i] = findViewById(resId);
        }
        for (int i = 0; i < 6; i++) {
            int resId = getResources().getIdentifier("edit_outcome" + (i + 1), "id", getPackageName());
            outcomeTexts[i] = findViewById(resId);
        }
        buttonSubmit = findViewById(R.id.buttonSubmitSolution);
        buttonExitToMenu = findViewById(R.id.buttonExitToMenu);
        buttonRulesLink = findViewById(R.id.buttonRulesLink);
        resultText = findViewById(R.id.resulttext);
    }

    private void generateRandomNumbers() {
        numbers = new ArrayList<>();
        for (int i = 1; i <= GRID_SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
    }

    private void assignNumbersToEditTexts() {
        for (int i = 0; i < GRID_SIZE; i++) {
            editTexts[i].setText(String.valueOf(numbers.get(i)));
        }
    }

    private void computeAndSetOutcomes() {
        outcomeTexts[0].setText(String.valueOf(numbers.get(0) + numbers.get(1) + numbers.get(2)));
        outcomeTexts[1].setText(String.valueOf(numbers.get(3) + numbers.get(4) + numbers.get(5)));
        outcomeTexts[2].setText(String.valueOf(numbers.get(6) + numbers.get(7) + numbers.get(8)));
        outcomeTexts[3].setText(String.valueOf(numbers.get(0) + numbers.get(3) + numbers.get(6)));
        outcomeTexts[4].setText(String.valueOf(numbers.get(1) + numbers.get(4) + numbers.get(7)));
        outcomeTexts[5].setText(String.valueOf(numbers.get(2) + numbers.get(5) + numbers.get(8)));
    }

    private void setupEditTextsForLevel() {
        List<Integer> editableIndexes = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            editableIndexes.add(i);
        }
        Collections.shuffle(editableIndexes);

        int numEditable = level;
        for (int i = 0; i < GRID_SIZE; i++) {
            if (editableIndexes.subList(0, numEditable).contains(i)) {
                editTexts[i].setText("");
                editTexts[i].setHint(".");
                editTexts[i].setFocusable(true);
                editTexts[i].setFocusableInTouchMode(true);
            } else {
                editTexts[i].setFocusable(false);
                editTexts[i].setFocusableInTouchMode(false);
                editTexts[i].setEnabled(false);
                autoFilledNumbers.add(numbers.get(i));
            }
        }
    }

    private void validateSolution() {
        Set<Integer> enteredNumbers = new HashSet<>();
        boolean hasZero = false, hasEmpty = false, hasDuplicates = false;
        boolean correct = true;

        for (EditText editText : editTexts) {
            editText.setTextColor(Color.BLACK);
        }
        resultText.setText("");

        for (int i = 0; i < GRID_SIZE; i++) {
            String value = editTexts[i].getText().toString();
            if (value.isEmpty()) {
                hasEmpty = true;
            } else {
                int num = Integer.parseInt(value);
                if (num == 0) {
                    hasZero = true;
                    editTexts[i].setTextColor(Color.RED);
                } else if (!enteredNumbers.add(num)) {
                    hasDuplicates = true;
                    editTexts[i].setTextColor(Color.RED);
                }
            }
        }

        if (hasZero) {
            resultText.setText("THERE CANNOT BE 0");
            return;
        }
        if (hasEmpty) {
            resultText.setText("FILL ALL FIELDS");
            return;
        }
        if (hasDuplicates) {
            resultText.setText("NUMBERS CANNOT REPEAT");
            return;
        }

        int[][] checkIndices = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}};
        for (int i = 0; i < 6; i++) {
            int sum = Integer.parseInt(outcomeTexts[i].getText().toString());
            int userSum = Integer.parseInt(editTexts[checkIndices[i][0]].getText().toString()) +
                    Integer.parseInt(editTexts[checkIndices[i][1]].getText().toString()) +
                    Integer.parseInt(editTexts[checkIndices[i][2]].getText().toString());
            if (sum == userSum) {
                for (int index : checkIndices[i]) {
                    editTexts[index].setTextColor(Color.GREEN);
                }
            } else {
                for (int index : checkIndices[i]) {
                    editTexts[index].setTextColor(Color.RED);
                }
                correct = false;
            }
        }

        if (correct) {
            resultText.setText("CONGRATULATIONS YOU WON");
            isWon = true;
            for (EditText editText : editTexts) {
                editText.setFocusable(false);
            }
            buttonSubmit.setEnabled(false);
        } else {
            resultText.setText("INCORRECT SOLUTION, TRY AGAIN");
        }
    }

    private void exitToMenu() {
        Intent intent = new Intent(this, MagicSquareHomeActivity.class);
        intent.putExtra("isWon", isWon);
        startActivity(intent);
        finish();
    }

    private void openRulesPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Magic_square"));
        startActivity(browserIntent);
    }
}
