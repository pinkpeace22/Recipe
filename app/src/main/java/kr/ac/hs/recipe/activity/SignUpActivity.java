package kr.ac.hs.recipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import kr.ac.hs.recipe.R;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static kr.ac.hs.recipe.Util.showToast;

public class SignUpActivity extends BasicActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.login_Button).setOnClickListener(onClickListener);
        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_Button:
                    myStartActivity(LoginActivity.class);
                    break;
                case R.id.checkButton:
                    sendEmailVerification();
                    break;
                case R.id.signUpButton:
                    signUp();
                    break;

            }
        }
    };

    private void signUp() {
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.checkPasswordEditText)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) {
                final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                loaderLayout.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loaderLayout.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    showToast(SignUpActivity.this, "회원가입에 성공하였습니다.");
                                    myStartActivity(MainActivity.class);
                                } else {
                                    if (task.getException() != null) {
                                        showToast(SignUpActivity.this, task.getException().toString());
                                    }
                                }
                            }
                        });
            } else {
                showToast(SignUpActivity.this, "비밀번호가 일치하지 않습니다.");
            }
        } else {
            showToast(SignUpActivity.this, "이메일 또는 비밀번호를 입력해 주세요.");
        }
    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();

        if (email.isEmpty()) {
            //이메일 공백
            showToast(SignUpActivity.this, "이메일이 공백입니다.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            showToast(SignUpActivity.this, "이메일 형식이 불일치.");
            return false;
        } else {
            showToast(SignUpActivity.this, "사용가능한 이메일입니다.");
            return true;
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && isValidEmail()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                            }
                        }

                    });
        }

    }

}
