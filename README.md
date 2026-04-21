<div align="center">

# 🏨 Hotel Reservation System

**A fully object-oriented hotel management backend built in Java**

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![OOP](https://img.shields.io/badge/Architecture-OOP-1B3A5C?style=for-the-badge)
![Status](https://img.shields.io/badge/Milestone-1%20%E2%80%94%20Backend-2E6DA4?style=for-the-badge)
![University](https://img.shields.io/badge/Ain%20Shams%20University-Spring%202026-green?style=for-the-badge)

*OOP Course Project · Spring 2026 · Ain Shams University / University of East London*

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Class Reference](#-class-reference)
  - [Enumerations](#enumerations)
  - [Entity Classes](#entity-classes)
  - [User Hierarchy](#user-hierarchy)
  - [Staff & Operations](#staff--operations)
  - [Booking & Finance](#booking--finance)
  - [Interfaces](#interfaces)
  - [Core Infrastructure](#core-infrastructure)
- [Team & Task Assignment](#-team--task-assignment)
- [Dependency Map](#-dependency-map)
- [Getting Started](#-getting-started)
- [Running the Project](#-running-the-project)
- [Sample Flow](#-sample-flow)
- [Milestone Plan](#-milestone-plan)
- [Contributing](#-contributing)

---

## 🌐 Overview

The **Hotel Reservation System** is a backend-only Java application developed as a Milestone 1 deliverable for the Object-Oriented Programming course. It models the complete lifecycle of a hotel — from room inventory management and guest registration through to booking, check-in, payment, and check-out — using clean OOP principles throughout.

### Key Design Goals

| Goal | Implementation |
|------|----------------|
| **Separation of Concerns** | Entities hold data only; logic lives in `BookingEngine` |
| **Centralised State** | Single static `Database` class acts as the in-memory hub |
| **Loose Coupling** | `Manageable` and `Payable` interfaces decouple layers |
| **Testability** | `Main` runs a full end-to-end console simulation with no GUI |
| **Extensibility** | Modular package structure ready for GUI layer in Milestone 2 |

### What Milestone 1 Covers

- ✅ Complete enum library — `AccountStatus`, `DiningPackage`, `PaymentMethod`, `ReservationStatus`, `Gender`, `RoomView` **(Omar)**
- ✅ Physical entity classes — `Amenity`, `RoomType`, `Room`, `Review` **(Omar)**
- ✅ Interface contracts — `Manageable`, `Payable` **(Omar)**
- ✅ Centralised in-memory `Database` with file persistence **(Omar)**
- ✅ Folder structure, file pathing, and UML diagram **(Omar)**
- ✅ User inheritance tree — `User` → `Staff` **(Belal)**
- ✅ Administration — `Admin` **(Belal)**
- ✅ Transactional model — `Reservation`, `Invoice` **(Mostafa)**
- ✅ Staff operations — `Admin` CRUD methods, `Receptionist` **(Adam)**
- ✅ `BookingEngine` — fully implemented with contributions from all members **(see BookingEngine section)**
- ✅ System-wide debugging **(Omar, Belal, Adam)**
- ✅ `Main` console test runner **(Basel, Mostafa)**

---

## 🏗 Architecture

The system is organised into four strictly ordered tiers. Dependencies always flow **downward** — upper tiers depend on lower ones, never the reverse.

```
┌─────────────────────────────────────────────────────────────┐
│  TIER 4 — Integration & Logic                               │
│  Database  ·  BookingEngine  ·  Main                        │
├─────────────────────────────────────────────────────────────┤
│  TIER 3 — Contracts                                         │
│  <<interface>> Manageable  ·  <<interface>> Payable         │
├─────────────────────────────────────────────────────────────┤
│  TIER 2 — Domain Models                                     │
│  Guest  ·  Staff  ·  Admin  ·  Receptionist                 │
│  Reservation  ·  Invoice                                    │
├─────────────────────────────────────────────────────────────┤
│  TIER 1 — Foundation                                        │
│  Enums  ·  Amenity  ·  RoomType  ·  Room  ·  Review        │
└─────────────────────────────────────────────────────────────┘
```

### UML Diagram Highlights

- `Room` aggregates `RoomType` (1) and `List<Amenity>` (0..*) and `List<Review>` (0..*)
- `Guest` and `Staff` both extend the abstract `User`
- `Admin` extends `Staff` and **implements** `Manageable`
- `Invoice` **implements** `Payable`
- `Reservation` references `Guest`, `Room`, `DiningPackage`, and `List<Amenity>`
- `Database` holds static collections of every entity type

---

## 📁 Project Structure

```
hotel-reservation-system/
│
├── src/
│   └── hotel/
│       ├── model/
│       │   ├── enums/
│       │   │   ├── AccountStatus.java
│       │   │   ├── DiningPackage.java
│       │   │   ├── Gender.java
│       │   │   ├── PaymentMethod.java
│       │   │   ├── ReservationStatus.java
│       │   │   ├── RoomView.java
│       │   │   └── UserType.java
│       │   │
│       │   ├── entities/
│       │   │   ├── Amenity.java
│       │   │   ├── Room.java
│       │   │   ├── RoomType.java
│       │   │   └── Review.java
│       │   │
│       │   ├── users/
│       │   │   ├── User.java           ← abstract
│       │   │   ├── Guest.java
│       │   │   └── Staff.java          ← abstract
│       │   │
│       │   ├── staff/
│       │   │   ├── Admin.java
│       │   │   └── Receptionist.java
│       │   │
│       │   └── bookings/
│       │       ├── Reservation.java
│       │       ├── Invoice.java
│       │       └── PromoCode.java
│       │
│       ├── interfaces/
│       │   ├── Manageable.java
│       │   └── Payable.java
│       │
│       └── core/
│           ├── Database.java
│           ├── BookingEngine.java
│           └── Main.java
│
├── README.md
└── .gitignore
```

---

## 📚 Class Reference

### Enumerations

All enums live in `hotel.model.enums`. Defined by **Omar**.

#### `AccountStatus`
```java
public enum AccountStatus {
    ACTIVE, LOCKED
}
```
Tracks guest account state. Accounts are `LOCKED` after exceeding the failed login attempt threshold.

---

#### `DiningPackage`
```java
public enum DiningPackage {
    BREAKFAST_ONLY,
    HALF_BOARD,
    FULL_BOARD,
    ALL_INCLUSIVE;
}
```
Each value carries a per-night surcharge applied to the reservation total.

---

#### `PaymentMethod`
```java
public enum PaymentMethod {
    CASH, CREDIT_CARD, ONLINE
}
```

---

#### `ReservationStatus`
```java
public enum ReservationStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED
}
```
Status transitions: `PENDING → CONFIRMED → COMPLETED`, or `PENDING/CONFIRMED → CANCELLED`.

---

#### `Gender`
```java
public enum Gender {
    MALE, FEMALE
}
```

---

#### `RoomView`
```java
public enum RoomView {
    SEA_VIEW, GARDEN, POOL
}
```

---

### Entity Classes

Defined by **Omar**.

#### `Amenity`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `amenityName : String` | Display name of the amenity |
| `-` | `description : String` | Short description |
| `-` | `amenityPrice : double` | Per-stay surcharge |

Represents a bookable facility (e.g. minibar, safe, jacuzzi) that a guest can attach to a reservation.

---

#### `RoomType`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `typeName : String` | e.g. "Standard", "Suite" |
| `-` | `basePrice : double` | Nightly base rate |
| `-` | `description : String` | Type description |
| `-` | `seasonMultiplier : double` | Pricing multiplier (set by Admin) |
| `-` | `roomView : RoomView` | View type for this room category |
| `-` | `maxCapacity : int` | Maximum guest occupancy |
| `+` | `getEffectivePrice() : double` | Returns `basePrice × seasonMultiplier` |

---

#### `Room`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `roomNumber : int` | Unique room identifier |
| `-` | `floor : int` | Floor number |
| `-` | `roomType : RoomType` | Associated room type |
| `-` | `amenities : List<Amenity>` | Attached amenities |
| `-` | `reviews : List<Review>` | Guest reviews for this room |
| `+` | `addAmenity(Amenity) : void` | Add an amenity to this room |
| `+` | `removeAmenity(Amenity) : void` | Remove an amenity |
| `+` | `addReview(Review) : void` | Attach a guest review |
| `+` | `calculateAverageRating() : double` | Average score across all reviews |

---

#### `Review`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `score : int` | Numeric rating |
| `-` | `comment : String` | Guest comment text |

---

### User Hierarchy

Defined by **Belal**.

#### `User` *(abstract)*

Base class for all system users. Cannot be instantiated directly.

| Modifier | Member |
|----------|--------|
| `-` | `userName : String` |
| `-` | `password : String` |
| `-` | `typeOfUser : UserType` |
| `-` | `theGender : Gender` |
| `+` | `Login() : void` |
| `+` | `ResetPassword(String, UserType) : void` |
| `+` | `passwordconfirmation(String, String) : void` |
| `+` | `Datechecker(String) : boolean` |

---

#### `Guest` *(extends User)*

| Modifier | Member | Notes |
|----------|--------|-------|
| `-` | `balance : double` | Debited on invoice payment |
| `-` | `address : String` | |
| `-` | `phoneNumber : String` | |
| `-` | `dateOfBirth : LocalDate` | |
| `-` | `roomPreferences : List<String>` | Preference tags |
| `-` | `roomOptions : RoomType` | Preferred room type |
| `-` | `failedLoginAttempts : int` | Triggers account lock |
| `-` | `accountStatus : AccountStatus` | `ACTIVE` or `LOCKED` |
| `+` | `register() : void` | Collects guest-specific fields |
| `+` | `ViewReservation(String, int) : void` | View reservations by username and ID |
| `+` | `ViewReservationbyId() : void` | Fallback lookup by reservation ID |

---

#### `Staff` *(abstract, extends User)*

| Modifier | Member |
|----------|--------|
| `-` | `workingHours : int` |
| `+` | `viewAllGuests() : void` |
| `+` | `viewAllReservations() : void` |

---

### Staff & Operations

Defined by **Adam**.

#### `Admin` *(extends Staff, implements Manageable)*

Full CRUD access over Rooms, RoomTypes, and Amenities, plus seasonal pricing.

**Room CRUD**
```java
createRoom(Room room) : void
readRoom(int roomNumber) : Room
updateRoom(int roomNumber, Room updatedRoom) : void
deleteRoom(int roomNumber) : void
```

**Amenity CRUD**
```java
createAmenity(Amenity amenity) : void
readAmenity(String name) : Amenity
updateAmenity(String name, Amenity updatedAmenity) : void
deleteAmenity(String name) : void
```

**RoomType CRUD**
```java
createRoomType(RoomType type) : void
readRoomType(String typeName) : RoomType
updateRoomType(String typeName, RoomType updatedType) : void
deleteRoomType(String typeName) : void
```

**Additional Methods**
```java
setSeasonalMultiplier(String roomType, double multiplier) : void
generateFinancialReport(int days) : void
```

---

#### `Receptionist` *(extends Staff)*

```java
manageCheckIn(int reservationId) : void   // Confirms reservation, marks room unavailable
manageCheckOut(int reservationId) : void  // Completes reservation after payment is verified
```

---

### Booking & Finance

Defined by **Mostafa**.

#### `Reservation`

| Modifier | Member |
|----------|--------|
| `-` | `reservationID : int` |
| `-` | `guest : Guest` |
| `-` | `room : Room` |
| `-` | `checkInDate : LocalDate` |
| `-` | `checkOutDate : LocalDate` |
| `-` | `status : ReservationStatus` |
| `-` | `diningPackage : DiningPackage` |
| `-` | `requestedAmenities : List<Amenity>` |
| `-` | `cancellationPenalty : double` |
| `-` | `numChildren : int` |
| `-` | `numAdults : int` |
| `+` | `confirmreservation() : void` |
| `+` | `cancelreservation() : void` |
| `+` | `completereservation() : void` |
| `+` | `calcnights() : int` |
| `+` | `calcamenitytotal() : double` |
| `+` | `updatediningpack(DiningPackage) : void` |

> `calcnights()` uses `ChronoUnit.DAYS.between(CheckinDate, CheckoutDate)`

---

#### `Invoice` *(implements Payable)*

| Modifier | Member |
|----------|--------|
| `-` | `invoiceID : int` |
| `-` | `reservation : Reservation` |
| `-` | `isPaid : boolean` |
| `-` | `totalAmount : double` |
| `-` | `paymentMethod : PaymentMethod` |
| `-` | `paymentDate : LocalDate` |
| `-` | `appliedPromoCode : String` |
| `-` | `discountAmount : double` |
| `+` | `pay(Guest, PaymentMethod) : void` |
| `+` | `getTotal() : double` |

> `getTotal()` returns `totalAmount`
> `pay()` deducts from `guest.balance`, sets `isPaid = true`, records method and date

---

#### `PromoCode`

| Modifier | Member |
|----------|--------|
| `-` | `code : String` |
| `-` | `discountPercentage : double` |
| `-` | `expiryDate : LocalDate` |
| `-` | `isActive : boolean` |
| `+` | `isActive() : boolean` | Returns `true` only if active and not expired |

---

### Interfaces

Defined by **Omar**.

#### `Manageable`

```java
public interface Manageable {
    // Room
    void createRoom(Room room) throws Exception;
    Room readRoom(int roomNumber) throws Exception;
    void updateRoom(int roomNumber, Room updatedRoom) throws Exception;
    void deleteRoom(int roomNumber) throws Exception;

    // Amenity
    void createAmenity(Amenity amenity) throws Exception;
    Amenity readAmenity(String name) throws Exception;
    void updateAmenity(String name, Amenity updatedAmenity) throws Exception;
    void deleteAmenity(String name) throws Exception;

    // RoomType
    void createRoomType(RoomType type) throws Exception;
    RoomType readRoomType(String typeName) throws Exception;
    void updateRoomType(String typeName, RoomType updatedType) throws Exception;
    void deleteRoomType(String typeName) throws Exception;
}
```

Implemented by: `Admin`

---

#### `Payable`

```java
public interface Payable {
    void pay(Guest guest, PaymentMethod method);
    double getTotal();
}
```

Implemented by: `Invoice`

---

### Core Infrastructure

#### `Database`

Defined by **Omar**. Centralised static in-memory storage hub with file persistence. All collections are static and shared across the entire application.

```java
public class Database {
    private static List<Guest>       guests       = new ArrayList<>();
    private static List<Room>        rooms        = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static List<Invoice>     invoices     = new ArrayList<>();
    private static List<RoomType>    roomTypes    = new ArrayList<>();
    private static List<Amenity>     amenities    = new ArrayList<>();
    private static List<Admin>       admins       = new ArrayList<>();
    private static List<Receptionist> receptionists = new ArrayList<>();
    private static List<PromoCode>   promoCodes   = new ArrayList<>();

    public static void saveData()                                { ... }
    public static void loadData()                                { ... }
}
```

> ⚠️ `Database` is the **only** class in the system that uses static members.

---

#### `BookingEngine`

The `BookingEngine` contains all complex calculation and workflow logic. Each method was implemented by the team member indicated below, matching the UML assignment diagram.

```java
public class BookingEngine {

    // Room availability & filtering
    List<Room>   getAvailableRooms(LocalDate checkIn, LocalDate checkOut)     // Basel
    List<Room>   filterRooms(String roomType, RoomView roomView, double maxPrice) // Basel
    void         viewAllRooms()                                                // Basel
    List<Room>   sortRooms(List<Room> rooms, boolean ascending)               // Basel
    List<String> viewAllDiningPackages()                                       // Basel (implied)
    List<String> suggestPackages(Guest guest)                                  // Basel

    // Cost calculations
    double calculateRoomCost(Room room, LocalDate in, LocalDate out)          // Basel
    double calculateDiningCost(DiningPackage packageType, int nights)         // Omar
    double calculateAmenityCost(List<Amenity> selectedAmenities)              // Adam
    double calculateTotalReservationCost(Reservation reservation)             // Mostafa

    // Booking & payment workflow
    double      validatePromoCode(String code)                                // Belal
    Reservation createDraftReservation(Guest, Room, LocalDate, LocalDate,
                    DiningPackage, int, int, Receptionist)                    // Omar
    boolean     confirmReservation(int reservationId, PaymentMethod method)   // Omar
    void        processCancellation(int reservationId, LocalDate cancelDate)  // Mostafa (pending)
    double      calculateCancellationPenalty(int reservationId,
                    LocalDate cancelDate)                                      // Belal
    Invoice     generateInvoice(Reservation reservation, String promoCode)    // Omar

    // Financial reporting
    double calculateTotalRevenue()                                             // Adam
    double calcualteTotalRevenue(LocalDate startDate, LocalDate endDate)      // Adam
    double calculateOccupancyPercentage()                                      // Mostafa
}
```

---

## 👥 Team & Task Assignment

| # | Member | Domain | Responsibilities |
|---|--------|--------|-----------------|
| 01 | **Omar** | Foundation, Infrastructure & Debugging | `AccountStatus`, `DiningPackage`, `PaymentMethod`, `ReservationStatus`, `Gender`, `RoomView`, `UserType`, `Amenity`, `RoomType`, `Room`, `Review`, `Manageable`, `Payable`, `Database`, folder structure, file pathing, UML diagram · `BookingEngine`: `calculateDiningCost`, `createDraftReservation`, `confirmReservation`, `generateInvoice` · **System-wide debugging** |
| 02 | **Belal** | User Hierarchy, Authentication & Debugging | `User` *(abstract)*, `Guest`, `Staff` *(abstract)* · `BookingEngine`: `validatePromoCode`, `calculateCancellationPenalty` · **System-wide debugging** |
| 03 | **Adam** | Administration, Operations & Debugging | `Admin` CRUD methods, `Receptionist` · `BookingEngine`: `calculateAmenityCost`, `calculateTotalRevenue` (both overloads) · **System-wide debugging** |
| 04 | **Mostafa** | Booking, Finance & Console Testing | `Reservation`, `Invoice` · `BookingEngine`: `calculateTotalReservationCost`, `calculateOccupancyPercentage` · **`Main` console test runner** |
| 05 | **Basel** | Room Logic & Console Testing | `BookingEngine`: `getAvailableRooms`, `filterRooms`, `viewAllRooms`, `sortRooms`, `suggestPackages`, `calculateRoomCost` · **`Main` console test runner** |

---

## 🔗 Dependency Map

```
Omar   ──────────────────────────────────────────────────────► (no external dependencies)
Belal  ──── depends on ──► Omar   (enums: Gender, AccountStatus, UserType, RoomType entity)
Adam   ──── depends on ──► Omar   (Room, RoomType, Amenity, Database, Manageable interface)
             depends on ──► Belal  (Staff abstract class)
Mostafa ─── depends on ──► Omar   (Room, Amenity, Database, Payable interface)
             depends on ──► Belal  (Guest class)
Basel  ──── depends on ──► Omar   (Room, RoomType, Database)
             depends on ──► Mostafa (Reservation)
```

**Recommended build order:**
1. Omar delivers all enums, entity classes, interfaces, and empty `Database`
2. Belal delivers user hierarchy (`User`, `Guest`, `Staff`)
3. Adam and Mostafa work in parallel on their entity/staff classes
4. Basel implements `BookingEngine` room-side methods
5. Omar completes remaining `BookingEngine` methods and integration
6. Basel and Mostafa collaborate on `Main` console test runner
7. Omar, Belal, and Adam perform full system debugging

---

## ⚙️ Getting Started

### Prerequisites

- **Java 17+** (uses `java.time.LocalDate`, `ArrayList`, `HashMap`)
- **IDE**: IntelliJ IDEA or Eclipse recommended

### Clone the Repository

```bash
git clone https://github.com/<your-org>/hotel-reservation-system.git
cd hotel-reservation-system
```

### Compile

```bash
# From the project root
javac -d out -sourcepath src src/hotel/core/Main.java
```

### Run

```bash
java -cp out hotel.core.Main
```

---

## ▶️ Running the Project

The `Main` class is the entry point for Milestone 1. It runs a full end-to-end simulation in the console — no GUI required. The console test runner was implemented by **Basel** and **Mostafa**.

```
$ java -cp out hotel.core.Main

=== Hotel Reservation System — Console Test ===

[DATABASE] Initializing sample data...
  ✔ 3 room types loaded
  ✔ 10 rooms loaded
  ✔ 5 amenities loaded

[GUEST] Registering new guest: alice_smith
  ✔ Guest registered successfully

[ADMIN] Creating new room type: Presidential Suite
  ✔ Room type created

[ADMIN] Setting seasonal multiplier for Suite: 1.4
  ✔ Multiplier updated — effective price: $420.00/night

[GUEST] alice_smith logged in successfully
[GUEST] Viewing available rooms for 2025-12-20 → 2025-12-25

[RECEPTIONIST] Check-in for reservation #1042
  ✔ Reservation confirmed
  ✔ Room 204 marked unavailable

[GUEST] Paying invoice #5 via CREDIT_CARD
  ✔ Payment processed — total: $2,340.00

[RECEPTIONIST] Check-out for reservation #1042
  ✔ Reservation completed
  ✔ Room 204 marked available

[ADMIN] Generating financial report (last 30 days)
  Total revenue:        $18,450.00
  Occupancy rate:       74.5%
  Cancellations:        2

=== All tests passed ✔ ===
```

---

## 🔄 Sample Flow

The full guest lifecycle covered by the console test runner:

```
1. Admin creates RoomTypes and Rooms
            │
            ▼
2. Guest registers and logs in
            │
            ▼
3. BookingEngine finds available rooms and calculates cost
            │
            ▼
4. Guest creates a Reservation (status: PENDING)
            │
            ▼
5. Receptionist manages check-in (status: CONFIRMED, room unavailable)
            │
            ▼
6. Guest pays Invoice (balance debited, isPaid = true)
            │
            ▼
7. Receptionist manages check-out (status: COMPLETED, room available)
            │
            ▼
8. Admin generates financial report
```

---

## 🤝 Contributing

### Git Workflow

```
main
 └── feature/<member>/<classname>
       e.g. feature/omar/RoomType
            feature/omar/Database
            feature/mostafa/Reservation
            feature/adam/Receptionist
            feature/basel/BookingEngine
```

1. **Branch** off `main` using the naming convention above
2. Implement your assigned class(es)
3. Ensure your code compiles with the shared interfaces/stubs from `main`
4. Open a **Pull Request** — at least one other team member must review before merging
5. Do **not** push directly to `main`

### Code Conventions

| Rule | Detail |
|------|--------|
| All fields are `private` | Use getters/setters for access |
| No `null` returns | Use `Optional<T>` or empty collections |
| Dates use `java.time.LocalDate` | Never `java.util.Date` or Strings |
| Collections use `ArrayList<T>` | Initialised in constructor, never null |
| Only `Database` uses `static` | All other classes are instance-based |
| Packages match folder structure | `hotel.model.enums`, `hotel.core`, etc. |
| All entity classes implement `Serializable` | Required for `Database` file persistence |

---

<div align="center">

*Hotel Reservation System — OOP Course Project, Spring 2026*
*Ain Shams University · University of East London*

</div>