package com.example.ebookapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ebookapp.Model.Books;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class PdfDetail extends AppCompatActivity {

    private static final int WRITE_EXT_STORAGECODE = 1;
    private static final int READ_EXT_STORAGECODE = 2;

    FloatingActionButton favouriteBtn;
    FloatingActionButton downloadBtn;
    Button read;
    ImageView bookImageBack;
    TextView bookNameBack;
    TextView authorNameBack;
    TextView likesText;
    TextView readersText;
    TextView shareText;
    TextView bookCategory1;
    TextView downloads;
    TextView descriptionText;

    ArrayList<Books> dataListOffine;
    SharedPreferences sharedPreferences;

    File file;
    String str;
    Bitmap bitmap;
    ImageView backgroundImage;
    String id = "";

    String bookName, authorName, bookImage, pdfUrl, description, downloadsstring, bookCategory = "";
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_detail);

        progressDialog = new ProgressDialog(this);
        ((ProgressDialog) progressDialog).setMessage("Please wait\nDownloading pdf...");
        progressDialog.setCancelable(false);

        favouriteBtn = findViewById(R.id.fab);
        downloadBtn = findViewById(R.id.downloadBtn);
        bookImageBack = findViewById(R.id.bookImage);
        bookNameBack = findViewById(R.id.bookName);
        authorNameBack = findViewById(R.id.authorName);
        read = findViewById(R.id.readBtn);
        backgroundImage = findViewById(R.id.background_blur);
        likesText = findViewById(R.id.likesText);
        readersText = findViewById(R.id.readerText);
        shareText = findViewById(R.id.shareText);
        bookCategory1 = findViewById(R.id.bookCategory);
        downloads = findViewById(R.id.reviewText);
        descriptionText = findViewById(R.id.description);

        dataListOffine = new ArrayList<>();

        Intent i = getIntent();
        bookName = i.getStringExtra("bookName");
        authorName = i.getStringExtra("authorName");
        bookImage = i.getStringExtra("bookImage");
        pdfUrl = i.getStringExtra("pdfUrl");
        description = i.getStringExtra("description");
        bookCategory = i.getStringExtra("bookCategory");
        downloadsstring = i.getStringExtra("bookDownloads");

        descriptionText.setText(description);

        id = i.getStringExtra("id");

        Log.d("bookData:", "bookName " + bookName);
        Log.d("bookData:", "authorName " + authorName);
        Log.d("bookData:", "bookImage " + bookImage);
        Log.d("bookData:", "pdfUrl " + pdfUrl);
        Log.d("bookData:", "description " + description);
        Log.d("bookData:", "id " + id);
        Log.d("idTest: ", "Id: " + id);

        sharedPreferences = this.getSharedPreferences("favourites", Context.MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("liked_" + id, false);

        if (check) {
            favouriteBtn.invalidate();
            favouriteBtn.setBackground(getResources().getDrawable(R.drawable.ic_thumb_up_filled));
        } else {
            favouriteBtn.setImageResource(R.drawable.ic_thumb_up);
        }

        Glide.with(this)
                .load(bookImage)
                .into(bookImageBack);

        Glide.with(this).load(bookImage)
                .apply(bitmapTransform(new BlurTransformation(30)))
                .into(backgroundImage);

        bookNameBack.setText(bookName);
        authorNameBack.setText(authorName);
        bookCategory1.setText(bookCategory);

        try {
            downloads.setText(downloadsstring);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        final Query query = reference.orderByChild("id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Books books = snapshot1.getValue(Books.class);
                        likesText.setText(books.getLiked());
                        if (snapshot1.child("readers").exists()) {
                            readersText.setText(books.getReaders());
                        } else {
                            readersText.setText("0");
                        }

                        if (snapshot1.child("shared").exists()) {
                            shareText.setText(books.getShared());
                        } else {
                            shareText.setText("0");
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("favourites", Context.MODE_PRIVATE);
                boolean check = sharedPreferences.getBoolean("liked_" + id, false);

                if (check) {

                    sharedPreferences = getSharedPreferences("favourites", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean("liked_" + id, false);
                    editor.commit();

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                    final Query query = reference.orderByChild("id").equalTo(id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot != null) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Books books = snapshot1.getValue(Books.class);
                                    String liked = books.getLiked();
                                    if (liked.equals("0")) {
                                    } else {
                                        int integerLiked = (Integer.parseInt(liked)) - 1;
                                        books.setLiked(String.valueOf(integerLiked));
                                        reference.child(id).setValue(books);
                                        likesText.setText(books.getLiked());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    favouriteBtn.setImageResource(R.drawable.ic_thumb_up);
                    Toast.makeText(PdfDetail.this, "Dislike", Toast.LENGTH_SHORT).show();

                } else {
                    sharedPreferences = getSharedPreferences("favourites", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean("liked_" + id, true);
                    editor.commit();

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                    final Query query = reference.orderByChild("id").equalTo(id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot != null) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Books books = snapshot1.getValue(Books.class);
                                    String liked = books.getLiked();
                                    int integerLiked = (Integer.parseInt(liked)) + 1;
                                    books.setLiked(String.valueOf(integerLiked));
                                    reference.child(id).setValue(books);
                                    likesText.setText(books.getLiked());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    favouriteBtn.setImageResource(R.drawable.ic_thumb_up_filled);
                    Toast.makeText(com.example.ebookapp.PdfDetail.this, "Liked", Toast.LENGTH_SHORT).show();

                }
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        String[] permission = {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        };
                        requestPermissions(permission, WRITE_EXT_STORAGECODE);
                        return;
                    }
                }


                bitmap = ((BitmapDrawable) bookImageBack.getDrawable()).getBitmap();
                progressDialog.show();

                savepdf();

                Log.d("bookDataf:", "bookName " + bookName);
                Log.d("bookDataf:", "authorName " + authorName);

                Log.d("bookDataf:", "pdfUrl " + pdfUrl);
                Log.d("bookDataf:", "description " + description);
                Log.d("bookDataf:", "id " + id);

            }

            private void savepdf() {
                FileLoader.with(getApplicationContext())
                        .load(pdfUrl, false)
                        .fromDirectory("test4", FileLoader.DIR_INTERNAL)
                        .asFile(new FileRequestListener<File>() {
                            @Override
                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                saveimagetoGallary();
                            }

                            @Override
                            public void onError(FileLoadRequest request, Throwable t) {
                                progressDialog.dismiss();
                            }
                        });
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    loadOnlinePdf();
                } else {
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                    final Query query = reference.orderByChild("id").equalTo(id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot != null) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Books books = snapshot1.getValue(Books.class);
                                    if (snapshot1.child("readers").exists()) {
                                        String readers = books.getReaders();
                                        int integerReader = (Integer.parseInt(readers)) + 1;
                                        books.setReaders(String.valueOf(integerReader));
                                        reference.child(id).setValue(books);
                                        readersText.setText(books.getReaders());
                                    } else {
                                        books.setReaders("1");
                                        reference.child(id).setValue(books);
                                        readersText.setText(books.getReaders());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Intent i = new Intent(PdfDetail.this, BookView.class);
                    i.putExtra("pdfUrl", pdfUrl);
                    i.putExtra("bookId", id);
                    i.putExtra("bookName", bookName);
                    startActivity(i);

                }
            }


        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveimagetoGallary() {

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/DCIM/.eBookImages");
        dir.mkdir();
        String imagename = time + ".JPEG";
        file = new File(dir, imagename);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Booksdb bk = new Booksdb();
            bk.setBookName(bookName);
            bk.setAuthorName(authorName);
            bk.setBookImage(String.valueOf(file));
            bk.setPdfUrl(pdfUrl);
            bk.setDescription(description);
            bk.setId(id);

            MainActivity.myappdatabas.myDao().addBook(bk);
            progressDialog.dismiss();

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books").child(id);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String dwnlds1 = snapshot.child("downloads").getValue(String.class);

                    Log.e("TAGFIRE", "onDataChange: " + snapshot + ":" + dwnlds1 + "id" + id);
                    int val = Integer.parseInt(dwnlds1);
                    val = val + 1;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("downloads", String.valueOf(val));
                    final int finalVal = val;
                    reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            downloads.setText("" + finalVal);
                            Toast.makeText(PdfDetail.this, "Download complete ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    String[] permission = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                    };
                    requestPermissions(permission, WRITE_EXT_STORAGECODE);
                }
            }
        }
    }



    public void loadOnlinePdf() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        final Query query = reference.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Books books = snapshot1.getValue(Books.class);
                        if (snapshot1.child("readers").exists()) {
                            String readers = books.getReaders();
                            int integerReader = (Integer.parseInt(readers)) + 1;
                            books.setReaders(String.valueOf(integerReader));
                            reference.child(id).setValue(books);
                            readersText.setText(books.getReaders());
                        } else {
                            books.setReaders("1");
                            reference.child(id).setValue(books);
                            readersText.setText(books.getReaders());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent i = new Intent(PdfDetail.this, BookView.class);
        i.putExtra("pdfUrl", pdfUrl);
        i.putExtra("bookId", id);
        i.putExtra("bookName", bookName);
        startActivity(i);


    }
}