package com.kraken.krakenhax;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Database-layer unit tests for {@link EventRepository} using Mockito.
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

    /**
     * Initializes the repository under test and configures Firestore mocks
     * before each test is executed.
     */
    @Before
    public void setUp() {
        eventRepository = new EventRepository(mockDb);

        // Simplified Event; only id is important for this test
        testEvent = new Event("Test Event");
        testEvent.setId("event123");

        when(mockDb.collection("Events")).thenReturn(mockEventsCollection);
        when(mockEventsCollection.document("event123")).thenReturn(mockDocRef);
    }

    /**
     * Ensures that saving an event writes it to the "Events" collection
     * using the event ID as the document ID.
     */
    @Test
    public void testSaveEventWritesToEventsCollection() {
        eventRepository.saveEvent(testEvent);

        verify(mockDb).collection("Events");
        verify(mockEventsCollection).document("event123");
        verify(mockDocRef).set(testEvent);
    }

    /**
     * Ensures that deleting an event removes it from the "Events" collection
     * using the event ID as the document ID.
     */
    @Test
    public void testDeleteEventDeletesFromEventsCollection() {
        eventRepository.deleteEvent(testEvent);

        verify(mockDb).collection("Events");
        verify(mockEventsCollection).document("event123");
        verify(mockDocRef).delete();
    }

}
