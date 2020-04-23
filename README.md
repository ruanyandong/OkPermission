# OkPermission
## 简介
  **OkPermission**是一个用于简化Android运行时权限请求的开源库。
## 原理
  对Android运行时权限API的封装一般需要在Activity或者Fragment中接收onRequestPermissionResult()方法的回调才行，不能简单的将整个操作封装到一个独立的类中，
目前的一些方案，比如将运行时权限的操作封装到BaseActivity中，或者提供一个透明的Activity来处理运行时权限。

  OkPermission则是用了不同于以上两种的方式，Google在Fragment中也提供了一份相同的请求权限的API，使得我们在Fragment中也能申请运行时权限。
  Fragment不像Activity那样必须需要界面，我们可以向Activity中添加一个隐藏的Fragment，然后在这个Fragment中对运行时权限的API进行封装，
这是一种轻量级的做法，不需要担心隐藏的Fragment会对Activity的性能造成什么影响。

# 使用
  首先添加如下配置将OKPermission引入到你的项目当中：
  
```groovy
dependencies {
     implementation 'com.ruanyandong:OkPermission:1.0.0'
}
```
  然后使用如下方式就可以在项目中使用了

  java方式使用：
```java
OkPermission.request(MainActivity.this, new PermissionCallBack() {
                    @Override
                    public void onResult(boolean allGranted, List<String> deniedList) {
                        if (allGranted){
                            //授权执行逻辑
                        }else {
                        //没有授权执行逻辑
                            Toast.makeText(MainActivity.this,"Have no permissions!",Toast.LENGTH_LONG).show();
                        }
                    }
                }, Manifest.permission.CALL_PHONE);
```
  Kotlin使用方式
```kotlin
OkPermission.requestPermission(MainActivity.this,Manifest.permission.CALL_PHONE){

            allGranted,deniedList ->

            if (allGranted){
                //授权执行逻辑
            } else{
             //没有授权执行逻辑
            }
        }
```
申请的权限可以是一个或者多个，根据实际情况而定。
