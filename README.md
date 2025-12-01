# KrakenHax

**CMPUT 301 – Project Part 4**

---

## Overview

**KrakenHax** is an Android mobile application for managing event registrations using a **waitlist + lottery** system.

- **Entrants** discover events and join waitlists.
- **Organizers** configure events, run lotteries, and notify entrants.
- **Administrators** oversee events, profiles, and notification activity.

---

## Team Members

- Amaan Sayyad  
- Logan Pope  
- Markus Henze  
- Jacob McCormick  
- William Grant  
- Shahbaz Qureshi  

---

## Summary of System Flow

1. **Entrant** browses or filters events on the home screen.
2. Entrant **joins the waitlist** for a chosen event (optionally with geolocation).
3. At registration close, the **lottery system** selects winners and updates:
   - `wonList`, `lostList`, `waitList`, `cancelList`, `acceptList`.
4. The system stores **Notification** objects in Firestore and shows:
   - In-app notification list.
   - Android system notifications (if the user hasn’t opted out).
5. **Organizers** view and manage participant lists, send broadcast messages, and rerun lotteries.
6. **Admins** inspect notifications and maintain overall system integrity.

---

## Core Features

### Entrant Features

- Browse all upcoming events.
- View event details (title, description, poster, QR code, date/time).
- Join or withdraw from event waitlists.
- Accept or decline a spot if selected in the lottery.
- Receive notifications when:
  - They **win** the lottery and are invited to participate.
  - They **lose** the lottery.
  - They are **removed** from an event.
  - They **cancel** their own spot.
- View all notifications in a dedicated **Notifications** screen.
- Toggle **opt-in/opt-out** of notifications from the **Profile** screen.

### Organizer Features

- Create and edit events (including timeframe, QR code, and geolocation requirements).
- Configure **number of winners** for the event lottery.
- Run the **lottery** from the organizer event details screen.
- See categorized participant lists:
  - Waitlisted, Won/Enrolled, Lost, Accepted, Cancelled.
- Send **broadcast notifications** to:
  - All waitlisted entrants.
  - All selected entrants.
  - All cancelled entrants.
- Remove entrants from lists (with appropriate notifications).

### Admin Features

- View global notification list for auditing (who sent what to whom).
- Monitor events and user activity.
- Maintain higher-level oversight of system behavior.

---

## Architecture (High-Level)

- **Language:** Java  
- **UI Layer:** Activities + Fragments (e.g., `EventsFragment`, `EventFragment`, `MyEventDetailsFragment`, `EntrantInfoFragment`, `NotificationFragment`, `ProfileFragment`)  
- **ViewModels:**  
  - `EventViewModel` (QR bitmap loading)  
  - `ProfileViewModel` (list of profiles)  
- **Domain Models:**  
  - `Profile`, `Event`, `NotificationJ`, `Action`  
- **Repositories (for testing Firestore):**  
  - `EventRepository` (save/delete events)  
  - `NotificationRepository` (store notifications under profiles)  
- **Backend:** Firebase Firestore + Firebase Storage  
- **Notifications:**  
  - Local OS notifications via `NotifyUser` and Android `NotificationManager`  
  - Stored notification objects via `NotificationJ` and Firestore subcollections

---

## Notification & Lottery Flow (Part 4)

- When an organizer runs the lottery from **`MyEventDetailsFragment`**:
  - `Event.drawLottery(...)` updates `wonList` and `lostList`.
  - For each winner:
    - A `NotificationJ` is created (e.g., **“You won the lottery!”**) and stored under  
      `Profiles/{winnerId}/Notifications`.
  - For each loser:
    - A `NotificationJ` is created (e.g., **“You were not selected.”**).

- When an entrant **declines** a spot in **`EventFragment`**:
  - They are moved to `cancelList`.
  - A `NotificationJ` (**“You cancelled your spot”**) is stored under their notifications.

- `EventsFragment` listens for new notifications for the **current user**:
  - Shows Android system notifications using `NotifyUser`.
  - Keeps the in-app notifications list consistent.

- Entrants can **opt out** using a toggle in **`ProfileFragment`**, which updates `Profile.notificationsEnabled`.  
  `NotifyUser` checks this flag and skips OS notifications if disabled.

---

## Testing

The project includes **three layers of testing**:

### 1. Unit Tests (Domain Logic)

Located under: `app/src/test/java/com/kraken/krakenhax/`

- `ProfileTest`
  - Verifies construction, setters, validation, equality, hashCode, and `toString()`.
- `EventTest`
  - Verifies title changes, category add/remove behavior, and waitlist initialization.
- (Optional) `ExampleUnitTest`
  - Simple sanity test (`2 + 2 = 4`).

### 2. Database Tests (Mockito)

To satisfy the TA requirement for database testing, Firestore operations are wrapped in **repositories** and tested using **Mockito**:

- **Repositories (production code)**
  - `EventRepository`
    - `saveEvent(Event)` → writes to `Events/{eventId}`.
    - `deleteEvent(Event)` → deletes `Events/{eventId}`.
  - `NotificationRepository`
    - `saveNotificationForProfile(NotificationJ)` → writes to `Profiles/{recipientId}/Notifications`.

- **Tests**
  - `EventRepositoryTest`
    - Uses `MockitoJUnitRunner` and mocks `FirebaseFirestore`.
    - Verifies that `saveEvent()` calls `db.collection("Events").document(id).set(event)`.
    - Verifies that `deleteEvent()` calls `db.collection("Events").document(id).delete()`.
  - `NotificationRepositoryTest`
    - Mocks the Firestore path: `Profiles -> document(recipientId) -> collection("Notifications")`.
    - Verifies `add(notification)` is called on the correct collection.
    - Mocks the returned `Task` object to avoid NPE on `addOnSuccessListener`.

This demonstrates **database-layer behavior** without needing a real Firestore instance.

### 3. UI Tests (Espresso / Instrumentation)

Located under: `app/src/androidTest/java/com/kraken/krakenhax/`

- `ProfileFragmentTest`
  - Uses `FragmentScenario` to launch `ProfileFragment`.
  - Verifies that the **notification Switch** is visible.
  - Ensures the switch can be toggled with Espresso.

- `EventFragmentTest`
  - Builds a fake `Event` and launches `EventFragment` with it via arguments.
  - Verifies that:
    - The event title `TextView` is displayed and shows the correct text.
    - The `Sign Up` button is visible.

These tests validate that key **UI elements** for notifications and events behave as expected.

---
