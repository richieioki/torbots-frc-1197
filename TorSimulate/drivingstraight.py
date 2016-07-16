# -*- coding: utf-8 -*-
"""
Created on Sun Jul 10 11:05:10 2016

@author: Joe
"""

import numpy as np

class DrivingStraight:
    # system constants
    m = 65.0                # mass of robot [kg]
    R = 0.0508              # wheel radius [m]
    G = 3.54                # gear reduction
    N = 6.0                 # number of motors
    Vm = 12.0               # motor's rated voltage
    T = 2.41*N              # stall torque [N*m]
    W = 558.2               # free speed [rad/s]
    Imax = 131.0*N          # stall current [amps]
    Imin = 2.7*N            # free current [amps]
    Z = Vm/Imax             # motor winding resistance [ohms]
    VG = Vm*(1-(Imin/Imax)) # maximum back emf [volts]
    j=G*VG/(R*W)
    k=m*R*Z*(Imax-Imin)/(G*T)
    
    def __init__(self, dt):
        self.dt = dt
        self.s0 = 0.0   #initial robot position
        self.s1 = 0.0   #initial robot velocity
        self.s2 = 0.0   #initial robot acceleration
        self.x = np.matrix([[self.s0],[self.s1]])
        # matrix machinery
        a = 1
        b = self.dt-(self.j*dt**2/(2*self.k))
        c = 0
        d = 1-(self.j*self.dt/self.k)
        e = self.dt**2/(2*self.k)
        f = self.dt/self.k
        self.A = np.matrix([[a, b], [c, d]])
        self.B = np.matrix([[e],[f]])
    
    def update(self):
        self.u = 12-self.Imin*self.Z              #commanded voltage
        self.x = self.A*self.x + self.B*self.u
        return self.x.A1[0], self.x.A1[1]