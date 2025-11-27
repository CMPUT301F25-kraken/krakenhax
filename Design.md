# KrakenHax – Design & Implementation Document  
*CMPUT 301 – Project Part 4*

---

## 1. Introduction

**KrakenHax** is an Android mobile application that manages event registrations through a waitlist and lottery-based system. The application supports three primary roles:

- **Entrants** – discover events, join waitlists, and respond to lottery results.
- **Organizers** – create and manage events, configure lotteries, and send notifications to participants.
- **Administrators** – monitor events, profiles, and notifications for auditing and system integrity.

Part 4 of the project focuses on:

- A full **notification subsystem** (winners, losers, cancellations, and broadcast messages).
- Integration of the **lottery workflow** with participant state transitions.
- Implementing **database-level tests** using Mockito.
- Implementing **UI tests** using Espresso and FragmentScenario.

The following sections describe the system architecture, key classes, detailed feature design, and the testing strategy.

---

## 2. System Overview

At a high level, KrakenHax manages the lifecycle of event participation:

1. **Entrant** discovers events on the home screen and views event details.
2. Entrant **joins the waitlist** for a chosen event. For some events, geolocation is required.
3. When registration closes, the **lottery** is run by the organizer to select winners from the waitlist.
4. The system generates and stores **Notification** objects indicating whether each entrant has **won** or **lost**.
5. Entrants receive notifications:
   - In a dedicated **Notifications** screen.
   - As Android system notifications (if they have not opted out).
6. Entrants can **accept** or **decline** their spot if selected, and can also cancel or withdraw.
7. Organizers can:
   - View categorized participant lists (waitlisted, winners, cancelled, etc.).
   - Send **group broadcast notifications** to specific participant groups.
8. Administrators can view an aggregated list of notifications for auditing.

The backend uses **Firebase Firestore** to store events, profiles, and notifications, and **Firebase Storage** to store event poster images and QR codes.

---

## 3. Architecture & Key Classes

KrakenHax follows a modular architecture with three main layers:

- **Domain Model** – Core entities of the system.
- **Application / UI Layer** – Activities, Fragments, and ViewModels.
- **Data Layer** – Firestore collections and simple repository classes for testable database access.

### 3.1 Domain Model

#### `Profile`

Represents an application user (entrant, organizer, or admin).

- Fields:
  - `id`, `username`, `email`, `phoneNumber`, `type` ("Entrant", "Organizer", "Admin")
  - `notificationsEnabled` (opt-in/out flag)
  - `latitude`, `longitude` (for geolocation-based events)
  - `history` (`List<Action>`) – records user actions (e.g., join, withdraw, accept).
- Responsibilities:
  - Store user metadata and preferences.
  - Maintain a history of actions.
  - Persist to Firestore.

#### `Event`

Represents an event that users can join.

- Fields:
  - Basic info: `id`, `title`, `eventDetails`, `location`, `categories`, `dateTime`, `timeframe`
  - Media & location: `poster`, `qrCodeURL`, `radius`, `useGeolocation`
  - Organizer: `orgId`
  - Lottery configuration: `winnerNumber`, `waitListCap`
  - Participant lists:
    - `waitList`, `wonList`, `lostList`, `cancelList`, `acceptList` (`List<Profile>`)
- Responsibilities:
  - Hold all event data and participant lists.
  - Support waitlist operations and cancellations.
  - Implement `drawLottery(...)` to select winners and update `wonList` / `lostList`.

#### `NotificationJ`

Represents an individual notification stored in Firestore.

- Fields:
  - `title`, `body`, `sender` (organizer/admin profile ID)
  - `recipient` (profile ID)
  - `eventID`
  - `timestamp`
  - `read` (boolean)
- Responsibilities:
  - Model a single notification for a user.
  - Map cleanly to/from Firestore documents.
  - Link notifications to events and senders.

#### `Action`

Represents an entry in a user’s history (e.g., “Join waitlist”, “Decline event”).

- Fields:
  - `description` (what action occurred)
  - `value` (optional associated value)
  - `eventId` (which event it relates to)
- Responsibilities:
  - Provide a concise, structured record of user actions.

---

### 3.2 Application Layer

#### `MainActivity`

- Hosts the navigation graph and bottom navigation.
- Holds the currently logged-in `Profile` (`currentUser`).
- Creates and registers the Android **NotificationChannel** used by the app.
- Handles runtime permissions for notifications (Android 13+).

#### `EventsFragment`

- Displays a real-time list of events via a RecyclerView.
- Listens to the `Events` collection in Firestore and updates the UI.
- Navigates to `EventFragment` when an event is selected.
- Listens to the current user’s notification subcollection
  (`Profiles/{id}/Notifications`) to trigger system notifications via `NotifyUser`.

#### `EventFragment`

Entrant-facing detailed event screen.

- Shows event details: title, description, poster, location, date/time, QR code.
- Manages entrant actions:
  - Join waitlist (with geolocation if required).
  - Withdraw from waitlist.
  - Accept/decline a spot if the user is in `wonList`.
- Updates event participant lists and writes changes back to Firestore.
- Updates the user’s `history` with `Action` objects.
- On **Decline**, moves the user to `cancelList` and creates a `NotificationJ` (“You cancelled your spot”).

#### `MyEventDetailsFragment`

Organizer-facing detailed event screen.

- Displays event details from the organizer’s perspective.
- Provides a **Run Lottery** button which:
  - Calls `Event.drawLottery(...)` using `winnerNumber` and the current pool.
  - Updates Firestore with the modified event.
  - Creates and stores `NotificationJ` objects for all winners and losers.

#### `EntrantInfoFragment`

Organizer participant management screen.

- Shows lists of entrants filtered by role:
  - Waitlisted, Won/Enrolled, Lost, Accepted, Cancelled.
- Allows organizers to:
  - Remove entrants from a list (with notifications).
  - Open a **broadcast overlay** to:
    - Select a group (e.g., Waitlisted, Enrolled, Cancelled).
    - Compose a message and send notifications to all group members.
- Uses `NotificationJ` and Firestore to persist these broadcast notifications.

#### `NotificationFragment`

Entrant notification inbox.

- Reads notifications from `Profiles/{id}/Notifications`.
- Displays them in a RecyclerView (newest first).
- On item click:
  - Uses `eventID` to fetch the related `Event` and navigates to `EventFragment`.

#### `AdminListFragment`

Admin notification audit screen.

- Displays notifications from a global collection or an admin-specific query.
- Resolves `sender` IDs to display human-readable organizer/admin names.
- Supports auditing which notifications were sent, by whom, and for which events.

#### `ProfileFragment`

Profile and preferences screen.

- Displays profile fields (username, email, phone number).
- Allows the user to edit and save profile information.
- Provides a switch for enabling/disabling notifications (`notificationsEnabled`).
- Writes changes back to the user’s Firestore document.
- Provides sign-out functionality.

---

### 3.3 Data Layer & Repositories

The application uses **Firebase Firestore** for persistence:

- `Events/{eventId}` – event documents.
- `Profiles/{profileId}` – profile documents.
- `Profiles/{profileId}/Notifications/{notificationId}` – per-user notifications.

To make database testing feasible, simple repository classes were introduced:

#### `EventRepository`

- Methods:
  - `saveEvent(Event event)` – writes the event to `Events/{eventId}`.
  - `deleteEvent(Event event)` – deletes `Events/{eventId}`.
- Used indirectly by fragments for abstraction and tested with Mockito.

#### `NotificationRepository`

- Methods:
  - `saveNotificationForProfile(NotificationJ notification)` – writes to
    `Profiles/{recipient}/Notifications/`.
- Responsible for correctly targeting the notification subcollection path.

---

### 3.4 Notification Utility

#### `NotifyUser`

- Wraps Android’s `NotificationManager` and Android notification APIs.
- Supports:
  - `sendNotification(Profile, String)` – sends a single system notification.
  - `sendBroadcast(List<Profile>, String)` – sends system notifications to multiple recipients.
- Checks `Profile.notificationsEnabled` before showing Android notifications, but notifications are still stored in Firestore even if OS notifications are disabled.

---

## 4. Detailed Feature Design (Part 4)

### 4.1 Lottery Workflow

The lottery workflow is centralized around the `Event` and `MyEventDetailsFragment`:

1. The organizer sets the **winner count** for each event (`winnerNumber`).
2. Entrants join the event’s **waitlist** via `EventFragment`.
3. When the organizer decides to run the lottery:
   - In `MyEventDetailsFragment`, the “Run Lottery” button invokes:
     ```java
     event.drawLottery(waitList, event.getWinnerNumber());
     ```
   - `drawLottery(...)`:
     - Randomly selects the configured number of winners from the waitlist.
     - Moves selected entrants into `wonList`.
     - Moves non-selected entrants into `lostList`.
4. The updated event is written to Firestore via `updateEventInFirestore(Event)`.

The lottery can be re-run (depending on application rules) to refill spots or handle cancellations by selecting from remaining participants.

### 4.2 Notification Workflow

The notification subsystem works with both **stored notifications** and **Android system notifications**.

#### 4.2.1 Win / Lose Notifications After Lottery

After `drawLottery(...)` executes in `MyEventDetailsFragment`:

- For each entrant in `wonList`:
  - A `NotificationJ` is created with a message such as “You won the lottery for [Event Title]”.
  - This notification is stored under:
    - `Profiles/{winnerId}/Notifications/{autoId}`.
- For each entrant in `lostList`:
  - A `NotificationJ` is created with a message such as “You were not selected for [Event Title]”.
  - Stored under the corresponding profile’s Notifications subcollection.

This implements:

- US 01.04.01 – Notify entrants when they are chosen (win).
- US 01.04.02 – Notify entrants when they are not chosen (lose).
- Backlog 57 – Notify entrants about win/lose status when the lottery completes.

#### 4.2.2 Entrant Cancellation Notifications

When an entrant **declines** their spot in `EventFragment`:

- The entrant is moved from `wonList` to `cancelList`.
- A `NotificationJ` is generated with a message such as “You cancelled your spot for [Event Title]”.
- This notification is stored under their `Profiles/{id}/Notifications` subcollection.

This implements:

- Backlog 77 – Notify entrants that they have cancelled for an event.

#### 4.2.3 Organizer Broadcast Notifications

In `EntrantInfoFragment`, organizers can send notifications to groups:

- Group selections:
  - Waitlisted entrants.
  - Selected entrants (winners / enrolled).
  - Cancelled entrants.
- For a chosen group:
  - The organizer composes a message in a UI overlay.
  - For each profile in the group:
    - A `NotificationJ` is created and stored under that profile’s notification subcollection.
    - Optionally, `NotifyUser` may be used to show a system notification.

This implements:

- US 02.07.01 – Send notifications to all waitlisted entrants.
- US 02.07.02 – Send notifications to all selected entrants.
- US 02.07.03 – Send notifications to all cancelled entrants.
- Backlog 71, 81, 89 – Organizer-driven broadcast features.

#### 4.2.4 Opt-Out Handling

Entrants can control notification behavior through `ProfileFragment`:

- A toggle switch binds to `Profile.notificationsEnabled`.
- When changed, this preference is updated in the user’s Firestore document.
- `NotifyUser.sendNotification(...)` checks this flag and suppresses the Android system notification if `notificationsEnabled` is `false`.
- The `NotificationJ` objects are still stored in Firestore so the user can view them later in the in-app inbox.

This implements:

- US 01.04.03 – Allow entrants to opt out of receiving notifications.

#### 4.2.5 System Notification Display

`EventsFragment` is responsible for listening to notification changes:

- A Firestore listener is attached to the current user’s `Profiles/{id}/Notifications` subcollection.
- When a new `NotificationJ` is detected:
  - `NotifyUser` builds and displays an Android system notification (if the user is opted in).
  - Tapping the notification can bring the user back into the app and, via `NotificationFragment`, to the related event.

Administrators can additionally inspect notifications via `AdminListFragment`, which reads aggregated notification data for auditing.

---

## 5. UML & CRC Cards

A UML class diagram was created using PlantUML to capture:

- Relationships between `Profile`, `Event`, `NotificationJ`, and `Action`.
- Dependencies between Fragments (`EventsFragment`, `EventFragment`, `MyEventDetailsFragment`, `EntrantInfoFragment`, `NotificationFragment`, `AdminListFragment`, `ProfileFragment`).
- The data repositories (`EventRepository`, `NotificationRepository`) and utility (`NotifyUser`).

CRC (Class Responsibility Collaborator) cards were created for key classes, including:

- Domain: `Profile`, `Event`, `NotificationJ`, `Action`
- Application: `MainActivity`, `EventsFragment`, `EventFragment`, `MyEventDetailsFragment`, `EntrantInfoFragment`, `NotificationFragment`, `AdminListFragment`, `ProfileFragment`
- Data: `EventRepository`, `NotificationRepository`
- Utility: `NotifyUser`

These artefacts document the responsibilities and collaborators for each major class and provide a concise summary of the system design.

---

## 6. Testing

Testing for Part 4 is split into three main categories: unit tests, database tests using Mockito, and UI tests using Espresso.

### 6.1 Unit Tests (Domain Logic)

Located under `app/src/test/java/com/kraken/krakenhax/`:

- `ProfileTest`
  - Verifies initialization, getters, and setters.
  - Tests validation (e.g., null/empty arguments).
  - Checks equality and `hashCode` consistency.
  - Ensures `toString()` contains meaningful fields (e.g., username, type).

- `EventTest`
  - Verifies title and category management.
  - Ensures the waitlist is initialized correctly.
  - Optionally verifies `drawLottery()` behavior (e.g., winner/loser counts).

These tests validate the correctness and robustness of the core domain model.

### 6.2 Database Tests (Mockito)

To fulfil the requirement for **database testing** without hitting real Firestore, repository classes are tested with Mockito:

- `EventRepositoryTest`
  - Mocks `FirebaseFirestore`, `CollectionReference`, and `DocumentReference`.
  - Verifies that `saveEvent(...)` correctly calls:
    - `db.collection("Events").document(eventId).set(event)`.
  - Verifies that `deleteEvent(...)` correctly calls:
    - `db.collection("Events").document(eventId).delete()`.

- `NotificationRepositoryTest`
  - Mocks the Firestore path `Profiles/{recipientId}/Notifications`.
  - Mocks the `Task<DocumentReference>` returned by `add(...)` to avoid null pointer exceptions.
  - Verifies that:
    - `db.collection("Profiles")`
    - `document(recipientId)`
    - `collection("Notifications")`
    - `add(notification)`
    are all invoked as expected.

These tests demonstrate that the application correctly targets the intended Firestore collections and documents, satisfying the TA’s database testing requirement.

### 6.3 UI Tests (Espresso and FragmentScenario)

Located under `app/src/androidTest/java/com/kraken/krakenhax/`:

- `ProfileFragmentTest`
  - Uses `FragmentScenario` to launch `ProfileFragment`.
  - Verifies that the notification toggle switch is displayed.
  - Ensures the toggle can be clicked (i.e., user can enable/disable notifications).

- `EventFragmentTest`
  - Creates a fake `Event` and passes it via a `Bundle` to `EventFragment`.
  - Launches `EventFragment` using `FragmentScenario`.
  - Verifies that:
    - The event title `TextView` is visible and shows the correct text.
    - The sign-up button is visible.

These UI tests validate the basic functionality of the primary user interaction screens relevant to Part 4 (profile preferences and event participation).

### 6.4 Manual Testing

In addition to automated tests, a manual test plan was executed to validate:

- Entrant flows:
  - Join/withdraw from waitlists.
  - Receive win/lose notifications after lottery.
  - Cancel an accepted spot and receive a cancellation notification.
  - Toggle notification preferences and confirm OS notifications honour this setting.

- Organizer flows:
  - Create events and set `winnerNumber`.
  - Run the lottery and verify participant lists (waitList, wonList, lostList).
  - Send broadcast notifications to waitlisted, selected, and cancelled entrants.
  - Remove entrants and verify resulting notifications.

- Admin flows:
  - Open the admin notification view and inspect stored `NotificationJ` objects.

---

## 7. Requirements Traceability

The following table summarizes how key user stories and backlog items are implemented and tested.

| Requirement / Backlog Item                                             | Implementation (Code)                                                                                                  | Verification (Tests)                                               |
|------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------|
| US 01.04.01 – Entrant notified when they **win** the lottery          | `Event.drawLottery`, `MyEventDetailsFragment` lottery button, `NotificationJ` stored under Profiles/{id}/Notifications | `EventRepositoryTest`, `NotificationRepositoryTest`, manual tests |
| US 01.04.02 – Entrant notified when they **lose** the lottery         | Same as above, using `lostList` branch                                                                                 | `NotificationRepositoryTest`, manual tests                         |
| US 01.04.03 – Entrant can opt out of notifications                    | `Profile.notificationsEnabled`, switch in `ProfileFragment`, check in `NotifyUser.sendNotification`                    | `ProfileTest`, `ProfileFragmentTest`                               |
| US 02.05.01 – Organizer notifies chosen entrants to sign up           | `EntrantInfoFragment` broadcast to selected group (`wonList` / enrolled entrants)                                      | `NotificationRepositoryTest`, manual tests                         |
| US 02.05.02 – Organizer configures number of winners                  | `Event.winnerNumber`, set in event creation, used in `drawLottery`                                                     | `EventTest` (lottery behavior), manual tests                       |
| US 02.06.01 – Organizer views list of chosen entrants                 | `MyEventDetailsFragment` and `EntrantInfoFragment` display `wonList` and other lists                                   | Manual tests (organizer flows)                                     |
| Backlog 56 – Store which organizer sent notifications                 | `NotificationJ.sender` set to organizer/admin profile ID in lottery and broadcast flows                                | `NotificationRepositoryTest`, admin manual testing                 |
| Backlog 57 – Notify entrants when lottery completes (win/lose)        | `MyEventDetailsFragment` creates `NotificationJ` for both winners and losers                                           | Same as US 01.04.01/02                                             |
| Backlog 59 – Store notifications received by entrants                 | Notifications stored in `Profiles/{profileId}/Notifications` using `NotificationRepository`                            | `NotificationRepositoryTest`                                       |
| Backlog 71/81/89 – Organizer sends group notifications                | Group selection + broadcast in `EntrantInfoFragment`                                                                   | `NotificationRepositoryTest`, manual tests                         |
| Backlog 77 – Notify entrants that they have cancelled for an event    | Decline logic in `EventFragment` moves user to `cancelList` and stores a `NotificationJ` for the entrant               | `EventRepositoryTest`, manual tests                                |

---

## 8. Limitations & Future Work

- **Authentication** is limited; integration with a full authentication provider could be added for production use.
- **Notification content** is currently simple text. Future work could:
  - Add richer templates and localized messages.
  - Include actions (e.g., buttons) within notifications.
- **Lottery logic** could be extended to handle more complex fairness constraints or tie-breaker rules.
- **Admin tools** are basic; more advanced dashboards and analytics for engagement and fairness could be added.

---

## 9. Conclusion

KrakenHax implements a complete event registration system using waitlists, lotteries, and a robust notification pipeline. Part 4 focused on connecting the lottery mechanism with real-time notifications, allowing organizers to communicate with entrants and enabling entrants to control their notification preferences.

The design is supported by an explicit domain model, well-defined responsibilities at the fragment and repository layers, and a combination of unit, database, and UI tests that contribute to overall system reliability and maintainability.
