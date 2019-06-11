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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.drean.ayabacafarm.R;
import xyz.drean.ayabacafarm.abstraction.General;
import xyz.drean.ayabacafarm.pojo.Order;

public class AdapterOrder extends FirestoreRecyclerAdapter<Order, AdapterOrder.OrderHolder> {

    private Activity activity;

    public AdapterOrder(@NonNull FirestoreRecyclerOptions<Order> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    private void call(String number){
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        activity.startActivity(i);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderHolder holder, int position, @NonNull final Order model) {
        holder.name.setText(model.getNameClient());
        holder.adders.setText(model.getAddress());
        holder.product.setText(model.getNameProduct());
        General.loadImage(model.getUrlImg(), holder.img, activity);

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(model.getCel());
            }
        });
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order, viewGroup, false);
        return new AdapterOrder.OrderHolder(v);
    }

    class OrderHolder extends RecyclerView.ViewHolder{

        private CircleImageView img;
        private TextView name;
        private TextView product;
        private TextView adders;
        private ImageView call;
        RelativeLayout content;

        OrderHolder(View itemView) {
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
        getSnapshots().getSnapshot(position).getReference().delete();
        Toast.makeText(activity, activity.getResources().getString(R.string.order_delete), Toast.LENGTH_SHORT).show();
    }
}
