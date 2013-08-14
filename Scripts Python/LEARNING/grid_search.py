#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division
import csv
import numpy as np
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.grid_search import GridSearchCV
from pprint import pprint


databases = ['IRISM','BREASTCANCER','PIMA-INDIANS-DIABETES','VEHICLEM','LANDSAT']

classifier = 'rfc' #RandomForest or DecisionTree Classifier

# Main
if __name__ == "__main__":
    f = open("best_parameters_"+classifier+".txt",'w')
    for database in databases:
        print 'Grid Search CV on '+database
        f.write('Grid Search CV on '+database+'\n')
        if database is 'IRISM':
            numberOfLines_ls = 120
            numberOfColumns = 4
        elif database is 'BREASTCANCER':
            numberOfLines_ls = 546
            numberOfColumns = 9
        elif database is 'PIMA-INDIANS-DIABETES':
            numberOfLines_ls = 614
            numberOfColumns = 8
        elif database is 'VEHICLEM':
            numberOfLines_ls = 677
            numberOfColumns = 18
        elif database is 'LANDSAT':
            numberOfLines_ls = 3548
            numberOfColumns = 36
        with open('Datasets/'+database+' - Learning Set.csv', 'rb') as ls:
            reader = csv.reader(ls, delimiter=',')
            i = 0
            X_ls = np.zeros((numberOfLines_ls,numberOfColumns))
            Y_ls = np.zeros((numberOfLines_ls,))
            
            for row in reader:
                j = 0
                for k in range(len(row)-1):
                    X_ls[i][j] = row[k]
                    j = j + 1
                Y_ls[i] = row[len(row)-1]
                i = i + 1
                
        if classifier is 'rfc':
            clf = RandomForestClassifier(n_estimators=1000)
            parameters = {
                'max_features' : tuple(range(1,numberOfColumns-1)),
                'min_samples_split' : tuple(range(1, numberOfLines_ls)),
            }
        elif classifier is 'dtc':
            clf = DecisionTreeClassifier()
            parameters = {
                'min_samples_split' : tuple(range(1, numberOfLines_ls)),
                'max_features' : tuple(range(1,numberOfColumns-1)),
            }

        grid_search = GridSearchCV(clf, parameters, n_jobs=3, verbose=1)
        grid_search.fit(X_ls, Y_ls)
    
        print "Best score: %0.5f" % grid_search.best_score_
        print 'Best error: '+str((1-grid_search.best_score_)*100)+'%'
        f.write('Best score: %0.5f' % grid_search.best_score_+'\n')
        f.write('Best error: '+str((1-grid_search.best_score_)*100)+'%\n')
        print "Best parameters set:"
        f.write('Best parameters set:\n')
        best_parameters = grid_search.best_estimator_.get_params()
        for param_name in sorted(parameters.keys()):
            print "\t%s: %r" % (param_name, best_parameters[param_name])
            f.write('\t%s: %r' % (param_name, best_parameters[param_name])+'\n')
        #print "Results: "
        #pprint(grid_search.grid_scores_)
        print "---------------------------"
        f.write("--------------------------\n")
    f.close()
