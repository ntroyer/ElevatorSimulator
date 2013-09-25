ElevatorSimulator
=================

Simulates an elevator for an assignment in CS 350 at BU

To run: Run either ElevatorSynchro or ElevatorMaintenance in a Java compiler.

Explanation:

ElevatorSynchro

- Simulates an elevator in a building with 20 occupants and 5 floors. The elevator can hold a maximum of 3 occupants and 
uses a SCAN approach to servicing requests by occupants and will sleep for the time it takes to move from one floor to '
another while moving to another floor.

ElevatorMaintenance

- Simulates the above elevator, only a maintenance man will come and clean the elevator when requested.  The man will be requested
after 80 floors have been traveled.  When a request is made, the elevator will go to the first floor, then sleep for a specific
amount of time while the elevator is being cleaned, then the janitor will exit and the elevator will serve the next request.

Customer

- Object class for a person moving about the building.
