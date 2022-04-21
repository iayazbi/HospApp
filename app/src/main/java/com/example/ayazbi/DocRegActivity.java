package com.example.ayazbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DocRegActivity extends AppCompatActivity {
    private TextView RegistrationPageQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_reg);

        RegistrationPageQuestion = findViewById(R.id.RegistrationPageQuestion);
        RegistrationPageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DocRegActivity.this,LoginActivity.class); ///Иконка для перехода в логин
                startActivity(intent);
            }
        });
    }
}
