package com.example.apn.setimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button choose,upload,viewImage;
    private ImageView selected,download;
    private final int image_request = 1;
    private Bitmap bitmap;
    private EditText name;
    RequestQueue requestQueue;
    String url = " http://192.168.43.88:3000/upload/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        choose = (Button) findViewById(R.id.button1);
        upload = (Button) findViewById(R.id.button2);
        viewImage = (Button) findViewById(R.id.button3);
        selected = (ImageView) findViewById(R.id.imageView1);
        download = (ImageView) findViewById(R.id.imageView2);
        name = (EditText) findViewById(R.id.editText);

        choose.setOnClickListener(this);
        upload.setOnClickListener(this);
        viewImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:selectimage();
                break;
            case R.id.button2:upload();
                break;
        }
    }

    private void selectimage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,image_request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==image_request && resultCode==RESULT_OK && data!=null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                selected.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void upload(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s = response.trim();

                if(s.equalsIgnoreCase("Loi")){
                    Toast.makeText(MainActivity.this, "Loi", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Thanh COng", Toast.LENGTH_SHORT).show();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error+"", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String ten = name.getText().toString();
                String image = getStringImage(bitmap);
                Map<String ,String> params = new HashMap<String,String>();

                params.put("TEN",ten);
                params.put("HINH",image);

                return params;
            }
        };
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private String imageToString (Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
}
