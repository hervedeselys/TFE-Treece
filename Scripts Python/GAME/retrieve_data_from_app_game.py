#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np
import csv

# Main
if __name__ == "__main__":   
    with open('GAME.csv', 'rb') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        listIDTree = []
        text = []
        for row in reader:
            text.append(row)
            if row[8] == 'END':
                 listIDTree.append(row[2])

        print 'Number of built trees: '+str(len(listIDTree))
        valuableData = [2,3,6,7,9,10,11,12]
        for i in range(1,16):
            fileToWrite = 'LEVEL'+str(i)+'.csv'
            f = open(fileToWrite, 'w') 
            for id in listIDTree:
                for row in text:
                    level = 'LEVEL'+str(i)
                    if row[2] == id and row[8] == level:
                        for j in valuableData:
                            if j == 12:
                                f.write(row[j])
                            else:
                                f.write(row[j]+',')
                        f.write('\n')      
            f.close()
