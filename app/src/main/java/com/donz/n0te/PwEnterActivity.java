package com.donz.n0te;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class PwEnterActivity extends SecureActivity{
    private EditText etPw = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pw_enter);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pw_enter_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPw = findViewById(R.id.et_pw_input);

        // implement function of show password.
        ((Switch)findViewById(R.id.pw_enter_swtich_show_pw)).setOnCheckedChangeListener((btn, isChecked)->{
            if (isChecked) {
                // 1. 设置为可见文本
                etPw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 2. 设置为隐藏密码（星号/圆点）
                etPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        findViewById(R.id.btn_enter_pw).setOnClickListener(v->{
            char[] pw = new char[etPw.getText().length()];
            etPw.getText().getChars(0, pw.length, pw, 0);
            boolean isPwCorrect = false;
            try {
                ((NoteApplication) getApplication()).openDb(pw);
                isPwCorrect = true;
            }
            catch (Throwable e){
                Toast.makeText(this, "password wrong", Toast.LENGTH_SHORT).show();
            }
            finally{
                Arrays.fill(pw, '0');
            }

            if (isPwCorrect){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }
}
