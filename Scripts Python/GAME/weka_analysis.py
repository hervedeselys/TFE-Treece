#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np


weka = [0,0,1.5,0.5,9,0,0,3,0,0.5,5,14,15.5,2,12]

# Main
if __name__ == "__main__":
    pl.xlabel('Levels')
    pl.ylabel('Error (%)')
    pl.title(r'Error on 10-fold cross-validation with functional tree from Weka')
    pl.bar(np.arange(len(weka)),weka, color='blue', align='center')
    pl.autoscale(tight=True, axis='x')
    pl.xticks(np.arange(0,15),range(1,16), size='small')
    pl.yticks(range(0,55,5), range(0,55,5), size='small')
    pl.savefig('weka_ft_game.png')
    pl.savefig('weka_ft_game.eps')
