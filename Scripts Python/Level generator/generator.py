#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np
from sklearn.datasets import make_classification

# Main
if __name__ == "__main__":

    pl.figure(figsize=(16,12))
    pl.subplots_adjust(bottom=.05, top=.9, left=.05, right=.95)
    
    X, Y = make_classification(n_samples=1000, n_features=4, n_informative=4, n_redundant=0, n_repeated=0,
                               n_classes=2, n_clusters_per_class=1, weights=None,flip_y=0,
                               class_sep=4.0, hypercube=True,shift=0.0, scale=1.0, shuffle=True, random_state=1)
    aY = np.array([Y])
    con = np.concatenate((X,aY.T), axis=1)
    np.savetxt('generated_data.csv', con, delimiter=",")
    
    ax = pl.subplot(341)
    #add a line on the plo
    #ax.add_line(pl.Line2D([-4,6],[-5,10]))
    pl.scatter(X[:, 0], X[:, 1], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(342)
    pl.scatter(X[:, 0], X[:, 2], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(343)
    pl.scatter(X[:, 0], X[:, 3], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(344)
    pl.scatter(X[:, 1], X[:, 0], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(345)
    pl.scatter(X[:, 1], X[:, 2], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(346)
    pl.scatter(X[:, 1], X[:, 3], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(347)
    pl.scatter(X[:, 2], X[:, 0], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(348)
    pl.scatter(X[:, 2], X[:, 1], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(349)
    pl.scatter(X[:, 2], X[:, 3], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(3,4,10)
    pl.scatter(X[:, 3], X[:, 0], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(3,4,11)
    pl.scatter(X[:, 3], X[:, 1], marker='o', c=Y, cmap=pl.cm.cool)

    pl.subplot(3,4,12)
    pl.scatter(X[:, 3], X[:, 2], marker='o', c=Y, cmap=pl.cm.cool)
    
    pl.show()
