#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division
import csv
import numpy as np
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.grid_search import GridSearchCV
from pprint import pprint

classifier = 'rfc' #RandomForest or DecisionTree Classifier

# Main
if __name__ == "__main__":
    #f = open("best_parameters_"+classifier+".txt",'w')
    for l in range(1,16):
        level = 'Level'+str(l)
        print 'Grid Search CV on '+level
        #f.write('Grid Search CV on '+level+'\n')
        with open('Levels/'+level+' - Learning Set.csv', 'rb') as ls:
            reader = csv.reader(ls, delimiter=',')
            i = 0
            X_ls = np.zeros((200,4))
            Y_ls = np.zeros((200,))
            
            for row in reader:
                j = 0
                for k in range(len(row)-1):
                    X_ls[i][j] = row[k]
                    j = j + 1
                Y_ls[i] = row[len(row)-1]
                i = i + 1
                
        if classifier is 'rfc':
            clf = RandomForestClassifier()
            parameters = {
                'n_estimators' : tuple(range(1,500,50)),
                'min_samples_split' : tuple(range(1, 201)),
                'max_features' : tuple(range(1,5)),
            }
        elif classifier is 'dtc':
            clf = DecisionTreeClassifier()
            parameters = {
                'min_samples_split' : tuple(range(1, 201)),
                'max_features' : tuple(range(1,5)),
            }

        grid_search = GridSearchCV(clf, parameters, n_jobs=3, verbose=1)
        grid_search.fit(X_ls, Y_ls)
    
        print "Best score: %0.5f" % grid_search.best_score_
        #f.write('Best score: %0.5f' % grid_search.best_score_+'\n')
        print "Best parameters set:"
        #f.write('Best parameters set:\n')
        best_parameters = grid_search.best_estimator_.get_params()
        for param_name in sorted(parameters.keys()):
            print "\t%s: %r" % (param_name, best_parameters[param_name])
            f.write('\t%s: %r' % (param_name, best_parameters[param_name])+'\n')
        #print "Results: "
        #pprint(grid_search.grid_scores_)
        print "---------------------------"
        #f.write("--------------------------\n")
    #f.close()
