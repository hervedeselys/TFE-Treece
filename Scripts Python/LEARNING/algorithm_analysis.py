#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np
import csv
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier

databases = ['IRISM','BREASTCANCER','PIMA-INDIANS-DIABETES','VEHICLEM','LANDSAT']
min_samples_split_per_database_dtc = [40,96,142,19,7]
max_features_per_database_dtc = [2,5,6,11,33]
min_samples_split_per_database_rfc = [10,1,40,1]
max_features_per_database_rfc = [1,1,2,7]

classifier = 'dtc' #RandomForest or DecisionTree Classifier

# Main
if __name__ == "__main__":
    
    errors = []
    d = 0
    for database in databases:     
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

        if database is 'PIMA-INDIANS-DIABETES':
            database = 'PIMA'

        if classifier is 'rfc':
            clf = RandomForestClassifier(n_estimators=1000)
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
            print 'Error from database '+database+' from RandomForestClassifier'
        elif classifier is 'dtc':
            print 'Error from database '+database+' from DecisionTreeClassifier'
        print "LS error: "+str((1-score_ls)*100)+"%"
        print "TS error: "+str((1-score_ts)*100)+"%"
        print "------------------"
        errors.append((1-score_ts)*100)
            
    databases.remove('PIMA-INDIANS-DIABETES')
    databases.insert(2,'PIMA')

    databases.remove('IRISM')
    databases.insert(0,'IRIS')

    databases.remove('VEHICLEM')
    databases.insert(3,'VEHICLE')
            
    pl.xlabel('Databases')
    pl.ylabel('Error (%)') 
    if classifier is 'rfc':
        pl.title(r'Error on TS from RandomForestClassifier')
    elif classifier is 'dtc':
        pl.title(r'Error on TS from DecisionTreeClassifier') 
    pl.bar(np.arange(len(errors)),errors, color='blue', align='center')
    pl.autoscale(tight=True)
    pl.xticks(range(len(databases)),databases, size='small')
    pl.yticks(range(0,105,5), range(0,105,5), size='small')
    if classifier is 'rfc':
        pl.savefig('mean_error_TS_rfc_learning.png')
    elif classifier is 'dtc':
        pl.savefig('mean_error_TS_dtc_learning.png')
