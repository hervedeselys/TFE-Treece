#! /usr/bin/env python
# -*- coding: utf-8 -*-

import csv

# Main
if __name__ == "__main__":   
    with open('LEARNING.csv', 'rb') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        listIDTree = []
        databases = ['BREASTCANCER','IRIS','LANDSAT','PIMA-INDIANS-DIABETES','VEHICLE']
        text = []
        for row in reader:
            text.append(row)
            if row[8] == 'END':
                 listIDTree.append(row[2])

        print 'Number of built trees: '+str(len(listIDTree))
        valuableData = [2,3,6,7,9,10,11,12]
        for database in databases:
            fileToWrite = database+'.csv'
            f = open(fileToWrite, 'w') 
            for id in listIDTree:
                for row in text:
                    if row[2] == id and row[8] == database:
                        for i in valuableData:
                            if i == 12:
                                f.write(row[i])
                            else:
                                f.write(row[i]+',')
                        f.write('\n')     
            f.close()
