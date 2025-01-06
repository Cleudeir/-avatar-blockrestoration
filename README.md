## Summary

This Minecraft Forge mod, "Avatar Block Restoration," automatically restores broken blocks within a configurable radius of a designated "main block."  The mod tracks broken blocks and their original states, persistently saving this data across server restarts.  It provides visual feedback using particle animations and handles block placement and breaking within the restoration area.  The mod is configurable, allowing server operators to customize the main block type and restoration radius.  The core functionality is implemented using Forge event listeners that respond to server ticks, block placements, and block breaks.  The mod uses a custom configuration system for persistent storage.


## Tech Stack

Java, Minecraft Forge, Forge Config Spec,  Particle effects,  Event Handling (ServerTickEvent, BlockEvent.EntityPlaceEvent, ServerStoppingEvent, BreakEvent)
