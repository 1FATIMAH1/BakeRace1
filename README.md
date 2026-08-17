# BakeRace 🧁

BakeRace is a real-time multiplayer baking quiz game built in Java using a client-server architecture. Players join a shared lobby, compete to answer baking questions, and receive live score updates through an interactive desktop interface.

## Overview

BakeRace challenges players across three timed rounds of baking-related questions. In each round, the server sends the same question to all connected players simultaneously. The first player to submit the correct answer earns one point.

Questions increase in difficulty, starting with basic baking ingredients and progressing to more detailed baking knowledge.

## Features

- Real-time multiplayer gameplay using a client-server model
- Java Swing desktop interface
- Username-based player registration
- Shared waiting room for connected players
- Support for up to four players
- Automatic game start when the lobby is full or after 30 seconds
- Three timed question rounds with a 15-second timer
- Simultaneous question delivery to all players
- Live score updates for connected players
- Progressive question difficulty
- Player leave notifications and real-time player-list updates
- Automatic winner announcement when only one player remains
- Sound effects and custom visual assets

## Game Flow

1. Players start the client application and enter a username.
2. Players join the waiting room.
3. The game starts when four players are ready or after the 30-second waiting period.
4. The server sends the same baking question to all players.
5. Players have 15 seconds to submit an answer.
6. The first player to answer correctly earns one point.
7. The server broadcasts the updated scores to all active players.
8. After the final round, the player with the highest score is declared the winner.

## Example Round

**Question:** What is the main baking ingredient used in cupcake batter?  
**Time Limit:** 15 seconds  
**Correct Answer:** Flour  

The first player to submit the correct answer receives one point, and the server broadcasts the updated scores to all connected players.

## Architecture

BakeRace follows a client-server design:

- **Server:** Manages client connections, the waiting room, questions, game rounds, timers, scores, and game results.
- **Client:** Provides the graphical interface for joining the game, submitting answers, viewing scores, and leaving the session.
- **Communication:** Uses socket-based communication between the server and connected clients.

## Technologies

- Java
- Java Swing and AWT
- Java Sockets (`ServerSocket` and `Socket`)
- Multithreading
- Java I/O
- Java Sound API

## How to Run

### Prerequisites

- Java Development Kit (JDK)
- A Java IDE such as IntelliJ IDEA, Eclipse, or NetBeans

### Steps

1. Clone this repository.

   ```bash
   git clone https://github.com/1FATIMAH1/BakeRace1.git
   ```

2. Open the project in your Java IDE.

3. Make sure the `resources` folder is marked as a resources directory.

4. Run `BakeRaceServer.java` first. The server starts on port `9090`.

5. Run `BakeRaceClientFrame.java` for each player who wants to join the game.

6. Enter a username, join the waiting room, and start playing.

## Team

**Idea Owner:** Fatimah Alzeer  

**Team Members:**
- Noura bin Moammar
- Bateel Almojel
- Taif Alqahtani
