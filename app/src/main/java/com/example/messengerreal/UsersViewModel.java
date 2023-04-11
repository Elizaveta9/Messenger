package com.example.messengerreal;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {
    private FirebaseAuth auth;

    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private MutableLiveData<List<User>> usersListLD = new MutableLiveData<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    public LiveData<List<User>> getUsersListLD() {
        return usersListLD;
    }

    public UsersViewModel() {
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user.setValue(firebaseAuth.getCurrentUser());
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser == null) {
                    return;
                }
                List<User> usersDb = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User userFromSnapshot = dataSnapshot.getValue(User.class);
                    if (userFromSnapshot == null) {
                        return;
                    }
                    if (!userFromSnapshot.getId().equals(currentUser.getUid())) {
                        usersDb.add(userFromSnapshot);
                    }
                }
                usersListLD.setValue(usersDb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setOnlineStatus(boolean isOnline) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        databaseReference.child(firebaseUser.getUid()).child("online").setValue(isOnline);
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public void logout() {
        setOnlineStatus(false);
        auth.signOut();
    }
}
