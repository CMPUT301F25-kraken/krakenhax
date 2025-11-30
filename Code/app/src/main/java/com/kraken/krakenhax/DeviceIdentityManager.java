package com.kraken.krakenhax;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.HashMap;
import java.util.Map;


/**
 * Manages linking a physical device (via Firebase Installation ID) to a user account and
 * restoring an existing session if present.
 */
public class DeviceIdentityManager {
    private static final String TAG = "DeviceIdentityManager";

    /**
     * Simple callback interface for async user ID fetch.
     */
    public interface LinkedUserCallback {
        /**
         * Called with the linked user ID found for this device, or null if no link exists or on error.
         * @param userId the linked user ID, or null
         */
        void onResult(String userId); // userId will be null if not found or on error
    }

    /**
     * Returns a Task that resolves to the Firebase Installation ID for this app instance.
     */
    public static Task<String> getFIDAsync() {
        return FirebaseInstallations.getInstance().getId();
    }

    /**
     * Fetch the userId linked to this device (if any) and provide it via callback.
     * @param callback receives the userId or null if none/error.
     */
    public static void fetchLinkedUserId(LinkedUserCallback callback) {
        getFIDAsync().addOnCompleteListener(fidTask -> {
            if (!fidTask.isSuccessful()) {
                Log.e(TAG, "Failed to get FID", fidTask.getException());
                callback.onResult(null);
                return;
            }
            String fid = fidTask.getResult();
            if (fid == null) {
                callback.onResult(null);
                return;
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Devices").document(fid);
            docRef.get().addOnCompleteListener(deviceTask -> {
                if (!deviceTask.isSuccessful()) {
                    Log.e(TAG, "Failed to get device document", deviceTask.getException());
                    callback.onResult(null);
                    return;
                }
                DocumentSnapshot snap = deviceTask.getResult();
                if (snap != null && snap.exists()) {
                    callback.onResult(snap.getString("userId"));
                } else {
                    callback.onResult(null);
                }
            });
        });
    }

    /**
     * Update (or create) the device -> user link. Sets lastSeen every call; sets createdAt only
     * on first creation. Merge semantics to avoid overwriting other future fields.
     * @param userId The user ID to link this device to.
     */
    public static void updateAccountLink(String userId) {
        if (userId == null) {
            Log.w(TAG, "updateAccountLink called with null userId");
            return;
        }
        getFIDAsync().addOnCompleteListener(fidTask -> {
            if (!fidTask.isSuccessful()) {
                Log.e(TAG, "Failed to obtain FID for linking", fidTask.getException());
                return;
            }
            String fid = fidTask.getResult();
            if (fid == null) {
                Log.e(TAG, "FID is null, cannot link device");
                return;
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Devices").document(fid);

            docRef.get().addOnCompleteListener(getTask -> {
                if (!getTask.isSuccessful()) {
                    Log.e(TAG, "Failed to fetch existing device doc", getTask.getException());
                    return;
                }
                DocumentSnapshot existing = getTask.getResult();

                Map<String, Object> deviceData = new HashMap<>();
                deviceData.put("userId", userId);
                deviceData.put("fid", fid);
                deviceData.put("lastSeen", Timestamp.now());
                if (existing == null || !existing.exists()) {
                    deviceData.put("createdAt", Timestamp.now());
                }

                docRef.set(deviceData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Device linked with ID: " + docRef.getId()))
                        .addOnFailureListener(e -> Log.e(TAG, "Error linking device", e));
            });
        });
    }

    /**
     * Clear the association (e.g., on logout). Keeps the document so analytics like createdAt persist.
     * Returns a Task that completes when the server update finishes.
     */
    public static Task<Void> clearAccountLinkAsync() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        getFIDAsync().addOnCompleteListener(fidTask -> {
            if (!fidTask.isSuccessful()) {
                Exception ex = fidTask.getException() != null ? fidTask.getException() : new RuntimeException("Failed to get FID");
                Log.e(TAG, "Failed to obtain FID for clearing", ex);
                tcs.setException(ex);
                return;
            }
            String fid = fidTask.getResult();
            if (fid == null) {
                Exception ex = new IllegalStateException("FID null, cannot clear link");
                Log.e(TAG, "FID null, cannot clear link");
                tcs.setException(ex);
                return;
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Devices").document(fid);
            Map<String, Object> deviceData = new HashMap<>();
            deviceData.put("userId", null);
            deviceData.put("lastSeen", Timestamp.now());
            docRef.set(deviceData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Device link cleared for FID: " + fid);
                        tcs.setResult(null);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to clear device link", e);
                        tcs.setException(e);
                    });
        });
        return tcs.getTask();
    }

    /**
     * Convenience fire-and-forget wrapper around {@link #clearAccountLinkAsync()}.
     * Use when the caller doesn't need to await completion.
     */
    public static void clearAccountLink() {
        clearAccountLinkAsync();
    }

}