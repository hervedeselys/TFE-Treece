#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pylab as pl
import numpy as np
import csv
import networkx as nx

# Main
if __name__ == "__main__":
    gr = nx.balanced_tree(1,0)
    # Add nodes and edges
    gr.add_nodes_from(["Portugal","Spain","France","Germany","Belgium","Netherlands","Italy"])
    gr.add_nodes_from(["Switzerland","Austria","Denmark","Poland","Czech Republic","Slovakia","Hungary"])
    gr.add_nodes_from(["England","Ireland","Scotland","Wales"])

    gr.add_edge("Portugal", "Spain")
    gr.add_edge("Spain","France")
    gr.add_edge("France","Belgium")
    gr.add_edge("France","Germany")
    gr.add_edge("France","Italy")
    gr.add_edge("Belgium","Netherlands")
    gr.add_edge("Germany","Belgium")
    gr.add_edge("Germany","Netherlands")
    gr.add_edge("England","Wales")
    gr.add_edge("England","Scotland")
    gr.add_edge("Scotland","Wales")
    gr.add_edge("Switzerland","Austria")
    gr.add_edge("Switzerland","Germany")
    gr.add_edge("Switzerland","France")
    gr.add_edge("Switzerland","Italy")
    gr.add_edge("Austria","Germany")
    gr.add_edge("Austria","Italy")
    gr.add_edge("Austria","Czech Republic")
    gr.add_edge("Austria","Slovakia")
    gr.add_edge("Austria","Hungary")
    gr.add_edge("Denmark","Germany")
    gr.add_edge("Poland","Czech Republic")
    gr.add_edge("Poland","Slovakia")
    gr.add_edge("Poland","Germany")
    gr.add_edge("Czech Republic","Slovakia")
    gr.add_edge("Czech Republic","Germany")
    gr.add_edge("Slovakia","Hungary")
    nx.draw(gr)
    pl.show()
