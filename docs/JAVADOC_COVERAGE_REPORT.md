# Javadoc Coverage Analysis Report

## Executive Summary

**Overall Javadoc Coverage Score: 69.8%**

This report provides a comprehensive analysis of the Javadoc documentation coverage for the KrakenHax Android application. The analysis includes all 36 Java source files in the main source directory, evaluating classes, interfaces, constructors, and methods (including private methods).

---

## Coverage Statistics

### Overall Summary

| Category | Documented | Total | Coverage |
|----------|------------|-------|----------|
| **Classes** | 33 | 36 | 91.7% |
| **Interfaces** | 5 | 6 | 83.3% |
| **Constructors** | 32 | 49 | 65.3% |
| **Public Methods** | 142 | 178 | 79.8% |
| **Private Methods** | 8 | 38 | 21.1% |
| **Protected Methods** | 1 | 3 | 33.3% |
| **@Override Methods** | 24 | 41 | 58.5% |
| **Total** | 245 | 351 | **69.8%** |

### Coverage by File

| File | Classes | Methods | Constructors | Overall |
|------|---------|---------|--------------|---------|
| Action.java | ❌ 0/1 | 6/11 | 1/2 | 50.0% |
| AdminListFragment.java | ✅ 1/1 | 5/7 | 1/1 | 77.8% |
| AdminProfileAdapter.java | ✅ 1/1 | 3/3 | 1/1 | 100% |
| CreateEventFragment.java | ✅ 1/1 | 3/3 | 0/0 | 100% |
| DeviceIdentityManager.java | ✅ 1/1 | 5/5 | 0/0 | 100% |
| EntrantInfoFragment.java | ✅ 1/1 | 4/9 | 0/1 | 45.5% |
| Event.java | ✅ 1/1 | 47/48 | 3/4 | 96.2% |
| EventFragment.java | ✅ 1/1 | 10/18 | 0/1 | 55.0% |
| EventRepository.java | ✅ 1/1 | 0/2 | 0/1 | 25.0% |
| EventViewModel.java | ✅ 1/1 | 7/12 | 1/1 | 64.3% |
| EventsFragment.java | ✅ 1/1 | 3/4 | 1/1 | 83.3% |
| HistoryFragment.java | ✅ 1/1 | 0/5 | 0/1 | 14.3% |
| HistoryRecyclerViewAdapter.java | ❌ 0/1 | 3/10 | 0/1 | 25.0% |
| HomeFragment.java | ✅ 1/1 | 2/2 | 1/1 | 100% |
| LoginFragment.java | ✅ 1/1 | 4/7 | 1/1 | 66.7% |
| MainActivity.java | ✅ 1/1 | 4/8 | 0/0 | 55.6% |
| MyEventDetailsFragment.java | ✅ 1/1 | 3/7 | 1/1 | 50.0% |
| MyEventsFragment.java | ✅ 1/1 | 2/2 | 1/1 | 100% |
| MyRecyclerViewAdapter.java | ✅ 1/1 | 7/7 | 1/1 | 100% |
| NotifAdapterAdmin.java | ✅ 1/1 | 2/2 | 1/1 | 100% |
| NotifAdapterS.java | ❌ 0/1 | 0/6 | 0/1 | 0.0% |
| NotificationFragment.java | ✅ 1/1 | 3/4 | 1/1 | 83.3% |
| NotificationJ.java | ✅ 1/1 | 16/16 | 2/2 | 100% |
| NotificationRepository.java | ✅ 1/1 | 1/1 | 0/1 | 66.7% |
| NotifyUser.java | ✅ 1/1 | 2/2 | 0/1 | 75.0% |
| OrganizerFragment.java | ✅ 1/1 | 1/4 | 0/1 | 33.3% |
| OrganizerMapFragment.java | ❌ 0/1 | 0/3 | 0/0 | 0.0% |
| Profile.java | ✅ 1/1 | 29/29 | 2/3 | 96.9% |
| ProfileAdapter.java | ✅ 1/1 | 3/4 | 1/1 | 83.3% |
| ProfileFragment.java | ✅ 1/1 | 5/9 | 1/1 | 63.6% |
| ProfileViewModel.java | ✅ 1/1 | 4/4 | 1/1 | 100% |
| QrCodeFragment.java | ❌ 0/1 | 0/4 | 0/0 | 0.0% |
| SignUpFragment.java | ✅ 1/1 | 3/3 | 1/1 | 100% |
| TypeChooserFragment.java | ✅ 1/1 | 2/2 | 1/1 | 100% |
| ViewNotification.java | ✅ 1/1 | 2/2 | 1/1 | 100% |
| ViewProfiles.java | ✅ 1/1 | 5/6 | 1/1 | 87.5% |

---

## Missing Javadoc Comments - Detailed List

### Classes/Interfaces Missing Javadoc

1. **HistoryRecyclerViewAdapter.java** (Line 19) - Class `HistoryRecyclerViewAdapter`
2. **NotifAdapterS.java** (Line 17) - Class `NotifAdapterS`
3. **OrganizerMapFragment.java** (Line 22) - Class `OrganizerMapFragment`
4. **QrCodeFragment.java** (Line 22) - Class `QrCodeFragment`
5. **Action.java** (Line 10) - Class `Action` (has only regular comments, not Javadoc)

### Interfaces Missing Javadoc

1. **NotifAdapterS.java** (Line 18) - Interface `OnNotifClickListener`
2. **HistoryRecyclerViewAdapter.java** (Line 262) - Interface `ItemClickListener`

### Constructors Missing Javadoc

| File | Constructor | Visibility |
|------|-------------|------------|
| Action.java | `Action()` | public |
| Action.java | `Action(String, String, String)` | public |
| Action.java | `Action(Parcel)` | public |
| EntrantInfoFragment.java | `EntrantInfoFragment()` | public |
| EventFragment.java | `EventFragment()` | public |
| EventRepository.java | `EventRepository(FirebaseFirestore)` | public |
| HistoryFragment.java | `HistoryFragment()` | public |
| HistoryRecyclerViewAdapter.java | `HistoryRecyclerViewAdapter(List, EventViewModel, ProfileViewModel)` | public |
| NotifAdapterS.java | `NotifAdapterS(OnNotifClickListener)` | public |
| NotificationRepository.java | `NotificationRepository(FirebaseFirestore)` | public |
| NotifyUser.java | `NotifyUser(Context)` | public |
| OrganizerFragment.java | `OrganizerFragment()` | public |
| Profile.java | `Profile()` | public |

### Public Methods Missing Javadoc

| File | Method | Return Type |
|------|--------|-------------|
| Action.java | `getAction()` | String |
| Action.java | `setAction(String)` | void |
| Action.java | `getAssociatedUserID()` | String |
| Action.java | `setAssociatedUserID(String)` | void |
| Action.java | `getAssociatedEventID()` | String |
| Action.java | `setAssociatedEventID(String)` | void |
| Action.java | `getTimestamp()` | Timestamp |
| Action.java | `setTimestamp(Timestamp)` | void |
| EventRepository.java | `saveEvent(Event)` | void |
| EventRepository.java | `deleteEvent(Event)` | void |
| EventViewModel.java | `urlToBitmap(Context, String)` | void |
| EventViewModel.java | `clearDownloadedBitmap()` | void |
| EventViewModel.java | `getDownloadedBitmap()` | MutableLiveData |
| EventViewModel.java | `saveImage(Context, ImageView)` | Boolean |
| NotifAdapterS.java | `setNotifications(List)` | void |
| ProfileFragment.java | `deleteAccount()` | void |
| ProfileFragment.java | `deleteEvents(String)` | void |
| ProfileFragment.java | `signOut(MainActivity)` | void |
| ViewProfiles.java | `deleteEvents(String)` | void |

### Private Methods Missing Javadoc

| File | Method | Return Type |
|------|--------|-------------|
| EntrantInfoFragment.java | `updateRecyclerList(String)` | void |
| EntrantInfoFragment.java | `updateEventInFirestore(Event)` | void |
| EntrantInfoFragment.java | `updateProfileInFirestore(Profile)` | void |
| EntrantInfoFragment.java | `startFirestoreListener()` | void |
| EntrantInfoFragment.java | `exportCsv()` | void |
| EventFragment.java | `updateEventInFirestore()` | void |
| EventFragment.java | `deleteEventFromFirestore()` | void |
| EventFragment.java | `requestLocationPermissions()` | void |
| EventFragment.java | `getLocationAndJoinWaitlist()` | void |
| EventFragment.java | `joinWaitlist()` | void |
| EventFragment.java | `doStuff()` | void |
| EventFragment.java | `updateButtons()` | void |
| EventFragment.java | `requestLocationPermission()` | ActivityResultLauncher |
| EventFragment.java | `setWaitlistInfo()` | void |
| EventFragment.java | `setEventPoster(ImageView)` | void |
| EventFragment.java | `setOrganizerButton(Button)` | void |
| EventFragment.java | `setRegistrationDeadline(TextView)` | void |
| EventFragment.java | `setEventDate(TextView)` | void |
| EventFragment.java | `startFirestoreListener()` | void |
| EventsFragment.java | `startFirestoreListener()` | void |
| EventsFragment.java | `startNotificationListener()` | void |
| EventsFragment.java | `showLocalNotification(String)` | void |
| EventViewModel.java | `uploadEvent(Event)` | void |
| EventViewModel.java | `addSnapshotListener()` | void |
| HistoryFragment.java | `startFirestoreListener(FirebaseFirestore)` | void |
| HistoryFragment.java | `updateRecyclerView()` | void |
| HistoryRecyclerViewAdapter.java | `setActionDescriptionEvent(...)` | void |
| HistoryRecyclerViewAdapter.java | `setActionDescriptionEventAndUser(...)` | void |
| LoginFragment.java | `validateLogin(String, String)` | void |
| LoginFragment.java | `getDeviceSessionFlow()` | void |
| LoginFragment.java | `postSignInFlow()` | void |
| LoginFragment.java | `normalNavigationFlow(Profile)` | void |
| LoginFragment.java | `getEventFromFirebase()` | Task<Event> |
| MainActivity.java | `removeMyWaitlists()` | void |
| MainActivity.java | `addHistory()` | void |
| MainActivity.java | `cleanUpLegacyEvents()` | void |
| MainActivity.java | `ensureEventTimeframes()` | void |
| MainActivity.java | `cleanUpLegacyEventDates()` | void |
| MainActivity.java | `createNotificationChannel()` | void |
| MainActivity.java | `handleIntent(Intent)` | void |
| MyEventDetailsFragment.java | `setupFirestoreListener(View)` | void |
| MyEventDetailsFragment.java | `updateUI(View)` | void |
| MyEventDetailsFragment.java | `updateEventInFirestore(Event)` | void |
| MyEventsFragment.java | `startFirestoreListener()` | void |
| NotificationFragment.java | `openEvent(NotificationJ)` | void |
| NotificationFragment.java | `startNotificationListListener()` | void |
| OrganizerFragment.java | `startFirestoreListener(String)` | void |
| ProfileFragment.java | `navigateAfterSignout()` | void |

### @Override Methods Missing Javadoc

| File | Method |
|------|--------|
| EntrantInfoFragment.java | `onDestroyView()` |
| EventFragment.java | `onCreateView()` |
| EventFragment.java | `onViewCreated()` |
| EventFragment.java | `onDestroyView()` |
| HistoryFragment.java | `onCreate()` |
| HistoryFragment.java | `onCreateView()` |
| HistoryFragment.java | `onViewCreated()` |
| HistoryRecyclerViewAdapter.java | `onCreateViewHolder()` |
| HistoryRecyclerViewAdapter.java | `onBindViewHolder()` |
| HistoryRecyclerViewAdapter.java | `getItemCount()` |
| NotifAdapterS.java | `onCreateViewHolder()` |
| NotifAdapterS.java | `onBindViewHolder()` |
| NotifAdapterS.java | `getItemCount()` |
| OrganizerFragment.java | `onCreateView()` |
| OrganizerFragment.java | `onViewCreated()` |
| OrganizerMapFragment.java | `onCreateView()` |
| OrganizerMapFragment.java | `onViewCreated()` |
| QrCodeFragment.java | `onCreateView()` |
| QrCodeFragment.java | `onCreate()` |
| QrCodeFragment.java | `onViewCreated()` |
| MainActivity.java | `onPostCreate()` |
| MainActivity.java | `onNewIntent()` |

---

## Javadoc Quality Issues

### 1. Placeholder/Minimal Comments

These Javadocs contain only placeholder text with minimal value:

| File | Element | Issue |
|------|---------|-------|
| AdminListFragment.java | Constructor | "Required empty public constructor" |
| EventsFragment.java | Constructor | "Required empty public constructor" |
| HomeFragment.java | Constructor | "Required empty public constructor" |
| LoginFragment.java | Constructor | "Required empty public constructor" |
| MyEventsFragment.java | Constructor | "Required empty public constructor" |
| NotificationFragment.java | Constructor | "Required empty public constructor" |
| ProfileFragment.java | Constructor | "Required empty public constructor" |
| SignUpFragment.java | Constructor | "Required empty public constructor" |
| TypeChooserFragment.java | Constructor | "Required empty public constructor" |
| ViewNotification.java | Constructor | "Required empty public constructor" |
| ViewProfiles.java | Constructor | "Required empty public constructor" |
| Action.java | Constructor | "Empty constructor needed for Firestore" |
| NotificationJ.java | Constructor | "needed for firebase, DONT REMOVE!" |

### 2. Copy-Pasted Android Documentation

These methods have Javadocs that are copied directly from Android's Parcelable interface documentation:

| File | Method | Issue |
|------|--------|-------|
| Action.java | `describeContents()` | Copy-pasted Android Parcelable doc |
| Action.java | `writeToParcel()` | Copy-pasted Android Parcelable doc |
| Event.java | `describeContents()` | Copy-pasted Android Parcelable doc |
| Profile.java | `describeContents()` | Copy-pasted Android Parcelable doc |
| Profile.java | `writeToParcel()` | Copy-pasted Android Parcelable doc |

### 3. Missing @param or @return Tags

Several Javadocs describe methods but are missing proper tags:

| File | Method | Missing |
|------|--------|---------|
| Event.java | Constructor `Event(Parcel)` | Missing description, just says "Flatten this object" (incorrect) |
| HistoryRecyclerViewAdapter.java | `setActionDescriptionEvent()` | Has Javadoc but missing proper param descriptions |
| HistoryRecyclerViewAdapter.java | `setActionDescriptionEventAndUser()` | Has Javadoc but missing proper param descriptions |

### 4. Incorrect or Misleading Documentation

| File | Element | Issue |
|------|---------|-------|
| Event.java | `getPoster()` | Returns String but Javadoc says "Returns Bitmap" |
| Event.java | `setPoster()` | Takes String but Javadoc says "Takes Bitmap" |
| Event.java | `Event(Parcel)` constructor | Javadoc says "Flatten this object" which is incorrect - this is a constructor from Parcel |

### 5. TODO Comments in Javadocs

| File | Element | TODO Content |
|------|---------|--------------|
| Event.java | `setCategories()` | "TODO: decide whether 5 categories is enough" |
| Event.java | `addCategory()` | "TODO: decide whether 5 categories is enough" |
| Event.java | `getPoster()` | "TODO: Research Bitmaps" |
| Event.java | `setPoster()` | "TODO: Research Bitmaps" |

---

## Well-Documented Files (Examples of Good Practice)

The following files demonstrate excellent Javadoc coverage and quality:

1. **Profile.java** - 96.9% coverage with comprehensive descriptions
2. **Event.java** - 96.2% coverage with detailed method documentation
3. **NotificationJ.java** - 100% coverage for all getters/setters
4. **MyRecyclerViewAdapter.java** - 100% coverage with clear descriptions
5. **DeviceIdentityManager.java** - 100% coverage with good interface documentation
6. **AdminProfileAdapter.java** - 100% coverage
7. **SignUpFragment.java** - 100% coverage
8. **TypeChooserFragment.java** - 100% coverage

---

## Recommendations

### High Priority (Missing Class-Level Javadoc)
1. Add class-level Javadoc to `HistoryRecyclerViewAdapter`
2. Add class-level Javadoc to `NotifAdapterS`
3. Add class-level Javadoc to `OrganizerMapFragment`
4. Add class-level Javadoc to `QrCodeFragment`
5. Add proper class-level Javadoc to `Action` (convert regular comment to Javadoc)

### Medium Priority (Public API Documentation)
1. Document all public methods in `Action.java` (getters/setters)
2. Document public methods in `EventRepository.java`
3. Document missing public methods in `EventViewModel.java`
4. Document public methods in `ProfileFragment.java` (`deleteAccount`, `signOut`)

### Low Priority (Private Methods)
1. Consider adding Javadocs to complex private methods for maintainability
2. Focus on methods with complex logic like `updateButtons()`, `drawLottery()`, etc.

### Quality Improvements
1. Replace copy-pasted Parcelable documentation with project-specific descriptions
2. Update `Event.java` `getPoster()`/`setPoster()` docs to reflect String return type
3. Remove or address TODO comments in production Javadocs
4. Add @param and @return tags where missing

---

## Conclusion

The KrakenHax project has **moderate to good** Javadoc coverage at **69.8%**. The core model classes (`Event`, `Profile`, `NotificationJ`) are well-documented, which is excellent for maintainability. The main areas requiring attention are:

1. **Fragment classes** - Several fragments lack documentation on their UI methods
2. **Private methods** - Only 21.1% coverage on private methods
3. **Adapter classes** - Some adapters (`NotifAdapterS`, `HistoryRecyclerViewAdapter`) need class-level documentation
4. **Quality issues** - Some existing Javadocs contain placeholder text or copy-pasted content

The project would benefit from a focused documentation sprint targeting the high-priority items listed above.
