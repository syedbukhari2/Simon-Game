# Implementation of Simon Game on SwiftBot

## Overview

A SwiftBot (also known as Trilobot) is a Raspberry Pi powered robot used throughout the first year of Computer Science at Brunel University of London.

### This robot features:
* 2x front wheel drive, 1x rear castor
* Four tactile buttons and status LEDs
* Six-zone RGB underlighting
* Front facing ultrasound distance sensor and camera mount.

<p align="center">
  <img src="https://github.com/user-attachments/assets/b967d64b-8b34-41eb-bc51-b4a53c372e53" alt="Simon" width="230" />
  &nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/aba33305-499e-4200-8015-221e93e61c57" alt="SwiftBot image" width="270" />
</p>

## 🚀 Project
This project implements a Simon game using the SwiftBot, where the player must repeat a sequence of button presses corresponding to LEDs on the bot. Each button (A, B, X, Y) is paired with a unique colour (Red, Blue, Green, Yellow) and a corresponding LED. The game starts with a random colour sequence and each round adds another colour. If the player presses an incorrect button, the game ends. Every 5 rounds, the player can choose to continue or quit. If the player achieves a score of 5 or more, the SwiftBot celebrates with a V-shaped dive while randomly blinking the LEDs. The game ends with a message, "See you again Champ!" if the player quits.

## 🛠️ Additional functionality  
In addition to the basic game requirements, I have enhanced the experience by introducing several custom features:  
* **In-game shop:** Players can purchase additional colours (Brown, Cyan, Orange, Pink, Purple) for 120 coins each, and assign them to buttons A, B, X, or Y.  
* **Currency system:** Players earn 50 coins after completing 5 rounds, which can be used to buy new colours.  
* **Difficulty levels:** Four difficulty levels are available, with increasing sequence lengths:  
  * Level 1: 3 colours  
  * Level 2: 4-6 colours  
  * Level 3: 7-10 colours  
  * Level 4: 11-15 colours  
* **Lives system:** Players can start with a set number of lives, losing one with each incorrect input, rather than the game ending immediately.  
* **Countdown warning:** A countdown appears before the SwiftBot performs its celebration dive, preventing it from falling off tables.  
* **Player profiles:** User data is stored in a text file, preserving coin balance and purchased colours across sessions.

## 🎬 Watch the demonstration
Check out the video below to see the game in action:
<br>

<p align="center">
  <a href="https://www.youtube.com/watch?v=B0ejUsY-uG8">
    <img src="https://img.youtube.com/vi/B0ejUsY-uG8/0.jpg" alt="Watch the demo">
  </a>
</p>

## 🏆 Achievements
This implementation resulted in our group being awarded the **"Best Group Award"** by the Module Leader and my implementation video was shared with the entire cohort.

