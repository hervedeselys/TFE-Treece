#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division
import csv
import matplotlib.pyplot as plt
import numpy as np
from sklearn.tree import DecisionTreeClassifier
import math


databases = ['IRIS','BREASTCANCER','PIMA-INDIANS-DIABETES','VEHICLE','LANDSAT']

param = 'Depth' #Node or Depth

def depth_function(tree, node=0):
    if tree.children_left[node] == -1:
        return 1
    else:
        return 1 + max(depth_function(tree, tree.children_left[node]), depth_function(tree, tree.children_right[node]))

# Main
if __name__ == "__main__":
    if param is 'Node':
        print 'Mean number of nodes per database'
        mean_number_of_nodes_per_database = []
        number_of_nodes_per_database_dtc = []
        standard_deviation_per_database_node = []
    elif param is 'Depth':
        print 'Mean depth of tree per database'
        mean_depth_of_tree_per_database = []
        depth_of_the_tree_per_database_dtc = []
        standard_deviation_per_database_depth = []
    for database in databases:
        if database is 'IRIS':
            numberOfLines = 120
            numberOfColumns = 4
        elif database is 'BREASTCANCER':
            numberOfLines = 546
            numberOfColumns = 9
        elif database is 'PIMA-INDIANS-DIABETES':
            numberOfLines = 614
            numberOfColumns = 8
        elif database is 'VEHICLE':
            numberOfLines = 677
            numberOfColumns = 18
        elif database is 'LANDSAT':
            numberOfLines = 3548
            numberOfColumns = 36
        error_line = []
        with open(database+'_error_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                error_line.append(row)
             
        numberOfTrees = len(error_line)
        if numberOfTrees > 0:       
            if param is 'Node':
                sum_of_nodes = 0
                sum_sq_error_node = 0
            elif param is 'Depth':
                sum_of_tree = 0
                sum_sq_error_depth = 0
            for line in error_line:                      
                if param is 'Node':
                     sum_of_nodes = sum_of_nodes + float(line[4])
                     sum_sq_error_node = sum_sq_error_node + math.pow(float(line[4]),2)
                elif param is 'Depth':
                     sum_of_tree = sum_of_tree + float(line[5])
                     sum_sq_error_depth = sum_sq_error_depth + math.pow(float(line[5]),2)
                     
            if param is 'Node':
                mean_number_of_nodes = sum_of_nodes/numberOfTrees
                standard_deviation_node = math.sqrt((sum_sq_error_node/numberOfTrees) - (math.pow(mean_number_of_nodes,2)))
                standard_deviation_per_database_node.append(standard_deviation_node)
                mean_number_of_nodes_per_database.append(mean_number_of_nodes)
                print 'Database '+database+': '+str(mean_number_of_nodes)+' nodes'
            elif param is 'Depth':
                mean_depth_of_trees = sum_of_tree/numberOfTrees
                standard_deviation_depth = math.sqrt((sum_sq_error_depth/numberOfTrees) - (math.pow(mean_depth_of_trees,2)))
                standard_deviation_per_database_depth.append(standard_deviation_depth)
                mean_depth_of_tree_per_database.append(mean_depth_of_trees)
                print 'Database '+database+': '+str(mean_depth_of_trees)+' depth'
            
        else:
            if param is 'Node':
                mean_number_of_nodes_per_database.append(0)
                standard_deviation_per_database_node.append(0)
            elif param is 'Depth':
                mean_depth_of_tree_per_database.append(0)
                standard_deviation_per_database_depth.append(0)
            print 'No data for '+database

        X_ls = np.zeros((numberOfLines,numberOfColumns))
        Y_ls = np.zeros((numberOfLines,))

        if database is 'IRIS':
            database = 'IRISM'
        if database is 'VEHICLE':
            database = 'VEHICLEM'
        with open('Datasets/'+database+' - Learning Set.csv', 'rb') as ls:
            reader = csv.reader(ls, delimiter=',')
            i = 0
            for row in reader:
                j = 0
                for k in range(len(row)-1):
                    X_ls[i][j] = row[k]
                    j = j + 1
                Y_ls[i] = row[4]
                i = i + 1
                
        clf = DecisionTreeClassifier()
        clf = clf.fit(X_ls,Y_ls)
        if param is 'Node':
            node_count = clf.tree_.node_count
            print 'Node count: '+str(node_count)
            number_of_nodes_per_database_dtc.append(node_count)
        elif param is 'Depth':
            depth = depth_function(clf.tree_)
            print 'Depth of the tree: '+str(depth)
            depth_of_the_tree_per_database_dtc.append(depth)

    databases.remove('PIMA-INDIANS-DIABETES')
    databases.insert(2,'PIMA')
    
    plt.xlabel('Databases')
    w = 0.35
    if param is 'Node':
        plt.ylabel('Nnumber of nodes')
        plt.title(r'Number of nodes in each database')
        rect1 = plt.bar(np.arange(len(mean_number_of_nodes_per_database))-w/2,
                        mean_number_of_nodes_per_database,width=w,
                        yerr=standard_deviation_per_database_node, ecolor='black',
                        color='green', align='center')
        rect2 = plt.bar(np.arange(len(number_of_nodes_per_database_dtc))+w/2,
                        number_of_nodes_per_database_dtc,width=w,
                        color='red', align='center')
    elif param is 'Depth':
        plt.ylabel('Depth of tree')
        plt.title(r'Depth of tree in each database')
        rect1 = plt.bar(np.arange(len(mean_depth_of_tree_per_database))-w/2,
                        mean_depth_of_tree_per_database,width=w,
                        yerr=standard_deviation_per_database_depth, ecolor='black',
                        color='green', align='center')
        rect2 = plt.bar(np.arange(len(depth_of_the_tree_per_database_dtc))+w/2,
                        depth_of_the_tree_per_database_dtc,width=w,
                        color='red', align='center') 
    plt.autoscale(tight=True)
    plt.xticks(range(len(databases)), databases, size='small')
    if param is 'Node':
        plt.yticks(range(0,405,50), range(0,405,50), size='small')
    elif param is 'Depth':
        plt.yticks(range(0,45,5), range(0,45,5), size='small')
    plt.legend( (rect1[0], rect2[0]), ('Treece', 'DecisionTreeClassifier'))  
    if param is 'Node':
        plt.savefig('number_of_node_learning.png')
        plt.savefig('number_of_node_learning.eps')
    elif param is 'Depth':
        plt.savefig('depth_of_tree_learning.png')
        plt.savefig('depth_of_tree_learning.eps')
