package com.kraken.krakenhax;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Database-layer tests for NotificationRepository using Mockito.
 */
@RunWith(MockitoJUnitRunner.class)
public class NotificationRepositoryTest {

    @Mock
    FirebaseFirestore mockDb;

    @Mock
    CollectionReference mockProfilesCollection;

    @Mock
    DocumentReference mockProfileDoc;

    @Mock
    CollectionReference mockNotificationsCollection;

    @Mock
    Task<DocumentReference> mockTask;

    private NotificationRepository notificationRepository;
    private Notification notification;

    /**
     * Sets up the NotificationRepository and Firestore mocks before each test.
     */
    @Before
    public void setUp() {
        notificationRepository = new NotificationRepository(mockDb);

        notification = new Notification(
                "Test Title",
                "Body",
                "sender123",
                null,
                "eventX",
                "recipient456",
                false
        );

        // Mock the Firestore path: Profiles/{id}/Notifications
        when(mockDb.collection("Profiles")).thenReturn(mockProfilesCollection);
        when(mockProfilesCollection.document("recipient456")).thenReturn(mockProfileDoc);
        when(mockProfileDoc.collection("Notifications")).thenReturn(mockNotificationsCollection);

        // Now, when add(...) is called, return a mocked Task instead of null
        when(mockNotificationsCollection.add(notification)).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
    }

    /**
     * Verifies that saveNotificationForProfile uses the correct Firestore collection path.
     */
    @Test
    public void testSaveNotificationForProfileUsesCorrectPath() {
        notificationRepository.saveNotificationForProfile(notification);

        verify(mockDb).collection("Profiles");
        verify(mockProfilesCollection).document("recipient456");
        verify(mockProfileDoc).collection("Notifications");
        verify(mockNotificationsCollection).add(notification);
    }

}
