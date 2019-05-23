package xyz.drean.ayabacafarm.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.drean.ayabacafarm.R;
import xyz.drean.ayabacafarm.pojo.Order;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.OrderViewHolder> {

    private ArrayList<Order> orders;
    private Activity activity;

    private int CODE_PERMISSION_CALL = 0;

    public AdapterOrder(ArrayList<Order> productos, Activity activity) {
        this.orders = productos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new AdapterOrder.OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, int position) {
        final Order item = orders.get(position);

        holder.name.setText(item.getNameClient());
        holder.adders.setText(item.getAddress());
        holder.product.setText(item.getNameProduct());

        StorageReference str = FirebaseStorage.getInstance().getReference()
                .child("img")
                .child(item.getUrlImg());

        try {
            final File localFile = File.createTempFile("images", "jpg");
            str.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(activity).load(localFile).into(holder.img);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.CALL_PHONE)) {
                        //call(item.getCel());
                    } else {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.CALL_PHONE},
                                CODE_PERMISSION_CALL);
                        //call(item.getCel());
                    }
                }
                call(item.getCel());
            }
        });

    }

    private void call(String number){
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        activity.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        TextView name;
        TextView product;
        TextView adders;
        ImageView call;
        public RelativeLayout content;

        public OrderViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_order);
            name = itemView.findViewById(R.id.name_order);
            product = itemView.findViewById(R.id.product_order);
            adders = itemView.findViewById(R.id.address_order);
            call = itemView.findViewById(R.id.call);
            content = itemView.findViewById(R.id.content_order);
        }
    }

    public void removeItem(int position){
        String uid = orders.get(position).getUid();
        orders.remove(position);
        notifyItemRemoved(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(uid)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(activity, "¡Pedido eliminado!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
