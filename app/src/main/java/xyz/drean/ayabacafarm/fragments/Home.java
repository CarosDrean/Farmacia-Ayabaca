package xyz.drean.ayabacafarm.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import xyz.drean.ayabacafarm.DetailProduct;
import xyz.drean.ayabacafarm.R;
import xyz.drean.ayabacafarm.pojo.Product;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager llm;
    RecyclerView categoryList;
    ArrayList<Product> products;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        categoryList = v.findViewById(R.id.recycler_home);
        products = new ArrayList<>();
        init();
        getData();
        return v;
    }

    private void init(){
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        categoryList.setLayoutManager(llm);
        db = FirebaseFirestore.getInstance();
    }

    private void getData() {
        Query query = db
                .collection("products")
                .orderBy("uid")
                .limit(50);

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, new SnapshotParser<Product>() {
                    @NonNull
                    @Override
                    public Product parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Product p = snapshot.toObject(Product.class);
                        assert p != null;
                        p.setUid(snapshot.getId());
                        return p;
                    }
                })
                .build();



        adapter = new FirestoreRecyclerAdapter<Product, ProductHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductHolder holder, int position, @NonNull final Product model) {
                holder.name.setText(model.getName());
                holder.price.setText(String.valueOf(model.getPrice()));

                final Activity activity = getActivity();
                assert activity != null;

                loadImage(model.getUrlImg(), holder.background, activity);

                holder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goDetail(model, activity, v);
                    }
                });
            }

            @NonNull
            @Override
            public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_home, viewGroup, false);
                return new ProductHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        categoryList.setAdapter(adapter);
    }

    private void goDetail(Product model, Activity activity, View v) {
        Intent i = new Intent(activity, DetailProduct.class);
        i.putExtra("uid", model.getUid());
        i.putExtra("name", model.getName());
        i.putExtra("description", model.getDescription());
        i.putExtra("category", model.getCategory());
        i.putExtra("price", model.getPrice());
        i.putExtra("urlImg", model.getUrlImg());
        startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, v, activity.getString(R.string.trancicionFoto)).toBundle());
    }

    private void loadImage(String urlImg, final ImageView imageView, final Activity activity) {
        StorageReference str = FirebaseStorage.getInstance().getReference()
                .child("img")
                .child(urlImg);

        try {
            final File localFile = File.createTempFile("images", "jpg");
            str.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(activity)
                            .load(localFile)
                            .centerCrop()
                            .placeholder(R.drawable.holder)
                            .into(imageView);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView price;
        ImageView background;

        ProductHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_home);
            price = itemView.findViewById(R.id.price_home);
            background = itemView.findViewById(R.id.background_home);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
