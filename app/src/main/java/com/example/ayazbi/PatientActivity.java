package com.example.ayazbi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PatientActivity extends AppCompatActivity {
    private Button Request1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);


        Request1 = findViewById(R.id.Request1);
        Request1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PatientActivity.this, "Заявка успешно принята", Toast.LENGTH_SHORT).show();
            }
        });


    }




}