#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division
import csv
import matplotlib.pyplot as plt
import numpy as np
import math


# Main
if __name__ == "__main__":
    print 'Error on set of trees'
    error_per_level_ls = []
    error_per_level_vs = []
    error_per_level_ts = []
    for l in range(1,16):
        level = 'Level'+str(l)
        error_line = []
        print 'Error on '+level
        with open(level+'_error_set_of_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                error_per_level_ls.append(float(row[0]))
                error_per_level_ts.append(float(row[1]))
                print row[0]
                print row[1]
    
    w = 0.35  
    plt.xlabel('Level')
    plt.ylabel('Error (%)')
    plt.title(r'Error with set of trees from Treece')
    rect1 = plt.bar(np.arange(len(error_per_level_ls))-w/2,
                    error_per_level_ls, width=w, color='red', align='center')
    rect2 = plt.bar(np.arange(len(error_per_level_ts))+w/2,
                    error_per_level_ts, width=w, color='green', align='center')
    plt.autoscale(tight=True)
    plt.xticks(range(0,15), range(1,16), size='small')
    plt.yticks(range(0,55,5), range(0,55,5), size='small')
    plt.legend( (rect1[0], rect2[0]), ('LS', 'TS'))
    plt.savefig('mean_error_set_treece_game.png')
    plt.savefig('mean_error_set_treece_game.eps')
