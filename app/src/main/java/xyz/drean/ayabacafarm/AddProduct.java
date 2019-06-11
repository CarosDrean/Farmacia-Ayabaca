package xyz.drean.ayabacafarm;

import android.content.Intent;
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
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.drean.ayabacafarm.abstraction.DataBase;
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
    private DataBase dataBase;

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
        General.iconCloseActionBar(actionBar, this);
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

    private void init() {
        name = findViewById(R.id.name_add);
        description = findViewById(R.id.description_add);
        price = findViewById(R.id.price_add);
        category = findViewById(R.id.category_add);

        dataBase = new DataBase(this);
    }

    private void initEdit(ActionBar actionBar){
        actionBar.setTitle(getResources().getString(R.string.edit_product));
        name.setText(getIntent().getStringExtra("name"));
        description.setText(getIntent().getStringExtra("description"));
        price.setText(String.valueOf(getIntent().getDoubleExtra("price", 0.0)));
        category.setSelection(General.getIndexSpinner(category, getIntent().getStringExtra("category")));
        String urlImg = getIntent().getStringExtra("urlImg");
        imgUri = Uri.parse(urlImg);

        General.loadImage(urlImg, img, this);

        uid = getIntent().getStringExtra("uid");
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

            final String collection = "products";

            if(getIntent().getStringExtra("name") != null) {
                dataBase.addItem(p, uid, collection, getResources().getString(R.string.updated_product));
            } else {
                dataBase.uploadImg(imgUri);
                dataBase.addItem(p, p.getUid(), collection, getResources().getString(R.string.save_product));
            }
            finish();
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
