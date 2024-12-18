package com.example.pandora.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Class.Category;
import com.example.pandora.Class.Location;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.CatetgoryDatabase;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.Adapter.ImageAdapter;
import com.example.pandora.Main.SaveLocationReview;
import com.example.pandora.Main.SearchInfo;
import com.example.pandora.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Home extends Fragment {

    ImageView btnLocation;
    ImageView btnSaveLocation;
    boolean isLogin = false;
    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private ViewPager2 viewPager;
    private RestaurantDatabase restaurantDatabase;
    private List<Restaurant> restaurantList;
    EditText search_toolbar;
    List<Location> lc;
    private List<Restaurant> images;
    private int currentPage = 0;
    private boolean hasShownToast = false;
    int locationid = 0;
    int cateid = 0;
    private Handler handler1 = new Handler();
    private Runnable searchRunnable;
    Spinner spinnerTypeRestaurant;
    TextView txtLocation;
    List<String> item1;
    ArrayList<Integer> ds_history;
    LocationDatabase database;
    CatetgoryDatabase C;
    List<Category> categoryList;
    ArrayAdapter<String> bb;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (currentPage == images.size()) {
                currentPage = 0; // Quay lại tấm đầu tiên
            }
            viewPager.setCurrentItem(currentPage++, true);
            handler.postDelayed(this, 2000); // 2 giây cho mỗi lần lướt
        }
    };

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ds_history = new ArrayList<>();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định
        txtLocation = view.findViewById(R.id.txtLocation);

        search_toolbar = view.findViewById(R.id.search_toolbar);
        item1 = new ArrayList<>();
        //them dữ liệu location
        database = new LocationDatabase(getContext());
        database.open();
        if (database.isTableEmpty()) {
            database.addLocation(new Location("Hồ Chí Minh"));
            database.addLocation(new Location("Hà Nội"));
            database.addLocation(new Location("Nha Trang"));
            database.addLocation(new Location("Vũng Tàu"));
        }
        lc = database.getAllLocations();


        //them dữ liệu categories
        C = new CatetgoryDatabase(getContext());
        C.open();
        if (C.isTableEmpty()) {
            C.addCategory(new Category("Quán ăn nhanh"));
            C.addCategory(new Category("Quán ăn gia đình"));
            C.addCategory(new Category("Quán lẩu/nướng"));
        }
        categoryList= C.getAllCategories();

        restaurantDatabase = new RestaurantDatabase(getContext());
        restaurantDatabase.open();
        if (restaurantDatabase.getAllRestaurants().isEmpty()) {
            // Thêm dữ liệu vào cơ sở dữ liệu
            restaurantDatabase.addRestaurant(new Restaurant("Bánh Mì Cô Lan", "3/4 Lê Văn Lương", 1, 1, "Quán ăn phục vụ các món ăn truyền thống Việt Nam."));
            restaurantDatabase.addRestaurant(new Restaurant("Bistro Sài Gòn", "55 Đường Nguyễn Thị Minh Khai", 2, 2, "Bistro với các món ăn phương Tây, nổi bật với pizza và pasta."));
            restaurantDatabase.addRestaurant(new Restaurant("Nhà hàng Hải Sản Tươi Ngon", "123 Bãi Biển", 3, 3, "Hải sản tươi sống, được chế biến theo nhiều phong cách khác nhau."));
            restaurantDatabase.addRestaurant(new Restaurant("Lẩu Nấm Tân Bình", "72 Đường Tân Bình", 4, 1, "Nhà hàng chuyên phục vụ các món lẩu nấm, bổ dưỡng và ngon miệng."));
            restaurantDatabase.addRestaurant(new Restaurant("Khu Ẩm Thực Phố Cổ", "19 Đường Hàng Buồm", 1, 1, "Khu vực ẩm thực truyền thống với nhiều món ăn đặc sản Hà Nội."));
            restaurantDatabase.addRestaurant(new Restaurant("Café Xuân", "45 Đường Nguyễn Huệ", 2, 2, "Café với không gian yên tĩnh, phục vụ đồ uống và bánh ngọt đa dạng."));
            restaurantDatabase.addRestaurant(new Restaurant("Món Ngon Quê Hương", "18 Đường Lý Thường Kiệt", 3, 1, "Nhà hàng chuyên phục vụ các món ăn miền Trung đặc sắc."));
            restaurantDatabase.addRestaurant(new Restaurant("Nhà Hàng Bạch Mai", "10 Đường Bạch Mai", 4, 1, "Món ăn truyền thống Việt Nam với nguyên liệu tươi ngon, sạch."));
            restaurantDatabase.addRestaurant(new Restaurant("Pizza Italia", "100 Đường Lê Duẩn", 1, 2, "Pizza Italia mang đậm hương vị Ý với nhiều lựa chọn nhân phong phú."));
            restaurantDatabase.addRestaurant(new Restaurant("Cơm Văn Phòng", "30 Đường Nguyễn Xí", 2, 1, "Cơm trưa nhanh chóng, ngon miệng cho dân văn phòng, với các món ăn đa dạng."));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Bún Đậu Mắm Tôm", "10 Đường Tôn Đức Thắng", 1, 1, "Bún đậu mắm tôm truyền thống, món ăn đặc sản Hà Nội."));
            restaurantDatabase.addRestaurant(new Restaurant("Nhà Hàng 5 Sao", "88 Đường Lê Quang Đạo", 2, 2, "Nhà hàng sang trọng, phục vụ các món ăn quốc tế tinh tế và cao cấp."));
            restaurantDatabase.addRestaurant(new Restaurant("Lẩu Dê Ninh Bình", "123 Đường Phan Đăng Lưu", 3, 1, "Nhà hàng nổi tiếng với món lẩu dê Ninh Bình nổi bật."));
            restaurantDatabase.addRestaurant(new Restaurant("Sushi Sài Gòn", "22 Đường Nguyễn Công Trứ", 4, 2, "Sushi tươi ngon, đa dạng các món ăn Nhật Bản từ sushi, sashimi đến ramen."));
            restaurantDatabase.addRestaurant(new Restaurant("Bánh Xèo Sài Gòn", "15 Đường Lê Văn Sỹ", 1, 1, "Nhà hàng chuyên phục vụ bánh xèo miền Nam, giòn ngon, đậm đà."));
            restaurantDatabase.addRestaurant(new Restaurant("Gà Rán KFC", "30 Đường Hoàng Văn Thụ", 2, 2, "Gà rán giòn, món ăn nhanh đặc trưng của KFC, phù hợp cho cả gia đình."));
            restaurantDatabase.addRestaurant(new Restaurant("Nhà Hàng Ẩm Thực Trung Hoa", "100 Đường Phan Đình Phùng", 3, 3, "Ẩm thực Trung Hoa truyền thống với các món dim sum và mỳ đặc sắc."));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Cơm Tấm Hương Xưa", "33 Đường Nguyễn Trãi", 4, 1, "Cơm tấm Sài Gòn đậm đà với nhiều loại topping phong phú."));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Nướng BBQ", "45 Đường Lý Tự Trọng", 1, 2, "BBQ nướng than hoa, thực đơn đa dạng với các loại thịt, hải sản tươi ngon."));
            restaurantDatabase.addRestaurant(new Restaurant("Café Góc Phố", "20 Đường Lý Thái Tổ", 2, 1, "Café cổ điển với không gian yên tĩnh, lý tưởng để thưởng thức trà và cà phê thư giãn."));

        }

        search_toolbar.requestFocus();

        View searchClick = view.findViewById(R.id.searchClick);
        searchClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(requireContext(), SearchInfo.class);
                startActivity(myIntent);
            }
        });
        // Lấy lại danh sách nhà hàng

        restaurantList = restaurantDatabase.getAllRestaurants();

        images= new ArrayList<>();
        for(int i=0; i<4; i++){
            images.add(restaurantList.get(i));
        }

        // Cấu hình RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        restaurantAdapter = new RestaurantAdapter(getContext(), restaurantList);
        recyclerView.setAdapter(restaurantAdapter);
        restaurantAdapter.updateData(restaurantList);

        Button btnLocationCheck = view.findViewById(R.id.btnLocationCheck);
        Button btnHighRatingCheck = view.findViewById(R.id.btnHighRatingCheck);


        btnLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Change button backgrounds
                btnLocationCheck.setBackgroundResource(R.drawable.button_selected_home_background);
                btnHighRatingCheck.setBackgroundResource(R.drawable.button_unselected_home_background);

                // Update the adapter with restaurants based on location
                loading();
            }
        });

        btnHighRatingCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Change button backgrounds
                btnHighRatingCheck.setBackgroundResource(R.drawable.button_selected_home_background);
                btnLocationCheck.setBackgroundResource(R.drawable.button_unselected_home_background);

                // Update the adapter with high-rated restaurants
                loading();
                restaurantList.sort(((restaurant, t1) -> t1.getStar() - restaurant.getStar()));

                restaurantAdapter.updateData(restaurantList);
//                recyclerView.setAdapter(restaurantAdapter);  // Update the RecyclerView's adapter
            }
        });


        NestedScrollView scrollView = view.findViewById(R.id.scrollView); // ID của NestedScrollView

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Kiểm tra nếu cuộn lên đầu
                // Gọi hàm load lại dữ

                if (scrollY == 0 && !hasShownToast) {
                    hasShownToast = true;
                    if (searchRunnable != null) {
                        handler1.removeCallbacks(searchRunnable);
                    }

                    // Đặt Runnable mới với độ trễ 2 giây
                    searchRunnable = () ->{
                        Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                        loading();
                    };
                    handler1.postDelayed(searchRunnable, 1000);

                } else if (scrollY > 0) {
                    hasShownToast = false; // Reset trạng thái nếu không ở đầu
                }
            }
        });



        spinnerTypeRestaurant = view.findViewById(R.id.spinnerTypeRestaurant);
        item1.add("Chọn loại quán");
        if(!txtLocation.getText().toString().equals("Các địa điểm"))
            item1.set(0,"Type of restaurant");
        categoryList = C.getAllCategories();
        for (Category a : categoryList) {
            item1.add(a.getName());
        }
        bb = new ArrayAdapter<String>(getContext()
                , R.layout.spinner_item, item1);

        bb.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTypeRestaurant.setAdapter(bb);
        spinnerTypeRestaurant.setSelection(cateid);
        spinnerTypeRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                spinnerTypeRestaurant.setSelection(position);
                cateid = position;
                loading();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Xử lý sự kiện click
        restaurantAdapter.setOnItemClickListener(restaurant -> {
            DetailRestaurantFragment nextFragment = new DetailRestaurantFragment();

            // Truyền dữ liệu qua Bundle
            Bundle bundle = new Bundle();
            bundle.putInt("restaurant_id", restaurant.getId());

            nextFragment.setArguments(bundle);
            ds_history.add(restaurant.getId());
            // Chuyển Fragment
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in,  // Khi fragment xuất hiện
                            R.anim.fade_out    // Khi fragment rời đi
                    )
                    .replace(R.id.fragment_container, nextFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Thiết lập ViewPager và bắt đầu tự động cuộn
        viewPager = view.findViewById(R.id.viewPager);
        ImageAdapter imageAdapter=new ImageAdapter(getContext(),images);
        viewPager.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickListener(restaurant -> {
            DetailRestaurantFragment nextFragment = new DetailRestaurantFragment();

            // Truyền dữ liệu qua Bundle
            Bundle bundle = new Bundle();
            bundle.putInt("restaurant_id", restaurant.getId());

            nextFragment.setArguments(bundle);
            ds_history.add(restaurant.getId());
            // Chuyển Fragment
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in,  // Khi fragment xuất hiện
                            R.anim.fade_out    // Khi fragment rời đi
                    )
                    .replace(R.id.fragment_container, nextFragment)
                    .addToBackStack(null)
                    .commit();
        });

        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        handler.postDelayed(runnable, 2000); // Bắt đầu tự động lướt

        // Thiết lập chấm chỉ báo
        LinearLayout dotsLayout = view.findViewById(R.id.dotsLayout);
        setupDots(images.size(), dotsLayout);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        });




        // Xử lý sự kiện click cho các button
        btnLocation = view.findViewById(R.id.btnLocation);
        //button địa điểm
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocationAlertDialog();
            }
        });
        //button lich su
        btnSaveLocation = view.findViewById(R.id.btnSaveLocation);
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(requireContext(), SaveLocationReview.class);
                myIntent.putExtra("history", ds_history);
                startActivity(myIntent);
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restaurantDatabase.close();
        database.close();
        C.close();
        handler.removeCallbacks(runnable); // Ngừng lướt khi view của fragment bị hủy
    }

    // Khởi tạo các chấm chỉ báo
    public void setupDots(int count, LinearLayout dotsLayout) {
        dotsLayout.removeAllViews(); // Xóa tất cả các chấm cũ (nếu có)
        ImageView[] dots = new ImageView[count];

        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(requireContext());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.dot_inactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dot_active)); // Chấm đầu tiên sáng

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < count; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(
                            i == position ? R.drawable.dot_active : R.drawable.dot_inactive
                    ));
                }
            }
        });
    }


    private void showLocationAlertDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialogchooselocation_custom_alert, null);

        // Create AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Find the components in the layout
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        Spinner spinnerLocation = dialogView.findViewById(R.id.spinnerLocation);


        List<String> location = new ArrayList<>();
        location.add("Tất cả");
        if(!txtLocation.getText().toString().equals("Các địa điểm"))
            location.set(0,"All");
        lc=database.getAllLocations();

        for (Location a : lc) {
            location.add(a.getName());
        }
        // Use the custom spinner item layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,// Set the custom layout here
                location
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);  // Default dropdown view
        spinnerLocation.setAdapter(adapter);


        final String[] selectedLocation = new String[1];  // Store the selected location


        // Set OnItemSelectedListener for the Spinner
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLocation[0] = adapterView.getItemAtPosition(i).toString();  // Get the selected location
                locationid = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle when nothing is selected (optional)
            }
        });
        spinnerLocation.setSelection(locationid);
        // Handle the save button click
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedLocation[0] != null) {

                    // Truy cập btnLocation từ root view của fragment
                    if (txtLocation != null) {
                        txtLocation.setText(selectedLocation[0]);  // Cập nhật text của nút

                    }
                }
                if (selectedLocation[0].equals("Tất cả")) {

                }
                loading();
                alertDialog.dismiss();
            }
        });

        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    public void loading() {
        restaurantList = restaurantDatabase.getAllRestaurants();
        if (locationid != 0) {
            restaurantList = restaurantDatabase.getRestaurantsByLocation(locationid);
        }
        item1.clear();
        item1.add("Chọn loại quán");
        if(!txtLocation.getText().toString().equals("Các địa điểm"))
            item1.set(0,"Type of restaurant");
        categoryList = C.getAllCategories();
        for (Category a : categoryList) {
            item1.add(a.getName());
        }
        bb.notifyDataSetChanged();
        categoryList = C.getAllCategories();
        if (cateid != 0) {
            restaurantList = restaurantList.stream().filter(p -> p.getCateid() == cateid).collect(Collectors.toList());
        }
        restaurantAdapter.updateData(restaurantList);
    }


}
