package cs407.socialkarmaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    EditText e1, e2, e3;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        e1 = (EditText)findViewById(R.id.editText2);
        e2 = (EditText)findViewById(R.id.editText);
        e3 = (EditText)findViewById(R.id.editText3);
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
                        Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "User registered failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
