package com.example.data_protection_android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.data_protection_android.R;
import com.example.data_protection_android.util.Action;
import com.example.data_protection_android.util.Method;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private final int FILE_REQUEST_CODE = 1;
    private final String DEMO_FILE = "demo.txt";

    @BindView(R.id.tv_chosen_file)
    TextView chosenFileTv;

    @BindView(R.id.rg_method)
    RadioGroup methodRg;

    @BindView(R.id.rg_action)
    RadioGroup actionRg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_choose_file)
    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @OnClick(R.id.btn_demo)
    public void chooseDemo() {
        chosenFileTv.setText(DEMO_FILE);
        try {
            InputStream stream = getAssets().open(DEMO_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_start)
    public void start() {
        Action action = null;
        Method method = null;

        switch (methodRg.getCheckedRadioButtonId()) {
            case R.id.rb_haffman:
                method = Method.HAFFMAN;
                break;
            case R.id.rb_lzw:
                method = Method.LZW;
                break;
            case R.id.rb_aes:
                method = Method.AES;
                break;
            case R.id.rb_rsa:
                method = Method.RSA;
                break;
        }

        switch (actionRg.getCheckedRadioButtonId()) {
            case R.id.rb_encrypt:
                action = Action.ENCRYPT;
                break;
            case R.id.rb_decrypt:
                action = Action.DECRYPT;
                break;
        }

        if (method != null && action != null) {

        } else {
            Toast.makeText(this, getString(R.string.choose_all_info_alert), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,  resultCode, data);
        switch (requestCode) {
            case FILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    chosenFileTv.setText(uri.getPath());
                }
                break;
        }
    }
}
