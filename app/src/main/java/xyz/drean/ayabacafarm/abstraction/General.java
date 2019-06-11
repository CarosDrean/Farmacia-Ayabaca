package xyz.drean.ayabacafarm.abstraction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import xyz.drean.ayabacafarm.R;

public class General {

    public General() {
    }

    public static void loadImage(String urlImg, final ImageView imageView, final Activity activity) {
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

    public static int getIndexSpinner(Spinner spinner, String label) {
        int position = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(label)) {
                position = i;
            }
        }
        return position;
    }

    public static void iconCloseActionBar(ActionBar actionBar, Activity activity) {
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Drawable menuIcon = activity.getResources().getDrawable(R.drawable.ic_baseline_close_24px);
        menuIcon.setColorFilter(activity.getResources().getColor(R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(menuIcon);
    }

    public static void simpleAlert(String title, String message, String positiveButton, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message);
        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }
}
