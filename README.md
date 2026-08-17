# BakeRace 🧁

BakeRace is a real-time, multiplayer, text-based baking quiz game built with a client-server architecture. Players join a shared lobby, compete to answer baking questions, and receive live score updates throughout the game.

## Overview

BakeRace challenges players across five timed rounds of baking-related questions. In each round, the server sends the same question to all connected players simultaneously. The first player to submit the correct answer within the time limit earns one point.

Questions gradually increase in difficulty, starting with basic ingredients and progressing to more detailed baking knowledge and preparation steps.

## Features

- Real-time multiplayer gameplay using a client-server model
- Username-based player registration
- Shared waiting room for connected players
- Automatic game start when the maximum number of players joins or after 30 seconds
- Five timed rounds with a 15-second limit per question
- Simultaneous question delivery to all players
- Live score updates broadcast to every connected player
- Progressive question difficulty
- Real-time player leave notifications and player-list updates
- Automatic game completion when only one player remains
- Winner and tie detection based on final scores

## Game Flow

1. Players connect to the server and enter a username.
2. Players wait in the lobby until the maximum player count is reached or the 30-second waiting period ends.
3. The server starts the game and sends one baking question per round.
4. Players have 15 seconds to submit their answer.
5. The first player to answer correctly earns one point.
6. The server updates and broadcasts the scores in real time.
7. After five rounds, the player with the highest score wins. If multiple players have the same highest score, the game ends in a tie.

## Example Round

**Question:** What is the main baking ingredient used in cupcake batter?  
**Time Limit:** 15 seconds  
**Correct Answer:** Flour  

The first player to submit the correct answer receives one point, and the server broadcasts the updated scores to all connected players.

## Player Management

Players may leave the game at any time. When a player leaves, the server notifies all remaining players and updates the player list. If only one player remains, the server ends the game and declares that player the winner.

## Architecture

BakeRace follows a client-server design:

- **Server:** Manages player connections, the waiting room, questions, timers, scores, and game results.
- **Client:** Allows players to join the game, submit answers, view live updates, and leave the session.

## Team

**Idea Owner:** Fatimah Alzeer  

**Team Members:**
- Noura bin Moammar
- Bateel Almojel
- Taif Alqahtani
