#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np
import csv
import networkx as nx

cut = 0 #Cut or not

# Main
if __name__ == "__main__":
    for l in range(1,16):
        X = np.zeros((200,4))
        Y = np.zeros((200,))
        level = 'Level'+str(l)
        with open('Levels/'+level+' - Learning Set.csv', 'rb') as iris:
            reader = csv.reader(iris, delimiter=',')
            i = 0
            for row in reader:
                instance = []
                j = 0
                for k in range(len(row)-1):
                    X[i][j] = row[k]
                    j = j + 1
                Y[i] = row[len(row)-1]
                i = i + 1

        equations_info = []
        attributes_info = []
        with open(level+'_attribute_equation_info.csv', 'rb') as equations:
            reader = csv.reader(equations, delimiter=',')
            for row in reader:
                attributes_info.append([row[0],row[1]])
                equations_info.append([row[2],row[3]])

        attributes = []
        for i in range(0,4):
            for j in range(0,4):
                if i != j:
                    attributes.append([str(i),str(j)])
        
        pl.figure(figsize=(16,12))
        if cut == 1:
            pl.suptitle('All cuts of the '+level+' from Treece', fontsize=20)
        elif cut == 0:
            pl.suptitle('All Scatter Plots of the '+level+' from Treece', fontsize=20)
        pl.subplots_adjust(bottom=.05, top=.9, left=.05, right=.95)

        lines_to_draw = []
        for attr in attributes:
            indices = [i for i, x in enumerate(attributes_info) if x == attr]
            lines_to_draw.append(indices)
        
        for i in range(len(attributes)):
            ax = pl.subplot(3,4,i+1)
            line = lines_to_draw[i]
            if line != []:
                for j in line:
                    first_point_x = min(X[:, attributes[i][0]])
                    first_point_y = (float(equations_info[j][0])*first_point_x)+float(equations_info[j][1])
                    second_point_x = max(X[:, attributes[i][0]])
                    second_point_y = (float(equations_info[j][0])*second_point_x)+float(equations_info[j][1])
                    if cut == 1:
                        ax.add_line(pl.Line2D([first_point_x,second_point_x],[first_point_y,second_point_y]))
            ax.axis([min(X[:, attributes[i][0]])-.5,max(X[:, attributes[i][0]])+.5,min(X[:, attributes[i][1]])-.5,max(X[:, attributes[i][1]])+.5])
            pl.title('Attribute : '+str(attributes[i]))
            pl.scatter(X[:, attributes[i][0]], X[:, attributes[i][1]], c=Y, cmap=pl.cm.cool)

        if cut == 1:
            pl.savefig('Images/all_'+level+'_plot_with_cuts.eps')
            pl.savefig('Images/all_'+level+'_plot_with_cuts.png')
        elif cut == 0:
            pl.savefig('Images/all_'+level+'_plot.eps')
            pl.savefig('Images/all_'+level+'_plot.png')
        #pl.show()

