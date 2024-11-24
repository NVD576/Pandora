package com.example.pandora;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pandora.Database.DatabaseHelper;
import com.example.pandora.Database.UserDatabase;
import com.example.pandora.Login.Login;
import com.example.pandora.Main.Lobby;

import java.net.URISyntaxException;
import java.util.Objects;

public class Profile extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    boolean isLogin = false;
    String userName = "";
    int userid;
    private Uri contentUri;
    TextView login;
    public Profile() {
        // Required empty public constructor
    }

    private String getUserImageFromDatabase(int userId) {
        UserDatabase userDatabase = new UserDatabase(getActivity());
        userDatabase.open();

        // Lấy đường dẫn ảnh của người dùng từ cơ sở dữ liệu
        String imagePath = userDatabase.getUserImage(userId);

        userDatabase.close();
        return imagePath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        login= view.findViewById(R.id.loginProfile);


        Bundle bundle = getArguments();

        if (bundle!=null){

            isLogin = bundle.getBoolean("isLogin", false);
            if (isLogin){
                userid= bundle.getInt("userid");  // Sử dụng giá trị mặc định nếu không tìm thấy
                Log.e("Login", "UserID " +userid);
                userName = bundle.getString("userName");

                Log.e("Login", "name "+userName );

            }
        }
        if (isLogin)
        {
            login.setText(userName);
            Drawable[] drawables = login.getCompoundDrawablesRelative();
            login.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    drawables[0], // drawableStart
                    drawables[1], // drawableTop
                    null,         // drawableEnd
                    drawables[3]  // drawableBottom
            );

            // Lấy đường dẫn ảnh từ cơ sở dữ liệu
            String userImagePath = getUserImageFromDatabase(userid);

            // Nếu có đường dẫn ảnh, cập nhật ImageView
            if (userImagePath != null && !userImagePath.isEmpty()) {
                ImageView userImage = view.findViewById(R.id.userImage);
                Uri imageUri = Uri.parse(userImagePath);
                userImage.setImageURI(imageUri);
            }
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin)
                {
                    Intent myIntent = new Intent(getActivity(), Login.class);
                    startActivity(myIntent);
                }  else {
                    // Mở Image Picker khi đã đăng nhập
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            }
        });


        TextView changeInfo = view.findViewById(R.id.changeInfo);
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin)
                {
                    Toast.makeText(getActivity(),"Yêu cầu đăng nhập",Toast.LENGTH_SHORT).show();
                }
                else {
                    showEditProfileDialog();
                }
            }
        });

        TextView logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin)
                {
                    Toast.makeText(getActivity(),"Yêu cầu đăng nhập",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    login.setText("Đăng nhập");
                    Drawable drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_forward_ios_24);
                    Drawable[] drawables = login.getCompoundDrawablesRelative();
                    login.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            drawables[0],
                            drawables[1],
                            drawableEnd,
                            drawables[3]
                    );
                    isLogin = false;
                }
            }
        });

        return view;
    }



    private void showEditProfileDialog() {
        // Tạo view cho hộp thoại
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        // Ánh xạ các EditText trong hộp thoại
        EditText edtNumberPhone = dialogView.findViewById(R.id.edtNumberPhone);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);

        // Tạo hộp thoại
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chỉnh sửa thông tin cá nhân")
                .setView(dialogView)
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Lấy dữ liệu từ các EditText
                        String newNumberPhone = edtNumberPhone.getText().toString();
                        String newEmail = edtEmail.getText().toString();

                        // Cập nhật thông tin (Ví dụ: bạn có thể lưu vào cơ sở dữ liệu)
                        updateProfile(newNumberPhone, newEmail);
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void updateProfile(String numberPhone, String email) {
        // Cập nhật UI hoặc dữ liệu (ví dụ, cập nhật TextView hoặc lưu vào cơ sở dữ liệu)
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Lấy URI của ảnh được chọn
            Uri selectedImageUri = data.getData();

            // Lấy đường dẫn thực tế từ URI
            String imagePath = getRealPathFromURI(selectedImageUri);

            if (imagePath != null) {
                // Cập nhật vào database
                updateUserImage(imagePath);

                // Cập nhật ngay trong ImageView
                ImageView userImage = requireView().findViewById(R.id.userImage);
                userImage.setImageURI(selectedImageUri);

                // Thông báo thành công
                Toast.makeText(getActivity(), "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Không thể lấy ảnh", Toast.LENGTH_SHORT).show();
            }
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


}