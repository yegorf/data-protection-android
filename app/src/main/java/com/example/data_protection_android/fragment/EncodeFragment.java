package com.example.data_protection_android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.data_protection_android.R;
import com.example.data_protection_android.logic.haffman.CompressQualifier;
import com.example.data_protection_android.logic.haffman.HuffmanCompressor;
import com.example.data_protection_android.logic.haffman.Node;
import com.example.data_protection_android.logic.lzw.LzwCoder;
import com.example.data_protection_android.util.Method;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EncodeFragment extends Fragment {

    @BindView(R.id.tv_progress)
    TextView progressTv;

    @BindView(R.id.tv_method)
    TextView methodTv;

    @BindView(R.id.tv_file)
    TextView fileTv;

    private static final String FILE_PATH_KEY = "FILE_PATH_KEY";
    private static final String METHOD_KEY = "METHOD_KEY";

    public static EncodeFragment newInstance(String path, Method method) {
        EncodeFragment fragment = new EncodeFragment();
        Bundle args = new Bundle();
        args.putString(FILE_PATH_KEY, path);
        args.putSerializable(METHOD_KEY, method);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encode, container, false);
        ButterKnife.bind(this, view);

        String file = null;
        Method method = null;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            file = bundle.getString(FILE_PATH_KEY);
            method = (Method) bundle.getSerializable(METHOD_KEY);
        }

        if (file != null && method != null) {
            setData(file, method);
            try {
                start(file, method);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private void setData(String file, Method method) {
        fileTv.setText(file);
        methodTv.setText(method.name());
    }

    private void start(String file, Method method) throws IOException {
        StringBuilder builder;
        switch (method) {
            case HAFFMAN:
                Node tree;
                builder = new StringBuilder();

                String textFilename = file;
                String compressedTextFilename = file + ".haffman";
                String decodedTextFilename = file.substring(file.indexOf('.')) + "_decoded.txt";

                builder.append("Кодирование текстового файла\n");
                builder.append("----------------------------\n");

                tree = HuffmanCompressor.compress(textFilename, compressedTextFilename);
                builder.append("----------------------------\n");
                builder.append("Раскодирование текстового файла\n");
                builder.append("----------------------------\n");

                HuffmanCompressor.decompress(compressedTextFilename, decodedTextFilename, tree);

                builder.append("% компрессии = " + CompressQualifier.compressPercent(
                        new File(textFilename),
                        new File(compressedTextFilename)) + "\n");
                builder.append("Файлы идентичны = " + CompressQualifier.isUncompressedEqualsSource(
                        new File(textFilename),
                        new File(decodedTextFilename)) + "\n");

                progressTv.setText(builder.toString());
                break;
            case LZW:
                builder = new StringBuilder();
                LzwCoder coder = new LzwCoder();

                //File f = new File(file);
                BufferedReader br = new BufferedReader(new FileReader(file));
                builder.append("Архивация LZW");
                String archive = coder.archive(file);

                builder.append("Дерхивация LZW");
                coder.unzip(file, archive);

                progressTv.setText(builder.toString());
                break;
        }
    }
}
