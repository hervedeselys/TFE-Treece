#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division

class DTTree:
    def __init__(self, i, r):
        self.root = r
        self.id = i
        self.nodeList = []
        self.nodeList.append(self.root)

    def addNode(self, n):
        self.nodeList.append(n)

    def getGlobalErrorOnLS(self):
        error = 0
        totalNumberOfInstanceInLS = self.getTotalNumberOfInstanceInLS()
        for node in self.nodeList:
            if node.isLeaf():
                error = error + (node.getLocalErrorOnLS() * (len(node.learningSet) / totalNumberOfInstanceInLS))
        return error

    def getGlobalErrorOnTS(self):
        error = 0
        totalNumberOfInstanceInTS = self.getTotalNumberOfInstanceInTS()
        for node in self.nodeList:
            if node.isLeaf():
                error = error + (node.getLocalErrorOnTS() * (len(node.testSet) / totalNumberOfInstanceInTS))
        return error
 
    def getTotalNumberOfInstanceInLS(self):
        total = 0
        for node in self.nodeList:
            if node.isLeaf():
                total = total + len(node.learningSet)
        return total

    def getTotalNumberOfInstanceInTS(self):
        total = 0
        for node in self.nodeList:
            if node.isLeaf():
                total = total + len(node.testSet)
        return total
        
class DTNode:
    def __init__(self, n, line, p, ls, ts):
        self.learningSet = ls
        self.testSet = ts
        self.threshold = line
        self.nodeInfo = n
        self.attributePair = p
        self.children = []

    def addChild(self, child):
        self.children.append(child)

    def isLeaf(self):
        if len(self.children) == 0:
            return True
        else:
            return False

    def getPurity(self):
        numberOfInstance = len(self.learningSet)
        if numberOfInstance == 0:
            return 0
        outputIDs = []
        outputIDList = []
        frequencies = []
        for i in range(numberOfInstance):
            className = self.learningSet[i].className
            outputIDList.append(className)
            if className not in outputIDs:
                outputIDs.append(className)
        for i in range(len(outputIDs)):
            frequencies.append(outputIDList.count(outputIDs[i]))

        maxValue = max(frequencies)
        return maxValue / numberOfInstance

    def getLocalErrorOnLS(self):
        purity = self.getPurity()
        return 100 - (purity*100)

    def getLocalErrorOnTS(self):
        numberOfInstance = len(self.testSet)
        if numberOfInstance == 0:
            return 0
        outputIDs = []
        outputIDList = []
        frequencies = []
        for i in range(numberOfInstance):
            className = self.testSet[i].className
            outputIDList.append(className)
            if className not in outputIDs:
                outputIDs.append(className)
        for i in range(len(outputIDs)):
            frequencies.append(outputIDList.count(outputIDs[i]))

        maxValue = max(frequencies)
        return 100 - ((maxValue/numberOfInstance)*100)


class Instance:
    def __init__(self, id, l, o):
        self.attributeList = l
        self.className = o
        self.instanceID = id

    
class Attribute:
    def __init__(self, n ,v):
        self.attributeName = n
        self.value = v


    
