package com.example.android.spitit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AddContactsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button save;
    private FrameLayout photo1,photo2,photo3;
    private int id_name,id_no,id_image;
    private static final int PICK_CONTACT=1;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private ArrayList<String> numbers=new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private Uri pic1,pic2,pic3;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        getPermissionToReadUserContacts();
        toolbar=(Toolbar)findViewById(R.id.add_contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intialize();
        photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContact();
                id_name=R.id.name1;
                id_no=R.id.no1;
                id_image=R.id.image1;
            }
        });
        photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContact();
                id_name=R.id.name2;
                id_no=R.id.no2;
                id_image=R.id.image2;
            }
        });
        photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContact();
                id_name=R.id.name3;
                id_no=R.id.no3;
                id_image=R.id.image3;
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    TextView name1=(TextView)findViewById(R.id.name1);
                    TextView name2=(TextView)findViewById(R.id.name2);
                    TextView name3=(TextView)findViewById(R.id.name3);
                    TextView no1=(TextView)findViewById(R.id.no1);
                    TextView no2=(TextView)findViewById(R.id.no2);
                    TextView no3=(TextView)findViewById(R.id.no3);
                    if(name1.getText().toString().equals("Name:") || name2.getText().toString().equals("Name:") || name3.getText().toString().equals("Name:") || no1.getText().toString().equals("No.:") || no2.getText().toString().equals("No.:") || no3.getText().toString().equals("No.:"))
                        throw new NullPointerException();
                    mDatabase.child("Person1").child("Name").setValue(name1.getText().toString());
                    mDatabase.child("Person1").child("Phone").setValue(no1.getText().toString());
                    if(pic1!=null)
                        mDatabase.child("Person1").child("Photo").setValue(pic1.toString());
                    else
                        mDatabase.child("Person1").child("Photo").setValue("default_avatar");
                    mDatabase.child("Person2").child("Name").setValue(name2.getText().toString());
                    mDatabase.child("Person2").child("Phone").setValue(no2.getText().toString());
                    if(pic2!=null)
                        mDatabase.child("Person2").child("Photo").setValue(pic2.toString());
                    else
                        mDatabase.child("Person2").child("Photo").setValue("default_avatar");
                    mDatabase.child("Person3").child("Name").setValue(name3.getText().toString());
                    mDatabase.child("Person3").child("Phone").setValue(no3.getText().toString());
                    if(pic3!=null)
                        mDatabase.child("Person3").child("Photo").setValue(pic3.toString());
                    else
                        mDatabase.child("Person3").child("Photo").setValue("default_avatar");
                    Intent main=new Intent(AddContactsActivity.this,MainActivity.class);
                    main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main);
                    finish();
                }
                catch (NullPointerException npe) {
                    Toast.makeText(AddContactsActivity.this, "Field(s) cannot be blank", Toast.LENGTH_LONG).show();
                }
            }
        });
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> map=(HashMap<String,Object>)dataSnapshot.getValue();
                if(map != null)
                {
                    ArrayList<String> keySet=new ArrayList<>(map.keySet());
                    if(keySet.size() != 0)
                    {
                        for(String key:keySet)
                        {
                            switch (key) {
                                case "Person1":
                                    TextView name1 = (TextView) findViewById(R.id.name1);
                                    if(dataSnapshot.child("Person1").child("Name").getValue() != null)
                                        name1.setText(dataSnapshot.child("Person1").child("Name").getValue().toString());
                                    TextView no1 = (TextView) findViewById(R.id.no1);
                                    Log.e("Hello",""+dataSnapshot.child("Person1").child("Phone").getValue());
                                    if(dataSnapshot.child("Person1").child("Phone").getValue() != null)
                                        no1.setText(dataSnapshot.child("Person1").child("Phone").getValue().toString());
                                    ImageView image1 = (ImageView) findViewById(R.id.image1);
                                    if(dataSnapshot.child("Person1").child("Photo").getValue() != null)
                                    {
                                        if (dataSnapshot.child("Person1").child("Photo").getValue().toString().equals("default_avatar"))
                                            image1.setImageResource(R.drawable.default_avatar);
                                        else
                                            Picasso.with(AddContactsActivity.this).load(dataSnapshot.child("Person1").child("Photo").getValue().toString()).into(image1);
                                    }
                                    TextView hint1 = (TextView) findViewById(R.id.hint1);
                                    hint1.setVisibility(View.GONE);
                                    break;
                                case "Person2":
                                    TextView name2 = (TextView) findViewById(R.id.name2);
                                    if(dataSnapshot.child("Person2").child("Name").getValue() != null)
                                        name2.setText(dataSnapshot.child("Person2").child("Name").getValue().toString());
                                    TextView no2 = (TextView) findViewById(R.id.no2);
                                    Log.e("Hello",""+dataSnapshot.child("Person2").child("Phone").getValue());
                                    if(dataSnapshot.child("Person2").child("Phone").getValue() != null)
                                        no2.setText(dataSnapshot.child("Person2").child("Phone").getValue().toString());
                                    ImageView image2 = (ImageView) findViewById(R.id.image2);
                                    if(dataSnapshot.child("Person2").child("Photo").getValue() != null)
                                    {
                                        if (dataSnapshot.child("Person2").child("Photo").getValue().toString().equals("default_avatar"))
                                            image2.setImageResource(R.drawable.default_avatar);
                                        else
                                            Picasso.with(AddContactsActivity.this).load(dataSnapshot.child("Person2").child("Photo").getValue().toString()).into(image2);
                                    }
                                    TextView hint2 = (TextView) findViewById(R.id.hint2);
                                    hint2.setVisibility(View.GONE);
                                    break;
                                case "Person3":
                                    TextView name3 = (TextView) findViewById(R.id.name3);
                                    if(dataSnapshot.child("Person3").child("Name").getValue() != null)
                                        name3.setText(dataSnapshot.child("Person3").child("Name").getValue().toString());
                                    TextView no3 = (TextView) findViewById(R.id.no3);
                                    Log.e("Hello",""+dataSnapshot.child("Person1").child("Phone").getValue());
                                    if(dataSnapshot.child("Person3").child("Phone").getValue() != null)
                                        no3.setText(dataSnapshot.child("Person1").child("Phone").getValue().toString());
                                    ImageView image3 = (ImageView) findViewById(R.id.image3);
                                    if(dataSnapshot.child("Person3").child("Photo").getValue() != null)
                                    {
                                        if (dataSnapshot.child("Person3").child("Photo").getValue().toString().equals("default_avatar"))
                                            image3.setImageResource(R.drawable.default_avatar);
                                        else
                                            Picasso.with(AddContactsActivity.this).load(dataSnapshot.child("Person3").child("Photo").getValue().toString()).into(image3);
                                    }
                                    TextView hint3 = (TextView) findViewById(R.id.hint3);
                                    hint3.setVisibility(View.GONE);
                                    break;
                            }
                        }
                    }
                }
                mProgress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setContact()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
        Log.e("Hello","2");
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        Log.e("Hello","3");
        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {

                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        String cNumber="";

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1")).replace(" ","");
                            phones.close();
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        mProgress.setTitle("Uploading");
                        mProgress.setMessage("Please wait...");
                        mProgress.setCanceledOnTouchOutside(false);
                        mProgress.show();
                        InputStream pic=openPhoto(Long.parseLong(id));
                        System.out.println("number is:"+cNumber);
                        System.out.println("name is:"+name);
                        System.out.println(pic);
                        if(!numbers.contains(cNumber))
                            numbers.add(cNumber);
                        else
                            Toast.makeText(this,"Contact already exists",Toast.LENGTH_LONG).show();
                        singleContact(name,cNumber,pic);
                    }
                }
                break;
        }
    }

    private void singleContact(String name, String cNumber, InputStream pic)
    {
        TextView nameView=(TextView)findViewById(id_name);
        nameView.setText(name);
        TextView number=(TextView)findViewById(id_no);
        number.setText(cNumber);
        final ImageView image=(ImageView)findViewById(id_image);
        if(id_no == R.id.no1)
        {
            StorageReference filepath=mStorage.child("Photos").child(mAuth.getCurrentUser().getUid().toString()).child("Person1");
            try {
                if(pic!=null)
                {
                    byte []thumb_byte=extract(pic);
                    uploadPhoto(thumb_byte,filepath,image);
                }
                else
                {
                    TextView hint=(TextView)findViewById(R.id.hint1);
                    hint.setVisibility(View.GONE);
                    mProgress.dismiss();
                    image.setImageResource(R.drawable.default_avatar);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(id_no == R.id.no2)
        {
            StorageReference filepath=mStorage.child("Photos").child(mAuth.getCurrentUser().getUid().toString()).child("Person2");
            try {
                if(pic!=null)
                {
                    byte []thumb_byte=extract(pic);
                    uploadPhoto(thumb_byte,filepath,image);
                }
                else
                {
                    TextView hint=(TextView)findViewById(R.id.hint2);
                    hint.setVisibility(View.GONE);
                    mProgress.dismiss();
                    image.setImageResource(R.drawable.default_avatar);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(id_no == R.id.no3)
        {
            StorageReference filepath=mStorage.child("Photos").child(mAuth.getCurrentUser().getUid().toString()).child("Person3");
            try {
                if(pic!=null)
                {
                    byte []thumb_byte=extract(pic);
                    uploadPhoto(thumb_byte,filepath,image);
                }
                else
                {
                    TextView hint=(TextView)findViewById(R.id.hint3);
                    hint.setVisibility(View.GONE);
                    mProgress.dismiss();
                    image.setImageResource(R.drawable.default_avatar);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void intialize()
    {
        mProgress=new ProgressDialog(this);
        mStorage= FirebaseStorage.getInstance().getReference();
        photo1=(FrameLayout) findViewById(R.id.photo1);
        photo2=(FrameLayout) findViewById(R.id.photo2);
        photo3=(FrameLayout) findViewById(R.id.photo3);
        save=(Button)findViewById(R.id.save);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Contacts");
    }

    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public ByteArrayInputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private byte[] extract(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, read);
        }
        baos.flush();
        return  baos.toByteArray();
    }

    public void uploadPhoto(byte[] bytes, StorageReference filepath, final ImageView imageView)
    {
        final Uri[] uri = new Uri[1];
        UploadTask uploadTask=filepath.putBytes(bytes);
        System.out.println("Filepath:"+filepath+"\nByte:"+bytes.toString());
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    uri[0] = task.getResult().getDownloadUrl();
                    System.out.println("Uri: "+uri[0]);
                    Picasso.with(AddContactsActivity.this).load(uri[0]).into(imageView);
                    if(id_image==R.id.image1)
                    {
                        pic1=uri[0];
                        TextView hint=(TextView)findViewById(R.id.hint1);
                        hint.setVisibility(View.GONE);
                    }
                    else if(id_image==R.id.image2)
                    {
                        pic2=uri[0];
                        TextView hint=(TextView)findViewById(R.id.hint2);
                        hint.setVisibility(View.GONE);
                    }
                    else if(id_image==R.id.image3)
                    {
                        pic3=uri[0];
                        TextView hint=(TextView)findViewById(R.id.hint3);
                        hint.setVisibility(View.GONE);
                    }
                    mProgress.dismiss();
                }
            }
        });
    }
}