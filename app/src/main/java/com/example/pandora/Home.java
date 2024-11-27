package com.example.pandora;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.pandora.Class.Restaurant;
import com.example.pandora.Database.RestaurantDatabase;

import java.util.Arrays;
import java.util.List;

public class Home extends Fragment {


    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private ViewPager2 viewPager;
    private RestaurantDatabase restaurantDatabase;
    private List<Restaurant> restaurantList;

    private List<Integer> images = Arrays.asList(R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4);
    private int currentPage = 0;
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
//        Context context = null;
//        context.deleteDatabase("restaurant_db"); // Xóa cơ sở dữ liệu cũ

        restaurantDatabase = new RestaurantDatabase(getContext());
        restaurantDatabase.open();
        if (restaurantDatabase.getAllRestaurants().isEmpty())
        {
            // Adding restaurant data using the constructor without 'id'
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn A", "Đánh giá rất tốt, món ăn ngon", R.drawable.image1,4));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn B", "Không gian thoải mái, phục vụ nhanh", R.drawable.image2,3));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn C", "Món ăn đậm đà, giá cả hợp lý", R.drawable.image3,5));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn D", "Đồ ăn không ngon lắm", R.drawable.image4,1));
            restaurantDatabase.addRestaurant(new Restaurant("Quán Ăn E", "Đồ ăn không ngon lắm", R.drawable.image1,2));

        }


        // Lấy lại danh sách nhà hàng sau khi thêm
        restaurantList = restaurantDatabase.getAllRestaurants();


        // Cấu hình RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        restaurantAdapter = new RestaurantAdapter(restaurantList);
        recyclerView.setAdapter(restaurantAdapter);
        restaurantAdapter.notifyDataSetChanged();

        // Xử lý sự kiện click
        restaurantAdapter.setOnItemClickListener(restaurant -> {
            DetailRestaurantFragment nextFragment = new DetailRestaurantFragment();

            // Truyền dữ liệu qua Bundle
            Bundle bundle = new Bundle();
            bundle.putString("restaurant_name", restaurant.getName());
            bundle.putString("restaurant_review", restaurant.getReview());
            bundle.putInt("restaurant_rating", restaurant.getStart());
            nextFragment.setArguments(bundle);

            // Chuyển Fragment
            getParentFragmentManager().beginTransaction()
                    // Áp dụng animation vào fragment xuất hiện và fragment rời đi
                    .setCustomAnimations(
                            R.anim.fragment_enter,  // Khi fragment xuất hiện
                            R.anim.fragment_exit    // Khi fragment rời đi
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
}