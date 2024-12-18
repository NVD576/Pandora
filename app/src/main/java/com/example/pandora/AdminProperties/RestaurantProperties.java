package com.example.pandora.AdminProperties;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Class.Category;
import com.example.pandora.Class.Image;
import com.example.pandora.Class.Location;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.CatetgoryDatabase;
import com.example.pandora.Database.ImageDatabase;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RestaurantProperties extends AppCompatActivity {

    RecyclerView recyclerView;
    private RestaurantDatabase db;
    List<Restaurant> restaurantList;
    RestaurantAdapter adapter;
    CatetgoryDatabase catetgoryDatabase;
    LocationDatabase locationDatabase;
    private static final int PICK_IMAGE_REQUEST = 1;
    Restaurant restaurant;
    View dialogView;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    List<String> items, item1;
    ImageDatabase imageDatabase;
    List<Image> imageList;
    String fileName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_properties);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // location
        locationDatabase = new LocationDatabase(this);
        locationDatabase.open();
        List<Location> lc = locationDatabase.getAllLocations();
        imageDatabase= new ImageDatabase(this);
        imageDatabase.open();



        items = new ArrayList<>();
        items.add("Chọn thành phố");
        for (Location l : lc) {
            items.add(l.getName());
        }

        //Lay ds category
        catetgoryDatabase = new CatetgoryDatabase(this);
        catetgoryDatabase.open();
        List<Category> categoryList = catetgoryDatabase.getAllCategories();


        item1= new ArrayList<>();
        item1.add("Loại quán ăn");
        for (Category l : categoryList) {
            item1.add(l.getName());
        }



        db = new RestaurantDatabase(this); // Khởi tạo với context
        db.open();
        restaurantList = db.getAllRestaurants();


        adapter = new RestaurantAdapter(this,restaurantList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant restaurant1) {
                restaurant=restaurant1;
                showUpdateRestaurantAlertDialog(restaurant);

            }
        });


        EditText searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter the list as the text changes
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                // Đặt Runnable mới với độ trễ 2 giây
                searchRunnable = () -> filterList(charSequence.toString());
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddRestaurantAlertDialog();
            }
        });

    }

    private void showUpdateRestaurantAlertDialog(Restaurant restaurant) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        dialogView = inflater.inflate(R.layout.dialog_update_restaurant_admin, null);

        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        TextView deleteType = dialogView.findViewById(R.id.deleteType);
        EditText updateName= dialogView.findViewById(R.id.updateName);
        EditText updateAddress= dialogView.findViewById(R.id.updateAddress);
        EditText updateDescription= dialogView.findViewById(R.id.updateDescription);

        TextView textImage=dialogView.findViewById(R.id.textImage);
        TextView textImage1=dialogView.findViewById(R.id.textImage1);
        TextView textImage2=dialogView.findViewById(R.id.textImage2);
        TextView textImage3=dialogView.findViewById(R.id.textImage3);



        updateName.setText(restaurant.getName());
        updateAddress.setText(restaurant.getAddress());
        updateDescription.setText(restaurant.getDescription());

        imageList = imageDatabase.getImageByRestaurantId(restaurant.getId());

        if (!imageList.isEmpty())
            textImage.setText(imageList.get(0).getImageUrl());
        if (imageList.size()>1)
            textImage1.setText(imageList.get(1).getImageUrl());
        if (imageList.size()>2)
            textImage2.setText(imageList.get(2).getImageUrl());
        if (imageList.size()>3)
            textImage3.setText(imageList.get(3).getImageUrl());



        Spinner addLocation = dialogView.findViewById(R.id.updateLocation);
        ArrayAdapter<String> aa = new
                ArrayAdapter<String>(this, R.layout.spinner_item,
                items);

        aa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        addLocation.setAdapter(aa);

        addLocation.setSelection(restaurant.getLocationid());
        addLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//               addLocation.setText(items[position]);
                if (items.equals("Chọn loại quán")) {
                    // Không làm gì nếu người dùng chưa chọn
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn loại quán!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý loại quán đã chọn
                    restaurant.setLocationid(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //type of restaurant

        Spinner addRoleRestaurant = dialogView.findViewById(R.id.updateRoleRestaurant);
        ArrayAdapter<String> bb = new
                ArrayAdapter<String>(this, R.layout.spinner_item,
                item1);

        bb.setDropDownViewResource(R.layout.spinner_dropdown_item);
        addRoleRestaurant.setAdapter(bb);
        addRoleRestaurant.setSelection(restaurant.getCateid());
        addRoleRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//               spinner1.setText(items[position]);
                if (item1.equals("Chọn loại quán")) {
                    // Không làm gì nếu người dùng chưa chọn
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn loại quán!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý loại quán đã chọn
                    restaurant.setCateid(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Button btnChooseImage=dialogView.findViewById(R.id.btnChooseImage);
        Button btnChooseImage1=dialogView.findViewById(R.id.btnChooseImage1);
        Button btnChooseImage2=dialogView.findViewById(R.id.btnChooseImage2);
        Button btnChooseImage3=dialogView.findViewById(R.id.btnChooseImage3);

        //button chon image
        btnChooseImage.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST));
        btnChooseImage1.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST + 1));
        btnChooseImage2.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST + 2));
        btnChooseImage3.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST + 3));

        //Xu ly su kien xoa
        deleteType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!imageList.isEmpty())
                    deleteRestaurantImage(imageList.get(0).getImageUrl());
                if (imageList.size()>1)
                    deleteRestaurantImage(imageList.get(1).getImageUrl());

                if (imageList.size()>2)
                    deleteRestaurantImage(imageList.get(2).getImageUrl());

                if (imageList.size()>3)
                    deleteRestaurantImage(imageList.get(3).getImageUrl());

                db.deleteRestaurant(restaurant.getId());

                recreate();
//                adapter.notifyDataSetChanged();
            }
        });

        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {
            restaurant.setName(updateName.getText().toString());
            restaurant.setAddress(updateAddress.getText().toString());
            restaurant.setDescription(updateDescription.getText().toString());

            Image a=new Image(restaurant.getId(), textImage.getText().toString());
            Image b=new Image(restaurant.getId(), textImage1.getText().toString());
            Image c=new Image(restaurant.getId(), textImage2.getText().toString());
            Image d=new Image(restaurant.getId(), textImage3.getText().toString());

            if(!textImage.getText().toString().equals(""))
            {
                if(imageList.size()>0){
                    imageList.get(0).setImageUrl(a.getImageUrl());
                    imageDatabase.updateImage(imageList.get(0));
                }else
                    imageDatabase.addImage(a);
            }

            if(!textImage1.getText().toString().equals(""))
            {
                if(imageList.size()>1){
                    imageList.get(1).setImageUrl(b.getImageUrl());
                    imageDatabase.updateImage(imageList.get(1));
                }else
                    imageDatabase.addImage(b);
            }

            if(!textImage2.getText().toString().equals(""))
            {
                if(imageList.size()>2){
                    imageList.get(2).setImageUrl(c.getImageUrl());
                    imageDatabase.updateImage(imageList.get(2));
                }else
                    imageDatabase.addImage(c);
            }

            if(!textImage3.getText().toString().equals(""))
            {
                if(imageList.size()>2){
                    imageList.get(3).setImageUrl(d.getImageUrl());
                    imageDatabase.updateImage(imageList.get(3));
                }else
                    imageDatabase.addImage(d);
            }

            db.updateRestaurant(restaurant);
//            recreate();
            adapter.notifyDataSetChanged();

            alertDialog.dismiss();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        // Hiển thị hộp thoại
        alertDialog.show();
    }

    private void showAddRestaurantAlertDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // Dùng `this` thay cho `getApplicationContext()`
        dialogView = inflater.inflate(R.layout.dialog_add_restaurant_admin, null);

        EditText addRestaurantName= dialogView.findViewById(R.id.addRestaurantName);
        EditText addAddress= dialogView.findViewById(R.id.addAddress);
        EditText addDescription= dialogView.findViewById(R.id.addDescription);

        restaurant= new Restaurant();


        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this) // Dùng `this` thay cho `getApplicationContext()`
                .setView(dialogView)
                .setCancelable(true)
                .create();



        Spinner addLocation = dialogView.findViewById(R.id.addLocation);
        ArrayAdapter<String> aa = new
                ArrayAdapter<String>(this, R.layout.spinner_item,
                items);

        aa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        addLocation.setAdapter(aa);
        addLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//               spinner1.setText(items[position]);
                if (items.equals("Chọn loại quán")) {
                    // Không làm gì nếu người dùng chưa chọn
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn loại quán!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý loại quán đã chọn
                    restaurant.setLocationid(position);

                 }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //type of restaurant
        Spinner addRoleRestaurant = dialogView.findViewById(R.id.addRoleRestaurant);
        ArrayAdapter<String> bb = new
                ArrayAdapter<String>(this, R.layout.spinner_item,
                item1);

        bb.setDropDownViewResource(R.layout.spinner_dropdown_item);
        addRoleRestaurant.setAdapter(bb);
        addRoleRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//               spinner1.setText(items[position]);
                if (item1.equals("Chọn loại quán")) {
                    // Không làm gì nếu người dùng chưa chọn
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn loại quán!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý loại quán đã chọn
                    restaurant.setCateid(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        TextView textImage=dialogView.findViewById(R.id.textImage);
        TextView textImage1=dialogView.findViewById(R.id.textImage1);
        TextView textImage2=dialogView.findViewById(R.id.textImage2);
        TextView textImage3=dialogView.findViewById(R.id.textImage3);


        Button btnChooseImage=dialogView.findViewById(R.id.btnChooseImage);
        Button btnChooseImage1=dialogView.findViewById(R.id.btnChooseImage1);
        Button btnChooseImage2=dialogView.findViewById(R.id.btnChooseImage2);
        Button btnChooseImage3=dialogView.findViewById(R.id.btnChooseImage3);

        //button chon image
        btnChooseImage.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST));
        btnChooseImage1.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST + 1));
        btnChooseImage2.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST + 2));
        btnChooseImage3.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST + 3));




        // Tìm các thành phần trong layout dialog
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);


        // Xử lý sự kiện nút "Lưu"
        btnSave.setOnClickListener(view -> {
            restaurant.setName(addRestaurantName.getText().toString());
            restaurant.setAddress(addAddress.getText().toString());
            restaurant.setDescription(addDescription.getText().toString());
            db.addRestaurant(restaurant);


            if(!textImage.getText().toString().equals("")){
                imageDatabase.addImage(new Image(restaurant.getId(), textImage.getText().toString()));
            }
            if(!textImage1.getText().toString().equals("")){
                imageDatabase.addImage(new Image(restaurant.getId(), textImage1.getText().toString()));
            }
            if(!textImage2.getText().toString().equals("")){
                imageDatabase.addImage(new Image(restaurant.getId(), textImage2.getText().toString()));
            }
            if(!textImage3.getText().toString().equals("")){
                imageDatabase.addImage(new Image(restaurant.getId(), textImage3.getText().toString()));
            }

            adapter.updateData(restaurantList);
            recreate();
            alertDialog.dismiss();
        });

        // Xử lý sự kiện nút "Đóng"
        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        // Hiển thị hộp thoại
        alertDialog.show();
    }

    private void filterList(String query) {
        if (restaurantList == null || restaurantList.isEmpty()) {
            return;
        }

        List<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            if (restaurant.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(restaurant);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFilteredList(filteredList);

        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo đóng database khi Activity bị hủy
        if (db != null) {
            db.close();
        }
        if(locationDatabase!=null)
            locationDatabase.close();
        if(catetgoryDatabase!=null)
            catetgoryDatabase.close();
        if(imageDatabase!=null)
            imageDatabase.close();
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }
    //lưu ảnh
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileName="";

        if ( resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Lấy URI của ảnh được chọn
            Uri selectedImageUri = data.getData();

            // Lấy đường dẫn thực tế từ URI
            try {
                // Chuyển đổi URI sang Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                Calendar calendar = Calendar.getInstance(); // Khởi tạo Calendar
                long timestamp = calendar.getTimeInMillis(); // Lấy thời gian hiện tại dưới dạng milliseconds

                // Tạo tên file duy nhất
                fileName = "restaurant_image_" + timestamp + ".png";

                // Lưu Bitmap vào bộ nhớ trong
                saveImageToInternalStorage(bitmap, fileName);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Cập nhật ngay trong ImageView
            TextView textImage= dialogView.findViewById(R.id.textImage);
            TextView textImage1= dialogView.findViewById(R.id.textImage1);
            TextView textImage2= dialogView.findViewById(R.id.textImage2);
            TextView textImage3= dialogView.findViewById(R.id.textImage3);
            switch (requestCode) {

                case PICK_IMAGE_REQUEST:
                    textImage.setText(fileName);
                    break;
                case PICK_IMAGE_REQUEST + 1:
                    textImage1.setText(fileName);
                    break;
                case PICK_IMAGE_REQUEST + 2:
                    textImage2.setText(fileName);
                    break;
                case PICK_IMAGE_REQUEST + 3:
                    textImage3.setText(fileName);
                    break;
            }


            // Thông báo thành công
            Toast.makeText(this, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        try {
            // Lưu ảnh vào bộ nhớ trong
            FileOutputStream fos = this.openFileOutput(fileName, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.e("SaveImage", "Ảnh đã được lưu vào bộ nhớ trong: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lưu ảnh thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức xóa ảnh từ đường dẫn cụ thể
    public void deleteImage(String imagePath) {
        File imageFile = new File(imagePath);

        // Kiểm tra xem file có tồn tại không
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }

    // Ví dụ về cách sử dụng phương thức deleteImage()
    public void deleteRestaurantImage(String imageRestaurant) {
        // Giả sử bạn lấy đường dẫn ảnh từ cơ sở dữ liệu
        String imagePath = getRestaurantImagePath(imageRestaurant); // Lấy đường dẫn ảnh từ cơ sở dữ liệu

        if (imagePath != null) {
            deleteImage(imagePath);
        }
    }

    // Giả sử bạn lấy đường dẫn ảnh từ cơ sở dữ liệu
    private String getRestaurantImagePath(String imageRestaurant) {
        return "/data/data/com.example.pandora/files/"+imageRestaurant;
    }


}