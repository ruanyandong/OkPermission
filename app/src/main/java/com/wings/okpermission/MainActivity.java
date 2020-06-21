package com.wings.okpermission;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;



/**
 * @author AI
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1.0
//                OkPermission.request(MainActivity.this, new PermissionCallBack() {
//                    @Override
//                    public void onResult(boolean allGranted, List<String> deniedList) {
//                        if (allGranted){
//                            call();
//                        }else {
//                            Toast.makeText(MainActivity.this,"Have no permissions!",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }, Manifest.permission.CALL_PHONE);

                // 2.0
//                PermissionX.init(MainActivity.this).permissions(Manifest.permission.CALL_PHONE).request(new RequestCallback() {
//                    @Override
//                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
//                        if (allGranted){
//                            call();
//                        }else {
//                            Toast.makeText(MainActivity.this,"Have no permissions!",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });


            }
        });



    }

    @SuppressLint("MissingPermission")
    private void call(){
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:10086"));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
