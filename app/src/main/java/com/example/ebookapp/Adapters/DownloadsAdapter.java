package com.example.ebookapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ebookapp.Booksdb;
import com.example.ebookapp.Interfaces.FragmentCallback;
import com.example.ebookapp.PdfDetail;
import com.example.ebookapp.R;

import java.io.File;
import java.util.ArrayList;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.holder> {

    Context context;
    ArrayList<Booksdb> dataList;
    FragmentCallback callback;

    public DownloadsAdapter(Context context, ArrayList<Booksdb> dataList, FragmentCallback callback) {
        this.context = context;
        this.dataList = dataList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_list, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        final Booksdb books = dataList.get(position);

        Log.d("GoodData: ", "name: " + books.getBookName());
        Log.d("GoodData: ", "description: " + books.getDescription());
        Log.d("GoodData: ", "image " + books.getBookImage());

        holder.bookImage.setImageBitmap(getimage(books.getBookImage()));
        holder.heading.setText(books.getBookName());
        holder.description.setText(books.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PdfDetail.class);
                i.putExtra("bookImage", books.getBookImage());
                i.putExtra("bookName", books.getBookName());
                i.putExtra("authorName", books.getAuthorName());
                i.putExtra("description", books.getDescription());
                i.putExtra("pdfUrl", books.getPdfUrl());
                i.putExtra("id", books.getId());
                i.putExtra("bookDownloads", "1");
                context.startActivity(i);
            }
        });

    }

    private Bitmap getimage(String path) {
        File imgFile = new  File(path);

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Log.d("TAGIMG", "getimage: "+myBitmap);
            return myBitmap;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class holder extends RecyclerView.ViewHolder {

        ImageView favBtn;
        ImageView bookImage;
        TextView heading;
        TextView description;

        public holder(@NonNull View itemView) {
            super(itemView);

            favBtn = itemView.findViewById(R.id.favBtn);
            favBtn.setVisibility(View.GONE);
            heading = itemView.findViewById(R.id.text1);
            description = itemView.findViewById(R.id.text2);
            bookImage = itemView.findViewById(R.id.bookImageFavourite);

        }
    }
}
