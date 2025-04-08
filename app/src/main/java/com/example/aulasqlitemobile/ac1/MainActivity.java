package com.example.aulasqlitemobile.ac1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aulasqlitemobile.BancoHelper;
import com.example.aulasqlitemobile.R;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button buttonAdd;
    BancoHelper db;
    ArrayList<String> habitList = new ArrayList<>();
    ArrayList<Integer> idList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BancoHelper dbHelper = new BancoHelper(this);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        db1.execSQL("DROP TABLE IF EXISTS habits");
        dbHelper.onCreate(db1);

        db = new BancoHelper(this);
        listView = findViewById(R.id.listViewTasks);
        buttonAdd = findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, HabitsActivity.class);
            startActivity(i);
        });

        listView.setOnItemClickListener((parent, view, pos, id) -> {
            int habitId = idList.get(pos);
            Intent i = new Intent(this, HabitsActivity.class);
            i.putExtra("id", habitId);
            startActivity(i);
        });

        listView.setOnItemLongClickListener((parent, view, pos, id) -> {
            db.delete(idList.get(pos));
            Toast.makeText(this, getString(R.string.habit_deleted), Toast.LENGTH_SHORT).show();
            loadList();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    private void loadList() {
        habitList.clear();
        idList.clear();

        Cursor c = db.getAll();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String desc = c.getString(2);
            boolean done = c.getString(3).equals("1");

            // Pegando strings traduzidas dos recursos
            String habitLabel = getString(R.string.habit_label);
            String descLabel = getString(R.string.description_label);
            String doneLabel = getString(R.string.done_label);

            String doneText = done ? getString(R.string.done_yes) : getString(R.string.done_no);

            String item = habitLabel + ": " + title + "\n"
                    + descLabel + ": " + desc + "\n"
                    + doneLabel + ": " + doneText;

            habitList.add(item);
            idList.add(id);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, habitList);
        listView.setAdapter(adapter);
    }

}