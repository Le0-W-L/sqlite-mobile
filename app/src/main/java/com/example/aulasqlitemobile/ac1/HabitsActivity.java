package com.example.aulasqlitemobile.ac1;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aulasqlitemobile.BancoHelper;
import com.example.aulasqlitemobile.R;

public class HabitsActivity extends AppCompatActivity {
    EditText editTitle, editDescription;
    Switch switchStatus;
    Button buttonSave;
    BancoHelper db;
    int habitId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits);

        db = new BancoHelper(this);
        editTitle = findViewById(R.id.editTextTitle);
        editDescription = findViewById(R.id.editTextDescription);
        switchStatus = findViewById(R.id.switchStatus);
        buttonSave = findViewById(R.id.buttonSave);

        if (getIntent().hasExtra("id")) {
            habitId = getIntent().getIntExtra("id", -1);
            Cursor c = db.getById(habitId);
            if (c.moveToFirst()) {
                editTitle.setText(c.getString(1));
                editDescription.setText(c.getString(2));
                switchStatus.setChecked(c.getString(3).equals("1"));
            }
        }

        buttonSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String desc = editDescription.getText().toString();
            String done = switchStatus.isChecked() ? "1" : "0";

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (habitId == -1) {
                db.insert(title, desc, done);
                Toast.makeText(this, getString(R.string.habit_created), Toast.LENGTH_SHORT).show();
            } else {
                db.update(habitId, title, desc, done);
                Toast.makeText(this, getString(R.string.habit_updated), Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }
}