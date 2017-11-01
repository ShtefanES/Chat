package ru.eshtefan.recordaudio.dbLayer;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import ru.eshtefan.recordaudio.commonData.model.dbModel.User;
import ru.eshtefan.recordaudio.utils.FBReferences;

/**
 * Users предоставляет реализации операций для взаимодействия пользователей(User) с Firebase Realtime Database.
 * Created by eshtefan on 09.10.2017.
 */

public class Users implements IUsers {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void getUser(String userId, final UserCallback userCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FBReferences.Database.REF_USERS);
        myRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setUserId(dataSnapshot.getKey());

                userCallback.onUserGot(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    public void setUserListener(final User user, final UserTypingListener userTypingListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FBReferences.Database.REF_USERS);
        myRef.child(user.getUserId()).child(FBReferences.Database.REF_USERS_CHILD_IS_TYPING).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean changedIsTyping = dataSnapshot.getValue(boolean.class);
                userTypingListener.onChangedStTyping(user, changedIsTyping);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    public void setUsersListener(final UsersListener usersListener, String refUsers) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(refUsers);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                user.setUserId(dataSnapshot.getKey());
                usersListener.onAddedUser(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public User getCurrentUser() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String userId = currentUser.getUid();
        String name = currentUser.getEmail();
        String email = currentUser.getEmail();
        String notificationToken = FirebaseInstanceId.getInstance().getToken();

        return new User(userId, name, email, notificationToken);
    }

    @Override
    public void addUser(User user) {
        FirebaseDatabase.getInstance()
                .getReference(FBReferences.Database.REF_USERS)
                .child(user.getUserId()).setValue(user);
    }

    @Override
    public void updateUser(User user, String newNotificationToken) {

        FirebaseDatabase.getInstance()
                .getReference(FBReferences.Database.REF_USERS)
                .child(user.getUserId()).child(FBReferences.Database.REF_USERS_CHILD_NOTIFICATION_TOKEN).setValue(newNotificationToken);
    }

    @Override
    public void updateUser(User user, boolean newStateTyping) {
        FirebaseDatabase.getInstance()
                .getReference(FBReferences.Database.REF_USERS)
                .child(user.getUserId()).child(FBReferences.Database.REF_USERS_CHILD_IS_TYPING).setValue(newStateTyping);
    }
}
