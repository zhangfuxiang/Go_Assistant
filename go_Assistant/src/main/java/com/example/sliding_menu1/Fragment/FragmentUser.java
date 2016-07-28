package com.example.sliding_menu1.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sliding_menu1.Activity.LoginActivity;
import com.example.sliding_menu1.Activity.MainActivity;
import com.example.sliding_menu1.Activity.ModifyNameActivity;
import com.example.sliding_menu1.Activity.ModifyPwdActivity;
import com.example.sliding_menu1.R;
import com.example.sliding_menu1.entity.BaseApplication;
import com.example.sliding_menu1.entity.CacheUtils;
import com.example.sliding_menu1.entity.CommonConstant;
import com.example.sliding_menu1.entity.GlobalParams;
import com.example.sliding_menu1.entity.SpUtils;
import com.example.sliding_menu1.entity.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Fragment
 * 作者： win
 * 创建日期：2016/3/26 16:44
 * 实现的主要功能：
 */
public class FragmentUser extends Fragment implements View.OnClickListener {

    protected SpUtils sputil;// 保存
    private Context context;
    private RelativeLayout user_header;// 用户头像
    private ImageView iv_user_header;// 头像
    private RelativeLayout user_name;// 用户昵称
    private TextView tv_user_name;// 昵称
    private RelativeLayout user_sex;// 用户性别
    private CheckBox cb_user_sex;// 性别
    private RelativeLayout user_remember;// 记住密码
    private CheckBox cb_user_remember;// 记住密码
    private RelativeLayout modify_pw;// 修改密码
    private Button btn_quit;;// 退出用户

    private View albumView;// 选择图片来源
    private AlertDialog albumDialog;// 选择图片来源
    private File tempFile;// 头像缓存
    private Bitmap bitmap;// 头像
    private String dateTime;// 拍照时间
    private String iconUrl;// 头像Url

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_user,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context=getView().getContext();
        init();
        setListener();
        initAlbumDialog();
        initdata();
    }



    /**
     *
     * @Description: 初始化选择图片来源弹框
     */
    private void initAlbumDialog() {
        albumDialog = new AlertDialog.Builder(getActivity()).create();
        //点击域外取消
        albumDialog.setCanceledOnTouchOutside(true);
        albumView = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_select_picture, null);
        TextView albumPic = (TextView) albumView.findViewById(R.id.album_pic);
        TextView cameraPic = (TextView) albumView.findViewById(R.id.camera_pic);
        albumPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                albumDialog.dismiss();
                Date date1 = new Date(System.currentTimeMillis());
                dateTime = date1.getTime() + "";
                getAvataFromAlbum();
            }
        });
        cameraPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                albumDialog.dismiss();
                Date date = new Date(System.currentTimeMillis());
                dateTime = date.getTime() + "";
                getAvataFromCamera();
            }
        });
    }
    /**
     * 初始化数据
     * */
    private void initdata() {
        //昵称
        tv_user_name.setText(GlobalParams.userInfo.getName());
        // 性别
        if (GlobalParams.userInfo.getSex() != null) {
            cb_user_sex.setChecked(GlobalParams.userInfo.getSex());
        } else {
            cb_user_sex.setChecked(false);
        }
        //头像
        BmobFile avatarFile = GlobalParams.userInfo.getAvatar();
        if (null != avatarFile) {
            ImageLoader.getInstance().displayImage(
                    avatarFile.getFileUrl(getActivity()),
                    iv_user_header,
                    BaseApplication.getInstance().getOptions(
                            R.drawable.user_icon_default_main),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                        }
                    });
        }
        //是否记住密码
        if(LoginActivity.preferences.getBoolean(LoginActivity.REMEMBER_USER,false)){
            cb_user_remember.setChecked(true);
        }else
        {
            cb_user_remember.setChecked(false);
        }
    }
    /**
     * 初始化监听
     * */
    private void setListener() {
        user_header.setOnClickListener(this);
        user_name.setOnClickListener(this);
        cb_user_sex.setOnClickListener(this);
        cb_user_remember.setOnClickListener(this);
        modify_pw.setOnClickListener(this);
        btn_quit.setOnClickListener(this);
    }

    private void init() {
        user_header= (RelativeLayout) getView().findViewById(R.id.user_header);
        user_name= (RelativeLayout) getView().findViewById(R.id.user_name);
        user_sex= (RelativeLayout) getView().findViewById(R.id.user_sex);
        user_remember= (RelativeLayout) getView().findViewById(R.id.user_remember);
        modify_pw= (RelativeLayout) getView().findViewById(R.id.user_modify_pw);
        iv_user_header= (ImageView) getView().findViewById(R.id.iv_user_header);
        tv_user_name= (TextView) getView().findViewById(R.id.tv_user_name);
        cb_user_sex= (CheckBox) getView().findViewById(R.id.cb_user_sex);
        cb_user_remember= (CheckBox) getView().findViewById(R.id.cb_user_remember);
        btn_quit= (Button) getView().findViewById(R.id.btn_quit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_header:
                showAlbumDialog();

                break;
            case R.id.user_name:
                modifyName();
                break;
            case R.id.cb_user_sex:
                switchSex();
                break;
            case R.id.cb_user_remember:
                switchRemberPwd();
                break;
            case R.id.user_modify_pw:
                modifyPwd();
                break;
            case R.id.btn_quit:
                Intent intent=new Intent(context, LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }

    }

    private void modifyName() {
        Intent intent=new Intent(getActivity(), ModifyNameActivity.class);
        startActivityForResult(intent,CommonConstant.REQUESTCODE_EDIT_NICK);
    }

    //修改密码
    private void modifyPwd() {
        Intent intent=new Intent(getActivity(), ModifyPwdActivity.class);
        startActivity(intent);
    }

    private void showAlbumDialog() {
        albumDialog.show();
        albumDialog.setContentView(albumView);
        albumDialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void switchRemberPwd() {
        if (cb_user_remember.isChecked()){
            LoginActivity.editor.putString(LoginActivity.USER_NAME,GlobalParams.userInfo.getUsername());
            LoginActivity.editor.putString(LoginActivity.PASSWORD,GlobalParams.userInfo.getPassword_need());
            LoginActivity.editor.putBoolean(LoginActivity.REMEMBER_USER,true);
            Log.i("asd","switchRemberPwd:"+GlobalParams.userInfo.getPassword_need());

        }else{
            LoginActivity.editor.putString(LoginActivity.USER_NAME,null);
            LoginActivity.editor.putString(LoginActivity.PASSWORD,null);
            LoginActivity.editor.putBoolean(LoginActivity.REMEMBER_USER,false);
        }
        Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
        LoginActivity.editor.commit();
    }

    private void switchSex() {
        if (cb_user_sex.isChecked()==true){
            GlobalParams.userInfo.setSex(true);
            Log.i("asd","true");
        }else {
            GlobalParams.userInfo.setSex(false);
            Log.i("asd","false");
        }
        GlobalParams.userInfo.update(getActivity(),
                GlobalParams.userInfo.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context,"修改性别成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        switch (i) {
                            case 9010: {// 网络超时
                                Toast.makeText(context,"网络超时，请检查您的手机网络",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 9016: {// 无网络连接，请检查您的手机网络
                                Toast.makeText(context,"无网络连接，请检查您的手机网络",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                Toast.makeText(context,"修改失败，请重试",Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                });
    }
    /**
     *
     * @Description: 判断存储卡是否可以用
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     *
     * @Description: 从相册获取
     */
    protected void getAvataFromAlbum() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CommonConstant.REQUESTCODE_PHOTO_ALBUM);
    }

    /**
     *
     * @Description: 从拍照获取
     */
    protected void getAvataFromCamera() {
        File f = new File(CacheUtils.getCacheDirectory(getActivity(), true,
                "icon") + dateTime);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.fromFile(f);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(camera, CommonConstant.REQUESTCODE_PHOTO_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonConstant.REQUESTCODE_EDIT_NICK) {

            if (resultCode == CommonConstant.RESULTCODE_EDIT_NICK_OK) {
                // 修改昵称成功
                tv_user_name.setText(GlobalParams.userInfo.getName());
                MainActivity.tv_iv_header_name.setText(GlobalParams.userInfo.getName());
                Toast.makeText(context,"更改昵称成功",Toast.LENGTH_SHORT).show();

            } else if (resultCode == CommonConstant.RESULTCODE_EDIT_NICK_CANCEL) {
                // 取消修改昵称
                Toast.makeText(context,"已取消更改昵称",Toast.LENGTH_SHORT).show();

            }
        }
        if (resultCode == Activity.RESULT_OK) {
            // 访问相册
            if (requestCode == CommonConstant.REQUESTCODE_PHOTO_ALBUM) {
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    crop(uri);
                }
            }
            // 访问相机
            if (requestCode == CommonConstant.REQUESTCODE_PHOTO_CAMERA) {

                if (hasSdcard()) {
                    String files = CacheUtils.getCacheDirectory(getActivity(),
                            true, "icon") + dateTime;
                    tempFile = new File(files);
                    if (tempFile.exists() && tempFile.length() > 0) {
                        Uri uri = Uri.fromFile(tempFile);
                        crop(uri);
                    } else {
                        Toast.makeText(context,"!!!",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                }
            }
            // 图片裁剪
            if (requestCode == CommonConstant.REQUESTCODE_PHOTO_CUT) {

                try {
                    bitmap = data.getParcelableExtra("data");
                    iconUrl = saveToSdCard(bitmap);
                    iv_user_header.setImageBitmap(bitmap);
//                    MainActivity.iv_header.setImageBitmap(bitmap);
                    updateIcon(iconUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     *
     * @Description: 切图
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, CommonConstant.REQUESTCODE_PHOTO_CUT);
    }
    /**
     *
     * @Description: 把图片保存到sdcard
     */
    private String saveToSdCard(Bitmap bitmap) {
        String files = CacheUtils
                .getCacheDirectory(getActivity(), true, "icon")
                + dateTime
                + "_12.jpg";
        File file = new File(files);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
    /**
     *
     * @Description: 更新头像
     */
    private void updateIcon(String avataPath) {
        if (avataPath != null) {
            final BmobFile file = new BmobFile(new File(avataPath));
            file.upload(getActivity(), new UploadFileListener() {

                @Override
                public void onSuccess() {// 上传成功
                    GlobalParams.userInfo.setAvatar(file);
                    GlobalParams.userInfo.update(getActivity(),
                            new UpdateListener() {

                                @Override
                                public void onSuccess() {// 更新成功
                                    Toast.makeText(context,"更改头像成功",Toast.LENGTH_SHORT).show();
                                    //更新导航页的头像
                                    BmobFile avatarFile = GlobalParams.userInfo.getAvatar();
                                    if (null != avatarFile) {
                                        ImageLoader.getInstance().displayImage(
                                                avatarFile.getFileUrl(getActivity()),
                                                MainActivity.iv_header,
                                                BaseApplication.getInstance().getOptions(
                                                        R.drawable.user_icon_default_main),
                                                new SimpleImageLoadingListener() {
                                                    @Override
                                                    public void onLoadingComplete(String imageUri,
                                                                                  View view, Bitmap loadedImage) {
                                                        super.onLoadingComplete(imageUri, view, loadedImage);
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onFailure(int code, String message) {// 更新失败
                                    switch (code) {
                                        case 9010: {// 网络超时
                                            Toast.makeText(context,"网络超时，请检查您的手机网络",Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        case 9016: {// 无网络连接，请检查您的手机网络
                                            Toast.makeText(context,"无网络连接，请检查您的手机网络",Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        default: {
                                            Toast.makeText(context,"更改头像失败，请重试",Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                }
                            });
                }

                @Override
                public void onFailure(int code, String message) {// 上传失败
                    switch (code) {
                        case 9010: {// 网络超时
                            Toast.makeText(context,"网络超时，请检查您的手机网络",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 9016: {// 无网络连接，请检查您的手机网络
                            Toast.makeText(context,"无网络连接，请检查您的手机网络",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        default: {
                            Toast.makeText(context,"更改头像失败，请重试",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            });
        }
    }
}
