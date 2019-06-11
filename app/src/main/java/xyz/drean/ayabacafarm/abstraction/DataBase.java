package xyz.drean.ayabacafarm.abstraction;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DataBase {

    private FirebaseFirestore db;
    private Activity activity;
    private StorageReference str;

    public DataBase(Activity activity) {
        this.activity = activity;
        getInstance();
    }

    private void getInstance() {
        db = FirebaseFirestore.getInstance();
        str = FirebaseStorage.getInstance().getReference();
    }

    public void addItem(Object item, String uid, String collection, String message) {
        db.collection(collection).document(uid).set(item);
        Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show();
    }

    public void deleteItem(String collection, String uid, final String message) {
        db.collection(collection).document(uid)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadImg(Uri uri) {
        String nameImg = uri.getLastPathSegment();
        assert nameImg != null;
        this.str.child("img")
                .child(nameImg);
        this.str.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }
}
