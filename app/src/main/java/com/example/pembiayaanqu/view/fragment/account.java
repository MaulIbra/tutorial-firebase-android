package com.example.pembiayaanqu.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pembiayaanqu.R;
import com.example.pembiayaanqu.view.activity.edit_profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class account extends Fragment {
    private FirebaseAuth mAuth;
    private ImageView setting;
    private String photoUrl;
    private String provider;
    private TextView name;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account,container,false);

        name = v.findViewById(R.id.gantiFoto);
        setting = v.findViewById(R.id.setting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        final ImageView photoview = v.findViewById(R.id.imageAccount);
        firebaseFirestore = FirebaseFirestore.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            firebaseFirestore.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                String photo = documentSnapshot.getString("uriPhotoProfile");
                                if (photo== null){
                                    photoview.setImageResource(R.mipmap.user);
                                }else{
                                    Glide.with(getActivity()).load(photo).fitCenter().into(photoview);
                                }
                                name.setText(documentSnapshot.getString("Nama Lengkap"));
                            }else if(user.getPhotoUrl() != null){
                                for (UserInfo profile : user.getProviderData()) {
                                    provider = user.getProviders().get(0);
                                    photoUrl = user.getPhotoUrl().toString();
                                    String photo = checkResolutionPhoto(provider,photoUrl,user);
                                    name.setText(profile.getDisplayName());
                                    Glide.with(getActivity()).load(photo).fitCenter().into(photoview);
                                }
                            }else{
                                name.setText(user.getDisplayName());
                                photoview.setImageResource(R.mipmap.user);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            }
        return v;
    }

   public void showPopup(final View v){
       PopupMenu popup = new PopupMenu(getActivity(),v);
       popup.inflate(R.menu.setting_menu);
       popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               switch (item.getItemId()){
                   case R.id.edit_profile:
                       Intent i = new Intent(getContext(), edit_profile.class);
                       startActivity(i);
               }
               return true;
           }
       });
       popup.show();
   }

    private String checkResolutionPhoto(String provider, String photoUrl, FirebaseUser user){
        if (provider.equals("facebook.com")) {
            photoUrl = photoUrl + "?height=500";
        }
        else if(provider.equals("google.com"))
        {
            photoUrl = photoUrl.substring(0, photoUrl.length() - 15) + "s400-c/photo.jpg";

        }
        else
        {
            photoUrl=photoUrl;
        }
        return photoUrl;
    }

}
