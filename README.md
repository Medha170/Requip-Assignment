# High-Performance User Management System (REST API Backend)

An enterprise-grade, highly validated User Management System designed to handle critical user registration, auditing, and soft-deletion constraints using Spring Boot and MySQL.

---

## 🚀 Technical Highlights & Architectural Decisions

### 1. Functional & Non-Functional Design Best Practices
* **JPA-Level Soft Deletion:** Implemented modern `@SQLDelete` and `@SQLRestriction` overrides. Instead of destructive data elimination, record deletions toggle an `is_deleted` flag. Reads automatically omit deactivated users, maintaining references safely for audit compliance.
* **Optimistic Locking Over Currency (`@Version`):** Guarded user updates using a version tracking counter to prevent race conditions or data overwriting if concurrent admins modify data simultaneously.
* **Granular Payload Validation:** Managed via `jakarta.validation.constraints`. Restricts malformed requests (e.g., verifying 12-digit Aadhaar limits or matching regex configurations for official PAN masks) immediately at the routing stage to conserve thread resource metrics.
* **Predictable Pagination Processing:** Utilizes `Pageable` memory-boundary processing for list outputs to eliminate server memory exhaustion.

### 2. Tech Stack Overview
* **Backend Framework:** Spring Boot 3.x / Java
* **Persistence Layer:** Spring Data JPA / Hibernate 6+
* **Database Target:** MySQL 9.x (Local standalone instance)
* **Testing Engine:** JUnit 5 & Mockito 5

---

## 📝 Pain Points & Key Engineering Learnings

1. **Hibernate Annotation Version Shift (Modern Syntax Integration):**
   * *Problem:* Traditional `@Where(clause = "...")` modifiers became deprecated in Hibernate 6/Spring Boot 3, causing syntax mismatches.
   * *Learning:* Adapted to the newer, safer `@SQLRestriction("is_deleted = false")` implementation which provides cleaner decoupled parsing during low-level execution.
2. **First-Commit Local Git State Restoration:**
   * *Problem:* Accidentally trying to execute standard `HEAD~1` restorations inside a repository that hasn't successfully committed its initial tracking tree fails because a root parent node doesn't exist yet.
   * *Learning:* Learned to use lower-level plumbing tools (`git update-ref -d HEAD`) to clear the target reference pointers completely, alongside index caching operations (`git rm --cached`) to safely enforce `.gitignore` rules without losing active code.
3. **Hibernate 7 Overridden Mutation Parameters Conflict:**

*Problem:* Enforcing `@Version` optimistic locking alongside custom `@SQLDelete` properties causes an index mismatch error (`Parameter index out of range`).

*Learning:* Hibernate automatically attempts to assert concurrent state integrity during structural modifications by appending version constraints to execution statements. Custom `@SQLDelete` overrides must explicitly include `AND version = ?` or declare `ResultCheckStyle.NONE` to ensure parameter binding maps align.

---

## 🛠️ Local API Endpoint Registry

| Method | Endpoint | Description | Payload Constraints / Query Params |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/users` | Registers a fresh unique user profile | Strict unique checks on Email, Phone, PAN, Aadhaar |
| **PUT** | `/api/v1/users/{id}`| Updates non-sensitive address fields | Validates dynamic target payload rules |
| **GET** | `/api/v1/users` | Paginated search tracking retrieval | `?page=0&size=10&sortBy=id` |
| **DELETE**| `/api/v1/users/{id}`| Triggers soft-deletion workflow | Executes update query behind abstraction layer |

---

## 🧪 Verification Coverage (Unit Testing Execution)

Logic is thoroughly verified via isolated Mockito unit suites testing input constraints, exception routing blocks, and state assertions:
```bash
./mvnw test

```

