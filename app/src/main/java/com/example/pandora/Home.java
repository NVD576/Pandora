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

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pandora.Adapter.RestaurantAdapter;
import com.example.pandora.Adapter.RestaurantHighRatingAdapter;
import com.example.pandora.Class.Category;
import com.example.pandora.Class.Location;
import com.example.pandora.Class.Restaurant;
import com.example.pandora.Class.User;
import com.example.pandora.Database.CatetgoryDatabase;
import com.example.pandora.Database.LocationDatabase;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.Main.SearchInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Home extends Fragment {

    ViewPager2 viewPager2;
    User user;
    ImageView btnLocation;
    ImageView btnSaveLocation;
    boolean isLogin = false;
    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private RestaurantHighRatingAdapter restaurantHighRatingAdapter;
    private ViewPager2 viewPager;
    private RestaurantDatabase restaurantDatabase;
    private List<Restaurant> restaurantList;
    EditText search_toolbar;
    List<Location> lc;
    private List<Integer> images = Arrays.asList(R.drawable.res1, R.drawable.res2, R.drawable.res3, R.drawable.res4);
    private int currentPage = 0;
    private boolean hasShownToast = false;
    int locationid = 0;
    int cateid = 0;
    private Handler handler1 = new Handler();
    private Runnable searchRunnable;
    Spinner spinnerTypeRestaurant;
    List<String> item1;
    ArrayList<Integer> ds_history;
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

        ds_history = new ArrayList<>();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("isLogin", false); // false là giá trị mặc định

        search_toolbar = view.findViewById(R.id.search_toolbar);
        item1 = new ArrayList<>();
        //them dữ liệu location
        LocationDatabase database = new LocationDatabase(getContext());
        database.open();
        if (database.isTableEmpty()) {
            database.addLocation(new Location("Hồ Chí Minh"));
            database.addLocation(new Location("Hà Nội"));
        }
        lc = database.getAllLocations();
        database.close();

        //them dữ liệu categories
        CatetgoryDatabase C = new CatetgoryDatabase(getContext());
        C.open();
        if (C.isTableEmpty()) {
            C.addCategory(new Category("Quán ăn nhanh"));
            C.addCategory(new Category("Quán ăn gia đình"));
            C.addCategory(new Category("Quán lẩu/nướng"));
        }

        List<Category> categoryList = C.getAllCategories();
        item1.add("Chọn loại quán");
        for (Category a : categoryList) {
            item1.add(a.getName());
        }
        C.close();

        restaurantDatabase = new RestaurantDatabase(getContext());
        restaurantDatabase.open();
        if (restaurantDatabase.getAllRestaurants().isEmpty()) {
            // Thêm dữ liệu vào cơ sở dữ liệu
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn A", 1, 2, 0));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn B", 1, 1, 0));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn C", 2, 3, 0));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn D", 1, 2, 0));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn E", 2, 1, 0));
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
        ArrayAdapter<String> bb = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, item1);

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
            bundle.putString("restaurant_name", restaurant.getName());
            bundle.putInt("restaurant_rating", restaurant.getStar());
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
        location.add("All");
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

        // Handle the save button click
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedLocation[0] != null) {

                    // Truy cập btnLocation từ root view của fragment
                    TextView txtLocation = getView().findViewById(R.id.txtLocation);
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

        if (cateid != 0) {
            restaurantList = restaurantList.stream().filter(p -> p.getCateid() == cateid).collect(Collectors.toList());
        }
        restaurantAdapter.updateData(restaurantList);
    }


}
