package com.example.ebookapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ebookapp.Adapters.BookmarkAdapter;
import com.example.ebookapp.Interfaces.FragmentCallback;
import com.example.ebookapp.Model.Books;
import com.example.ebookapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Bookmark_Fragment extends Fragment implements FragmentCallback {

    RecyclerView recyclerView;
    List<Books> dataList;
    BookmarkAdapter bookmarkAdapter;
    ProgressDialog progressDialog;
    TextView nothingTextBookmark;

    SharedPreferences sharedPreferences;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark_, container, false);

        nothingTextBookmark = view.findViewById(R.id.nothingTextBookmark);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        context = getContext();

        recyclerView = view.findViewById(R.id.recycleViewBookmark);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            dataList = new ArrayList<>();
            bookmarkAdapter = new BookmarkAdapter(getContext(), dataList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataList.clear();
                    if (snapshot.getValue() != null) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            progressDialog.dismiss();
                            Books books = snapshot1.getValue(Books.class);

                            sharedPreferences = context.getSharedPreferences("bookmarks", Context.MODE_PRIVATE);
                            int value = sharedPreferences.getInt("pageNumber_" + books.getId(), 0);

                            if(value != 0){
                                nothingTextBookmark.setVisibility(View.GONE);
                                dataList.add(books);
                            }
                        }
                        recyclerView.setAdapter(bookmarkAdapter);
                    }else{
                        progressDialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Download_Fragment()).commit();
        }

    }

    @Override
    public void doSomething() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Bookmark_Fragment()).commit();
    }
}