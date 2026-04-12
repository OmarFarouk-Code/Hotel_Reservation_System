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

- ✅ Complete enum library — `AccountStatus`, `DiningPackage`, `PaymentMethod`, `ReservationStatus`, `Gender` **(Bassel)**
- ✅ User inheritance tree — `User` → `Guest` / `Staff` → `Admin` / `Receptionist` **(Belal + Adam)**
- ✅ Transactional model — `Reservation`, `Invoice` **(Mostafa)**
- ✅ Physical entity classes — `Amenity`, `RoomType`, `Room` **(Omar)**
- ✅ Interface contracts — `Manageable`, `Payable` **(Omar)**
- ✅ Centralised in-memory `Database` **(Omar)**
- ✅ Console test runner proving end-to-end backend correctness **(Omar)**
- ⏳ `BookingEngine` — deferred to later phase, skeleton defined **(Omar)**

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
│  Enums  ·  Amenity  ·  RoomType  ·  Room                   │
└─────────────────────────────────────────────────────────────┘
```

### UML Diagram Highlights

- `Room` aggregates `RoomType` (1) and `List<Amenity>` (0..*)
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
│       │   │   └── ReservationStatus.java
│       │   │
│       │   ├── entities/
│       │   │   ├── Amenity.java
│       │   │   ├── Room.java
│       │   │   └── RoomType.java
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
│       │       └── Invoice.java
│       │
│       ├── interfaces/
│       │   ├── Manageable.java
│       │   └── Payable.java
│       │
│       └── core/
│           ├── Database.java
│           ├── BookingEngine.java      ← deferred
│           └── Main.java
│
├── README.md
└── .gitignore
```

---

## 📚 Class Reference

### Enumerations

All enums live in `hotel.model.enums`. Defined by **Bassel**.

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
    BREAKFAST_ONLY, HALF_BOARD, FULL_BOARD, ALL_INCLUSIVE;

    private double pricePerNight;

    public double getPricePerNight() { ... }
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
| `-` | `seasonalMultiplier : double` | Pricing multiplier (set by Admin) |
| `+` | `getEffectivePrice() : double` | Returns `basePrice × seasonalMultiplier` |

---

#### `Room`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `roomNumber : int` | Unique room identifier |
| `-` | `floor : int` | Floor number |
| `-` | `isAvailable : boolean` | Availability flag |
| `-` | `roomType : RoomType` | Associated room type |
| `-` | `amenities : List<Amenity>` | Attached amenities |
| `+` | `addAmenity(Amenity) : void` | Add an amenity to this room |
| `+` | `removeAmenity(Amenity) : void` | Remove an amenity |
| `+` | `markAvailable() : void` | Set `isAvailable = true` |
| `+` | `markUnavailable() : void` | Set `isAvailable = false` |

---

### User Hierarchy

Defined by **Belal**.

#### `User` *(abstract)*

Base class for all system users. Cannot be instantiated directly.

| Modifier | Member |
|----------|--------|
| `-` | `userName : String` |
| `-` | `password : String` |
| `-` | `dateOfBirth : LocalDate` |
| `-` | `phoneNumber : String` |
| `-` | `email : String` |
| `+` | `login(username, password) : boolean` |
| `+` | `resetPassword(email) : void` |

---

#### `Guest` *(extends User)*

| Modifier | Member | Notes |
|----------|--------|-------|
| `-` | `balance : double` | Debited on invoice payment |
| `-` | `address : String` | |
| `-` | `gender : Gender` | |
| `-` | `roomPreferences : List<String>` | Preference tags |
| `-` | `failedLoginAttempts : int` | Triggers account lock |
| `-` | `accountStatus : AccountStatus` | `ACTIVE` or `LOCKED` |
| `+` | `register() : void` | Self-registration |
| `+` | `viewReservation() : void` | View own bookings |
| `+` | `checkout(reservationId) : void` | Initiate checkout |
| `+` | `payInvoice(invoiceId, method) : void` | Settle an invoice |

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

Full CRUD access over Rooms, RoomTypes, and Amenities, plus financial reporting.

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
manageCheckOut(int reservationId) : void  // Completes reservation, marks room available
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
| `-` | `selectedAmenities : List<Amenity>` |
| `-` | `cancellationPenalty : double` |
| `+` | `confirmReservation() : void` |
| `+` | `cancelReservation() : void` |
| `+` | `completeReservation() : void` |
| `+` | `calculateNights() : int` |
| `+` | `calculateAmenityTotal() : double` |
| `+` | `updateDiningPackage(DiningPackage) : void` |

> `calculateNights()` uses `ChronoUnit.DAYS.between(checkInDate, checkOutDate)`

---

#### `Invoice` *(implements Payable)*

| Modifier | Member |
|----------|--------|
| `-` | `invoiceID : integer` |
| `-` | `reservation : Reservation` |
| `-` | `isPaid : boolean` |
| `-` | `totalAmount : double` |
| `-` | `paymentMethod : PaymentMethod` |
| `-` | `paymentDate : LocalDate` |
| `-` | `appliedPromoCode : String` |
| `-` | `discountAmount : double` |
| `+` | `pay(Guest, PaymentMethod) : void` |
| `+` | `getTotal() : double` |
| `+` | `generateItemizedSummary() : String` |

> `getTotal()` returns `totalAmount - discountAmount`  
> `pay()` deducts from `guest.balance`, sets `isPaid = true`, records method and date

---

### Interfaces

Defined by **Omar**.

#### `Manageable`

```java
public interface Manageable {
    // Room
    void createRoom(Room room);
    Room readRoom(int roomNumber);
    void updateRoom(int roomNumber, Room updatedRoom);
    void deleteRoom(int roomNumber);

    // Amenity
    void createAmenity(Amenity amenity);
    Amenity readAmenity(String name);
    void updateAmenity(String name, Amenity updatedAmenity);
    void deleteAmenity(String name);

    // RoomType
    void createRoomType(RoomType type);
    RoomType readRoomType(String typeName);
    void updateRoomType(String typeName, RoomType updatedType);
    void deleteRoomType(String typeName);
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

Defined by **Omar**. Omar's scope spans the widest range in the project — from physical entity classes (`Amenity`, `RoomType`, `Room`) through to interface contracts, the central database, the console test runner, and the deferred booking engine.

#### `Database`

Centralised static in-memory storage hub. All collections are static and shared across the entire application.

```java
public class Database {
    private static List<Guest>       guests       = new ArrayList<>();
    private static List<Room>        rooms        = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static List<Invoice>     invoices     = new ArrayList<>();
    private static List<RoomType>    roomTypes    = new ArrayList<>();
    private static List<Amenity>     amenities    = new ArrayList<>();
    private static Map<String, Double> promoCodes = new HashMap<>();

    public static void   initializeData()                        { ... }
    public static Guest  findGuestByUsername(String username)    { ... }
    public static Room   findRoomByNumber(int roomNumber)        { ... }
    public static void   loadFromFiles()                         { ... }
    public static void   saveToFiles()                           { ... }
}
```

> ⚠️ `Database` is the **only** class in the system that uses static members.

---

#### `BookingEngine` *(Deferred — Later Phase)*

The `BookingEngine` holds all complex calculation and workflow logic. It is defined now but implemented after all entity models are stable.

```java
public class BookingEngine {
    private Database database;

    // Room availability & filtering
    List<Room>    getAvailableRooms(LocalDate checkIn, LocalDate checkOut)
    List<Room>    filterRooms(String roomType, double maxPrice)
    List<Room>    sortRooms(List<Room> rooms, boolean ascending)
    List<String>  suggestPackages()

    // Cost calculations
    double calculateRoomCost(Room room, LocalDate in, LocalDate out)
    double calculateDiningCost(DiningPackage packageType, int nights)
    double calculateAmenityCost(List<Amenity> selectedAmenities)
    double calculateTotalReservationCost(Reservation reservationDraft)

    // Booking workflow
    double      validatePromoCode(String code)
    Reservation createDraftReservation(Guest guest, Room room, LocalDate in, LocalDate out)
    boolean     confirmReservation(int reservationId, PaymentMethod paymentMethod)
    void        processCancellation(int reservationId, LocalDate cancelDate)

    // Financial reporting
    double  calculateCancellationPenalty(Reservation reservation, LocalDate cancelDate)
    double  calculateTotalRevenue()
    double  calculateOccupancyPercentage()
    Invoice generateInvoice(Reservation reservation)
}
```

---

## 👥 Team & Task Assignment

| # | Member | Domain | Classes / Interfaces |
|---|--------|--------|----------------------|
| 01 | **Bassel** | Foundation — Enums | `AccountStatus`, `DiningPackage`, `PaymentMethod`, `ReservationStatus`, `Gender` |
| 02 | **Belal** | User Hierarchy & Authentication | `User` *(abstract)*, `Guest`, `Staff` *(abstract)* |
| 03 | **Adam** | Administration & Operations | `Admin`, `Receptionist` |
| 04 | **Mostafa** | Booking & Financial Models | `Reservation`, `Invoice` |
| 05 | **Omar** | Entities, Integration, Storage & Console Flow | `Amenity`, `RoomType`, `Room`, `Manageable`, `Payable`, `Database`, `Main`, `BookingEngine` |

---

## 🔗 Dependency Map

```
Bassel ──────────────────────────────────────────────────────► (no dependencies)
Belal  ──── depends on ──► Bassel (enums: Gender, AccountStatus, PaymentMethod)
Adam   ──── depends on ──► Omar   (Room, RoomType, Amenity)
             depends on ──► Belal  (Staff abstract class)
             depends on ──► Omar   (Manageable interface)
Mostafa ─── depends on ──► Omar   (Room, Amenity)
             depends on ──► Bassel (DiningPackage enum)
             depends on ──► Belal  (Guest class)
             depends on ──► Omar   (Payable interface)
Omar   ──── depends on ──► Bassel (enums for entity fields)
             depends on ──► ALL    (Database aggregates every entity type)
```

**Recommended build order:**
1. Bassel delivers all enums
2. Omar delivers `Amenity`, `RoomType`, `Room`, `Manageable`, `Payable`, and empty `Database`
3. Belal delivers user hierarchy
4. Adam and Mostafa work in parallel
5. Omar completes `Main` and `BookingEngine`

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

The `Main` class is the entry point for Milestone 1. It runs a full end-to-end simulation in the console — no GUI required.

```
$ java -cp out hotel.core.Main

=== Hotel Reservation System — Console Test ===

[DATABASE] Initializing sample data...
  ✔ 3 room types loaded
  ✔ 10 rooms loaded
  ✔ 5 amenities loaded
  ✔ 2 promo codes loaded

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

## 🗓 Milestone Plan

| Milestone | Timeline | Owner | Deliverable |
|-----------|----------|-------|-------------|
| M1 | Week 1 | Bassel | All 5 enums (`AccountStatus`, `DiningPackage`, `PaymentMethod`, `ReservationStatus`, `Gender`) |
| M2 | Week 1 | Omar | `Amenity`, `RoomType`, `Room`, `Manageable`, `Payable`, `Database` skeleton |
| M3 | Week 1–2 | Belal | `User`, `Guest`, `Staff` |
| M4 | Week 2 | Adam | `Admin`, `Receptionist` |
| M5 | Week 2 | Mostafa | `Reservation`, `Invoice` |
| M6 | Week 3 | Omar | `Main` console test runner |
| M7 | Week 3+ | Omar | `BookingEngine` full implementation |

---

## 🤝 Contributing

### Git Workflow

```
main
 └── feature/<member>/<classname>
       e.g. feature/omar/RoomType
            feature/omar/Database
            feature/bassel/DiningPackage
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

---

<div align="center">

*Hotel Reservation System — OOP Course Project, Spring 2026*  
*Ain Shams University · University of East London*

</div>