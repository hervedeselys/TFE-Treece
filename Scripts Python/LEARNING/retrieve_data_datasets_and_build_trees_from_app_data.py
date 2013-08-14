#! /usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division
import csv
import decision_tree as dt
import math
import random
import itertools
import operator

databases = ['BREASTCANCER','IRIS','LANDSAT','PIMA-INDIANS-DIABETES','VEHICLE']

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
    trees_number = open('number_of_trees_per_database.txt','w')
    for database in databases:
        instances = [[]]   
        listIDTree = []
        learningData = []
        fullDataSet = []
        instanceID = 0
        with open('Datasets/'+database+' - Learning Set.csv', 'rb') as sets:
            reader = csv.reader(sets, delimiter=',')    
            for row in reader:
                attributes = []
                missingValue = False
                for i in range(len(row)-1):
                    attr = dt.Attribute(i, row[i])
                    if row[i] == '?':
                        missingValue = True
                        
                    attributes.append(attr)
                if(missingValue == False):
                    instanceID = instanceID + 1
                    inst = dt.Instance(instanceID, attributes, row[len(row)-1])
                    fullDataSet.append(inst)

        testSet = []
        with open('Datasets/'+database+' - Test Set.csv', 'rb') as sets:
            reader = csv.reader(sets, delimiter=',')    
            for row in reader:
                attributes = []
                missingValue = False
                for i in range(len(row)-1):
                    attr = dt.Attribute(i, row[i])
                    if row[i] == '?':
                        missingValue = True
                        
                    attributes.append(attr)
                if(missingValue == False):
                    instanceID = instanceID + 1
                    inst = dt.Instance(instanceID, attributes, row[len(row)-1])
                    testSet.append(inst)

        random.shuffle(fullDataSet)
        
        learningSet = []
        validationSet = []
        validationSetSize = int(math.ceil((len(fullDataSet)/100)*30))
        learningSetSize = len(fullDataSet) - validationSetSize

        for i in range(learningSetSize):
            learningSet.append(fullDataSet[i])
            
        for i in range(learningSetSize, learningSetSize+validationSetSize):
            validationSet.append(fullDataSet[i])
      
        with open(database+'.csv', 'rb') as data:
            reader = csv.reader(data, delimiter=',')
            for row in reader:
                learningData.append(row)
                if row[0] not in listIDTree:
                    listIDTree.append(row[0])

        f = open(database+'_attribute_equation_info.csv','w')
        for line in learningData:
            for i in range(4,8):
                if i == 7:
                    f.write(line[i])
                else:
                    f.write(line[i]+',')
            f.write('\n')
        f.close()
        print 'Number of trees in '+database+': '+str(len(listIDTree))
        trees_number.write('Number of trees in '+database+': '+str(len(listIDTree))+'\n')
        treesError = []
        trees= []
        for id in listIDTree:
            dataForTree = []
            missingLine = False
            for line in learningData:
                if line[0] == id:
                    dataForTree.append(line)

            nodeInfo = []
            attributeInfo = []
            equationInfo = []

            for line in dataForTree:
                nodeInfo.append(list([line[1],line[2],line[3]]))
                attributeInfo.append(list([line[4],line[5]]))
                equationInfo.append(list([line[6],line[7]]))
                                
    ##        print nodeInfo
    ##        print attributeInfo
    ##        print equationInfo

            nodeInfoList = []
            for i in range(len(nodeInfo)):
                node = dt.DTNode(nodeInfo[i], equationInfo[i], attributeInfo[i], None, None, None)
                nodeInfoList.append(node)

    ##        print 'List nodes'
    ##        for node in nodeList:
    ##            print 'Node: '+node.nodeInfo[0]
    ##            print 'Equation: '+str(node.threshold)
    ##            print 'Left child: '+node.nodeInfo[1]
    ##            print 'Right child: '+node.nodeInfo[2]
    ##            print '-----------------------------'

            root = dt.DTNode(nodeInfo[0], equationInfo[0], attributeInfo[0], learningSet, validationSet, testSet)
            tree = dt.DTTree(id, root)
            depth = 0
            currentNode = 0
            depth_array = []
            for intern_node in nodeInfoList:
                intern_nodeID = intern_node.nodeInfo[0]
                if currentNode > intern_nodeID:
                    depth = 0
                else:
                    depth = depth + 1
                depth_array.append(depth)
                currentNode = intern_nodeID
                node_search = []
                for k in tree.nodeList:
                    if k.nodeInfo[0] == intern_nodeID:
                        node_search.append(k)
                if len(node_search) == 0:
                    missingLine = True
                else:
                    node = node_search[0]
                        
                    if len(node.nodeInfo) > 1:     
                        trainUp = []
                        trainDown = []
                        validationUp = []
                        validationDown = []
                        testUp = []
                        testDown = []
                        equation = node.threshold
                        attributePair = node.attributePair
                
                        m = float(equation[0])
                        p = float(equation[1])
                        firstAttribute = int(attributePair[0])
                        secondAttribute = int(attributePair[1])

                        for instance in node.learningSet:
                            attributeList = instance.attributeList
                            x = float(attributeList[firstAttribute].value)
                            y = float(attributeList[secondAttribute].value)

                            equation = y - (m*x) - p

                            if equation < 0:
                                trainUp.append(instance)
                            else:
                                trainDown.append(instance)

                        for instance in node.validationSet:
                            attributeList = instance.attributeList
                            x = float(attributeList[firstAttribute].value)
                            y = float(attributeList[secondAttribute].value)

                            equation = y - (m*x) - p
            
                            if equation < 0:
                                validationUp.append(instance)
                            else:
                                validationDown.append(instance)

                        for instance in node.testSet:
                            attributeList = instance.attributeList
                            x = float(attributeList[firstAttribute].value)
                            y = float(attributeList[secondAttribute].value)

                            equation = y - (m*x) - p
            
                            if equation < 0:
                                testUp.append(instance)
                            else:
                                testDown.append(instance)

                        idList = []
                        for n in nodeInfoList:
                            idList.append(n.nodeInfo[0])
                            if n.nodeInfo[0] == node.nodeInfo[1]:
                                left = dt.DTNode(n.nodeInfo, n.threshold , n.attributePair , trainUp, validationUp, testUp)
                                tree.addNode(left)
                                node.addChild(left)
                            elif n.nodeInfo[0] == node.nodeInfo[2]:
                                right = dt.DTNode(n.nodeInfo, n.threshold , n.attributePair, trainDown, validationDown, testDown)
                                tree.addNode(right)
                                node.addChild(right)
                       
                        if len(node.nodeInfo) > 0 and node.nodeInfo[1] not in idList:
                            n = dt.DTNode(node.nodeInfo[1], None , None, trainUp, validationUp, testUp)
                            node.addChild(n)
                            tree.addNode(n)
                        if len(node.nodeInfo) > 1 and node.nodeInfo[2] not in idList:
                            n = dt.DTNode(node.nodeInfo[2], None , None, trainDown, validationDown, testDown)
                            node.addChild(n)
                            tree.addNode(n)
                    
            ##        print '-----------------------------'
            ##        print 'Tree Info'
            ##        for i in tree.treeList:
            ##            print 'Node Info (ID, LeftID, RightID): '+str(i.nodeInfo)
            ##            print 'Ls size: '+str(len(i.learningSet))
            ##            print 'Ts size: '+str(len(i.testSet))
            ##
            ##            print '-----------------------------'

            depthMax = max(depth_array)
            globalErrorOnLS = tree.getGlobalErrorOnLS()
            globalErrorOnVS = tree.getGlobalErrorOnVS()
            globalErrorOnTS = tree.getGlobalErrorOnTS()
            numberOfNodes = len(tree.nodeList)
            #print 'Tree error on LS: '+str(globalErrorOnLS)
            #print 'Tree error on TS: '+str(globalErrorOnTS)
            error = [tree.id, str(globalErrorOnLS), str(globalErrorOnVS), str(globalErrorOnTS), str(numberOfNodes), str(depthMax)]
            #print 'Tree id: '+ id+' (Number of nodes: '+str(numberOfNodes)+')'
            if not missingLine:
                treesError.append(error)
            trees.append(tree)
                    
        f = open(database+'_error_trees.csv','w')
        for tree in treesError:
            for i in range(6):
                if i == 5:
                    f.write(tree[i])
                else:
                    f.write(tree[i]+',')
            f.write('\n')
        f.close()
        
        misclassification_count_ls = 0
        f = open(database+'_error_set_of_trees.csv','w')
        for instance in learningSet:
            classPredictions = []
            for tree in trees:
                leafList = []
                for node in tree.nodeList:
                    if node.isLeaf():
                        leafList.append(node)
                for node in leafList:
                    if instance in node.learningSet:
                        instance_class = []
                        for inst in node.learningSet:
                            instance_class.append(inst.className)
                        majorityClassID = most_common(instance_class)
                        classPredictions.append(majorityClassID)
            if classPredictions:
                majority_class_predicted_by_trees = most_common(classPredictions)
                #print 'Majority class of the trees: '+str(majority_class_predicted_by_trees)
                #print 'Class of the instance: '+instance.className
                if majority_class_predicted_by_trees != instance.className:
                    misclassification_count_ls = misclassification_count_ls + 1
        error_set_ls = (misclassification_count_ls/len(learningSet))*100
        print 'Error of the set of tree on LS: '+str(error_set_ls)
        f.write(str(error_set_ls)+',')

        misclassification_count_vs = 0
        for instance in validationSet:
            classPredictions = []
            for tree in trees:
                leafList = []
                for node in tree.nodeList:
                    if node.isLeaf():
                        leafList.append(node)
                for node in leafList:
                    if instance in node.validationSet:
                        instance_class = []
                        for inst in node.validationSet:
                            instance_class.append(inst.className)
                        majorityClassID = most_common(instance_class)
                        classPredictions.append(majorityClassID)
            if classPredictions:
                majority_class_predicted_by_trees = most_common(classPredictions)
                #print 'Majority class of the trees: '+str(majority_class_predicted_by_trees)
                #print 'Class of the instance: '+instance.className
                if majority_class_predicted_by_trees != instance.className:
                    misclassification_count_vs = misclassification_count_vs + 1
        error_set_vs = (misclassification_count_vs/len(testSet))*100
        print 'Error of the set of tree on VS: '+str(error_set_vs)
        f.write(str(error_set_vs)+',')

        misclassification_count_ts = 0
        for instance in testSet:
            classPredictions = []
            for tree in trees:
                leafList = []
                for node in tree.nodeList:
                    if node.isLeaf():
                        leafList.append(node)
                for node in leafList:
                    if instance in node.testSet:
                        instance_class = []
                        for inst in node.testSet:
                            instance_class.append(inst.className)
                        majorityClassID = most_common(instance_class)
                        classPredictions.append(majorityClassID)
            if classPredictions:
                majority_class_predicted_by_trees = most_common(classPredictions)
                #print 'Majority class of the trees: '+str(majority_class_predicted_by_trees)
                #print 'Class of the instance: '+instance.className
                if majority_class_predicted_by_trees != instance.className:
                    misclassification_count_ts = misclassification_count_ts + 1
        error_set_ts = (misclassification_count_ts/len(testSet))*100
        print 'Error of the set of tree on TS: '+str(error_set_ts)
        f.write(str(error_set_ts))
        f.close()          
                    
        
    trees_number.close()
