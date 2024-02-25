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
            "1. Thể thao",
            "2. Amino Acids",
            "3. Grains and Starches",
            "4. Fibers and Legumes",
            "5. Vitamins",
            "6. Minerals",
            "7. Nutraceuticals",
            "8. Processing functional ingredients",
            "9. Fats and Oils",
            "10. Preservatives"
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