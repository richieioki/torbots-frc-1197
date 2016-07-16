# -*- coding: utf-8 -*-
"""
Created on Sun Jul 10 10:59:09 2016

@author: Joe
"""

import matplotlib.transforms as xfrm

class Drawing:
    
    patches = []       # List of matplotlib.patch objects in the drawing
    x = 0.0            # Drawing location and orientation in the plot
    y = 0.0
    theta = 0.0
    
    def __str__(self):
        return str(self.__class__).split('.')[-1]
    
    def __init__(self, axes):   # Constructor requires a matplotlib.axes,
        self.axes = axes        #   otherwise transforms will be invalid
        self.figure = axes.get_figure()
    
    def add_shape(self, patch):
        self.patches.append(self.axes.add_patch(patch))
        
    def translate_to(self, x, y):
        self.x = x
        self.y = y
        for patch in self.patches:
            patch.set_transform(xfrm.Affine2D().translate(self.x, self.y) 
                                +xfrm.Affine2D().rotate_around(self.x, self.y
                                                               , self.theta)
                                + self.axes.transData)
    
    def rotate_to(self, theta):
        self.theta = theta
        for patch in self.patches:
            patch.set_transform(xfrm.Affine2D().translate(self.x, self.y) 
                                +xfrm.Affine2D().rotate_around(self.x, self.y
                                                               , self.theta)
                                + self.axes.transData)
    
    def set_animated(self, b):
        for patch in self.patches:
            patch.set_animated(b)
            
    def set_visible(self, b):
        for patch in self.patches:
            patch.set_visible(b)
    
    def draw(self, renderer):
        for patch in self.patches:
            patch.draw(renderer)