# Cricket Scorecard Management System

## Overview

The **Cricket Scorecard Management System** is a Java-based application that simulates a cricket match. Users can name both teams, select the number of overs, and input the outcome of each ball. The system tracks runs, wickets, and generates scorecards for both teams. It uses **JDBC with MySQL** to store match data and player statistics. At the end of the match, the system generates:
- A text file with the player scorecard for **Team 1**.
- A text file with the player scorecard for **Team 2**.
- A text file summarizing the outcomes of each ball and the final match result.

## Features

- Name both teams before starting the match.
- Set the number of overs for the match.
- Perform a toss to decide which team bats first.
- Enter the outcome of each ball (runs, wickets, extras, etc.).
- Track the current score, wickets, and overs in real-time.
- Save the match data in a MySQL database.
- Automatically generate 3 text files at the end of the match:
  - Player scorecard for both teams.
  - A match summary with the outcome of each ball and the final result.

## System Requirements

- **Java 8 or higher**
- **MySQL 5.7 or higher**
- **JDBC Driver for MySQL**
- **Maven** (optional, for dependency management)

## Setup Instructions

### 1. Clone the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/anshulmmodi/CricketScorecard.git
cd CricketScorecard
