package kr.ac.hs.recipe.ui.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.*;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.activity.MainActivity;

public class CustomAdapter extends BaseAdapter {
    public ArrayList<ListView> itemList = new ArrayList<ListView>() ;

    public CustomAdapter() {

    }

    @Override
    public int getCount() {
        return itemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final int checkBoxPosition = position;
        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.searchlist_layout, parent, false);

            holder = new ViewHolder();
            holder.imageView = v.findViewById(R.id.searchlist_img);
            holder.nameView = v.findViewById(R.id.searchlist_name) ;
            holder.aboutView = v.findViewById(R.id.searchlist_about) ;
            holder.keepView = v.findViewById(R.id.keepBtn);

            v.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListView listItem = itemList.get(position);

        //Bitmap img = urlToBitmap(listItem.getBImg());
        //holder.imageView.setImageBitmap(img);
        holder.imageView.setImageBitmap(listItem.getBImg());
        holder.nameView.setText(listItem.getName());
        holder.aboutView.setText(listItem.getAbout());

        holder.keepView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView checkViewItem = (ListView) getItem(checkBoxPosition);
                if(checkViewItem.isChecked){
                    itemList.get(checkBoxPosition).isChecked = false;
                }
                else {
                    itemList.get(checkBoxPosition).isChecked = true;
                    Toast.makeText(v.getContext(), itemList.get(checkBoxPosition).getName() + " 즐겨찾기! ", Toast.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();
            }
        });

        holder.keepView.setChecked(itemList.get(position).isChecked);

        return v;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position) ;
    }

    public void addItem(String url, String name, String about,String seq) {
        ListView item = new ListView();

        Bitmap img = urlToBitmap(url); // url > bitmap
        //item.setBImg(url);
        item.setBImg(img);
        item.setName(name);
        item.setAbout(about);
        item.setSeq(seq);

        itemList.add(item);
    }

    //URL을 받아 Bitmap 파일로 전환
    public Bitmap urlToBitmap(final String Url){
        final Bitmap[] bitmap = new Bitmap[1];
        Thread mThread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL(Url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

        try{
            //멀티쓰레드 작업 중 서브스레드의 결과를 메인스레드에 적용시켜야 할 필요가 있는 경우
            //메인스레드의 필요한 작업이 종요할 때 까지 대기하도록 하는 메서드
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap[0];
    }

    public void clear() {
        itemList.clear();
    }

    public class ViewHolder
    {
        public ImageView imageView;
        public TextView nameView, aboutView;
        public CheckBox keepView;
    }
}
