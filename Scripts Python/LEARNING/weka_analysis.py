#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np

databases = ['IRIS','BREASTCANCER','PIMA','VEHICLE','LANDSAT']
weka = [5,2.86,23.29,22.45,14.18]

# Main
if __name__ == "__main__":
    pl.xlabel('Databases')
    pl.ylabel('Error (%)')
    pl.title(r'Error on 10-fold cross-validation with functional tree from Weka')
    pl.bar(np.arange(len(weka)),weka, color='blue', align='center')
    pl.autoscale(tight=True, axis='x')
    pl.xticks(range(len(databases)),databases, size='small')
    pl.yticks(range(0,70,5), range(0,70,5), size='small')
    pl.savefig('weka_ft_learning.png')
    pl.savefig('weka_ft_learning.eps')
