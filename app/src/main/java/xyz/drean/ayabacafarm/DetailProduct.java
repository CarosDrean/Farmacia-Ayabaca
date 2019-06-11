package xyz.drean.ayabacafarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.chip.Chip;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.drean.ayabacafarm.abstraction.DataBase;
import xyz.drean.ayabacafarm.abstraction.General;

public class DetailProduct extends AppCompatActivity {

    private String name;
    private String description;
    private String category;
    private double price;
    private String urlImg;
    private String uid;
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        init(); // inicializa la variable name

        getSupportActionBar().setTitle(name);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }else if (id == R.id.action_delete) {
            dataBase.deleteItem("products", uid, "¡Producto eliminado!");
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void edit(){
        Intent i = new Intent(DetailProduct.this, AddProduct.class);
        i.putExtra("uid", uid);
        i.putExtra("name", name);
        i.putExtra("description", description);
        i.putExtra("category", category);
        i.putExtra("price", price);
        i.putExtra("urlImg", urlImg);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_product, menu);
        return true;
    }

    private void init() {
        TextView name_u = findViewById(R.id.name_detail);
        TextView description_u = findViewById(R.id.descrption_detail);
        Chip category_u = findViewById(R.id.category_detail);
        Chip price_u = findViewById(R.id.price_detail);
        ImageView img = findViewById(R.id.img_detail);

        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");
        category = getIntent().getStringExtra("category");
        price = getIntent().getDoubleExtra("price", 0.0);
        urlImg = getIntent().getStringExtra("urlImg");
        uid = getIntent().getStringExtra("uid");

        name_u.setText(name);
        description_u.setText(description);
        category_u.setText(category);
        price_u.setText(String.format("S/. %s", price));

        General.loadImage(urlImg, img, DetailProduct.this);
        dataBase = new DataBase(this);
    }
}
