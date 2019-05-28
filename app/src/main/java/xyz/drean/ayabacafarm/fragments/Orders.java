package xyz.drean.ayabacafarm.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import xyz.drean.ayabacafarm.R;
import xyz.drean.ayabacafarm.adapters.AdapterOrder;
import xyz.drean.ayabacafarm.adapters.RecyclerItemTouchHelper;
import xyz.drean.ayabacafarm.pojo.Order;
import xyz.drean.ayabacafarm.pojo.Product;

/**
 * A simple {@link Fragment} subclass.
 */
public class Orders extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private ArrayList<Order> orders;
    private FirebaseFirestore db;
    private LinearLayoutManager llm;
    private RecyclerView orderList;
    private AdapterOrder adapter;

    public Orders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orders, container, false);

        init(v);

        RecyclerItemTouchHelper itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(orderList);

        getData();
        return v;
    }

    private void init(View v){
        orderList = v.findViewById(R.id.recycler_order);
        orders = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        orderList.setLayoutManager(llm);
    }

    private void initAdapter() {
        adapter = new AdapterOrder(orders, getActivity());
        orderList.setAdapter(adapter);
    }

    private void getData() {
        orders.clear();
        db.collection("orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        assert value != null;
                        for(QueryDocumentSnapshot doc: value) {
                            Order o = doc.toObject(Order.class);
                            o.setUid(doc.getId());
                            orders.add(o);
                            initAdapter();
                        }
                    }
                });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        adapter.removeItem(viewHolder.getAdapterPosition());
    }

}
