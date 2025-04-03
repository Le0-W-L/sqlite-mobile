package com.example.aulasqlitemobile;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ToDoActivity extends AppCompatActivity {

    EditText editTextTitle, editTextDescription;
    Button buttonSave;
    Switch status;
    ListView listViewTasks;
    BancoHelper databaseHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> listTasks;
    ArrayList<Integer> listIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_do);

        try {
            // Inicialize os elementos da interface do usuário
            editTextTitle = findViewById(R.id.editTextTitle);
            editTextDescription = findViewById(R.id.editTextDescription);
            buttonSave = findViewById(R.id.buttonSave);
            status = findViewById(R.id.switchStatus);
            listViewTasks = findViewById(R.id.listViewTasks);
            databaseHelper = new BancoHelper(this);

            // Carregue as tarefas do banco de dados
            buttonSave.setOnClickListener(v -> {

                // Obtenha os valores dos campos de edição
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();
                String statusValue = status.isChecked() ? "1" : "0";

                // Insira a tarefa no banco de dados
                if (!title.isEmpty() && !description.isEmpty()) {
                    long result = databaseHelper.createTask(title, description, statusValue);

                    // Verifique se a inserção foi bem-sucedida
                    if (result != -1) {
                        Toast.makeText(this, "Tarefa Criada!", Toast.LENGTH_SHORT).show();
                        editTextTitle.setText("");
                        editTextDescription.setText("");
                        status.setChecked(false);
                        loadTasks();
                    }
                    else {
                        Toast.makeText(this, "Erro ao salvar tarefa!", Toast.LENGTH_SHORT).show();
                    }
                }
                // Caso contrário, exiba uma mensagem de erro
                else {
                    Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }
            });

            listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
                // Obtenha o ID da tarefa selecionada
                int taskId = listIds.get(position);

                // Obtenha os detalhes da tarefa
                String title = listTasks.get(position).split(" - ")[1];
                String description = listTasks.get(position).split(" - ")[2];
                String statusValue = listTasks.get(position).split(" - ")[3];

                // Preencha os campos de edição com os detalhes da tarefa
                editTextTitle.setText(title);
                editTextDescription.setText(description);
                status.setChecked(statusValue.equals("1"));
                buttonSave.setText("Update");

                buttonSave.setOnClickListener(v -> {
                    // Obtenha os novos valores dos campos de edição
                    String newTitle = editTextTitle.getText().toString();
                    String newDescription = editTextDescription.getText().toString();
                    String newStatusValue = status.isChecked() ? "1" : "0";

                    // Atualize a tarefa no banco de dados
                    if (!newTitle.isEmpty() && !newDescription.isEmpty()) {
                        int result = databaseHelper.updateTask(taskId, newTitle, newDescription, newStatusValue);

                        if(result > 0) {
                            Toast.makeText(this, "Usuário atualizado!", Toast.LENGTH_SHORT).show();
                            loadTasks();
                            editTextTitle.setText("");
                            editTextDescription.setText("");
                            status.setChecked(false);
                            buttonSave.setText("Save");
                        }
                        else {
                            Toast.makeText(this, "Erro ao atualizar!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });

            listViewTasks.setOnItemLongClickListener((adapterView, view1, position, l) -> {
                int taskId = listIds.get(position);

                // Exclua a tarefa do banco de dados
                int deleted = databaseHelper.deleteTask(taskId);

                if(deleted > 0) {
                    Toast.makeText(this, "Tarefa excluída!", Toast.LENGTH_SHORT).show();
                    loadTasks();
                }

                return true;
            });
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadTasks() {
        Cursor cursor = databaseHelper.readTasks();
        listTasks = new ArrayList<>();
        listIds = new ArrayList<>();

        if(cursor.moveToFirst()){
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                String statusValue = cursor.getString(3).equals("1") ? "Feito" : "Não Feito";

                listTasks.add("Número da Tarefa: " + id +
                        "\nTítulo: "+ title +
                        "\nDescrição: "+ description +
                        "\nStatus: "+ statusValue);
                listIds.add(id);
            } while(cursor.moveToNext());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listTasks);
        listViewTasks.setAdapter(adapter);
    }
}