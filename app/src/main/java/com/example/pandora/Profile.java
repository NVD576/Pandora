package com.example.pandora;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pandora.AdminProperties.AdminProperties;
import com.example.pandora.Class.User;
import com.example.pandora.Database.DatabaseHelper;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.Login.Login;
import com.example.pandora.Main.Lobby;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class Profile extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView userImage;
    boolean isLogin = false;
    String userName = "";
    int userid;
    private Uri contentUri;
    TextView login;
    User user;

    public Profile() {
        // Required empty public constructor
    }

    private String getUserImageFromDatabase(int userId) {
        UserDatabase userDatabase = new UserDatabase(getActivity());
        userDatabase.open();
        User u= userDatabase.getUserById(userId);
        // Lấy đường dẫn ảnh của người dùng từ cơ sở dữ liệu
        String imagePath = u.getImage();

        userDatabase.close();
        return imagePath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        login = view.findViewById(R.id.loginProfile);
        userImage = view.findViewById(R.id.userImage);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userid = sharedPreferences.getInt("userid", -1); // -1 là giá trị mặc định nếu không tìm thấy
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định
        TextView logout= view.findViewById(R.id.logout);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        //xem quyền truy cập
        TextView adminMode = view.findViewById(R.id.adminMode);
        adminMode.setAlpha(0f);
        adminMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.isRole()>0) {
                    Intent myIntent = new Intent(requireContext(), AdminProperties.class);
                    startActivity(myIntent);

                }
            }
        });

        if (isLogin) {
            UserDatabase db= new UserDatabase(getContext());
            db.open();
            user = db.getUserById(userid);
            db.close();
            userName=user.getName();
            login.setText(user.getName());

            if (user.isRole()>0){
                adminMode.setAlpha(1f);
            } else adminMode.setAlpha(0f);
            logout.setVisibility(View.GONE);

            // Lấy đường dẫn ảnh từ cơ sở dữ liệu
            String userImagePath = getUserImageFromDatabase(userid);

            // Nếu có đường dẫn ảnh, cập nhật ImageView
            if (userImagePath != null && !userImagePath.isEmpty()) {
                Bitmap bitmap = loadImageFromInternalStorage(userImagePath);
                if (bitmap != null) {
                    userImage.setImageBitmap(bitmap);
                }
            }
        }else{
            logout.setVisibility(View.VISIBLE);
        }

        //nút đăng nhập
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), Login.class);
                startActivity(myIntent);
            }
        });
        // thay đổi avartar
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                } else {
                    Toast.makeText(getActivity(), "Yêu cầu đăng nhập để thay đổi ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // nút thay đổi thông tin
        TextView changeInfo = view.findViewById(R.id.changeInfo);
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin) {
                    Toast.makeText(getActivity(), "Yêu cầu đăng nhập", Toast.LENGTH_SHORT).show();
                } else {
                    showEditProfileDialog();
                }
            }
        });
        // nút đăng xuất

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin) {
                    Toast.makeText(getActivity(), "Yêu cầu đăng nhập", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    adminMode.setAlpha(0f);
                    login.setText("Đăng nhập");
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    userImage.setImageResource(R.drawable.person_icon);

                    isLogin = false;
                }
            }
        });

        return view;
    }

    //hộp thoại thay đổi thông tin
    private void showEditProfileDialog() {
        // Tạo view cho hộp thoại
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        // Ánh xạ các EditText trong hộp thoại
        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtNumberPhone = dialogView.findViewById(R.id.edtNumberPhone);

        edtName.setText(user.getName());
        edtNumberPhone.setText(user.getSDT());

        // Tạo hộp thoại
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy dữ liệu từ các EditText
                String newName = edtName.getText().toString();
                String newNumberPhone = edtNumberPhone.getText().toString();

                // Cập nhật thông tin (Ví dụ: bạn có thể lưu vào cơ sở dữ liệu)
                updateProfile(newName, newNumberPhone);

                login.setText(newName);
                alertDialog.dismiss();
            }
        });

        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    private void updateProfile(String name, String numberPhone) {
        // Cập nhật UI hoặc dữ liệu (ví dụ, cập nhật TextView hoặc lưu vào cơ sở dữ liệu)
        UserDatabase db = new UserDatabase(getContext());
        db.open();

        user.setName(name);
        user.setSDT(numberPhone);
        db.updateUser(user);
        db.close();

    }

    //lưu ảnh
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Lấy URI của ảnh được chọn
            Uri selectedImageUri = data.getData();

            // Lấy đường dẫn thực tế từ URI
            try {
                // Chuyển đổi URI sang Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);

                // Tạo tên file duy nhất
                String fileName = "user_image_" + userid + ".png";

                // Cập nhật vào database
                updateUserImage(fileName);
                // Lưu Bitmap vào bộ nhớ trong
                saveImageToInternalStorage(bitmap, fileName);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Cập nhật ngay trong ImageView
            ImageView userImage = requireView().findViewById(R.id.userImage);
            userImage.setImageURI(selectedImageUri);

            // Thông báo thành công
            Toast.makeText(getActivity(), "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm cập nhật ảnh vào cơ sở dữ liệu
    private void updateUserImage(String imagePath) {
        // Mở cơ sở dữ liệu và cập nhật ảnh
        UserDatabase userDatabase = new UserDatabase(getActivity());
        userDatabase.open();
        userDatabase.updateUserImage(userid, imagePath);
        userDatabase.close();
    }

    // Hàm hỗ trợ lấy đường dẫn thực tế từ URI
    private String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        try (Cursor cursor = getActivity().getContentResolver().query(contentUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu không thể lấy đường dẫn từ MediaStore, sử dụng phương thức khác để lấy URI
        if (path == null) {
            if ("content".equalsIgnoreCase(contentUri.getScheme())) {
                // For API level >= 29, use ContentResolver to get the file path
                String[] filePathColumn = {MediaStore.Images.Media.RELATIVE_PATH};
                try (Cursor cursor = getActivity().getContentResolver().query(contentUri, filePathColumn, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);
                        path = cursor.getString(columnIndex);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return path;
    }

    private void saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        try {
            // Lưu ảnh vào bộ nhớ trong
            FileOutputStream fos = requireContext().openFileOutput(fileName, getContext().MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.e("SaveImage", "Ảnh đã được lưu vào bộ nhớ trong: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Lưu ảnh thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap loadImageFromInternalStorage(String fileName) {
        try {
            FileInputStream fis = requireContext().openFileInput(fileName);
            return BitmapFactory.decodeStream(fis); // Chuyển đổi thành Bitmap
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu không tìm thấy ảnh
        }
    }

}
