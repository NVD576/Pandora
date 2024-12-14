package com.example.pandora.AdminProperties;


import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.Class.Restaurant;
import com.example.pandora.Class.Review;
import com.example.pandora.Database.RestaurantDatabase;
import com.example.pandora.Database.ReviewDatabase;
import com.example.pandora.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKe extends AppCompatActivity {
    BarChart barChart;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thong_ke_admin_properties);

        barChart = findViewById(R.id.bar_chart);
        pieChart = findViewById(R.id.pie_chart);


        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        RestaurantDatabase restaurantDatabase = new RestaurantDatabase(this);
        restaurantDatabase.open();
        List<Restaurant> restaurantList = restaurantDatabase.getAllRestaurants();
        restaurantDatabase.close();

        ReviewDatabase reviewDatabase = new ReviewDatabase(this);
        reviewDatabase.open();
        List<Review> reviewList = reviewDatabase.getAllReviews();


        int index = 0;
        for (Restaurant restaurant : restaurantList) {
            // Thống kê số lượng bình luận (Bar Chart)

            BarEntry barEntry =new BarEntry(index, (long) reviewList.stream()
                    .filter(p -> p.getRestaurantid() == restaurant.getId()).count());

            barEntry.setData(restaurant.getName());
            barEntries.add(barEntry);
            // Thống kê trung bình đánh giá (Pie Chart)
//            pieEntries.add(new PieEntry(restaurant.getStar(), restaurant.getName()));

            index++;
        }


        Map<Float, Integer> starCountMap = new HashMap<>();

        // Đếm số lượng nhà hàng theo từng số sao
        for (Restaurant restaurant : restaurantList) {
            float star = restaurant.getStar();
            starCountMap.put(star, starCountMap.getOrDefault(star, 0) + 1);
        }

        // Thêm dữ liệu vào Pie Chart
        for (Map.Entry<Float, Integer> entry : starCountMap.entrySet()) {
            float star = entry.getKey(); // Số sao
            int count = entry.getValue(); // Số lượng nhà hàng có số sao này
            float percentage = (count * 100f) / restaurantList.size(); // Tính phần trăm
            pieEntries.add(new PieEntry(count,
                    star + " sao / " + String.format("%.1f", percentage) + "%"+" / "+count)); // Hiển thị số sao và %
        }


        reviewDatabase.close();


        BarDataSet barDataSet = new BarDataSet(barEntries, "Comment");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(true);
        barChart.setData(new BarData(barDataSet));
        barChart.animateY(1000);
        barChart.getDescription().setText("Comment Char");
        barChart.getDescription().setTextColor(Color.BLUE);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Rating");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setDrawValues(false);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.animateY(1000);
        pieChart.getDescription().setText("Rating Char");
        pieChart.getDescription().setTextColor(Color.BLUE);



        // Thêm sự kiện khi nhấn vào thanh
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Lấy tên nhà hàng từ Entry (BarEntry)
                String restaurantName = (String) e.getData();

                // Hiển thị tên nhà hàng bằng Toast
                if (restaurantName != null) {
                    Toast.makeText(getApplicationContext(), "Nhà hàng: " + restaurantName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Xử lý khi không có thanh nào được chọn (nếu cần)
            }
        });
    }
}