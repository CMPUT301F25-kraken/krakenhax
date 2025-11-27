package com.kraken.krakenhax;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;

/**
 * Database-layer unit tests for EventRepository using Mockito.
 * Verifies Firestore is called with the correct collection and document IDs.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventRepositoryTest {

    @Mock
    FirebaseFirestore mockDb;

    @Mock
    CollectionReference mockEventsCollection;

    @Mock
    DocumentReference mockDocRef;

    private EventRepository eventRepository;
    private Event testEvent;

    @Before
    public void setUp() {
        eventRepository = new EventRepository(mockDb);

        // Simplified Event; only id is important for this test
        testEvent = new Event("Test Event");
        testEvent.setId("event123");

        when(mockDb.collection("Events")).thenReturn(mockEventsCollection);
        when(mockEventsCollection.document("event123")).thenReturn(mockDocRef);
    }

    @Test
    public void testSaveEventWritesToEventsCollection() {
        eventRepository.saveEvent(testEvent);

        verify(mockDb).collection("Events");
        verify(mockEventsCollection).document("event123");
        verify(mockDocRef).set(testEvent);
    }

    @Test
    public void testDeleteEventDeletesFromEventsCollection() {
        eventRepository.deleteEvent(testEvent);

        verify(mockDb).collection("Events");
        verify(mockEventsCollection).document("event123");
        verify(mockDocRef).delete();
    }
}
