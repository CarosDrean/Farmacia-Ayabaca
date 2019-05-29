package xyz.drean.ayabacafarm.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.drean.ayabacafarm.R;
import xyz.drean.ayabacafarm.abstraction.General;
import xyz.drean.ayabacafarm.pojo.Order;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.OrderViewHolder> {

    private ArrayList<Order> orders;
    private Activity activity;

    public AdapterOrder(ArrayList<Order> products, Activity activity) {
        this.orders = products;
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

        General general = new General();
        general.loadImage(item.getUrlImg(), holder.img, activity);

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    static class OrderViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView img;
        private TextView name;
        private TextView product;
        private TextView adders;
        private ImageView call;
        RelativeLayout content;

        OrderViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_order);
            name = itemView.findViewById(R.id.name_order);
            product = itemView.findViewById(R.id.product_order);
            adders = itemView.findViewById(R.id.address_order);
            call = itemView.findViewById(R.id.call);
            content = itemView.findViewById(R.id.content_order);
        }
    }

    // error al eliminar
    public void removeItem(int position){
        String uid = orders.get(position).getUid();
        orders.remove(position);
        notifyItemRemoved(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(uid)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(activity, activity.getResources().getText(R.string.order_delete), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
