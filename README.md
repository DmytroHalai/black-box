# Project Overview

The project consists of several independent but connected modules:

- **Game**
- **Generator of implementations**
- **Test Runner**
- **Copy Utility**
- **Web Application**
- **Tests**

## Goal of the project
The project is an educational platform designed to help students learn how to write strong, production-like tests against a black-box API.
It generates 999 mutated + 1 correct implementations of the same Tic-Tac-Toe engine and asks students to identify the single correct implementation using only their tests.
The system also tracks each student’s attempts and progress, allowing instructors to analyse how effectively students design and improve their test suites.

## Tech stack & requirements

- Java / JDK 21+
- Maven 3.9+
- Spring Boot (REST API)
- JUnit 5 (testing)
- Mockito (mocking)
- JSON file–based storage (file as db)

## Quick start (local setup)

This section shows how to run the project locally from a clean checkout.

**Prerequisites**

- Java / JDK 21+ installed
- Maven installed and available on `PATH`

**1. Clone the repository**

```bash
git clone https://github.com/DmytroHalai/black-box
cd black-box
```
This downloads the project and moves you into the project directory.

2. Build the project and run unit tests

This command runs e2e test, which generates and runs test-runner, but it doesn't run unit tests for game by itself
```
mvn clean install
```

Maven will download all dependencies, compile the code, and run the test suite.
You should see BUILD SUCCESS at the end.

3. Generate 1000 engine implementations
```
mvn exec:java@generate
```

This command creates 1000 mutated implementations of the Tic-Tac-Toe engine in the configured output directory.

4. Run all tests against all implementations

If you've generated new implementations and want to test them, you have to run command below
```
mvn test-compile && mvn exec:java@run-tests
```

But if don't, you may just run this command
```
mvn exec:java@run-tests
```

This runs the student test suite against all 1000 implementations and writes the summary to the tests_summary file in the project root.
tests_summary file  includes all the implementations, which passed all tests. If this file includes only 1 implementation, then the task is solved.

5. (Optional) Copy core files to another project
```
mvn exec:java@copy -DtargetPath=/absolute/path/to/student/project
```

This copies the game logic, test runner, test class, student's pom.xml and README.md into a student’s project, excluding all sections marked between //begin of private and //end of private.

## Running the web application

The web module exposes a REST API for generating implementation batches and tracking student progress.

### Start the application

From the project root, run:

```bash
mvn spring-boot:run
```
This starts the Spring Boot application.
By default, the API is available at:
`
http://localhost:8080
`

Configuration

The main configuration file is located at:

src/main/resources/application.properties

The web application uses a JSON file as a simple datastore for students. This file is automatically creates during application launch if it didn't exist.

## 1. Game

### Structure

#### Root (`src/main/java/org/example`)
- `Engine.java` - is the concrete Tic-Tac-Toe game engine that implements all abstract GameEngine logic: initializing the board,validating and applying moves, switching turns, tracking win/draw states, and reporting the current board and winner.

#### `src/main/java/org/example/logic/api`
- `BoardView` - defines an interface for accessing and printing the current 3×3 state of a Tic-Tac-Toe board.
- `GameEngine` - is an abstract base class that defines the full game logic contract for Tic-Tac-Toe (board state, turns, move validation, win/draw detection and helpers), which concrete engine implementations must realize.
- `IllegalMoveException` - represents an error thrown when a player attempts an invalid or rule-breaking move in the game.
- `Move` - is a simple immutable record that represents a single action in the game, storing the x-coordinate, y-coordinate, and the player making the move.
- `Player` - is an enum representing the two Tic-Tac-Toe players, X and O, and provides a helper method to switch between them.
- `Result` - is an enum representing the current game outcome: ongoing, X wins, O wins, or draw.
- `View` - is an immutable snapshot of the board that implements BoardView, mapping the engine’s internal 3×3 cell array to readable characters ('X', 'O', or ' ').

#### `src/main/java/org/example/logic/app`
- `Main` - is a simple console launcher that runs a human-vs-human Tic-Tac-Toe game using a chosen `GameEngine` implementation.

#### `src/main/java/org/example/logic/core`
- `BoardState` - is an immutable snapshot of the board that implements BoardView, internally storing a 3×3 grid as a cloned 9-cell array and providing safe read-only access to it.

Students have full access to this module.  
All generated implementations are based on the `Engine` class located in the project root.  
Tests must target the abstract API defined by `GameEngine`.  
All parts of the game module are documented.


## 2. Generator

The generator produces mutated implementations of the engine. Mutations represent typical programming mistakes and are applied using JavaParser.

- `BugLibrary` — defines available bug patterns  
- `BugMutation` — interface for applying an individual mutation  
- `BugRegistry` — registry storing mutation implementations  
- `Generator` — generates mutated implementation files and ensures that:
  - implementations are not duplicated  
  - mutations do not accidentally neutralize each other  

Tests for the generator are located in:  
`src/test/java/org/example/generator`

The end-to-end test:
1. Generates 1000 implementations in a temporary folder  
2. Executes the provided test suite  
3. Identifies the correct implementation  
4. Verifies that all mutation strategies behave as expected  


## 3. Test Runner

The Test Runner executes the student’s tests from  
`src/test/java/org/example/logic/api/GameEngineTest.java`  
against each generated implementation (1000 total).  
All tests must target the `GameEngine` abstraction.
The results of passing tests are printed in file tests_summary in project source root.


## 4. Copy Utility

The Copy Utility transfers selected files (game logic, test runner, test class, `pom.xml`, `README.md`) into another project.  
It simplifies updates to student repositories and eliminates manual copying mistakes.

Sections of code surrounded by:

```java
//begin of private
...
//end of private
```

are not copied.  
This prevents internal reference logic (for example, full test suites) from being exposed in student repositories.


## 5. Web Application

The web module interacts with students, generates implementations, and stores all data in a JSON-based datastore.

### Main Endpoints (`AppController`)

| Endpoint | Description |
|---------|-------------|
| `/generate/{studentData}` | Generates a `.zip` archive with 1000 implementations. Saves student data. Returns errors for duplicates or invalid input. |
| `/check/{studentData}/{num}` | Checks if the provided number matches the correct implementation. Stores each check. |
| `/all/active/admin` | Returns students who performed at least one check. |
| `/all/solved/admin` | Returns students who successfully identified the correct implementation. |
| `/all/admin` | Returns all students who received implementations. |

### Components

**Error Handler**  
Handles exceptions and returns consistent error responses.

**Models**
- `CheckResult` — timestamp and result of a single check  
- `Student` — entity containing student data (`name`, `num`, `checkResults`)  
- `ImplementationBatch` — DTO containing generated implementations and index of the correct one  

**Repository**
- `JsonRepo` — JSON file–based storage  
- `StudentRepo` — service-level wrapper around JSON storage  

**Service**  
Implements the business logic of the web API.


## Shell Scripts (Maven)

| Description | Command |
|------------|---------|
| Run all tests against all implementations | `mvn exec:java@run-tests` |
| Generate 1000 implementations | `mvn exec:java@generate` |
| Copy files to another project | `mvn exec:java@copy -DtargetPath={project_root}` |

`targetPath` must be the absolute path to a project root.


## Database Example

```json
[
  {
    "name": "Test",
    "correctImpl": 968,
    "checkResults": []
  }
]
```
