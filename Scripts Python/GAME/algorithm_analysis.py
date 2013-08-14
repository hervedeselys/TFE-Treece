#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np
import csv
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier

classifier = 'dtc' #RandomForest or DecisionTree Classifier
min_samples_split_per_level_dtc = [1,33,3,3,10,1,10,38,4,12,4,2,1,21,2]
max_features_per_level_dtc = [1,1,2,1,3,1,2,1,1,2,3,4,3,3,3]
min_samples_split_per_level_rfc = [1,1,1,1,2,1,2,1,1,18,4,1,1,2,1]
max_features_per_level_rfc = [1,1,1,1,1,1,1,1,1,2,1,1,1,1,4]

def depth(tree, node=0):
    if tree.children_left[node] == -1:
        return 1
    else:
        return 1 + max(depth(tree, tree.children_left[node]), depth(tree, tree.children_right[node]))

# Main
if __name__ == "__main__":
    errors = []
    d = 0
    for l in range(1,16):
        X_ls = np.zeros((200,4))
        Y_ls = np.zeros((200,))
        X_ts = np.zeros((800,4))
        Y_ts = np.zeros((800,))
        with open('Levels/Level'+str(l)+' - Learning Set.csv', 'rb') as ls:
            reader = csv.reader(ls, delimiter=',')
            i = 0
            for row in reader:
                j = 0
                for k in range(len(row)-1):
                    X_ls[i][j] = row[k]
                    j = j + 1
                Y_ls[i] = row[len(row)-1]
                i = i + 1
                
        with open('Levels/Level'+str(l)+' - Test Set.csv', 'rb') as ts:
            reader = csv.reader(ts, delimiter=',')
            i = 0
            for row in reader:
                j = 0
                for k in range(len(row)-1):
                    X_ts[i][j] = row[k]
                    j = j + 1
                Y_ts[i] = row[len(row)-1]
                i = i + 1     

        if classifier is 'rfc':
            clf = RandomForestClassifier(n_estimators=1000)
        elif classifier is 'dtc':
            clf = DecisionTreeClassifier()
        clf = clf.fit(X_ls,Y_ls)
        d = d + 1
        
        Z = clf.predict(X_ts)
        
        # Compute the rate of accuracy
        score_ls = clf.score(X_ls, Y_ls)
        score_ts = clf.score(X_ts, Y_ts)

        # Display scores and errors of each set
        if classifier is 'rfc':
            print 'Error from level '+str(l)+' from RandomForestClassifier'
        elif classifier is 'dtc':
            print 'Error from level '+str(l)+' from DecisionTreeClassifier'
        print "LS error: "+str((1-score_ls)*100)+"%"
        print "TS error: "+str((1-score_ts)*100)+"%"
        if classifier is 'dtc':
            print 'Node count: '+str(clf.tree_.node_count)
            print 'Depth of the tree: '+str(depth(clf.tree_))
        print "------------------"
        errors.append((1-score_ts)*100)
     
    pl.xlabel('Levels')
    pl.ylabel('Error (%)') 
    if classifier is 'rfc':
        pl.title(r'Error on TS from RandomForestClassifier (n=1000)')
    elif classifier is 'dtc':
        pl.title(r'Error on TS from DecisionTreeClassifier')  
    pl.bar(np.arange(len(errors)),errors, color='blue', align='center')
    pl.autoscale(tight=True)
    pl.xticks(range(0,15),range(1,16), size='small')
    pl.yticks(range(0,30,5), range(0,30,5), size='small')
    if classifier is 'rfc':
        pl.savefig('mean_error_TS_rfc_game.png')
    elif classifier is 'dtc':
        pl.savefig('mean_error_TS_dtc_game.png')
 
