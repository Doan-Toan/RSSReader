package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ReaderActivity extends AppCompatActivity {
    ListView lvRSS;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> descriptions;
    ArrayList<String> imgUrls;
    ArrayList<String> mediaDes;
    Integer pos;
    String urls[] = {
            "https://vnexpress.net/rss/the-thao.rss",
            "https://vnexpress.net/rss/the-gioi.rss",
            "https://vnexpress.net/rss/gia-dinh.rss",
            "https://vnexpress.net/rss/kinh-doanh.rss",
            "https://vnexpress.net/rss/giai-tri.rss",
            "https://vnexpress.net/rss/oto-xe-may.rss",
            "https://vnexpress.net/rss/cuoi.rss",
            "https://vnexpress.net/rss/so-hoa.rss"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        imgUrls = new ArrayList<String>();
        mediaDes = new ArrayList<String>();
        lvRSS = findViewById(R.id.lvRSS);
        pos = getIntent().getIntExtra("position",0);
        lvRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialogCreate(position);
            }
        });
        new ProcessInBackground().execute();
    }

    public void alertDialogCreate(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReaderActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout,null);
        builder.setView(dialogView);
        TextView txtTitle = dialogView.findViewById(R.id.txtTitle);
        TextView txtDes = dialogView.findViewById(R.id.txtDes);
        //TextView txtMedia = dialogView.findViewById(R.id.txtMedia);
        ImageView img = dialogView.findViewById(R.id.picture);
        txtTitle.setText(titles.get(pos));
        txtDes.setText(descriptions.get(pos));
        //txtMedia.setText(mediaDes.get(pos));
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("More", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(links.get(pos));
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        imgDownload(imgUrls.get(pos),img);
        builder.create();
        builder.show();
    }

    public InputStream setInputStream(URL url){
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e){
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer,Void,Exception>{

        ProgressDialog progressDialog = new ProgressDialog(ReaderActivity.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading data...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            try{
                URL url = new URL(urls[pos]);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(setInputStream(url), "UTF_8");
                boolean insideItem = false;
                int eventType = xpp.getEventType();
                while(eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            insideItem = true;
                        }
                        else if(xpp.getName().equalsIgnoreCase("title")){
                            if(insideItem){
                                titles.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("link")){
                            if(insideItem){
                                links.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("description")){
                            if(insideItem) {
                                String inputString =xpp.nextText();
                                int srcIndex = inputString.indexOf("src=");
                                int greaterThanIndex = inputString.indexOf(">", srcIndex);
                                String part = inputString.substring(srcIndex + "src=".length() + 1, greaterThanIndex - 2);
                                imgUrls.add(part);

                                int breakIndex = inputString.indexOf("</br>");
                                int dotIndex = inputString.indexOf(".", breakIndex);
                                String part2 = inputString.substring(breakIndex + "</br>".length(), dotIndex);
                                descriptions.add(part2);
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
            }
            catch (MalformedURLException e){
                exception = e;
            }
            catch(XmlPullParserException e){
                exception = e;
            }
            catch(IOException e){
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReaderActivity.this,R.layout.list_item,R.id.text_view,titles);
            lvRSS.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }
    public void imgDownload(String photoUrl,ImageView imageView){
        if (!photoUrl.equals("") ) {
            Picasso.Builder builder = new Picasso.Builder(ReaderActivity.this);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    picasso.load(photoUrl).error(R.drawable.images).placeholder(R.drawable.images).into(imageView);
                }
            });
            Picasso picasso = builder.build();
            picasso.load(photoUrl).error(R.drawable.images).placeholder(R.drawable.images).into(imageView);
        } else {
            Picasso.with(ReaderActivity.this).load((Uri) null).error(R.drawable.images).placeholder(R.drawable.images).into(imageView);
        }
    }

}