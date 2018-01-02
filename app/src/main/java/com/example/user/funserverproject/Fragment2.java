package com.example.user.funserverproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 2017-12-30.
 */

public class Fragment2 extends Fragment {

    public static final int IMAGE_GALLERY_REQUEST = 122;
    GridView gridView;
    ArrayList<String> imageList;
    public static List<Uri> uriList = new ArrayList<Uri>();
    public static List<Bitmap> bitmapList = new ArrayList<>();

    public Fragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        Activity thisActivity = getActivity();
        int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
        Log.d("Fragment2", "Starting log");

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



        //perform this activity only once
        if(MainActivity.get_frag2First()) {
            int a = 0;
            /*
            imageList = getAllShownImagesPath(getActivity());

            //Convery uri to bitmap images
            for (int i = 0; i < imageList.size(); i++) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), Uri.fromFile(new File(imageList.get(i))));
                    Log.d("Fragment2", "Bitmap " + i + " added");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Fragment2", "Bitmap " + i + " EXCEPTION");
                }
                bitmapList.add(bitmap);
            }
            MainActivity.false_frag2First();*/
        } else {
            Collections.shuffle(bitmapList);
        }



        gridView = view.findViewById(R.id.gridview);
        gridView.setAdapter(new GridViewAdapter(this.getActivity(), bitmapList));
        Log.d("Fragment2","Adapter set");



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id)
            {
                /*AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                alertDialog.setTitle("hi");
                alertDialog.setMessage("this is my app");

                alertDialog.setButton("Continue..", new DialogInterface.OnClickListener() {
                    //functions
                });*/
                final int mPosition = position;
                ShortClickAlert(mPosition);

                Toast.makeText(getActivity().getBaseContext(),
                        "pic" + (position + 1) + " selected",
                        Toast.LENGTH_SHORT).show();
                //alertDialog.show();

            }

        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                Toast.makeText(getActivity().getBaseContext(), "LONG PRESS", Toast.LENGTH_SHORT).show();
                final int mPosition = position;
                LongClickDialog(arg1, mPosition);
                return true;
            }
            //alertDialog.show();
        });

        Button btnAddPhoto = (Button) view.findViewById(R.id.btnAddPhoto);

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                onImageGalleryClicked(v);
            }
        });

        Log.d("Fragment2","Finished conclicklistener");



        /*bitmapList = GetBitmapImages (imageList);
        bitmapList.Adapter = new GridViewAdapter(this, bitmapList);
        for (int i = 0; i<imageList.size(); i++) {
            Log.d("Fragment2", i+"'rd imageList: "+imageList.get(i));
        }
        Log.d("Fragment2", "There were a total of "+imageList.size()+"images on this phone");
        ImageView imageView = (image)
        Uri imgUri = Uri.parse(imageList.get(0));
        imageView.setImageURI(null);
        imageView.setImageURI(imgUri);*/

        return view;
    }

    public ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while(cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;

    }
    public void LongClickDialog(View view, int position){
        final int finalPosition = position;

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        //adb.setView(Main.this);
        adb.setTitle("Do you wish to delete this image?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                bitmapList.remove(finalPosition);
                gridView.setAdapter(new GridViewAdapter(getActivity(), bitmapList));
                Toast.makeText(getActivity().getApplicationContext(), "OK and removed", Toast.LENGTH_LONG).show();
            } });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getApplicationContext(), "cancel", Toast.LENGTH_LONG).show();
                //finish();
            } });

        AlertDialog alertDialog = adb.create();
        alertDialog.show();

    }

    public void ShortClickAlert(int position) {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
        alertadd.setTitle("Android");

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.oneimage, null);

        ImageView image = view.findViewById(R.id.imageView);
        Uri imgUri = uriList.get(position);
        //image.setImageResource(R.drawable.ic_launcher_background);
        image.setImageURI(null);
        image.setImageURI(imgUri);

        alertadd.setNeutralButton("return", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int ts) {

                    }
                });
        alertadd.setView(view);
        alertadd.show();


    }

    public void onImageGalleryClicked(View view) {
        // invoke image gallery using implicit intent

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        //get uri representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type

        //set the data and type. get all image types
        photoPickerIntent.setDataAndType(data, "image/*");

        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //if we are here, everything processed successfully
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                //if we are here, we are hearing back from image gallery

                // the address of the image on the SD Card
                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    uriList.add(imageUri);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                    if(!containBitMap(bitmap, bitmapList)) {
                        Log.d("Fragment2", "Bitmap added");
                        bitmapList.add(bitmap);
                        gridView.setAdapter(new GridViewAdapter(getActivity(), bitmapList));
                    } else {
                        Log.d("Fragment2", "Found duplicate");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Fragment2", "Bitmap EXCEPTION");
                }

            }
        }
    }

    public boolean containURI(Uri testUri, List<Uri> uriList) {
        String stringUri = testUri.toString();
        for (int i = 0; i < uriList.size(); i++) {
            Log.d("Fragment2", "Compared" + stringUri + " with " + uriList.get(i));
            if(stringUri.compareTo(uriList.get(i).toString()) == 0){
                return true;
            }
        }
        return false;
    }

    public boolean containBitMap(Bitmap bitmap, List<Bitmap> bitmapList) {;
        for (int i = 0; i < bitmapList.size(); i++) {
            if(bitmap.sameAs(bitmapList.get(i))){
                return true;
            }
        }
        return false;
    }

}
