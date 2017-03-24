# Monte Carlo Localization with particle filtering

It is a very sensitive system, since the main purpose of it was the algorithm itself. This was later integrated in a robot warehouse system.

The program first creates a map in the Localization.java class. You can modify that method to create your own map.

After that, the inputs are provided from the keyboard, and each input represent a movement or a sensed data from the surroundings.

The order of the input is the following: first you input the character corresponding to an action (goind FORWARD/BACKWARD/RIGHT/LEFT) and then you input four more characters representing the surrounding, in order front/right/back/left. 

NOTE: This is assumed to be a grid map.

### Command list

These are just simple characters, more precisely numbers.

1. Go forward = 1
2. Go right = 2
3. Go backward = 3
4. Go left = 4

Now the commands for the surroundings:

1. You have a wall in that direction, right next to you = 1
2. You don't have a wall in that direction, right next to you = 0

### Example

Consider you start in a grid from where you can move forward. So you move forward and you input 1. After this you have to input the surroundings. Suppose you have a wall in your front, no wall on right, no wall in back and wall on left. The inputs will be: 1 0 0 1.

#### Feel free to use at and improve it because it can be improved. For example the resampling can be improved, or it can be transformed to work in continuous spaces!
