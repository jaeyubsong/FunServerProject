package com.example.user.funserverproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    ProgressDialog asyncDialog;
    public static List<Uri> uriList = new ArrayList<Uri>();
    public static List<Bitmap> bitmapList = new ArrayList<>();
    public Bitmap bitmap = null;
    public int delete_idx = 0;

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



        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncDialog = new ProgressDialog(getActivity());
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("로딩중입니다..");

                asyncDialog.show();

                new JSONImgDown().execute("http://13.125.110.222:3000/imges");
            }
        };

        Button btndownload = (Button) view.findViewById(R.id.downbtn);
        btndownload.setOnClickListener(listener);


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
                asyncDialog = new ProgressDialog(getActivity());
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("로딩중입니다..");

                asyncDialog.show();

                bitmapList.remove(finalPosition);
                delete_idx = finalPosition;
                new JSONImgDel().execute("http://13.125.110.222:3000/del");
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
                try {

                    uriList.add(imageUri);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                    if(!containBitMap(bitmap, bitmapList)) {


                        asyncDialog = new ProgressDialog(getActivity());
                        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        asyncDialog.setMessage("로딩중입니다..");

                        asyncDialog.show();

                        Log.d("Fragment2", "Bitmap added");
                        bitmapList.add(bitmap);
                        new JSONImgPost().execute("http://13.125.110.222:3000/upload");
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

    public class JSONImgPost extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                ByteArrayOutputStream imgstream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, imgstream);
                byte[] byteArray = imgstream.toByteArray();
                String encodeByte = Base64.encodeToString(byteArray, Base64.DEFAULT);
                int i = uriList.size();
                jsonObject.put("name" , uriList.get(i-1).toString());
                jsonObject.put( "imgfile" , encodeByte);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    //con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
//                    writer.write(jsonObject.toString());
//                    writer.flush();
//                    writer.close();//버퍼를 받아줌
                    outStream.write(jsonObject.toString().getBytes());
                    outStream.close();
                    con.getResponseCode();

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    asyncDialog.dismiss();

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            asyncDialog.dismiss();

        }
    }

    public class JSONImgDel extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("delname" ,uriList.get(delete_idx));


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    //con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
//                    writer.write(jsonObject.toString());
//                    writer.flush();
//                    writer.close();//버퍼를 받아줌
                    outStream.write(jsonObject.toString().getBytes());
                    outStream.close();
                    con.getResponseCode();

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    asyncDialog.dismiss();

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            asyncDialog.dismiss();

        }
    }

    public class JSONImgDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();//연결 수행

                    //입력 스트림 생성
                    InputStream stream = con.getInputStream();

                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuffer buffer = new StringBuffer();

                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";

                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                    return buffer.toString();

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONArray ja = (JSONArray) new JSONArray(result);

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);
                    uriList.add(Uri.parse(order.getString("file_id")));
                    String img_encoded = order.getString("file_n");
                    byte[] byteArray = Base64.decode(img_encoded, Base64.DEFAULT);
                    Bitmap newbitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    bitmapList.add(newbitmap);
                }
                gridView.setAdapter(new GridViewAdapter(getActivity(), bitmapList));

                asyncDialog.dismiss();
            }
            catch (JSONException e){
                return ;
            }
        }

    }

}