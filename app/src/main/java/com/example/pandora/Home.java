package com.example.pandora;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Class.Location;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Class.User;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.Main.SearchInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Home extends Fragment {

    ViewPager2 viewPager2;
    User user;
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
    private List<Integer> images = Arrays.asList(R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4);
    private int currentPage = 0;
    private boolean hasShownToast = false;
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

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định

        search_toolbar = view.findViewById(R.id.search_toolbar);
        LocationDatabase database= new LocationDatabase(getContext());
        database.open();
        if(database.isTableEmpty()){
            database.addLocation(new Location("Hồ Chí Minh"));
            database.addLocation(new Location("Hà Nội"));
        }
        lc= database.getAllLocations();
        database.close();

        restaurantDatabase = new RestaurantDatabase(getContext());
        restaurantDatabase.open();
        if (restaurantDatabase.getAllRestaurants().isEmpty()) {
            // Thêm dữ liệu vào cơ sở dữ liệu
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn A", 1, 0));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn B",  1, 0));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn C",  2, 0));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn D", 1, 0));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn E", 2, 0));
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


        // Cấu hình RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        restaurantAdapter = new RestaurantAdapter(restaurantList);
        recyclerView.setAdapter(restaurantAdapter);
        NestedScrollView scrollView = view.findViewById(R.id.scrollView); // ID của NestedScrollView

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Kiểm tra nếu cuộn lên đầu
                // Gọi hàm load lại dữ liệu
                if (scrollY == 0 && !hasShownToast) {
                    onResume();
                    Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                    hasShownToast = true;
                } else if (scrollY > 0) {
                    hasShownToast = false; // Reset trạng thái nếu không ở đầu
                }
            }
        });
        restaurantAdapter.notifyDataSetChanged();

        // Xử lý sự kiện click
        restaurantAdapter.setOnItemClickListener(restaurant -> {
            DetailRestaurantFragment nextFragment = new DetailRestaurantFragment();

            // Truyền dữ liệu qua Bundle
            Bundle bundle = new Bundle();
            bundle.putString("restaurant_name", restaurant.getName());
            bundle.putInt("restaurant_rating", restaurant.getStar());
            bundle.putInt("restaurant_id", restaurant.getId());
            nextFragment.setArguments(bundle);

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
        viewPager.setAdapter(new ImageAdapter(images));
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
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocationAlertDialog();
            }
        });

        btnSaveLocation = view.findViewById(R.id.btnSaveLocation);
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(requireContext(), SaveLocationReview.class);
                startActivity(myIntent);
            }
        });



//        btnAddReview = view.findViewById(R.id.btnAddReview);
//        btnAddReview.setOnClickListener(v -> replaceFragment(new AddReviewFragment()));
//
//        btnReviewList = view.findViewById(R.id.btnReviewList);
//        btnReviewList.setOnClickListener(v -> replaceFragment(new ReviewListFragment()));
//
//        btnShare = view.findViewById(R.id.btnShare);
//        btnShare.setOnClickListener(v -> replaceFragment(new ShareFragment()));




        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restaurantDatabase.close();
        handler.removeCallbacks(runnable); // Ngừng lướt khi view của fragment bị hủy
    }

    // Khởi tạo các chấm chỉ báo
    @SuppressLint("UseCompatLoadingForDrawables")
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

        dots[0].setImageDrawable(ContextCompat.getDrawable( getContext(), R.drawable.dot_active)); // Chấm đầu tiên sáng

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

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // Thêm hiệu ứng chuyển đổi
        transaction.setCustomAnimations(
                R.anim.slide_in_bottom, // Hiện từ dưới lên
                R.anim.fade_out,        // Mất đi khi chuyển tiếp
                R.anim.fade_in,         // Hiện lại khi quay lại
                R.anim.slide_out_top    // Mất từ trên xuống khi quay lại
        );

        // Thay thế fragment hiện tại bằng fragment mới
        transaction.replace(R.id.fragment_container, fragment);

        // Thêm vào back stack nếu muốn có khả năng quay lại fragment trước đó
        transaction.addToBackStack(null);

        // Commit transaction
        transaction.commit();
    }

    private void showLoginAlertDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialogsavelocation_custom_alert, null);

        // Tạo AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Tìm các thành phần trong layout
        Button btnPositive = dialogView.findViewById(R.id.btnPositive);

        // Thiết lập hành động nút
        btnPositive.setOnClickListener(v -> alertDialog.dismiss());

        // Hiển thị hộp thoại
        alertDialog.show();
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



        List<String> location= new ArrayList<>();
        location.add("Tất cả");
        for (Location a: lc){
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

        // If there is a previously saved location, set it in the Spinner
        String savedLocation = getSavedLocation();  // Fetch the saved location from SharedPreferences
        if (savedLocation != null) {
            // Find the index of the saved location and set it in the Spinner
            int position = adapter.getPosition(savedLocation);
            spinnerLocation.setSelection(position);
        }

        // Set OnItemSelectedListener for the Spinner
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLocation[0] = adapterView.getItemAtPosition(i).toString();  // Get the selected location
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle when nothing is selected (optional)
            }
        });

        // Handle the save button click
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedLocation[0] != null) {
                    saveLocation(selectedLocation[0]);

                    // Truy cập btnLocation từ root view của fragment
                    TextView txtLocation = getView().findViewById(R.id.txtLocation);
                    if (txtLocation != null) {
                        txtLocation.setText(selectedLocation[0]);  // Cập nhật text của nút
                    }
                }
                if(selectedLocation[0].equals("Tất cả")){
                    restaurantList=restaurantDatabase.getAllRestaurants();
                }else{
                    restaurantList=restaurantDatabase.getRestaurantsByLocationName(selectedLocation[0]);
                }
                restaurantAdapter.updateData(restaurantList);
                alertDialog.dismiss();
            }
        });

        btnDismiss.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    // Method to save the selected location in SharedPreferences
    private void saveLocation(String location) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selected_location", location);
        editor.apply();
    }

    // Method to retrieve the saved location from SharedPreferences
    private String getSavedLocation() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("selected_location", null);  // Return null if no location is saved
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Khong quay lai dc");
        RestaurantDatabase re= new RestaurantDatabase(getContext());
        re.open();
        restaurantList=re.getAllRestaurants();
        re.close();
        restaurantAdapter.updateData(restaurantList); // Hàm tải lại dữ liệu từ cơ sở dữ liệu
    }


}