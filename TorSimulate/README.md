# TorSimulate
A simulator to improve our control software.
Stuff we need to add:
-A module for more accurate/flexible physics models
  -incorporate static/kinetic friction between the wheels and the ground
  -incorporate gearbox efficiency
  -keep track of total rotation of each side of the drivetrain
  
-A module to represent the plant. We will probably have two major uses for this 
    class: one to execute the java file that will run on the physical robot, and
    one to run prototypes (simpler controllers that we'll write in Python).

-A module for a sensor class. A sensor object should read from the module, add 
    noise or change units where appropriate, and feed that info to the plant.
    
-Other stuff too.
