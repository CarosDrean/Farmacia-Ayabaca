package xyz.drean.ayabacafarm;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.drean.ayabacafarm.pojo.Product;

public class AddProduct extends AppCompatActivity {

    int SELECT_PICTURE = 1;
    private CircleImageView img;
    private Uri imgUri;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        final Drawable menuIcon = getResources().getDrawable(R.drawable.ic_baseline_close_24px);
        menuIcon.setColorFilter(getResources().getColor(R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(menuIcon);

        img = findViewById(R.id.img_add);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalery();
            }
        });
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
            Uri uri = data.getData();
            imgUri = uri;
            Glide.with(this).load(uri).into(img);
        }
    }

    private void uploadImg(Uri uri) {
        StorageReference str = FirebaseStorage.getInstance().getReference()
                .child("img")
                .child(uri.getLastPathSegment());
        str.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    private void saveData() {
        EditText name = findViewById(R.id.name_add);
        EditText description = findViewById(R.id.description_add);
        EditText price = findViewById(R.id.price_add);
        Spinner category = findViewById(R.id.category_add);

        if(!name.getText().toString().equals("") || !String.valueOf(price.getText()).equals("") || !description.getText().toString().equals("")){
            Product p = new Product(
                    String.valueOf(System.currentTimeMillis()),
                    name.getText().toString(),
                    imgUri.getLastPathSegment(),
                    Double.parseDouble(String.valueOf(price.getText())),
                    description.getText().toString(),
                    category.getSelectedItem().toString()
            );

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products").add(p);
            uploadImg(imgUri);
            onBackPressed();
        } else {
            Toast.makeText(this, "¡Llene todos los campos!", Toast.LENGTH_SHORT).show();
        }

    }

    private void openGalery(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Seleccionar Imagen"), SELECT_PICTURE);
    }
}
