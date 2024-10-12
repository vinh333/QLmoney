package com.example.qlmoney.User;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlmoney.DatabaseHelper;
import com.example.qlmoney.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private DatabaseHelper databaseHelper;
    private TextView btnlogin_link;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, Wo4111rld!");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }

        // Khởi tạo databaseHelper
        databaseHelper = new DatabaseHelper(this);




        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirm_password);
        btnSignUp = findViewById(R.id.signup_button);
        btnlogin_link = findViewById(R.id.login_link);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các trường đăng ký
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                // Kiểm tra xem mật khẩu đã được xác nhận chính xác hay không
                if (password.equals(confirmPassword)) {
                    if (password.length() >= 6) {
                        // Lưu thông tin đăng ký vào cơ sở dữ liệu
                        dang_ky(email, password);
                        // Thực hiện các hành động khác sau khi đăng ký thành công
                    } else {
                        Toast.makeText(RegisterActivity.this, "Password phải lớn hơn 6 kí tự ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Hiển thị thông báo lỗi nếu mật khẩu không khớp
                    Toast.makeText(RegisterActivity.this, "Passwords không trùng ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnlogin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến hoạt động đăng ký (RegisterActivity)
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }



    private void dang_ky( String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });


    }
    public void updateUI(FirebaseUser account){

        if(account != null){
            Toast.makeText(this,"You Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,LoginActivity.class));

        }else {
            Toast.makeText(this,"You Didnt signed in",Toast.LENGTH_LONG).show();
        }

    }

}
