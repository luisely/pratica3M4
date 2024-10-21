package com.example.listadetarefas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView tasksListView;
    private ArrayList<String> tasksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> tasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        tasksListView = findViewById(R.id.taskListView);
        tasksListView.setAdapter(tasksAdapter);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            tasksAdapter.add(result.getData().getStringExtra("task"));
                        }
                    }
                }
        );
        Button addTaskButton = findViewById(R.id.addTaskButton);

        addTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                activityResultLauncher.launch(new Intent(MainActivity.this, AddTaskActivity.class));
            }
        });

        tasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle("Tarefa");
                dialog.setMessage("Finalizar tarefa?");
                dialog.setPositiveButton("sim", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        tasksAdapter.remove(tasksAdapter.getItem(position));
                    }
                });
                dialog.setNegativeButton("Não", null);
                dialog.create();
                dialog.show();

                return false;
            }
        });

        Button voiceCommandButton = findViewById(R.id.voiceCommandButton);
        voiceCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommandOptions();
            }
        });
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale a tarefa...");

        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                addTaskFromVoice(spokenText);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addTaskFromVoice(String task) {
        ArrayAdapter<String> tasksAdapter = (ArrayAdapter<String>) tasksListView.getAdapter();
        tasksAdapter.add(task);
    }

    private void showCommandOptions() {
        CharSequence[] commandOptions = {"Adicionar", "Sair"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma opção")
                .setItems(commandOptions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Lidar com a seleção do usuário
                        switch (which) {
                            case 0:
                                startVoiceRecognition();
                                break;
                            case 1:
                                backToMainActivity();
                                break;
                        }
                    }
                });

        builder.create().show();
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
