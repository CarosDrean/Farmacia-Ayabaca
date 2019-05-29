package xyz.drean.ayabacafarm;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.drean.ayabacafarm.abstraction.General;
import xyz.drean.ayabacafarm.pojo.Product;

public class AddProduct extends AppCompatActivity {

    int SELECT_PICTURE = 1;
    private CircleImageView img;
    private Uri imgUri;

    private EditText name;
    private EditText description;
    private EditText price;
    private Spinner category;

    private String uid;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        iconActionBar(actionBar);
        init();

        img = findViewById(R.id.img_add);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        if(getIntent().getStringExtra("name") != null) {
            initEdit(actionBar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SELECT_PICTURE){
            assert data != null;
            Uri uri = data.getData();
            imgUri = uri;
            Glide.with(this).load(uri).into(img);
        }
    }

    private void iconActionBar(ActionBar actionBar) {
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Drawable menuIcon = getResources().getDrawable(R.drawable.ic_baseline_close_24px);
        menuIcon.setColorFilter(getResources().getColor(R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(menuIcon);
    }

    private void init() {
        name = findViewById(R.id.name_add);
        description = findViewById(R.id.description_add);
        price = findViewById(R.id.price_add);
        category = findViewById(R.id.category_add);

        db = FirebaseFirestore.getInstance();
    }

    private void initEdit(ActionBar actionBar){
        actionBar.setTitle(getResources().getString(R.string.edit_product));
        name.setText(getIntent().getStringExtra("name"));
        description.setText(getIntent().getStringExtra("description"));
        price.setText(String.valueOf(getIntent().getDoubleExtra("price", 0.0)));
        category.setSelection(getIndexSpinner(category, getIntent().getStringExtra("category")));
        String urlImg = getIntent().getStringExtra("urlImg");
        imgUri = Uri.parse(urlImg);

        General general = new General();
        general.loadImage(urlImg, img, this);

        uid = getIntent().getStringExtra("uid");
    }

    private void uploadImg(Uri uri) {
        String nameImg = uri.getLastPathSegment();
        Toast.makeText(this, getResources().getString(R.string.save_product), Toast.LENGTH_SHORT).show();
        assert nameImg != null;
        StorageReference str = FirebaseStorage.getInstance().getReference()
                .child("img")
                .child(nameImg);
        str.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    private static int getIndexSpinner(Spinner spinner, String label) {
        int position = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(label)) {
                position = i;
            }
        }
        return position;
    }

    private void saveData() {
        if(!name.getText().toString().equals("") || !String.valueOf(price.getText()).equals("") || !description.getText().toString().equals("")){
            Product p = new Product(
                    String.valueOf(System.currentTimeMillis()),
                    name.getText().toString(),
                    imgUri.getLastPathSegment(),
                    Double.parseDouble(String.valueOf(price.getText())),
                    description.getText().toString(),
                    category.getSelectedItem().toString()
            );


            if(getIntent().getStringExtra("name") != null) {
                db.collection("products").document(uid).set(p);
                Toast.makeText(this, getResources().getString(R.string.updated_product), Toast.LENGTH_SHORT).show();
            } else {
                db.collection("products").add(p);
                uploadImg(imgUri);
            }
            onBackPressed();
        } else {
            Toast.makeText(this, getResources().getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery(){
        try {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, getResources().getString(R.string.select_img)), SELECT_PICTURE);
        } catch (Error e) {
            Toast.makeText(this, getResources().getString(R.string.error_gallery), Toast.LENGTH_SHORT).show();
        }
    }
}
