package com.huijimuhe.monolog.ui.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.huijimuhe.monolog.R;
import java.util.regex.Pattern;

public abstract class AbstractActivity extends AppCompatActivity {
    public static final int STATE_FIRST_INIT = 0;
    public static final int STATE_ROTATE = 1;
    public static final int STATE_RESUME = 2;
    protected int mActivityState = STATE_FIRST_INIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mActivityState = STATE_ROTATE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    protected void hiddenKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    protected boolean isNull(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            return false;
        }
        return true;
    }

    protected boolean isNullOrOverLength(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            if (text.length() > 120) {
                return true;
            }
            return false;
        }
        return true;
    }

    protected boolean matchPhone(String text) {
        if (Pattern.compile("(\\d{11})").matcher(text).matches()) {
            return true;
        }
        return false;
    }

    public int getmActivityState() {
        return mActivityState;
    }
}
