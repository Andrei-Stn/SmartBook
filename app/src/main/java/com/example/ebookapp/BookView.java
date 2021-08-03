package com.example.ebookapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.ebookapp.Model.Books;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import android.icu.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

public class BookView extends AppCompatActivity implements OnLoadCompleteListener, OnPageChangeListener, OnPageErrorListener {

    private static final int WRITE_EXT_STORAGECODE = 1;
    PDFView pdfView;
    ProgressDialog progressDialog;
    Context context;
    MediaPlayer mp;
    String id1;
    String identity = "";
    String identity2 = "";
    String bookImageUrl;
    int pageNumber = 0;
    String pageNo = "";
    int pageNo2 = 0;
    int pageNoRecreate = 0;
    String url;
    OnTapListener onTapListener;

    String fileDirectory;
    boolean nightMode;
    boolean readType;

    File pdfUrl;
    String totalPage;
    String bookName;

    private NavigationView.OnNavigationItemSelectedListener navListenerview;
    BottomNavigationView bottomNav;
    private boolean tapped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookview);


        bottomNav = findViewById(R.id.bottom_navigation_bookView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        Intent i = getIntent();
        url = i.getStringExtra("pdfUrl");
        id1 = i.getStringExtra("bookId");
        identity = i.getStringExtra("identity");
        bookImageUrl = i.getStringExtra("bookImage");
        pageNo = i.getStringExtra("pageNumber");
        bookName = i.getStringExtra("bookName");


        SharedPreferences sharedPreferences = getSharedPreferences("bookmarks", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);
        readType = sharedPreferences.getBoolean("readType", true);
        identity2 = sharedPreferences.getString("identity", "");
        pageNoRecreate = sharedPreferences.getInt("page", -1);

        if (pageNoRecreate >= 0) {
            pageNo2 = pageNoRecreate;
        }


        if (pageNo != null) {
            pageNo2 = Integer.parseInt(pageNo);
        }

        pdfView = findViewById(R.id.pdfView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait\nFetching pdf...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        onTapListener = new OnTapListener() {
            @Override
            public boolean onTap(MotionEvent e) {
                if (tapped) {
                    bottomNav.setVisibility(View.INVISIBLE);
                    tapped = false;
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                    tapped = true;
                }
                return true;
            }
        };


        FileLoader.with(this)
                .load(url, false)
                .fromDirectory("test4", FileLoader.DIR_INTERNAL)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        pdfUrl = response.getBody();
                        if ("bookmarks".equals(identity) || "itself".equals(identity2)) {
                            try {
                                pdfView.fromFile(pdfUrl)
                                        .onTap(onTapListener)
                                        .onPageScroll(new OnPageScrollListener() {
                                            @Override
                                            public void onPageScrolled(int page, float positionOffset) {
                                                bottomNav.setVisibility(View.VISIBLE);

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bottomNav.setVisibility(View.INVISIBLE);
                                                    }
                                                }, 1500);
                                            }
                                        })
                                        .defaultPage(pageNo2)
                                        .enableSwipe(true)
                                        .enableAnnotationRendering(true)
                                        .onLoad(BookView.this)
                                        .onPageChange(BookView.this)
                                        .scrollHandle(new DefaultScrollHandle(BookView.this, readType))
                                        .enableDoubletap(true)
                                        .onPageError(BookView.this)
                                        .swipeHorizontal(readType)
                                        .pageFitPolicy(FitPolicy.WIDTH)
                                        .fitEachPage(true)
                                        .pageSnap(true)
                                        .pageFling(true)
                                        .spacing(0)
                                        .nightMode(nightMode)
                                        .load();

                                SharedPreferences sharedPreferences = getSharedPreferences("bookmarks", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("identity", "");
                                editor.putInt("page", 0);
                                editor.commit();
                                Log.i("FilePath: ", "onLoad: " + pdfUrl);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                pdfView.fromFile(pdfUrl)
                                        .onTap(onTapListener)
                                        .onPageScroll(new OnPageScrollListener() {
                                            @Override
                                            public void onPageScrolled(int page, float positionOffset) {
                                                bottomNav.setVisibility(View.VISIBLE);

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bottomNav.setVisibility(View.INVISIBLE);
                                                    }
                                                }, 1500);
                                            }
                                        })
                                        .defaultPage(0)
                                        .enableSwipe(true)
                                        .enableAnnotationRendering(true)
                                        .onLoad(BookView.this)
                                        .onPageChange(BookView.this)
                                        .scrollHandle(new DefaultScrollHandle(BookView.this, readType))
                                        .enableDoubletap(true)
                                        .onPageError(BookView.this)
                                        .swipeHorizontal(readType)
                                        .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                                        .fitEachPage(true)
                                        .pageSnap(true)
                                        .pageFling(true)
                                        .spacing(0)
                                        .nightMode(nightMode)
                                        .load();

                                Log.i("FilePath: ", "onLoad: " + pdfUrl);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(BookView.this, "" + t.getMessage() + ", File Error", Toast.LENGTH_SHORT).show();

                    }
                });


        sharedPreferences = this.getSharedPreferences("favourites", Context.MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("task_" + id1, false);

        Menu menu = bottomNav.getMenu();
        if (check) {
            menu.findItem(R.id.favourite).setIcon(R.drawable.ic_favourite);
        } else {
            menu.findItem(R.id.favourite).setIcon(R.drawable.ic_favourite_border);
        }


        if (nightMode != true) {
            menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_night);
        } else {
            menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_night_filled);

        }

        if (readType != true) {
            menu.findItem(R.id.readMode).setIcon(R.drawable.scroll);
        } else {
            menu.findItem(R.id.readMode).setIcon(R.drawable.swipe);
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        progressDialog.dismiss();
        Log.d("Tag2", "Pages2:" + pdfView.getPageCount());
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        totalPage = String.valueOf(pageCount);
        SharedPreferences sharedPreferences = this.getSharedPreferences("bookmarks", Context.MODE_PRIVATE);
        int value = sharedPreferences.getInt("pageNumber_" + id1, 0);
        Boolean clicked = sharedPreferences.getBoolean("pageClicked_" + id1, false);

        int result = value - 1;

        Menu menu = bottomNav.getMenu();
        if (result == pageNumber && clicked) {
            menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_bookmark_filled);
        } else {
            menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_bookmark_outline);
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            switch (id) {

                case R.id.nav_home:
                    SharedPreferences sharedPreferences = getSharedPreferences("bookmarks", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (nightMode != true) {

                        editor.putBoolean("nightMode", true);
                        editor.putString("identity", "itself");
                        editor.putInt("page", pageNumber);
                        editor.commit();
                        item.setIcon(R.drawable.ic_night);
                        recreate();

                        Toast.makeText(BookView.this, "Night Mode turned on", Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putBoolean("nightMode", false);
                        editor.putString("identity", "itself");
                        editor.putInt("page", pageNumber);
                        editor.commit();
                        item.setIcon(R.drawable.ic_night_filled);

                        recreate();
                        Toast.makeText(BookView.this, "Night Mode turned off", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.readMode:
                    sharedPreferences = getSharedPreferences("bookmarks", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    if (readType != true) {

                        editor.putBoolean("readType", true);
                        editor.putString("identity", "itself");
                        editor.putInt("page", pageNumber);
                        editor.commit();
                        item.setIcon(R.drawable.swipe);

                        recreate();
                        Toast.makeText(BookView.this, "Swipe to read", Toast.LENGTH_SHORT).show();

                    } else {
                        editor.putBoolean("readType", false);
                        editor.putString("identity", "itself");
                        editor.putInt("page", pageNumber);
                        editor.commit();
                        item.setIcon(R.drawable.scroll);

                        recreate();
                        Toast.makeText(BookView.this, "Scroll to read", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.bookmark:
                    sharedPreferences = getSharedPreferences("bookmarks", MODE_PRIVATE);
                    editor = sharedPreferences.edit();

                    pageNumber++;
                    editor.putBoolean("pageClicked_" + id1, true);
                    editor.putInt("pageNumber_" + id1, pageNumber);

                    editor.putString("pdfUrl_" + id1, url);
                    editor.putString("totalPage_" + id1, totalPage);

                    editor.commit();

                    item.setIcon(R.drawable.ic_bookmark_filled);
                    Toast.makeText(BookView.this, "Bookmarked", Toast.LENGTH_SHORT).show();

                    break;

                case R.id.favourite:
                    sharedPreferences = getSharedPreferences("favourites", Context.MODE_PRIVATE);
                    boolean check = sharedPreferences.getBoolean("task_" + id1, false);

                    if (check) {

                        sharedPreferences = getSharedPreferences("favourites", MODE_PRIVATE);
                        editor = sharedPreferences.edit();

                        editor.putBoolean("task_" + id1, false);
                        editor.commit();

                        item.setIcon(R.drawable.ic_favourite_border);
                        Toast.makeText(BookView.this, "Removed from favourites", Toast.LENGTH_SHORT).show();

                    } else {
                        sharedPreferences = getSharedPreferences("favourites", MODE_PRIVATE);
                        editor = sharedPreferences.edit();

                        editor.putBoolean("task_" + id1, true);
                        editor.commit();

                        item.setIcon(R.drawable.ic_favourite);
                        Toast.makeText(BookView.this, "Saved in favourites", Toast.LENGTH_SHORT).show();

                    }
                    break;

                case R.id.share:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Hey, I am reading this. If you want to read it download the app.\n\n*" + bookName + "*\n\n*Read on SmartEbook*\n";
                    String shareSubject = "Shareable link";

                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);


                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                    final Query query = reference.orderByChild("id").equalTo(id1);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot != null) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Books books = snapshot1.getValue(Books.class);
                                    if (snapshot1.child("shared").exists()) {
                                        String share = books.getShared();
                                        int integerReader = (Integer.parseInt(share)) + 1;
                                        books.setShared(String.valueOf(integerReader));
                                        reference.child(id1).setValue(books);
                                    } else {
                                        books.setShared("1");
                                        reference.child(id1).setValue(books);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    startActivity(Intent.createChooser(sharingIntent, "Share Using"));
                    break;

                default:
                    Toast.makeText(BookView.this, "Nav_Home", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };


}