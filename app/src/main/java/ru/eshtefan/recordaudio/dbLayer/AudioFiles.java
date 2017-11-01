package ru.eshtefan.recordaudio.dbLayer;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

import ru.eshtefan.recordaudio.utils.FBReferences;

/**
 * AudioFiles предоставляет реализации операций для взаимодействия с Firebase Storage.
 * Created by eshtefan on 02.10.2017.
 */

public class AudioFiles implements IAudioFiles {

    private final String LOG = getClass().getSimpleName();

    @Override
    public void uploadFile(String fullFilePath, final long duration, final AudioFileCallback audioFileCallback) {

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        String fileNameInStore = String.format("%d.3gp", Calendar.getInstance().getTimeInMillis());
        final StorageReference refFile = mStorageReference.child(FBReferences.Storage.CHILD_AUDIO).child(fileNameInStore);
        Uri fileUri = Uri.fromFile(new File(fullFilePath));

        refFile.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                refFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        audioFileCallback.OnSuccessUpload(uri.toString(), duration);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.w(LOG, exception.getMessage());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
                Log.w(LOG, e.toString());
            }
        });
    }
}
