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
plt.close()


#topics
mstimes = [0.021825201, 0.0152829, 0.006613601, 0.019778699, 0.0142496, 0.004942001, 0.0051454, 0.0084713, 0.003163599, 0.006821301, 0.006859299, 0.0046337, 0.005784401, 0.0121288, 0.0097328, 0.0113983, 0.007541, 0.0085538, 0.0031727, 0.0047009, 0.007203499, 0.003856899, 0.0082642, 0.008995899, 0.0123121, 0.007907699, 0.0085639, 0.0042083, 0.004193499, 0.0091521]
query_ids = []

for i in range(len(mstimes)):
    mstimes[i] = mstimes[i] * 1000
    query_ids.append(str(i+1))

fig = plt.figure(figsize = (10, 5))
plt.bar(query_ids, mstimes, color ='maroon',width = 0.4, label = mstimes)
plt.xlabel("Query Number")
plt.ylabel("Time(ms)")
plt.title("BioMedic Topic Query Answering")
plt.savefig("./query.png")
plt.close()

