package com.example.data_protection_android.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.data_protection_android.R;
import com.example.data_protection_android.fragment.EncodeFragment;
import com.example.data_protection_android.util.Method;

import java.net.URISyntaxException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private final int FILE_REQUEST_CODE = 1;
    private int WRITE_PERMISSION = 1;

    @BindView(R.id.tv_chosen_file)
    TextView chosenFileTv;

    @BindView(R.id.rg_method)
    RadioGroup methodRg;

    @BindView(R.id.fragment_container)
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermissions();
    }

    private void requestPermissions() {
        String permission = "";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permission += "read";

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_PERMISSION);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permission += "write";

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);

        }

        if (!permission.isEmpty()) {
            Toast.makeText(this, permission, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_PERMISSION) {

        }
    }

    @OnClick(R.id.btn_exit)
    public void exit() {
        finish();
    }

    @OnClick(R.id.btn_choose_file)
    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @OnClick(R.id.btn_start)
    public void start() {
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

        String file = chosenFileTv.getText().toString();
        if (method != null && file.length() != 0) {
            startFragment(file, method);
        } else {
            Toast.makeText(this, getString(R.string.choose_all_info_alert), Toast.LENGTH_LONG).show();
        }
    }

    private void startFragment(String file, Method method) {
        EncodeFragment fragment = EncodeFragment.newInstance(file, method);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("ENCODE_FRAGMENT")
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = "";
                    try {
                        path = getPath(this, uri);
                        Log.i("jija", path);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    chosenFileTv.setText(path);
                }
                break;
        }
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception ignored) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        menu.add(getString(R.string.exit)).setOnMenuItemClickListener(e -> {
            finish();
            return true;
        });
        return true;
    }
}
