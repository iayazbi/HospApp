package com.example.ayazbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SelectRegActivity extends AppCompatActivity {
    private TextView back;
    private Button PatReg,DocReg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_reg);


        PatReg = findViewById(R.id.PatReg);
        PatReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRegActivity.this, PatRegActivity.class);
                startActivity(intent);
            }
        });

        DocReg = findViewById(R.id.DocReg);
        DocReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRegActivity.this, DocRegActivity.class);
                startActivity(intent);
            }
        });
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRegActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}