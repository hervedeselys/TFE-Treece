#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division
import csv
import matplotlib.pyplot as plt
import numpy as np
from sklearn.tree import DecisionTreeClassifier
import math

param = 'Node' #Node or Depth

def depth_function(tree, node=0):
    if tree.children_left[node] == -1:
        return 1
    else:
        return 1 + max(depth_function(tree, tree.children_left[node]), depth_function(tree, tree.children_right[node]))

def autolabel(rects):
# attach some text labels
    for ii,rect in enumerate(rects):
        height = rect.get_height()
        plt.text(rect.get_x()+rect.get_width()/2., 1.02*height, '%s'% (numberOfTrees_per_level[ii]),
                ha='center', va='bottom')

# Main
if __name__ == "__main__":
    if param is 'Node':
        print 'Mean number of nodes per level'
        mean_number_of_nodes_per_level = []
        number_of_nodes_per_level_dtc = []
        standard_deviation_per_level_node = []
    elif param is 'Depth':
        print 'Mean depth of tree per level'
        mean_depth_of_tree_per_level = []
        depth_of_the_tree_per_level_dtc = []
        standard_deviation_per_level_depth = []
    numberOfTrees_per_level = [] 
    for i in range(1,16):
        error_line = []
        level = 'Level'+str(i)
        with open(level+'_error_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')

            for row in reader:
                error_line.append(row)
        
        numberOfTrees = len(error_line)
        numberOfTrees_per_level.append('('+str(numberOfTrees)+')')
        if numberOfTrees > 0:
            if param is 'Node':
                sum_of_nodes = 0
                sum_sq_error_node = 0
            elif param is 'Depth':
                sum_of_tree = 0
                sum_sq_error_depth = 0
            for line in error_line:                      
                if param is 'Node':
                     sum_of_nodes = sum_of_nodes + float(line[3])
                     sum_sq_error_node = sum_sq_error_node + math.pow(float(line[3]),2)
                elif param is 'Depth':
                     sum_of_tree = sum_of_tree + float(line[4])
                     sum_sq_error_depth = sum_sq_error_depth + math.pow(float(line[4]),2)
                
            if param is 'Node':
                mean_number_of_nodes = sum_of_nodes/numberOfTrees
                standard_deviation_node = math.sqrt((sum_sq_error_node/numberOfTrees) - (math.pow(mean_number_of_nodes,2)))
                standard_deviation_per_level_node.append(standard_deviation_node)
                mean_number_of_nodes_per_level.append(mean_number_of_nodes)
                print level+': '+str(mean_number_of_nodes)+' nodes'
            elif param is 'Depth':
                mean_depth_of_trees = sum_of_tree/numberOfTrees
                standard_deviation_depth = math.sqrt((sum_sq_error_depth/numberOfTrees) - (math.pow(mean_depth_of_trees,2)))
                standard_deviation_per_level_depth.append(standard_deviation_depth)
                mean_depth_of_tree_per_level.append(mean_depth_of_trees)
                print level+': '+str(mean_depth_of_trees)+' depth'
        else:
            mean_number_of_nodes_per_level.append(0)
            mean_number_of_trees_per_level.append(0)
            standard_deviation_per_level_node.append(0)
            standard_deviation_per_level_depth.append(0)
            print 'No data for '+level

        X_ls = np.zeros((200,4))
        Y_ls = np.zeros((200,))
        with open('Levels/Level'+str(i)+' - Learning Set.csv', 'rb') as ls:
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
            number_of_nodes_per_level_dtc.append(node_count)
        elif param is 'Depth':
            depth = depth_function(clf.tree_)
            print 'Depth of the tree: '+str(depth)
            depth_of_the_tree_per_level_dtc.append(depth)
            
    plt.xlabel('Levels')
    w = 0.35
    if param is 'Node':
        plt.ylabel('Nnumber of nodes')
        plt.title(r'Number of nodes in each level')
        rect1 = plt.bar(np.arange(len(mean_number_of_nodes_per_level))-w/2,
                        mean_number_of_nodes_per_level,width=w,
                        yerr=standard_deviation_per_level_node, ecolor='black',
                        color='green', align='center')
        rect2 = plt.bar(np.arange(len(number_of_nodes_per_level_dtc))+w/2,
                        number_of_nodes_per_level_dtc,width=w,
                        color='red', align='center')
    elif param is 'Depth':
        plt.ylabel('Depth of tree')
        plt.title(r'Depth of tree in each level')
        rect1 = plt.bar(np.arange(len(mean_depth_of_tree_per_level))-w/2,
                        mean_depth_of_tree_per_level,width=w,
                        yerr=standard_deviation_per_level_depth, ecolor='black',
                        color='green', align='center')
        rect2 = plt.bar(np.arange(len( depth_of_the_tree_per_level_dtc))+w/2,
                        depth_of_the_tree_per_level_dtc,width=w,
                        color='red', align='center')
     
    plt.autoscale(tight=True)
    plt.xticks(range(0,15), range(1,16), size='small')
    if param is 'Node':
        plt.yticks(range(0,95,5), range(0,95,5), size='small')
    elif param is 'Depth':
        plt.yticks(range(0,16), range(0,16), size='small')
    plt.legend( (rect1[0], rect2[0]), ('Treece', 'DecisionTreeClassifier')) 
    if param is 'Node':
        plt.savefig('number_of_node_game.png')
        plt.savefig('number_of_node_game.eps')
    elif param is 'Depth':
        plt.savefig('depth_of_tree_game.png')
        plt.savefig('depth_of_tree_game.eps')
    
