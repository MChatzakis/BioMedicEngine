import numpy as np
import matplotlib.pyplot as plt

# creating the dataset
data = {'Partitioning':14144.4895521/3600, 'Merging': 9243.8925636/3600,'Total Time':23913.6061804/3600}
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
mstimes = [76.5480231, 114.8475377, 80.1966347, 215.110752, 174.876142, 34.9997558, 27.865503, 50.0700643, 13.0315939, 40.3641997, 67.6530919, 53.7988957, 62.7743993, 201.7514457, 69.9828992, 134.9273687, 137.8054136, 171.6525911, 41.606378, 31.7604955, 58.8141795, 36.3708409, 65.4732272, 172.9405841, 183.1000054, 136.3675895, 105.9987965, 45.9454933, 56.9641469, 147.1197123]
query_ids = []

for i in range(len(mstimes)):
    #mstimes[i] = mstimes[i] * 1000
    query_ids.append(str(i+1))

fig = plt.figure(figsize = (10, 5))
plt.bar(query_ids, mstimes, color ='maroon',width = 0.4, label = mstimes)
plt.xlabel("Query Number")
plt.ylabel("Time(seconds)")
plt.title("BioMedic Topic Query Answering")
plt.savefig("./query.png")
plt.close()

