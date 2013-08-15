#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np
import csv
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier

databases = ['IRISM','BREASTCANCER','PIMA-INDIANS-DIABETES','VEHICLEM','LANDSAT']
classifier = 'rfc' #RandomForest or DecisionTree Classifier

min_samples_split_per_database_dtc = [40,96,142,19,7]
max_features_per_database_dtc = [2,5,6,11,33]
min_samples_split_per_database_rfc = [10,1,40,1]
max_features_per_database_rfc = [1,1,2,7]

# Main
if __name__ == "__main__":
    f = open('number_of_trees_per_database.txt','w')
    errors_algo = []
    errors_mean_treece = []
    errors_set_treece = []
    d = 0
    for database in databases:
        error_line = []
        if database is 'IRISM':
            numberOflines_ls = 120
            numberOflines_ts = 30
            numberOfColums = 4
        elif database is 'BREASTCANCER':
            numberOflines_ls = 546
            numberOflines_ts = 137
            numberOfColums = 9
        elif database is 'PIMA-INDIANS-DIABETES':
            numberOflines_ls = 614
            numberOflines_ts = 154
            numberOfColums = 8
        elif database is 'VEHICLEM':
            numberOflines_ls = 677
            numberOflines_ts = 169
            numberOfColums = 18
        elif database is 'LANDSAT':
            numberOflines_ls = 3548
            numberOflines_ts = 887
            numberOfColums = 36
                
        with open('Datasets/'+database+' - Learning Set.csv', 'rb') as ls:
            reader = csv.reader(ls, delimiter=',')
            i = 0
            X_ls = np.zeros((numberOflines_ls,numberOfColums))
            Y_ls = np.zeros((numberOflines_ls,))
            
            for row in reader:
                j = 0
                for k in range(len(row)-1):
                    X_ls[i][j] = row[k]
                    j = j + 1
                Y_ls[i] = row[len(row)-1]
                i = i + 1
                
        with open('Datasets/'+database+' - Test Set.csv', 'rb') as ts:
            reader = csv.reader(ts, delimiter=',')
            i = 0
            X_ts = np.zeros((numberOflines_ts,numberOfColums))
            Y_ts = np.zeros((numberOflines_ts,))
            for row in reader:
                j = 0
                for k in range(len(row)-1):
                    X_ts[i][j] = row[k]
                    j = j + 1
                Y_ts[i] = row[len(row)-1]
                i = i + 1

        if database is 'IRISM':
            database = 'IRIS'
        elif database is 'VEHICLEM':
            database = 'VEHICLE'

        with open(database+'_error_set_of_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                errors_set_treece.append(float(row[2]))
            
        with open(database+'_error_trees.csv', 'rb') as error:
            reader = csv.reader(error, delimiter=',')
            for row in reader:
                error_line.append(row)

        if database is 'PIMA-INDIANS-DIABETES':
            database = 'PIMA'

        numberOfTrees = len(error_line)
        f.write('Number of trees in '+database+': '+str(numberOfTrees)+'\n')
        if classifier is 'rfc':
            if numberOfTrees != 0:
                clf = RandomForestClassifier(n_estimators=numberOfTrees)
            else:
                clf = RandomForestClassifier()
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
            print 'Error from database '+database+' from RandomForestClassifier (n='+str(numberOfTrees)+')'
        elif classifier is 'dtc':
            print 'Error from database '+database+' from DecisionTreeClassifier'  
        print "LS error: "+str((1-score_ls)*100)+"%"
        print "TS error: "+str((1-score_ts)*100)+"%"
        errors_algo.append((1-score_ts)*100)

        if numberOfTrees > 0:
            error_mean = 0
            for line in error_line:
                error_mean = error_mean + float(line[3])
            errors_mean_treece.append(error_mean/numberOfTrees)
            print database+' TS with '+str(numberOfTrees)+' trees from Treece: '+str(error_mean/numberOfTrees)
            print "------------------"
        else:
            errors_treece.append(0)
            print 'No data for '+database
            print "------------------"
            
    databases.remove('PIMA-INDIANS-DIABETES')
    databases.insert(2,'PIMA')

    databases.remove('IRISM')
    databases.insert(0,'IRIS')

    databases.remove('VEHICLEM')
    databases.insert(3,'VEHICLE')

    f.close()
    w = 0.35
    pl.xlabel('Databases')
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
    pl.xticks(range(len(databases)),databases, size='small')
    pl.yticks(range(0,70,5), range(0,70,5), size='small')
    if classifier is 'rfc':
        pl.legend( (rect1[0], rect2[0]), ('Treece','RandomForestClassifier'))
        pl.savefig('mean_error_TS_comparison_rfc_learning.png')
        pl.savefig('mean_error_TS_comparison_rfc_learning.eps')
    elif classifier is 'dtc':
        pl.legend( (rect1[0], rect2[0]), ('Treece','DecisionTreeClassifier'))
        pl.savefig('mean_error_TS_comparison_dtc_learning.png')
        pl.savefig('mean_error_TS_comparison_dtc_learning.eps')
