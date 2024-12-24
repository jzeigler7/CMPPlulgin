# CMPPlugin for Minecraft

## Overview
CMPPlugin is a custom Minecraft Bukkit plugin designed to create a unique multiplayer experience involving progressive gameplay phases and competitive scoring. Players gather resources, defend their bases, and accumulate points through strategic planning and gameplay.

## Features
1. **Phase-Based Gameplay**:
   - **Phase 1**: Build and gather resources while PVP is disabled.
   - **Break**: Mandatory cooldown period for players.
   - **Phase 2**: PVP enabled, free-for-all base raiding and defense.
   - **Phase 3**: Resource accumulation with PVP disabled.
2. **Custom Scoring**:
   - Points are assigned to various in-game items, stored in player chests, inventories, and Ender Chests.
   - Shulker boxes and nested inventories are fully appraised.
3. **Player Commands**:
   - `/startCMP`: Starts the game phases (admin-only).
   - `/getPhase`: Displays the current game phase.
   - `/setRef`: Sets the player's reference point for chest scoring.
   - `/scoreMe`: Calculates the player's score.
   - `/appraise`: Displays the value of the item in the player's hand.
4. **Event Management**:
   - Prevents PVP damage during restricted phases.
   - Displays phase-specific instructions to players upon joining the server.

## Installation
1. Download the CMPPlugin `.jar` file.
2. Place the `.jar` file in the `plugins` directory of your Minecraft server.
3. Restart the server to load the plugin.

## Usage
### Admin Commands
- **Start the Game**:
  ```
  /startCMP <phase1_minutes> <break_minutes> <phase2_minutes> <phase3_minutes>
  ```
  Initiates the CMP gameplay cycle.

### Player Commands
- **Set Reference Point**:
  ```
  /setRef
  ```
  Establishes the starting point for scoring based on adjacent chests.

- **View Current Phase**:
  ```
  /getPhase
  ```
  Displays the active game phase.

- **Calculate Score**:
  ```
  /scoreMe
  ```
  Tallies the points for all owned chests and inventories.

- **Appraise Items**:
  ```
  /appraise
  ```
  Shows the value of the currently held item stack.

## Gameplay Phases
1. **Phase 1**:
   - Players gather resources, hide their bases, and set traps.
   - Chests must be marked with a sign containing the player's username for scoring.
   - PVP is disabled.

2. **Break**:
   - Players are temporarily kicked and notified of the break duration.

3. **Phase 2**:
   - PVP enabled; players raid each other’s bases.
   - Objective: Gain the most points through resources and items.
   - The Dragon Egg grants a massive point bonus.

4. **Phase 3**:
   - PVP is disabled.
   - Players dismantle their bases and accumulate resources for final scoring.

## Scoring
1. **Chest Scoring**:
   - Points are calculated from all marked storage containers, including adjacent and nested containers.
   - Example: Shulker boxes inside chests are appraised.

2. **Points Table**:
   - Materials are assigned specific point values (e.g., Dirt: 0.01 points, Diamond: 200 points, Dragon Egg: 1,500,000 points).
   - Comprehensive scoring ensures fair play.

3. **Command Scoring**:
   - `/scoreMe` computes points for:
     - All marked chests.
     - Player inventory.
     - Ender Chest contents.

## Events and Listeners
- **PVP Restrictions**:
  PVP damage is automatically canceled in restricted phases.
- **Player Join Messages**:
  New players receive phase-specific instructions.

## Dependencies
- Minecraft Bukkit/Spigot server API.



