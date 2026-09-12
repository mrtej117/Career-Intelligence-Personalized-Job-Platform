# 💼 Job Portal — Backend (Spring Boot)

A production-ready backend for a Job Portal platform where **Employers** post jobs and manage applications, and **Job Seekers** search, save, and apply to jobs. Built with **Spring Boot**, **Spring Security (JWT)**, **Hibernate/JPA**, and **MySQL**.

---

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Domain Model](#domain-model)
- [Roles & Permissions](#roles--permissions)
- [API Overview](#api-overview)
- [Auth & Security](#auth--security)
- [DTOs](#dtos)
- [Validation](#validation)
- [Pagination & Sorting](#pagination--sorting)
- [Error Handling](#error-handling)
- [Configuration](#configuration)
- [Run Locally](#run-locally)
- [Build & Deployment](#build--deployment)
- [Common Workflows](#common-workflows)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

---

## Features
- **Separate signup flows & DTOs** for **Job Seekers** and **Employers**
- **JWT-based authentication** with stateless security and role-based authorization
- **Fine-grained permissions** per role (Employer vs Job Seeker)
- **Jobs**: create, update, delete (employers), list & search (everyone), save/unsave (job seekers)
- **Applications**: apply with resume + screening answers, list & manage status
- **Notifications**: unread counts, toast-friendly fetch, mark-as-read
- **Profile management** with **profile picture** and **resume** upload (multipart)
- **Search endpoints** split for clarity (e.g., by title, by type)
- **Robust DTO layer** isolating entities from API payloads
- **Hibernate/JPA** with MySQL, auditing fields, and clear relationships
- **CORS**, **input validation**, **global error handling**, **sensible pagination defaults**

---

## Tech Stack
- **Language**: Java 17+
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security, JWT (HS256)
- **Persistence**: Spring Data JPA (Hibernate), MySQL 8.x
- **Build**: Maven
- **Mapping/JSON**: Jackson
- **Validation**: Jakarta Validation (Bean Validation)
- **Logging**: SLF4J + Logback

---

## Architecture
- **Layered**: `Controller → Service → Repository → Entity`
- **DTO mapping** in controllers/services to prevent entity leakage
- **Security Filter**: Custom `JwtAuthFilter` authenticates tokens and injects the current user
- **Stateless**: No server sessions, Authorization via `Bearer <JWT>`

---

## Domain Model

### Entities (Core)
- **User** `(id, name, username, email, password, role, createdAt)`
  - Subtypes / role flags: `JOB_SEEKER` or `EMPLOYER`
- **JobSeeker** `(id, user, dob, profilePictureUrl, resumeUrl, savedJobs: many-to-many)`
- **Employer** `(id, user, companyName, profilePictureUrl, jobs: one-to-many)`
- **Job** `(id, title, description, location, type: JobType, workMode: WorkMode, postedAt, employer, applications: one-to-many)`
- **Application** `(id, job, jobSeeker, status: ApplicationStatus, resumeUrl, screeningAnswers: JSON/text, createdAt)`
- **Notification** `(id, user, message, isRead, createdAt)`

### Enums
- **Role**: `JOB_SEEKER`, `EMPLOYER`
- **JobType**: `FULL_TIME`, `PART_TIME`, `INTERNSHIP`, `CONTRACT`, `TEMPORARY`
- **WorkMode**: `ONSITE`, `REMOTE`, `HYBRID`
- **ApplicationStatus**: `PENDING`, `REVIEWED`, `INTERVIEW`, `OFFERED`, `REJECTED`, `WITHDRAWN`

---

## Roles & Permissions

| Capability | Guest | Job Seeker | Employer |
|---|---|---|---|
| Sign up / Sign in | ✅ | ✅ | ✅ |
| View jobs | ✅ | ✅ | ✅ |
| Search jobs | ✅ | ✅ | ✅ |
| Save/Unsave jobs | ❌ | ✅ | ❌ |
| Apply to job | ❌ | ✅ | ❌ |
| View own applications | ❌ | ✅ | ❌ |
| Withdraw own application | ❌ | ✅ | ❌ |
| Create job | ❌ | ❌ | ✅ |
| Update/Delete own job | ❌ | ❌ | ✅ |
| View applicants for own jobs | ❌ | ❌ | ✅ |
| Update application status for own jobs | ❌ | ❌ | ✅ |
| Notifications (fetch/mark) | ❌ | ✅ | ✅ |
| Upload profile picture/resume | ❌ | ✅ | ✅ (profile pic) |

> Authorization is enforced via **endpoint-level configuration** and **method-level checks** where necessary.

---

## API Overview

> Base URL: `/` (e.g., `http://localhost:8080/`).  
> All protected routes require header: `Authorization: Bearer <JWT>`.

### Auth
- `POST /auth/signup/jobseeker` — Register job seeker (basic info)
- `POST /auth/signup/employer` — Register employer (basic info)
- `POST /auth/signin` — Login (returns JWT)
- **Uploads (after sign-in)**:
  - `POST /user/jobseeker/upload-resume` — multipart
  - `POST /user/jobseeker/upload-profile-picture` — multipart
  - `POST /user/employer/upload-profile-picture` — multipart

### Jobs (Public & Role-Specific)
- `GET /jobs` — List all jobs (paged)
- `GET /jobs/{id}` — Get job details
- **Search (separate endpoints for simplicity)**:
  - `GET /jobs/search/title?query=...`
  - `GET /jobs/search/type?type=FULL_TIME`
  - `GET /jobs/search/location?city=...`
  - `GET /jobs/search/workmode?mode=REMOTE`
- **Employer-only**:
  - `POST /jobs` — Create job
  - `PUT /jobs/{id}` — Update own job
  - `DELETE /jobs/{id}` — Delete own job
  - `GET /employer/jobs` — List jobs created by the authenticated employer
  - `GET /employer/jobs/{id}/applicants` — List applicants for a job you own

### Saved Jobs (Job Seeker)
- `POST /saved-jobs/{jobId}` — Save job
- `DELETE /saved-jobs/{jobId}` — Unsave job
- `GET /saved-jobs` — List saved jobs (paged)

### Applications
- **Job Seeker**:
  - `POST /applications` — Apply to a job (DTO includes `jobId`, `resumeUrl`, `screeningAnswers`)
  - `GET /applications/my` — List my applications
  - `GET /applications/{id}` — View my application details
  - `DELETE /applications/{id}` — Withdraw my application
- **Employer (for own jobs)**:
  - `GET /employer/applications?jobId=...` — Applicants for a job
  - `PUT /applications/{id}/status` — Update status (e.g., `INTERVIEW`, `REJECTED`)

### Notifications
- `GET /notifications/unread-count` — Count unread
- `GET /notifications/unread` — Latest unread list
- `PUT /notifications/{id}/read` — Mark a notification as read
- `PUT /notifications/read-all` — Mark all as read

### Profile
- **Job Seeker**: `GET /profile/me`, `PUT /profile/me`
- **Employer**: `GET /employer/profile/me`, `PUT /employer/profile/me`

---

## Auth & Security

### JWT Flow
1. User signs in → `POST /auth/signin` returns `token`.
2. Client stores token (securely) and sends `Authorization: Bearer <token>` header.
3. `JwtAuthFilter`:
   - Extracts token from header.
   - Validates signature & expiry.
   - Loads user details and sets `SecurityContext`.
   - (If used) attaches current user to request (e.g., `request.setAttribute("user", user)`).
4. Endpoint authorization via **Spring Security** and role checks.

### Security Config Highlights
- **Stateless** session; CSRF disabled for APIs.
- **CORS** allowed for frontend origin(s).
- **Password hashing** with BCrypt.
- **AntMatchers/HttpSecurity** (Spring Boot 3: `authorizeHttpRequests`) restrict endpoints by role.

---

