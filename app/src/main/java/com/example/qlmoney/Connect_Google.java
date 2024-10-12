package com.example.qlmoney;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

public class Connect_Google extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_google);

        Button googleSignInButton = findViewById(R.id.btn_SignIn);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String sqlFilePath = "ĐƯỜNG_DẪN_TỆP_SQL";
            File sqlFile = new File(sqlFilePath);
            exportDataToSQLFile();

            Intent backupIntent = new Intent(Connect_Google.this, BackupActivity.class);
            startActivity(backupIntent);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void uploadFileToDrive(File file) {
        Drive driveService = getDriveService();
        if (driveService != null) {
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(file.getName());

            String mimeType = "application/octet-stream";
            FileContent mediaContent = new FileContent(mimeType, file);

            try {
                com.google.api.services.drive.model.File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();

                String fileId = uploadedFile.getId();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tải lên thành công! File ID: " + fileId, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Lỗi khi tải lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Không thể kết nối với dịch vụ Google Drive", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private Drive getDriveService() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(account.getAccount());
            return new Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new GsonFactory(),
                    credential)
                    .setApplicationName("Your Application Name")
                    .build();
        }
        return null;
    }

    private void exportDataToSQLFile() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getApplicationContext().getDatabasePath("qlmoney.db").getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_ITEM;
        Cursor cursor = db.rawQuery(query, null);
        StringBuilder sqlData = new StringBuilder();

        while (cursor.moveToNext()) {
            String amount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
            String kieu = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KIEU));
            int idPhanLoaiNganh = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_PHANLOAINGANH));
            String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME));
            String note = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE));

            sqlData.append("INSERT INTO ")
                    .append(DatabaseHelper.TABLE_ITEM)
                    .append(" (")
                    .append(DatabaseHelper.COLUMN_AMOUNT).append(", ")
                    .append(DatabaseHelper.COLUMN_KIEU).append(", ")
                    .append(DatabaseHelper.COLUMN_ID_PHANLOAINGANH).append(", ")
                    .append(DatabaseHelper.COLUMN_DATE).append(", ")
                    .append(DatabaseHelper.COLUMN_TIME).append(", ")
                    .append(DatabaseHelper.COLUMN_NOTE)
                    .append(") VALUES ('")
                    .append(amount).append("', '")
                    .append(kieu).append("', ")
                    .append(idPhanLoaiNganh).append(", '")
                    .append(date).append("', '")
                    .append(time).append("', '")
                    .append(note).append("');\n");
        }

        cursor.close();
        db.close();

        File sqlFile = createSQLFile();
        if (sqlFile != null) {
            try {
                FileWriter writer = new FileWriter(sqlFile);
                writer.append(sqlData);
                writer.flush();
                writer.close();

                new UploadFileAsyncTask().execute(sqlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class UploadFileAsyncTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... files) {
            File sqlFile = files[0];
            uploadFileToDrive(sqlFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent backupIntent = new Intent(Connect_Google.this, BackupActivity.class);
            startActivity(backupIntent);
        }
    }

    private File createSQLFile() {
        String fileName = "backup_" + System.currentTimeMillis() + ".sql";
        File sqlFile = new File(getApplicationContext().getFilesDir(), fileName);
        try {
            if (!sqlFile.exists()) {
                sqlFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sqlFile;
    }
}
