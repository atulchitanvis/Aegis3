package com.example.android.spitit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private TextInputEditText usr_contact;
    private EditText usr_first_name,usr_last_name,usr_email;
    private static TextView usr_dob;
    private ImageView inc_img,add_pic;
    private static int GALLERY_PICK=2;
    private Button save;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private Uri downloadUrl,uri;
    private static String date;
    private FirebaseAuth mAuth;
    private Uri thumb_uri;
    private String personName,personGivenName,personFamilyName,personEmail,personId,gender;
    private Uri personPhoto;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup your profile");

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            personId = acct.getId();
            personPhoto = acct.getPhotoUrl();

            System.out.println(personName+"@"+personGivenName+"@"+personFamilyName+"@"+personEmail+"@"+personId+"@"+personPhoto);
        }

        initialize();

        if(!isNetworkAvailable())
            Toast.makeText(EditProfileActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_PICK);
            }
        });
        spinner.setPrompt("Select your gender");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                gender=(String)adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    HashMap<String,String> dataMap=new HashMap<String,String>();
                    String name=usr_first_name.getText().toString().trim();
                    String email=usr_email.getText().toString().trim();
                    if(!isValidEmail(email))
                        throw new IllegalArgumentException();
                    String last_name=usr_last_name.getText().toString().trim();
                    String dob=usr_dob.getText().toString().trim();
                    if(TextUtils.isEmpty(dob))
                        throw new NullPointerException();
                    String contact=usr_contact.getText().toString().trim();
                    if(!validContact(contact))
                        throw new ArrayIndexOutOfBoundsException();
                    mProgress.setTitle("Uploading");
                    mProgress.setMessage("Please wait...");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    dataMap.put("First name",name);
                    dataMap.put("Last name",last_name);
                    dataMap.put("DOB",dob);
                    dataMap.put("Email",email);
                    dataMap.put("Contact",contact);
                    dataMap.put("Gender",gender);
                    Log.e("thumbnail ",thumb_uri.toString());
                    dataMap.put("Image",thumb_uri.toString());
                    dataMap.put("UID",mAuth.getCurrentUser().getUid());
                    mDatabase.child(mAuth.getCurrentUser().getUid()).setValue(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProgress.dismiss();
                        }
                    });
                    startActivity(new Intent(EditProfileActivity.this,MainActivity.class));
                    finish();
                }
                catch (NullPointerException e)
                {
                    Toast.makeText(EditProfileActivity.this,"Field(s) cannot be blank",Toast.LENGTH_LONG).show();
                }
                catch (IllegalArgumentException iae)
                {
                    Toast.makeText(EditProfileActivity.this,"Invalid Email-ID",Toast.LENGTH_LONG).show();
                }
                catch (ArrayIndexOutOfBoundsException aiobe)
                {
                    Toast.makeText(EditProfileActivity.this,"Invalid contact",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK)
        {
            uri=data.getData();
            CropImage.activity(uri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                Uri resultUri = result.getUri();
                downloadUrl=resultUri;
                Bitmap thumb_bitmap=null;

                File thumb_pathfile=new File(resultUri.getPath());
                try {
                    thumb_bitmap= new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_pathfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference thumb_filepath=mStorage.child("thumbnails").child(mAuth.getCurrentUser().getUid().toString()).child("Person");

                StorageReference filepath=mStorage.child("Photos").child(mAuth.getCurrentUser().getUid().toString()).child("Person");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            downloadUrl=task.getResult().getDownloadUrl();

                            UploadTask uploadTask=thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    if(thumb_task.isSuccessful())
                                    {
                                        mProgress.dismiss();
                                        thumb_uri=thumb_task.getResult().getDownloadUrl();
                                        Log.e("thumbnail ",thumb_uri.toString());
                                        Picasso.with(EditProfileActivity.this).load(thumb_uri).into(inc_img);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void initialize()
    {
        usr_first_name=(EditText) findViewById(R.id.user_first_name);
        usr_first_name.setText(personGivenName);
        usr_last_name=(EditText)findViewById(R.id.user_last_name);
        usr_last_name.setText(personFamilyName);
        usr_dob=(TextView)findViewById(R.id.user_dob);
        usr_email=(EditText)findViewById(R.id.user_email);
        usr_email.setText(personEmail);
        usr_contact=(TextInputEditText)findViewById(R.id.user_contact);
        inc_img=(ImageView)findViewById(R.id.user_img);
        add_pic=(ImageView) findViewById(R.id.action_add);
        save=(Button)findViewById(R.id.save);
        mStorage= FirebaseStorage.getInstance().getReference();
        mProgress=new ProgressDialog(this);
        Picasso.with(EditProfileActivity.this).load(personPhoto).into(inc_img);
        spinner=(Spinner)findViewById(R.id.gender);
        thumb_uri=personPhoto;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private int month,year,day;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            date=day+"-"+(month+1)+"-"+year;
            setDate();
        }
    }

    public static void setDate() {
        usr_dob.setText(date);
    }

    public final boolean isValidEmail(CharSequence target) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches())
            return true;
        return false;
    }
    public boolean validContact(String number)
    {
        if(number.length()==10)
            return true ;
        return false;
    }
}
