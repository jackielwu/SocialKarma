package cs407.socialkarmaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText e1, e2, e3;
    EditText usernameEditText;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        e1 = (EditText)findViewById(R.id.editText2);
        e2 = (EditText)findViewById(R.id.editText);
        e3 = (EditText)findViewById(R.id.editText3);
        usernameEditText = findViewById(R.id.editText_signup_username);
        backButton = findViewById(R.id.signup_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
    }

    public void sign_up(View v){
        String email = e1.getText().toString();
        Log.d("email name", email);
        String password = e2.getText().toString();
        String password2 = e3.getText().toString();
        if(email.equals("") && password.equals("")){
            Toast.makeText(getApplicationContext(), "Email/Password is blank", Toast.LENGTH_LONG).show();
        }
        else if(!password.equals(password2)){
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
        }
        else{
            progressDialog.setMessage("Registering please wait");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.hide();
                        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_LONG).show();
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users");
                                String username = usernameEditText.getText().toString();
                                if (username == null || username.isEmpty()) {
                                    username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                }
                                dbref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(username);
                                dbref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Karma").setValue(0);

                                finish();
                            }
                        });
                    }
                    else{
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), "This email has already sign up for an account", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
