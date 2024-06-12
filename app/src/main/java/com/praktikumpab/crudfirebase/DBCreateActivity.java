package com.praktikumpab.crudfirebase;

import static android.text.TextUtils.isEmpty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBCreateActivity extends AppCompatActivity {

    private DatabaseReference database;
    // variable fields EditText dan Button
    private Button btSubmit;
    private EditText etNik;
    private EditText etNama;
    private Spinner etJa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dbcreate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activitydbcreate), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi fields EditText dan Button
        etNik = findViewById(R.id.nik);
        etNama = findViewById(R.id.nama_dosen);
        etJa = findViewById(R.id.spinnerJA);
        btSubmit = findViewById(R.id.bt_submit);

        // Mengambil referensi ke Firebase Database
        database = FirebaseDatabase.getInstance().getReference();

        //Final Update
        final Dosen dosen = (Dosen) getIntent().getSerializableExtra("data");
        if (dosen != null) {
            // Ini untuk update
            etNik.setText(dosen.getNik());
            etNama.setText(dosen.getNama());
            // Assuming etJa is a Spinner, set the correct value (if necessary)

            btSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dosen.setNik(etNik.getText().toString());
                    dosen.setNama(etNama.getText().toString());
                    dosen.setJa(etJa.getSelectedItem().toString());
                    updateDosen(dosen);
                }
            });
        } else {
            // Kode yang dipanggil ketika tombol Submit diklik
            btSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEmpty(etNik.getText().toString()) && !isEmpty(etNama.getText().toString())) {
                        submitDosen(new Dosen(etNik.getText().toString(), etNama.getText().toString(), etJa.getSelectedItem().toString()));
                    } else {
                        Snackbar.make(findViewById(R.id.bt_submit), "Data Dosen tidak boleh kosong", Snackbar.LENGTH_LONG).show();
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etNama.getWindowToken(), 0);
                }
            });
        }
    }

    private boolean isEmpty(String s) {
        // Cek apakah ada fields yang kosong, sebelum disubmit
        return TextUtils.isEmpty(s);
    }


    private void updateDosen(Dosen dosen) {
        // Update Dosen
        database.child("dosen")
                .child(dosen.getKey())
                .setValue(dosen)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(findViewById(R.id.bt_submit), "Data Berhasil di Update",
                                Snackbar.LENGTH_LONG).setAction("OKE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        }).show();
                    }
                });
    }

    //fungsi Simpan data Dosen
    private void submitDosen(Dosen dosen) {
        database.child("dosen").push().setValue(dosen).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                etNik.setText("");
                etNama.setText("");
                // Assuming you need to reset the spinner to default position
                etJa.setSelection(0);
                Snackbar.make(findViewById(R.id.bt_submit), "Data berhasil ditambahkan", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public static Intent getActIntent(Activity activity) {
        // kode untuk pengambilan Intent
        return new Intent(activity, DBCreateActivity.class);
    }
}
