package com.kraken.krakenhax;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ViewModel for managing event data.
 * Handles Firestore interactions and business logic.
 */
public class EventViewModel extends ViewModel {
    private final MutableLiveData<Bitmap> qrCode;
    private final FirebaseFirestore db;
    private final CollectionReference eventCollection;
    private final StorageReference storageRef;
    private final MutableLiveData<Bitmap> downloadedBitmap = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Event>> eventList;
    private Target picassoTarget;

    /**
     * Constructor initializes Firestore and snapshot listener.
     */
    public EventViewModel() {
        eventList = new MutableLiveData<>(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        eventCollection = db.collection("Events");
        storageRef = FirebaseStorage.getInstance().getReference();
        qrCode = new MutableLiveData<>();
        addSnapshotListener();
    }

    /**
     * Adds an event locally and uploads to Firestore.
     */
    public void addEvent(Event event) {
        ArrayList<Event> currentList = eventList.getValue();
        if (currentList != null) {
            currentList.add(event);
            eventList.setValue(currentList);
        }
        uploadEvent(event);
    }

    /**
     * Uploads a poster image for an event and updates Firestore.
     */
    public void uploadPosterForEvent(Event event, Uri filePath) {
        if (filePath == null || event == null || event.getId() == null) {
            Log.e("Firebase", "Event or file path is null");
            return;
        }

        StorageReference eventPosterRef =
                storageRef.child("event_posters/" + event.getId() + ".jpg");

        UploadTask uploadTask = eventPosterRef.putFile(filePath);

        uploadTask.addOnSuccessListener(taskSnapshot ->
                eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Poster URL: " + downloadUrl);

                    event.setPoster(downloadUrl);

                    db.collection("Events")
                            .document(event.getId())
                            .set(event)
                            .addOnSuccessListener(aVoid ->
                                    Log.d("Firebase", "Event poster uploaded successfully"))
                            .addOnFailureListener(e ->
                                    Log.e("Firebase", "Error uploading event poster", e));
                })
        ).addOnFailureListener(e ->
                Log.e("Firebase", "Upload failed", e)
        );
    }

    /**
     * Generates a QR code bitmap for an event ID.
     */
    public Bitmap generateQR(String eventId) throws WriterException {
        String deepLinkUrl = "krakenhax://event/" + eventId;
        Log.d("QRCodeTest", "Deep Link URL: " + deepLinkUrl);
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(deepLinkUrl, BarcodeFormat.QR_CODE, 400, 400);
        BarcodeEncoder encoder = new BarcodeEncoder();
        return encoder.createBitmap(matrix);
    }

    /**
     * Uploads a QR code image to Firebase Storage and updates Firestore.
     */
    public void uploadQrCode(Event event, Bitmap qrCodeBitmap, OnSuccessListener<Uri> onSuccessListener) {
        if (qrCodeBitmap == null || event == null || event.getId() == null) {
            Log.e("Firebase", "Event or QR code is null");
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference qrCodeRef =
                storageRef.child("qr_codes/" + event.getId() + ".png");

        UploadTask uploadTask = qrCodeRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot ->
                qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "QR Code URL: " + downloadUrl);
                    onSuccessListener.onSuccess(uri);

                    db.collection("Events").document(event.getId())
                            .update("qrCodeURL", downloadUrl)
                            .addOnSuccessListener(aVoid ->
                                    Log.d("Firebase", "QR code URL uploaded successfully"))
                            .addOnFailureListener(e ->
                                    Log.e("Firebase", "Error uploading QR code URL", e));
                }).addOnFailureListener(e ->
                        Log.e("Firebase", "QR code URL upload failed: could not get URL", e))
        ).addOnFailureListener(e ->
                Log.e("Firebase", "QR code upload failed", e)
        );
    }

    public void urlToBitmap(Context context, String url) {
        picassoTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                downloadedBitmap.setValue(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e("Picasso", "Error loading image", e);
                downloadedBitmap.setValue(null);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.get().load(url).into(picassoTarget);
    }

    public void clearDownloadedBitmap() {
        downloadedBitmap.setValue(null);
    }

    public MutableLiveData<Bitmap> getDownloadedBitmap() {
        return downloadedBitmap;
    }

    /**
     * Uploads an event document to Firestore.
     */
    private void uploadEvent(Event event) {
        if (event == null || event.getId() == null) {
            Log.e("Firebase", "Event or ID is null");
            return;
        }

        eventCollection.document(event.getId())
                .set(event)
                .addOnSuccessListener(aVoid ->
                        Log.d("Firebase", "Event added successfully"))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Error adding event", e));
    }

    /**
     * Real-time Firestore listener for events.
     */
    private void addSnapshotListener() {
        eventCollection.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", "Listen failed", error);
                return;
            }

            if (snapshots != null) {
                ArrayList<Event> events = new ArrayList<>();

                for (QueryDocumentSnapshot doc : snapshots) {
                    try {
                        Event event = doc.toObject(Event.class);

                        if (event.getId() == null) {
                            event.setId(doc.getId());
                        }

                        events.add(event);

                    } catch (Exception e) {
                        Log.e("Firestore Parse", "Error converting document", e);
                    }
                }

                eventList.setValue(events);
            }
        });
    }

    public Boolean saveImage(Context context, ImageView imageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "qr_code.png");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        }
        Uri imageUri = null;

        OutputStream outputStream = null;
        try {
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri == null) {
                throw new IOException("Failed to create a new MediaStore Record.");
            }
            outputStream = contentResolver.openOutputStream(imageUri);
            if (outputStream == null) {
                throw new IOException("Failed to get output stream.");
            }
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                throw new IOException("Failed to save bitmap.");
            }
            Log.d("ImageSave", "Image saved to gallery successfully: " + imageUri);
            return true;
        } catch (Exception e) {
            Log.e("ImageSave", "Error saving image", e);
            if (imageUri != null) {
                contentResolver.delete(imageUri, null, null);
            }
            return false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                Log.e("ImageSave", "Error closing output stream", e);
            }
        }
    }

    /**
     * Finds the event object from an event ID.
     */
    public void lookupEvent(String eventID, OnSuccessListener<Event> callback) {
        List<Event> events = eventList.getValue();

        // 1. Try to find it in the local list first (Instant)
        if (events != null) {
            for (Event event : events) {
                if (Objects.equals(event.getId(), eventID)) {
                    callback.onSuccess(event);
                    return;
                }
            }
        }

        // 2. If not found locally, fetch from Firestore (Asynchronous)
        db.collection("Events").document(eventID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        callback.onSuccess(event);
                    } else {
                        callback.onSuccess(null); // Event ID doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error looking up event", e);
                    callback.onSuccess(null);
                });
    }

}
