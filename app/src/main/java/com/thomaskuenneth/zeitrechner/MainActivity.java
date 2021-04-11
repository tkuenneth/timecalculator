/*
 * Copyright (C) 2016 - 2021  Thomas Kuenneth
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thomaskuenneth.zeitrechner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView inputTextView;
    private TextView outputTextView;
    private ScrollView scrollView;

    private final StringBuilder sb = new StringBuilder();

    private int result;
    private String lastOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputTextView = (TextView) findViewById(R.id.inputTextView);
        outputTextView = (TextView) findViewById(R.id.outputTextView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        clearInputTextView();
        resetOutputTextView();
    }

    public void handleButtonClick(View v) {
        if (v instanceof Button) {
            String txt = ((Button) v).getText().toString();
            switch (txt) {
                case "CE":
                    if (inputTextView.length() > 0) {
                        clearInputTextView();
                    } else {
                        resetOutputTextView();
                    }
                    break;
                case "+":
                case "-":
                case "=":
                    handleInput(txt);
                    break;
                case ":":
                default:
                    inputTextView.append(txt);
                    break;
            }
        }
    }

    private void handleInput(String op) {
        String line = inputTextView.getText().toString();
        if (!line.contains(":")) {
            if (line.length() == 4) {
                line = line.substring(0, 2) + ":" + line.substring(2);
            } else {
                int total = 0;
                if (line.length() > 0) {
                    total = Integer.parseInt(line);
                }
                int hours = total / 60;
                int minutes = total % 60;
                // trim() is important because we further process the result
                line = getTimeAsString(hours, minutes).trim();
            }
        }
        int pos = line.indexOf(':');
        int hours = getIntFromString(line.substring(0, pos));
        int minutes = getIntFromString(line.substring(pos + 1));
        int total = (hours * 60) + minutes;
        if ("-".equals(lastOp)) {
            total = -total;
        }
        lastOp = op;
        result += total;
        if (sb.length() > 0) {
            sb.append("\n");
        }
        sb.append(getTimeAsString(hours, minutes))
                .append(" ")
                .append(op);
        if ("=".equals(op)) {
            int temp = (result < 0) ? -result : result;
            hours = temp / 60;
            minutes = temp % 60;
            String strResult = getTimeAsString(hours, minutes).trim();
            sb.append(" ")
                    .append((result < 0) ? "-" : "")
                    .append(strResult)
                    .append("\n");
            result = 0;
            inputTextView.setText(strResult);
        } else {
            clearInputTextView();
        }
        updateOutputTextView();
    }

    private void clearInputTextView() {
        inputTextView.setText("");
    }

    private void updateOutputTextView() {
        outputTextView.setText(sb.toString());
        new Handler(Looper.getMainLooper()).postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN), 500);
    }

    private void resetOutputTextView() {
        result = 0;
        lastOp = "+";
        sb.setLength(0);
        updateOutputTextView();
    }

    private String getTimeAsString(int hours, int minutes) {
        return String.format(Locale.US, "%3d:%02d", hours, minutes);
    }

    private int getIntFromString(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
