# API Documentation

## Overview
This document provides a high-level overview of the data models and API endpoints for the application. The application is a task and note management system with user authentication, personal tasks, collaborative notes, and group functionality.

## Data Models

### User
Represents a user in the system with authentication capabilities.

**Fields:**
- `id` (Long): Unique identifier
- `username` (String): Unique username
- `email` (String): Unique email address
- `password` (String): Hashed password
- `enabled` (boolean): Account verification status (default: false)
- `picture` (String): Optional profile picture URL
- `verificationCode` (String): Code for email verification
- `verificationCodeExpiresAt` (LocalDateTime): Expiration time for verification code

**Relationships:**
- One-to-Many with Notes (as owner)
- One-to-Many with PersonalTasks (as owner)
- Many-to-Many with Notes (as collaborator)
- Many-to-Many with Groups (as member)
- Many-to-One with GroupTasks (as assignee)

### Note
Represents a collaborative note with a single owner and multiple collaborators.

**Fields:**
- `id` (Long): Unique identifier
- `title` (String): Note title
- `content` (String): Note content (TEXT)
- `createdAt` (LocalDateTime): Creation timestamp
- `lastActivity` (LocalDateTime): Last modified timestamp

**Relationships:**
- Many-to-One with User (owner)
- Many-to-Many with Users (collaborators)

**Derived Properties:**
- `participantCount`: Total number of participants (owner + collaborators)

### PersonalTask
Represents a simple personal task belonging to a single user.

**Fields:**
- `id` (Long): Unique identifier
- `text` (String): Task description
- `completed` (boolean): Completion status (default: false)

**Relationships:**
- Many-to-One with User (owner)

### Group
Represents a group that can contain multiple users and tasks.

**Fields:**
- `id` (Long): Unique identifier
- `name` (String): Group name

**Relationships:**
- Many-to-One with User (owner)
- Many-to-Many with Users (members)
- One-to-Many with GroupTasks (cascade delete)

### GroupTask
Represents a task belonging to a group that can be assigned to a member.

**Fields:**
- `id` (Long): Unique identifier
- `text` (String): Task description
- `completed` (boolean): Completion status (default: false)

**Relationships:**
- Many-to-One with Group
- Many-to-One with User (assignee, optional)

## API Endpoints

### Authentication
Base URL: `/auth`

- `POST /auth/signup`
  - Registers a new user
  - Request body: RegisterUserDto {email, password, username}
  - Response: User object

- `POST /auth/login`
  - Authenticates a user
  - Request body: LoginUserDto {username, password}
  - Response: LoginResponse {token, expiresIn}

- `POST /auth/verify`
  - Verifies a user's email
  - Request body: VerifyUserDto {email, verificationCode}
  - Response: Success message or error

- `POST /auth/resend`
  - Resends verification code
  - Request param: email
  - Response: Success message or error

### Users
Base URL: `/users`

- `GET /users/me`
  - Gets the current authenticated user
  - Response: User object

### Notes
Base URL: `/note`

- `GET /note`
  - Gets all notes for the current user (owner or collaborator)
  - Response: List of NoteListItemDto

- `GET /note/{id}`
  - Gets a specific note by ID
  - Response: Note object

- `POST /note`
  - Creates a new note
  - Request body: Note object
  - Response: Created Note object

- `PATCH /note/{id}`
  - Updates a note
  - Request body: Note object (partial update)
  - Response: Updated Note object

- `DELETE /note/{id}`
  - Deletes a note
  - Response: 200 OK (no body)

### Personal Tasks
Base URL: `/personal-tasks`

- `GET /personal-tasks`
  - Gets all personal tasks for the current user
  - Response: List of PersonalTask objects

- `POST /personal-tasks`
  - Creates a new personal task
  - Request body: PersonalTask object
  - Response: Created PersonalTask object

- `PUT /personal-tasks/{id}`
  - Updates a personal task
  - Request body: PersonalTask object
  - Response: Updated PersonalTask object

- `DELETE /personal-tasks/{id}`
  - Deletes a personal task
  - Response: 200 OK (no body)

## Data Transfer Objects (DTOs)

### RegisterUserDto
- `email` (String)
- `password` (String)
- `username` (String)

### LoginUserDto
- `username` (String)
- `password` (String)

### VerifyUserDto
- `email` (String)
- `verificationCode` (String)

### NoteDto
- `title` (String)
- `content` (String)

### NoteListItemDto
- `id` (Long)
- `title` (String)
- `lastActivity` (LocalDateTime)
- `formattedDate` (String)

### NoteCollaboratorDto
- `username` (String)

### PersonalTaskDto
- `owner` (User)
- `text` (String)
- `completed` (boolean)

### LoginResponse
- `token` (String): JWT authentication token
- `expiresIn` (long): Token expiration time in milliseconds