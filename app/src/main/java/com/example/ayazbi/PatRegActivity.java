package com.example.ayazbi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatRegActivity extends AppCompatActivity {
    private TextView loginPageQuestion;
    private TextInputEditText registrationfullname,registrationIIN,loginEmail,registrationPhone,loginPassword;
    private Button regButton;
    private CircleImageView profileimage;
    private Uri resultUri;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private ProgressDialog loader;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_reg);

        loginPageQuestion = findViewById(R.id.loginPageQuestion);
        loginPageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatRegActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        registrationfullname = findViewById(R.id.registrationfullname);
        registrationIIN = findViewById(R.id.registrationIIN);
        registrationPhone = findViewById(R.id.registrationPhone);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        regButton = findViewById(R.id.regButton);
        profileimage = findViewById(R.id.profileimage);

        loader = new ProgressDialog(this); // подклю БД
        mAuth = FirebaseAuth.getInstance();

        profileimage.setOnClickListener(new View.OnClickListener() { //profile image
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, 1);
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = loginEmail.getText().toString().trim(); //trim cleaning spaces
                final String password = loginPassword.getText().toString().trim();
                final String fullname = registrationfullname.getText().toString().trim();
                final String iin = registrationIIN.getText().toString().trim();
                final String phoneNumber = registrationPhone.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    loginEmail.setError("Необходимо ввести email");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    loginPassword.setError("Необходимо ввести пароль");
                    return;
                }
                if (TextUtils.isEmpty(fullname)){
                    registrationfullname.setError("Необходимо ввести ФИО");
                    return;
                }
                if (TextUtils.isEmpty(iin)){
                    registrationIIN.setError("Необходимо ввести ИИН");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)){
                    registrationPhone.setError("Необходимо ввести телефон");
                    return;
                }
                if (resultUri == null){
                    Toast.makeText(PatRegActivity.this, "Требуется фото",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    loader.setMessage("Registration in progress...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    //regist
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();//Переменная error
                                Toast.makeText(PatRegActivity.this, "Error occured" + error, Toast.LENGTH_SHORT).show();
                                
                            }//Успешно
                            else {
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(currentUserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("id", currentUserId);
                                userInfo.put("name", fullname);
                                userInfo.put("email", email);
                                userInfo.put("iin", iin);
                                userInfo.put("phonenumber", phoneNumber);
                                userInfo.put("type","patient");

                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(PatRegActivity.this, "Details Set successfully", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(PatRegActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                        loader.dismiss();
                                    }
                                });
                                
                                
                                if (resultUri !=null){
                                    final StorageReference filepath =
                                            FirebaseStorage.getInstance().getReference().child("profile pictures")
                                            .child(currentUserId);
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().
                                                getContentResolver(),resultUri);
                                    } catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                                    byte [] data = byteArrayOutputStream.toByteArray();

                                    UploadTask uploadTask = filepath.putBytes(data);
                                    
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        finish();
                                        return;
                                        }
                                    }); //database
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()  {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                         if (taskSnapshot.getMetadata() !=null){
                                             Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                             result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                 @Override
                                                 public void onSuccess(Uri uri) {
                                                 String imageUrl = uri.toString();
                                                 Map newImageMap = new HashMap();
                                                 newImageMap.put("profilepicture", imageUrl);
                                                 
                                                 userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                     @Override
                                                     public void onComplete(@NonNull Task task) {
                                                         if (task.isSuccessful()){
                                                             Toast.makeText(PatRegActivity.this, "Reg Success", Toast.LENGTH_SHORT).show();
                                                         }else {
                                                             Toast.makeText(PatRegActivity.this,
                                                                     task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                         }
                                                     }
                                                 });
                                                 finish();
                                                 }
                                             });
                                             Intent intent = new Intent(PatRegActivity.this,
                                                     MainActivity.class);
                                             startActivity(intent);
                                             finish();
                                             loader.dismiss();
                                         }
                                        }
                                    });
                                }


                            }
                            
                        }
                    });


                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK && data!=null){
            resultUri = data.getData();
            profileimage.setImageURI(resultUri);
        }
    }
}