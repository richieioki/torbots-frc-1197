#import matplotlib
#matplotlib.use('TkAgg')
import matplotlib.patches as pch            # Patches are shapes. Used for
                                            #   drawing the robot.
import matplotlib.pyplot as plt
import matplotlib.animation as animation    # Make the plots dance!
import drivingstraight as ds                # Dummy model for now (7/9/16)
import drawing as drw                       # Lets us treat robot geometry as
                                            #   as a single object, reducing
                                            #   typing needed to animate it.

# simulation parameters
DT = 0.001             # simulation timestep [s]
CONTROL_INTERVAL = 0.01              # control update timestep [s]
TEST_DURATION = 2.0     # length of simulation [s]
FRAME_INTERVAL = 0.020  # time between drawing each frame [s]

def simulate(t=0):
    s0 = 0.0;   # initial values for position and velocity
    s1 = 0.0;
    model = ds.DrivingStraight(DT)      # Initialize the model. 
    while t <= TEST_DURATION:
        if (t%FRAME_INTERVAL) < DT:      # If we're on a frame display step,
            yield t, s0, s1             #   return control to FuncAnimation
        #if (t%CONTROL_INTERVAL) < DT:   # If we're on a control-update step,
            #plant.control()             #   run controller update here
        t += DT                         # Go to the next timestep.
        s0, s1 = model.update()         # Figure out the next state.
    yield t, s0, s1                     # Don't forget the last one!

fig, axes_array = plt.subplots(1, 2)    # Set up figure w/ 2 plots side-by-side
#fig.set_size_inches(12, 6, forward=True)

s0line, = axes_array[0].plot([], [], lw=2)  # curve for position vs. time
s1line, = axes_array[0].plot([], [], lw=2)  # curve for velocity vs. time

axes_array[0].grid()                # Display grid on state plot.
axes_array[1].grid()                # Display grid on robot animation.
xdata, s0data, s1data = [], [], []  # TODO: Find better name than "data".

time_template = 'time = %.2fs'    # Just copied from "double pendulum" example.
time_text = axes_array[0].text(.05, .9, '', transform=axes_array[0].transAxes)

rectangle = pch.Rectangle((-.35,-.40), 0.70, 0.80)  # Draw the robot as a red 
rectangle.set_edgecolor('none')                     #   rectangle...
rectangle.set_facecolor('r')
arrow = pch.FancyArrow(0.0, -0.3, 0.0, 0.4, width = 0.1,    
                       head_width =0.3, head_length = 0.2) # ...with an arrow
arrow.set_edgecolor('none')            # so you can see where it's pointed.
arrow.set_facecolor('w')
robot = drw.Drawing(axes_array[1])     # Create robot drawing object.
robot.add_shape(rectangle)             # Add the geometry we just created.
robot.add_shape(arrow)
axes_array[0].set_ylim(0, 15)   # Set limits for each axis on each plot.
axes_array[0].set_xlim(0, 2.5)
axes_array[1].set_ylim(-1, 15)
axes_array[1].set_xlim(-10, 10)
axes_array[1].set_aspect('equal', 'datalim') # Make sure graphics plot isn't
                                             #   distorted.
def animate(input): # Update the data to display.
    t, s0, s1 = input
    
    if t<DT:
        del xdata[:]  # Clear the "data" :P
        del s0data[:]
        del s1data[:]
    
    xdata.append(t)
    s0data.append(s0)
    s1data.append(s1)
    
    s0line.set_data(xdata, s0data)
    s1line.set_data(xdata, s1data)
    time_text.set_text(time_template % t)
    
    robot.rotate_to(0)
    robot.translate_to(0, s0)
        
    return s0line, s1line, time_text, robot,

# Set up formatting for the movie files.
#Writer = animation.writers['ffmpeg']
#writer = Writer(fps=(1/FRAME_INTERVAL), metadata=dict(artist='Me'), bitrate=7200)

ani = animation.FuncAnimation(fig, animate, simulate,
                              interval=(FRAME_INTERVAL*1000),
                              repeat=False, repeat_delay=2000,
                              blit=False)
# Weird bug: if blit=True python randomly decides which subplot to animate.

#ani.save('simulator.mp4', writer=writer)

plt.show()
#TODO: Make the window pop up at a more convenient/larger size.