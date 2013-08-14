#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division
import csv
import matplotlib.pyplot as plt
import numpy as np
import math

databases = ['IRIS','BREASTCANCER','PIMA-INDIANS-DIABETES','VEHICLE','LANDSAT']

# Main
if __name__ == "__main__":
    print 'Mean error'
    error_per_database_ls = []
    error_per_database_vs = []
    error_per_database_ts = []
    standard_deviation_per_database_ls = []
    standard_deviation_per_database_vs = []
    standard_deviation_per_database_ts = []
    databases_and_numberOfTrees = []
    for database in databases:
        error_line = []
        with open(database+'_error_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                error_line.append(row)
        
        numberOfTrees = len(error_line)
        if database is 'PIMA-INDIANS-DIABETES':
            database = 'PIMA'
        databases_and_numberOfTrees.append(database+': '+str(numberOfTrees)+' trees')
        if numberOfTrees > 0:
            sum_error_ls = 0
            sum_error_vs = 0
            sum_error_ts = 0
            sum_sq_error_ls = 0
            sum_sq_error_vs = 0
            sum_sq_error_ts = 0
            for line in error_line:
                sum_error_ls = sum_error_ls + float(line[1])
                sum_error_vs = sum_error_vs + float(line[2])
                sum_error_ts = sum_error_ts + float(line[3])
                sum_sq_error_ls = sum_sq_error_ls + math.pow(float(line[1]),2)
                sum_sq_error_vs = sum_sq_error_vs + math.pow(float(line[2]),2)
                sum_sq_error_ts = sum_sq_error_ts + math.pow(float(line[3]),2)
            mean_error_ls = sum_error_ls/numberOfTrees
            mean_error_vs = sum_error_vs/numberOfTrees
            mean_error_ts = sum_error_ts/numberOfTrees
            error_per_database_ls.append(sum_error_ls/numberOfTrees)
            error_per_database_vs.append(sum_error_vs/numberOfTrees)
            error_per_database_ts.append(sum_error_ts/numberOfTrees)
            standard_deviation_ls = math.sqrt((sum_sq_error_ls/numberOfTrees)
                                              - (math.pow((mean_error_ls),2)))
            standard_deviation_vs = math.sqrt((sum_sq_error_vs/numberOfTrees)
                                              - (math.pow((mean_error_vs),2)))
            standard_deviation_ts = math.sqrt((sum_sq_error_ts/numberOfTrees)
                                              - (math.pow((mean_error_ts),2)))
            standard_deviation_per_database_ls.append(standard_deviation_ls)
            standard_deviation_per_database_vs.append(standard_deviation_vs)
            standard_deviation_per_database_ts.append(standard_deviation_ts)
            print 'Database '+database+' on LS: '+str(mean_error_ls)
            print 'Database '+database+' on VS: '+str(mean_error_vs)
            print 'Database '+database+' on TS: '+str(mean_error_ts)
        else:
            error_per_database_ls.append(0)
            error_per_database_vs.append(0)
            error_per_database_ts.append(0)
            standard_deviation_per_database_ls.append(0)
            standard_deviation_per_database_vs.append(0)
            standard_deviation_per_database_ts.append(0)
            print 'No data for '+database

    databases.remove('PIMA-INDIANS-DIABETES')
    databases.insert(2,'PIMA')
    
    f = open('number_of_trees_per_database.txt', 'w')
    for i in range(len(databases_and_numberOfTrees)):
        f.write(databases_and_numberOfTrees[i]+'\n')
    f.close()

    w = 0.25   
    plt.xlabel('Databases')
    plt.ylabel('Mean errors (%)')
    plt.title(r'Mean errors from Treece')
    rect1 = plt.bar(np.arange(len(error_per_database_ls))-w,
                    error_per_database_ls, width=w,
                    yerr=standard_deviation_per_database_ls,
                    ecolor='black', color='blue', align='center')
    rect2 = plt.bar(np.arange(len(error_per_database_vs)),
                    error_per_database_vs, width=w,
                    yerr=standard_deviation_per_database_vs,
                    ecolor='black', color='red', align='center')
    rect3 = plt.bar(np.arange(len(error_per_database_ts))+w,
                    error_per_database_ts, width=w,
                    yerr=standard_deviation_per_database_ts,
                    ecolor='black', color='green', align='center')
    plt.autoscale(tight=True)
    plt.xticks(range(len(databases)), databases, size='small')
    plt.yticks(range(0,70,5), range(0,70,5), size='small')
    plt.legend( (rect1[0], rect2[0], rect3[0]), ('LS', 'VS', 'TS'))
    plt.savefig('mean_error_treece_learning.png')
    plt.savefig('mean_error_treece_learning.eps')
