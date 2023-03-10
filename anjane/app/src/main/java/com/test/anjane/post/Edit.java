package com.test.anjane.post;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.test.anjane.R;
import com.test.anjane.post.models.User;
import com.test.anjane.post.models.mPost;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Edit extends BaseActivity {
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    private static final int ADDRESS = 1001;

    private static final String TAG = "Edit";
    private static final String REQUIRED = "Required";

    private FirebaseFirestore db;
    private TextView et_address; //??????
    private Button btn_search; //????????????
    private EditText mTitleField; //????????????
    private EditText mBodyField; //????????????
    public int imageCount;
    private ImageView ivAdd;
    private ImageView ivPreview1; //???????????????
    private ImageView ivPreview2; //???????????????
    private ImageView ivPreview3; //???????????????
    private String imageText1;
    private String imageText2;
    private String imageText3;
    private TextView BtnPost; //??????
    private Uri filePath; //?????? ?????????
    private Spinner spinner_postField;
    private Spinner spinner_postField_sub;
    private String formatDate;
    private Boolean isPermission = true;
    private TextView postClear;

    ArrayAdapter<CharSequence> adspin1, adspin2; //???????????? ??????
    String choice_do="";

    private static final String LOG_TAG = "Edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.edit);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        db = FirebaseFirestore.getInstance();

        mTitleField = (EditText)findViewById(R.id.edit_postTitle);
        mBodyField = (EditText)findViewById( R.id.edit_postContent);
        ivAdd = (ImageView)findViewById(R.id.iv_postAdd);
        ivPreview1 = (ImageView)findViewById( R.id.iv_postPreview1);
        ivPreview2 = (ImageView)findViewById( R.id.iv_postPreview2);
        ivPreview3 = (ImageView)findViewById( R.id.iv_postPreview3);
        BtnPost = (TextView) findViewById( R.id.postAdd);
        et_address = (EditText)findViewById( R.id.et_address);
        et_address.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent( Edit.this, DaumWebViewActivity.class);
                startActivityForResult(i, 100);
            }
        });
        btn_search = (Button)findViewById( R.id.bt_daum);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss");
        formatDate = sdfNow.format(date);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Edit.this, DaumWebViewActivity.class);
                startActivityForResult(i, 100);
            }
        });
        BtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        ivAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(imageText1 == null) {
                    imageCount = 1;
                    tedPermission();
                    makeDialog1();
                } else if(imageText2 == null) {
                    imageCount = 2;
                    tedPermission();
                    makeDialog1();
                } else if(imageText3 == null) {
                    imageCount = 3;
                    tedPermission();
                    makeDialog1();
                }
            }
        });

        postClear = findViewById(R.id.postClear);
        postClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinner_postField = (Spinner)findViewById(R.id.spinner_postField);
        spinner_postField_sub = (Spinner)findViewById(R.id.spinner_postField_sub);

        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner_do, android.R.layout.simple_spinner_dropdown_item);//????????? ???????????? ?????? ????????????. this=??? ??????class??? ???????????????. R.array.spinner_do??? ????????? ????????? ??? ??? ?????? ????????? ???????????? ????????? value->string.xml??? ?????? String????????? ????????????????????????. //R.layout.simple_~~~??? ????????????????????? ?????????????????? spinner ???????????????. ???????????? ????????? ???????????????. adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//???????????? ????????? ?????? ????????? ??????????????????. ????????? ???????????????. ?????? ???????????? ???????????????.
        spinner_postField.setAdapter(adspin1);//???????????? ????????? spinner??? ????????????. ???????????? ????????? ????????? spinner??? ????????? ????????? ????????????.
        spinner_postField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//????????? spinner ????????? ????????? ???????????????. setO ???????????? ????????? ?????????????????????. ????????? ?????????????????????.
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//????????? ???????????????????????? ???????????? ???????????? ????????? ??? ????????????. int i??? ??????????????? ?????? ?????? spinner??? ???????????? ??????????????? ?????? ???????????????. ???????????????? ??????
                if (adspin1.getItem(i).equals("????????????")) {//spinner??? ?????? ???????????? i ??????????????? ?????? ?????? ????????? ???????????? ???????????????.
                    choice_do = "????????????";//?????? ????????? ????????? ?????? ?????? ???????????????.
                    adspin2 = ArrayAdapter.createFromResource(Edit.this, R.array.spinner_do_danger, android.R.layout.simple_spinner_dropdown_item);//????????? ????????? ????????? spinner??? ?????? ????????????. //?????? this??? ?????? Main~~~??? ????????? ?????? this??? ????????????????????? ????????? ???????????????. //????????? ?????? ?????? ??????????????? ?????? ?????????????????? ??? ????????? ?????? ??????????????? ?????????.//????????? ?????? class -> Public View????????? ??????????????? ?????? view???.getContext()??? ???????????? ?????????.//?????? View rootView =~~ ???????????? ???????????? rootView.getContext()?????????????????????. this??? ????????????.
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_postField_sub.setAdapter(adspin2);//????????? ??????????????? ????????? spinner??? ???????????????.
                } else if (adspin1.getItem(i).equals("????????????")) {//????????? ????????? ???????????????. ?????????????????????.
                    choice_do = "????????????";
                    adspin2 = ArrayAdapter.createFromResource( Edit.this, R.array.spinner_do_convenience, android.R.layout.simple_spinner_dropdown_item );
                    adspin2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                    spinner_postField_sub.setAdapter( adspin2 );
                }
                else if (adspin1.getItem(i).equals("??????????????????")) {//????????? ????????? ???????????????. ?????????????????????.
                    choice_do = "??????????????????";
                    adspin2 = ArrayAdapter.createFromResource( Edit.this, R.array.spinner_do_accident, android.R.layout.simple_spinner_dropdown_item );
                    adspin2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                    spinner_postField_sub.setAdapter( adspin2 );
                }
                else if (adspin1.getItem(i).equals("??????")) {//????????? ????????? ???????????????. ?????????????????????.
                    choice_do = "??????";
                    adspin2 = ArrayAdapter.createFromResource( Edit.this, R.array.spinner_do_other, android.R.layout.simple_spinner_dropdown_item );
                    adspin2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                    spinner_postField_sub.setAdapter( adspin2 );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void makeDialog1() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(Edit.this);
        alt_bld.setTitle("?????? ?????????").setIcon(R.drawable.ic_photo_library_black_24dp).setCancelable(
                false).setPositiveButton("????????????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("??????", "??????????????? > ???????????? ??????");
                        // ?????? ?????? ??????
                        takePhoto1();
                    }
                }).setNeutralButton("????????????",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int id) {
                        Log.v("??????", "??????????????? > ???????????? ??????");
                        //???????????? ??????
                        selectAlbum1();
                    }

                }).setNegativeButton("??????   ",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("??????", "??????????????? > ?????? ??????");
                        // ?????? ??????. dialog ??????.
                        dialog.cancel();
                    }

                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void selectAlbum1() {
        //???????????? ??????
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction( Intent.ACTION_GET_CONTENT);
        intent.putExtra( Intent.EXTRA_ALLOW_MULTIPLE,true );
        startActivityForResult( Intent.createChooser(intent, "???????????? ???????????????."), FROM_ALBUM);
    }

    private void takePhoto1() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "????????? ?????? ??????! ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            //Uri photoUri = Uri.fromFile(photoFile);
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.test.anjane.provider", photoFile);
            filePath = photoUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, FROM_CAMERA);
        }
    }

    public File createImageFile() throws IOException{
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile= null;
        File storageDir = new File(Environment.getExternalStorageDirectory() ,imgFileName);

        if(!storageDir.exists()){
            Log.v("??????","storageDir ?????? x " + storageDir.toString());
            storageDir.mkdirs();
        }
        Log.v("??????","storageDir ????????? " + storageDir.toString());

        imageFile = new File(storageDir,imgFileName);

        switch (imageCount) {
            case 1:
                imageText1 = imageFile.getAbsolutePath();
                break;
            case 2:
                imageText2 = imageFile.getAbsolutePath();
                break;
            case 3:
                imageText3 = imageFile.getAbsolutePath();
        }
        return imageFile;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar??? back??? ????????? ??? ??????
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == 100) {
            et_address.setText(intent.getStringExtra("address"));
        }

        switch (requestCode) {
            case FROM_ALBUM: {
                //???????????? ????????????
                filePath = intent.getData();
                String[] imgUri = { MediaStore.Images.Media.DATA };

                if(imageCount == 1) {
                    Cursor cursor = getContentResolver().query(filePath, imgUri, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(imgUri[0]));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
                    bitmap = ExifUtils.rotateBitmap(imagePath,bitmap);
                    ivPreview1.setImageBitmap(bitmap);
                    cursor.close();
                    uplodFile();
                } else if (imageCount == 2) {
                    Cursor cursor = getContentResolver().query(filePath, imgUri, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(imgUri[0]));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
                    bitmap = ExifUtils.rotateBitmap(imagePath,bitmap);
                    ivPreview2.setImageBitmap(bitmap);
                    cursor.close();
                    uplodFile();
                } else if (imageCount == 3) {
                    Cursor cursor = getContentResolver().query(filePath, imgUri, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(imgUri[0]));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
                    bitmap = ExifUtils.rotateBitmap(imagePath,bitmap);
                    ivPreview3.setImageBitmap(bitmap);
                    cursor.close();
                    uplodFile();
                }
                break;
            }

            case FROM_CAMERA: {
                //??????
                try {
                    Log.v( "??????", "FROM_CAMERA ??????" );
                    galleryAddPic();
                    //??????????????? ???????????????
                    if(imageCount == 1) {
                        ivPreview1.setImageURI( filePath );
                    } else if (imageCount == 2) {
                        ivPreview2.setImageURI( filePath );
                    } else if (imageCount == 3) {
                        ivPreview3.setImageURI( filePath );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                uplodFile();
                break;
            }

            case ADDRESS: {
                String address = intent.getExtras().getString("address");
                if (address != null)
                    et_address.setText(address);
            }
        }
    }

    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f;

        switch (imageCount) {
            case 1:
                f = new File(imageText1);
                break;
            case 2:
                f = new File(imageText2);
                break;
            case 3:
                f = new File(imageText3);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + imageCount);
        }
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    //?????? ?????????
    private void uplodFile() {
        //???????????? ????????? ?????????
        if(filePath != null){
            //????????? ?????? Diglog
            //final ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle("????????????...");
            //progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            //???????????? ?????? ??? ?????????
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".jpg";

            switch (imageCount){
                case 1:
                    imageText1 = filename;
                    break;
                case 2:
                    imageText2 = filename;
                    break;
                case 3:
                    imageText3 = filename;
            }

            StorageReference storageRef = storage.getReferenceFromUrl("gs://anjane-821f9.appspot.com").child("postimage/" + filename);

            storageRef.putFile(filePath)
                    //?????????
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            hideProgressDialog(); //????????? ?????? Dialog ?????? ??????
                            //progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "????????? ??????!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //?????????
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            //progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "????????? ??????!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //?????????
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //?????? ?????? ?????? ???????????? ????????? ????????????. ??? ??????????
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog??? ???????????? ???????????? ????????? ??????
                            //progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                            Toast.makeText(getApplicationContext(),"Uploaded ((int) progress) + % ..." ,Toast.LENGTH_SHORT);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    private String splitDate(String formatDate) {
        if(formatDate.contains(" ")) {
            return formatDate.split(" ")[0];
        } else {
            return formatDate;
        }
    }

    private void submitPost() {
        final String addres = et_address.getText().toString();
        final String title = mTitleField.getText().toString();
        final String content = mBodyField.getText().toString();
        final String image1 = imageText1;
        final String image2 = imageText2;
        final String image3 = imageText3;
        final String spin1 = spinner_postField.getSelectedItem().toString();
        final String spin2 = spinner_postField_sub.getSelectedItem().toString();
        final String formatdate = formatDate;
        final String date = splitDate(formatDate);

        if (TextUtils.isEmpty(addres)) {
            et_address.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(content)) {
            mBodyField.setError(REQUIRED);
            return;
        }
        setEditingEnabled(false);
        Toast.makeText(this, "????????? ????????? ???...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject( User.class);
                if (user == null) {
                    Log.e(TAG, "User " + userId + " is unexpectedly null");
                    Toast.makeText( Edit.this,
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if(imageCount < 2 )
                        writeNewPost(addres, userId, user.username, title, content, image1, null, null, spin1, spin2, formatdate, date);
                    else if(imageCount < 3 )
                        writeNewPost(addres, userId, user.username, title, content, image1, image2, null, spin1, spin2, formatdate, date);
                    else if(imageCount == 3)
                        writeNewPost(addres, userId, user.username, title, content, image1, image2, image3, spin1, spin2, formatdate, date);

                }
                setEditingEnabled(true);
                finish();
            }
        });
    }

    private void setEditingEnabled(boolean enabled) {
        et_address.setEnabled(enabled);
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            BtnPost.setVisibility( View.VISIBLE);
        } else {
            BtnPost.setVisibility( View.GONE);
        }
    }

    private void writeNewPost(String addres, String userId, String username, String title, String body, String image1, String image2, String image3, String spin1, String spin2, String formatDate, String date) {
        mPost post = new mPost(addres, userId, username, title, body, image1, image2, image3, spin1, spin2, formatDate, date);
        db.collection("posts").add(post);
    }

    public void onClick(View view) {
        if(view.getId() == R.id.bt_daum){
            Intent intent = new Intent(getApplicationContext(), DaumWebViewActivity.class);
            startActivity(intent);
        }
    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // ?????? ?????? ??????
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ?????? ?????? ??????
                isPermission = false;
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}