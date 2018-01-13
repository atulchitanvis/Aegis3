package com.example.android.spitit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.HashMap;

public class EmergencyAdminActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 123;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ImageView imageView;
    private Uri downUrl;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_admin);

        Toolbar toolbar=(Toolbar)findViewById(R.id.emergency_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Emergency");
        initialize();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Emergency").child(mAuth.getCurrentUser().getUid());
        String location = getIntent().getExtras().getString("Location");
        ImageButton addBlueprint=(ImageButton)findViewById(R.id.action_add_img);
        addBlueprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionForStorage();
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,2);
            }
        });
        final EditText otherEditText=(EditText)findViewById(R.id.other_edit_text);
        final CheckBox other=(CheckBox)findViewById(R.id.other);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (other.isChecked())
                    otherEditText.setVisibility(View.VISIBLE);
                else
                    otherEditText.setVisibility(View.GONE);
            }
        });
        final TextView locationText=(TextView)findViewById(R.id.location);
        locationText.setText(location);
        Button emergency=(Button)findViewById(R.id.emergency_button);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    mProgress.setTitle("Uploading...");
                    mProgress.setMessage("Please wait");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    String loc=locationText.getText().toString();
                    String type="";
                    CheckBox fire=(CheckBox)findViewById(R.id.fire);
                    CheckBox earthquake=(CheckBox)findViewById(R.id.earthquake);
                    if (fire.isChecked())
                        type+="Fire~";
                    if (earthquake.isChecked())
                        type+="Earthquake~";
                    if (other.isChecked())
                        type+=otherEditText.getText().toString()+"~";
                    Log.e("Type",type);
                    String []str=type.split("~");
                    String newType="";
                    for (String single:str)
                    {
                        newType+=single+"\n";
                    }
                    newType=newType.substring(0,newType.lastIndexOf("\n"));
                    System.out.println(newType);
                    String blueprint=downUrl.toString();
                    TextInputEditText tipText=(TextInputEditText)findViewById(R.id.tips);
                    String tip=tipText.getText().toString();
                    if (TextUtils.isEmpty(loc) || TextUtils.isEmpty(newType) || TextUtils.isEmpty(blueprint) || TextUtils.isEmpty(tip))
                        throw new NullPointerException();
                    HashMap<String,String> map=new HashMap<>();
                    map.put("location",loc);
                    map.put("people","0");
                    map.put("tip",tip);
                    map.put("type",newType);
                    map.put("blueprint",blueprint);
                    mDatabase.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mProgress.dismiss();
                            Intent intent=new Intent(EmergencyAdminActivity.this,EmergencyActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(EmergencyAdminActivity.this,"Emergency already declared",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch(NullPointerException npe)
                {
                    Toast.makeText(EmergencyAdminActivity.this,"Field(s) cannot be blank",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initialize()
    {
        mAuth=FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();
        imageView=(ImageView)findViewById(R.id.blueprint);
        mProgress=new ProgressDialog(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Hello","ji");
        if(requestCode==2 && resultCode==RESULT_OK)
        {
            mProgress.setTitle("Uploading...");
            mProgress.setMessage("Please wait");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();
            Uri uri=data.getData();
            StorageReference filepath=mStorage.child("Blueprint").child(mAuth.getCurrentUser().getUid());
            filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        downUrl=task.getResult().getDownloadUrl();
                        Picasso.with(EmergencyAdminActivity.this).load(downUrl).into(imageView);
                        mProgress.dismiss();
                    }
                }
            });
        }
    }
    public void getPermissionForStorage() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
