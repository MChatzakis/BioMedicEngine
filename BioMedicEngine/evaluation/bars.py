import numpy as np
import matplotlib.pyplot as plt

# creating the dataset
data = {'Partitioning':14570.7038756/3600, 'Merging':11509.4868691/3600,'Total Time':26314.4998544/3600}
phases = list(data.keys())
values = list(data.values())

fig = plt.figure(figsize = (10, 5))

# creating the bar plot
plt.bar(phases, values, color ='maroon',width = 0.4, label = values)

plt.xlabel("Indexing Phases")
plt.ylabel("Time(hours)")
plt.title("BioMedic Indexing Phases")
plt.savefig("./index.png")
