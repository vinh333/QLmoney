package com.example.qlmoney;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BackupActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_conected);

        // Khởi tạo GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        Button backupButton = findViewById(R.id.btn_backup);
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gọi hàm sao lưu
                performBackup();
            }
        });
    }

    private void performBackup() {
        // Kiểm tra xem người dùng đã đăng nhập Google chưa
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Đã đăng nhập Google, bạn có thể lấy thông tin tài khoản và thực hiện sao lưu
            String email = account.getEmail();
            String displayName = account.getDisplayName();

            // Thực hiện sao lưu dữ liệu SQL
            SQLiteDatabase db = new DatabaseHelper(this).getWritableDatabase();
            // Thực hiện truy vấn SQL để lấy dữ liệu từ bảng TABLE_ITEM và TABLE_PHANLOAINGANH
            // Ví dụ:
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ITEM, null);
            List<String> items = new ArrayList<>();
            while (cursor.moveToNext()) {
                // Đọc dữ liệu từ con trỏ cursor và thêm vào danh sách items
                // Ví dụ:
                String itemId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_ITEM));
                String amount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
                String kieu = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KIEU));
                // ...
                // Thêm các giá trị vào danh sách items
                items.add(itemId + " " + amount + " " + kieu);
            }
            cursor.close();

            // Gửi danh sách items lên API để lưu trữ
            // Thực hiện mã HTTP POST hoặc gọi phương thức tương ứng để gửi dữ liệu lên API
            // Ví dụ:
            String backupData = TextUtils.join("\n", items); // Kết hợp danh sách items thành một chuỗi văn bản

            // Gửi dữ liệu sao lưu lên API
            sendBackupDataToApi(backupData);
        } else {
            // Chưa đăng nhập Google, yêu cầu người dùng đăng nhập
            signInWithGoogle();
        }
    }

    private void sendBackupDataToApi(String backupData) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                // Thực hiện tác vụ mạng ở đây, ví dụ: gửi dữ liệu lên API
                String url = "https://drive.google.com/drive/u/0/my-drive";
                try {
                    // Tạo yêu cầu POST
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.POST, url,
                            response -> {
                                // Xử lý phản hồi từ API (response) sau khi gửi dữ liệu thành công
                                publishProgress();
                            },
                            error -> {
                                // Xử lý lỗi trong quá trình gửi dữ liệu lên API
                                publishProgress();
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("backup_data", "C:\\Users\\VInh\\Downloads\\Shib fina (1).png"); // Đặt tham số backup_data là dữ liệu sao lưu
                            return params;
                        }
                    };
                    // Thêm yêu cầu vào hàng đợi
                    requestQueue.add(request);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "error";
                }
                return "success";
            }

            @Override
            protected void onPostExecute(String result) {
                // Xử lý kết quả sau khi tác vụ hoàn thành
                if (result.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Sao lưu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Lỗi khi sao lưu", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Thực hiện tác vụ mạng trong luồng riêng
        task.execute();
    }





    private void signInWithGoogle() {
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
            // Đăng nhập thành công, xử lý thông tin tài khoản của người dùng (account)
            Intent backupIntent = new Intent(BackupActivity.this, BackupActivity.class);
            startActivity(backupIntent);
        } catch (ApiException e) {
            // Đăng nhập thất bại, xử lý lỗi
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /* Lớp để thực hiện việc tải lên tệp cơ bản */

    /**
     * Tải lên tệp mới.
     *
     * @return Thông tin tệp đã được tải lên nếu thành công, {@code null} nếu không thành công.
     * @throws IOException nếu không tìm thấy tệp tin chứa thông tin xác thực tài khoản dịch vụ.
     */
//    public String uploadBasic() throws IOException {
//        // Load the user's approved credentials from the key file.
//        GoogleCredentials credentials = GoogleCredentials.fromStream(
//                        getResources().openRawResource(R.raw.keyfile))
//                .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
//        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
//
//        // Build a new authorized API client service.
//        Drive service = new Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer)
//                .setApplicationName("Drive samples")
//                .build();
//
//        // Upload the file to Drive.
//        File fileMetadata = new File();
//        fileMetadata.setName("_0.jpg");
//
//        // Specify the file type and path for the file.
//        java.io.File filePath = new java.io.File(getApplicationInfo().dataDir + "/res/drawable/_0.png");
//        FileContent mediaContent = new FileContent("image/png", filePath);
//
//        try {
//            File file = service.files().create(fileMetadata, mediaContent)
//                    .setFields("id")
//                    .execute();
//
//            return file.getId();
//        } catch (GoogleJsonResponseException e) {
//            System.err.println("Unable to upload file: " + e.getDetails());
//            throw e;
//        }
//    }



}
