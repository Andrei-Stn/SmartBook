package com.example.ebookapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebookapp.Adapters.DownloadsAdapter;
import com.example.ebookapp.Booksdb;
import com.example.ebookapp.Interfaces.FragmentCallback;
import com.example.ebookapp.MainActivity;
import com.example.ebookapp.R;

import java.util.ArrayList;
import java.util.List;

public class Download_Fragment extends Fragment implements FragmentCallback {

    RecyclerView recyclerView;
    ArrayList<Booksdb> dataList;
    DownloadsAdapter downloadAdapter;
    public List<Booksdb> booksdbs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_download_, container, false);
        recyclerView = view.findViewById(R.id.recycleViewDownloads);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        booksdbs = MainActivity.myappdatabas.myDao().getBooks();
        loadData();
        downloadAdapter = new DownloadsAdapter(getContext(), dataList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(downloadAdapter);
    }

    @Override
    public void doSomething() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Bookmark_Fragment()).commit();
    }

    private void loadData() {
        String info = "";
        dataList = new ArrayList<>();
        dataList.clear();

        for (Booksdb booksd : booksdbs)
        {
            dataList.add(booksd);
            String id = booksd.getId();
            String bookName = booksd.getBookName();
            String booksdesc = booksd.getDescription();
            info = info+"\n\n"+"id: "+id+"\n bookname: "+bookName+"\n bookcategory: "+booksdesc;
        }
        Log.e("TAGDATA", info);
    }
}