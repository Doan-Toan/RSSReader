package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    String title[] = {
            "1. Tin Nổi Bật",
            "2. Khoa Học",
            "3. Gia Đình",
            "4. Kinh Doanh",
            "5. Giải Trí",
            "6. Thế Giới",
            "7. Giáo dục - du học",
            "8. Số Hóa"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.list_item,R.id.text_view,title);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,ReaderActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }
}