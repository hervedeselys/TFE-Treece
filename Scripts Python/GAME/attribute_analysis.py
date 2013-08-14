#! /usr/bin/env python
# -*- coding: utf-8 -*-


from __future__ import division
import csv
import itertools
import operator

#http://stackoverflow.com/questions/1518522/python-most-common-element-in-a-list
def most_common(L):
  # get an iterable of (item, iterable) pairs
  SL = sorted((x, i) for i, x in enumerate(L))
  #print 'SL:', SL
  groups = itertools.groupby(SL, key=operator.itemgetter(0))
  # auxiliary function to get "quality" for an item
  def _auxfun(g):
    item, iterable = g
    count = 0
    min_index = len(L)
    for _, where in iterable:
      count += 1
      min_index = min(min_index, where)
    #print 'item %r, count %r, minind %r' % (item, count, min_index)
    return count, -min_index
  # pick the highest-count/earliest item
  return max(groups, key=_auxfun)[0]

# Main
if __name__ == "__main__":
     for i in range(1,16):
        attribute_line = []
        level = 'Level'+str(i)
        with open(level+'_attribute_equation_info.csv', 'rb') as attribute:
            reader = csv.reader(attribute, delimiter=',')
            for row in reader:
                attribute_line.append([row[0],row[1]])
        if attribute_line:
            maxi = most_common(attribute_line)
            print 'Attribute more used in '+level+': '+str(maxi)
        else:
            print 'No data for '+level
            

