#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division
import csv
import matplotlib.pyplot as plt
import numpy as np
import math


# Main
if __name__ == "__main__":
    print 'Mean error'
    error_per_level_ls = []
    error_per_level_ts = []
    standard_deviation_per_level_ls = []
    standard_deviation_per_level_ts = []
    levels_and_numberOfTrees = []
    for i in range(1,16):
        error_line = []
        level = 'Level'+str(i)
        with open(level+'_error_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                error_line.append(row)
        
        numberOfTrees = len(error_line)
        levels_and_numberOfTrees.append(level+': '+str(numberOfTrees)+' trees')
        if numberOfTrees > 0:
            sum_error_ls = 0
            sum_error_ts = 0
            sum_sq_error_ls = 0
            sum_sq_error_ts = 0
            for line in error_line:
                sum_error_ls = sum_error_ls + float(line[1])
                sum_error_ts = sum_error_ts + float(line[2])
                sum_sq_error_ls = sum_sq_error_ls + math.pow(float(line[1]),2)
                sum_sq_error_ts = sum_sq_error_ts + math.pow(float(line[2]),2)
            mean_error_ls = sum_error_ls/numberOfTrees
            mean_error_ts = sum_error_ts/numberOfTrees
            error_per_level_ls.append(mean_error_ls)
            error_per_level_ts.append(mean_error_ts)
            standard_deviation_ls = math.sqrt((sum_sq_error_ls/numberOfTrees) - (math.pow(mean_error_ls,2)))
            standard_deviation_ts = math.sqrt((sum_sq_error_ts/numberOfTrees) - (math.pow(mean_error_ts,2)))
            standard_deviation_per_level_ls.append(standard_deviation_ls)
            standard_deviation_per_level_ts.append(standard_deviation_ts)
            print level+' LS: '+str(mean_error_ls)
            print level+' TS: '+str(mean_error_ts)
            
        else:
            error_per_level_ls.append(0)
            error_per_level_ts.append(0)
            standard_deviation_per_level_ls.append(0)
            standard_deviation_per_level_ts.append(0)
            print 'No data for '+level
            
    f = open('number_of_trees_per_level.txt', 'w')
    for i in range(len(levels_and_numberOfTrees)):
        f.write(levels_and_numberOfTrees[i]+'\n')

    w = 0.35     
    plt.xlabel('Levels')
    plt.ylabel('Mean errors (%)')
    plt.title(r'Mean errors from Treece')
    rect1 = plt.bar(np.arange(len(error_per_level_ls))-w/2, error_per_level_ls,
                    width=w, yerr=standard_deviation_per_level_ls, ecolor='black',
                    color='red', align='center')
    rect2 = plt.bar(np.arange(len(error_per_level_ts))+w/2, error_per_level_ts,
                    width=w, yerr=standard_deviation_per_level_ts, ecolor='black',
                    color='green', align='center')
    plt.autoscale(tight=True)
    plt.xticks(range(0,15),range(1,16), size='small')
    plt.yticks(range(0,55,5), range(0,55,5), size='small')
    plt.legend( (rect1[0], rect2[0]), ('LS', 'TS'))
    plt.savefig('mean_error_treece_game.png')
    plt.savefig('mean_error_treece_game.eps')
