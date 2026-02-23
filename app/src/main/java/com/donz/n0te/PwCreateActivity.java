package com.donz.n0te;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class PwCreateActivity extends SecureActivity {
    private EditText etPw = null;
    private EditText etPwRepeat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pw_create);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pw_create_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPw = findViewById(R.id.et_set_pw_1);
        etPwRepeat = findViewById(R.id.et_set_pw_repeat);

        ((Switch)findViewById(R.id.swtich_show_pw)).setOnCheckedChangeListener((btnView, isChecked)->{
            if (isChecked) {
                // 1. 设置为可见文本
                etPw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                etPwRepeat.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 2. 设置为隐藏密码（星号/圆点）
                etPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                etPwRepeat.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        findViewById(R.id.btn_create_pw).setOnClickListener(v->{
            char[] strPw = new char[etPw.length()];
            etPw.getText().getChars(0, etPw.length(), strPw, 0);
            char[] strPwRepeat = new char[etPwRepeat.length()];
            etPwRepeat.getText().getChars(0, etPwRepeat.length(), strPwRepeat, 0);

            // length check
            if (strPw.length <6 || strPw.length > 12){
                Toast.makeText(this, "Password length should between 6 to 12;", Toast.LENGTH_SHORT).show();
                return;
            }

            // check pw and pwRepeat
            if (!Arrays.equals(strPw,strPwRepeat)){
                Toast.makeText(this, "The two passwords did not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            ((NoteApplication)getApplication()).setPwAndOpenDb(strPw);
            Arrays.fill(strPw, '0');    // fill 0 after use.
            SharedPreferences.Editor spEditor = getSharedPreferences(StringDef.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
            spEditor.putBoolean(StringDef.SP_KEY_IS_PW_SET, true);  // 密码设置完成后标记
            spEditor.commit();  // do not use apply()

            Intent intent2MainActivity = new Intent(this ,MainActivity.class);
            intent2MainActivity.putExtra(StringDef.KEY_PW, strPw);
            startActivity(intent2MainActivity);

            finish();

            // TODO do not forget encode whole app
        });

    }
}
