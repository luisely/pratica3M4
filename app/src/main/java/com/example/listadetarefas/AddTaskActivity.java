package com.example.listadetarefas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddTaskActivity extends AppCompatActivity {

    private Button addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);

        addTaskButton = findViewById(R.id.confirmButton);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText taskEditText = findViewById(R.id.taskEditText);
                String taskStr = (taskEditText.getText().toString());

                Intent resultIntent = new Intent();
                resultIntent.putExtra("task", taskStr);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }
}