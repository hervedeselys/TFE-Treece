#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np
import csv
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier

classifier = 'dtc' #RandomForest or DecisionTree Classifier or Weka
min_samples_split_per_level_dtc = [1,33,3,3,10,1,10,38,4,12,4,2,1,21,2]
max_features_per_level_dtc = [1,1,2,1,3,1,2,1,1,2,3,4,3,3,3]
min_samples_split_per_level_rfc = [1,1,1,1,2,1,2,1,1,18,4,1,1,2,1]
max_features_per_level_rfc = [1,1,1,1,1,1,1,1,1,2,1,1,1,1,4]


# Main
if __name__ == "__main__":
    f = open('number_of_trees_per_level.txt','w')
    errors_algo = []
    errors_mean_treece = []
    errors_set_treece = []
    d = 0
    for l in range(1,16):
        X_ls = np.zeros((200,4))
        Y_ls = np.zeros((200,))
        X_ts = np.zeros((800,4))
        Y_ts = np.zeros((800,))
        error_line = []
        level = 'Level'+str(l)
        with open(level+'_error_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                error_line.append(row)
                
        with open('Levels/'+level+' - Learning Set.csv', 'rb') as ls:
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
                
        with open(level+'_error_set_of_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                errors_set_treece.append(float(row[1]))
        
        numberOfTrees = len(error_line)
        f.write('Number of trees for '+level+': '+str(numberOfTrees)+'\n')
        if classifier is 'rfc':
            clf = RandomForestClassifier(n_estimators=numberOfTrees)
        elif classifier is 'dtc':
            clf = DecisionTreeClassifier()
        clf = clf.fit(X_ls,Y_ls)
        Z = clf.predict(X_ts)
        d = d + 1
        # Compute the rate of accuracy
        score_ls = clf.score(X_ls, Y_ls)
        score_ts = clf.score(X_ts, Y_ts)

        # Display scores and errors of each set
        if classifier is 'rfc':
            print 'Error from level '+str(l)+' from RandomForestClassifier (n='+str(numberOfTrees)+')'
        elif classifier is 'dtc':
            print 'Error from level '+str(l)+' from DecisionTreeClassifier'  
        print "LS error: "+str((1-score_ls)*100)+"%"
        print "TS error: "+str((1-score_ts)*100)+"%"
        
        errors_algo.append((1-score_ts)*100)

        if numberOfTrees > 0:
            error_mean = 0
            for line in error_line:
                error_mean = error_mean + float(line[2])
            errors_mean_treece.append(error_mean/numberOfTrees)
            print level+' TS with '+str(numberOfTrees)+' from Treece: '+str(error_mean/numberOfTrees)
            print "------------------"
        else:
            errors_treece.append(0)
            print 'No data for '+level

    f.close()       
    w = 0.35
    pl.xlabel('Levels')
    pl.ylabel('Error (%)')
    if classifier is 'dtc':
        pl.title(r'Comparison of the mean error of trees on TS')
        rect1 = pl.bar(np.arange(len(errors_mean_treece))-w/2, errors_mean_treece,
                   width=w, color='green', align='center')
    elif classifier is 'rfc':
        pl.title(r'Comparison of the error by a set of trees on TS')
        rect1 = pl.bar(np.arange(len(errors_set_treece))-w/2, errors_set_treece,
                   width=w, color='green', align='center')
    rect2 = pl.bar(np.arange(len(errors_algo))+w/2, errors_algo,
                   width=w, color='red', align='center')
    pl.autoscale(tight=True)
    pl.xticks(np.arange(0,15),range(1,16), size='small')
    pl.yticks(range(0,55,5), range(0,55,5), size='small')
    if classifier is 'rfc':
        pl.legend( (rect1[0], rect2[0]), ('Treece','RandomForestClassifier'))
        pl.savefig('mean_error_TS_comparison_rfc_game.png')
        pl.savefig('mean_error_TS_comparison_rfc_game.eps')
    elif classifier is 'dtc':
        pl.legend( (rect1[0], rect2[0]), ('Treece','DecisionTreeClassifier'))
        pl.savefig('mean_error_TS_comparison_dtc_game.png')
        pl.savefig('mean_error_TS_comparison_dtc_game.eps')
 
