package com.wings.okpermission;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


/**
 * @author AI
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
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
//            }
//        });
//
//

    }

//    @SuppressLint("MissingPermission")
//    private void call(){
//        try {
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:10086"));
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
