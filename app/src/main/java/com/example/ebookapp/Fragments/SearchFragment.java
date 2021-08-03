package com.example.ebookapp.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ebookapp.Adapters.SearchAdapter;
import com.example.ebookapp.Model.Books;
import com.example.ebookapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private List<Books> mBooks;
    EditText search_bar;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        search_bar= view.findViewById(R.id.search_bar);
        mBooks = new ArrayList<>();
        searchAdapter = new SearchAdapter(getContext(), mBooks);
        recyclerView.setAdapter(searchAdapter);

        populateBooks();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());
            }
        });
        return view;
    }


    private void filter(String text) {

        ArrayList<Books> filteredList = new ArrayList<>();

        for (Books book : mBooks) {
            if (book.getBookName().toLowerCase().contains(text.toLowerCase())||
                    book.getAuthorName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(book);
            }
        }

        searchAdapter.filterList(filteredList);
    }

    private void populateBooks(){
        Query query = FirebaseDatabase.getInstance().getReference("Books").orderByChild("bookName");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Books books = snapshot1.getValue(Books.class);
                    mBooks.add(books);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}