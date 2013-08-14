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
    print 'Error on set of trees'
    error_per_database_ls = []
    error_per_database_vs = []
    error_per_database_ts = []
    for database in databases:
        error_line = []
        print 'Error on '+database
        with open(database+'_error_set_of_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                error_per_database_ls.append(float(row[0]))
                error_per_database_vs.append(float(row[1]))
                error_per_database_ts.append(float(row[2]))
                print row[0]
                print row[1]
                print row[2]
            
    databases.remove('PIMA-INDIANS-DIABETES')
    databases.insert(2,'PIMA')
    
    w = 0.25   
    plt.xlabel('Databases')
    plt.ylabel('Error (%)')
    plt.title(r'Error with set of trees from Treece')
    rect1 = plt.bar(np.arange(len(error_per_database_ls))-w,
                    error_per_database_ls, width=w, color='blue', align='center')
    rect2 = plt.bar(np.arange(len(error_per_database_vs)),
                    error_per_database_vs, width=w, color='red', align='center')
    rect3 = plt.bar(np.arange(len(error_per_database_ts))+w,
                    error_per_database_ts, width=w, color='green', align='center')
    plt.autoscale(tight=True)
    plt.xticks(range(len(databases)), databases, size='small')
    plt.yticks(range(0,70,5), range(0,70,5), size='small')
    plt.legend( (rect1[0], rect2[0], rect3[0]), ('LS', 'VS', 'TS'))
    plt.savefig('mean_error_set_treece_learning.png')
    plt.savefig('mean_error_set_treece_learning.eps')
