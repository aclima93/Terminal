# Source: https://www.mapr.com/blog/tutorial-using-pyspark-and-mapr-sandbox

from collections import OrderedDict
from numpy import array
from math import sqrt
import sys
import os
import numpy
import urllib
import pyspark
from pyspark import SparkContext
from pyspark.mllib.feature import StandardScaler
from pyspark.mllib.clustering import KMeans, KMeansModel
from pyspark.mllib.linalg import DenseVector
from pyspark.mllib.linalg import SparseVector
from collections import OrderedDict
from time import time

# current working directory
os getcwd()

#
# Data Import and Exploration
#

# get data
f = urllib.urlretrieve ("http://kdd.ics.uci.edu/databases/kddcup99/kddcup.data.gz", "kddcup.data.gz")

os.listdir('/user/user01')

# PySpark can import compressed files directly into RDDs.
data_file = "./kddcup.data.gz"
kddcup_data = sc.textFile(data_file) # sc is a Spark Context, and we're opening a textfile
kddcup_data.count()

'''
This output is difficult to read. This is because we are asking PySpark to show us data that is in the RDD format. PySpark has a DataFrame functionality. If the Python version is 2.7 or higher, you can utilize the pandas package. However, pandas doesn’t work on Python versions 2.6, so we use the Spark SQL functionality to create DataFrames for exploration.
'''
kddcup_data.take(5)


'''
This output is difficult to read. This is because we are asking PySpark to show us data that is in the RDD format. PySpark has a DataFrame functionality. If the Python version is 2.7 or higher, you can utilize the pandas package. However, pandas doesn’t work on Python versions 2.6, so we use the Spark SQL functionality to create DataFrames for exploration.
'''
from pyspark.sql.types import *
from pyspark.sql import DataFrame
from pyspark.sql import SQLContext
from pyspark.sql import Row

kdd = kddcup_data.map(lambda l: l.split(","))
df = sqlContext.createDataFrame(kdd)
df.show(5)

# Now let's get an idea of the different types of labels in this data, and the total number for each label. Let's time how long this takes.
labels = kddcup_data.map(lambda line: line.strip().split(",")[-1])
start_label_count = time()
label_counts = labels.countByValue()
label_count_time = time()-start_label_count

sorted_labels = OrderedDict(sorted(label_counts.items(), key=lambda t: t[1], reverse=True))
for label, count in sorted_labels.items():    #simple for loop
	print label, count

'''
We see there are 23 distinct labels. Smurf attacks are known as directed broadcast attacks, and are a popular form of DoS packet floods. This dataset shows that “normal” events are the third most occurring type of event. While this is fine for learning the material, this dataset shouldn’t be mistaken for a real network log. In a real network dataset, there will be no labels and the normal traffic will be much larger than any anomalous traffic. This results in the data being unbalanced, making it much more challenging to identify the malicious actors.
'''

# Now we can start preparing the data for our clustering algorithm.


#
# Data Cleaning
#

'''
 K-means only uses numeric values. This dataset contains three features (not including the attack type feature) that are categorical. For the purposes of this exercise, they will be removed from the dataset. However, performing some feature transformations where these categorical assignments are given their own features and are assigned binary values of 1 or 0 based on whether they are “tcp” or not could be done.

First, we must parse the data by splitting the original RDD, kddcup_data, into columns and removing the three categorical variables starting from index 1 and removing the last column. The remaining columns are then converted into an array of numeric values, and then attached to the last label column to form a numeric array and a string in a tuple.
'''

def parse_interaction(line):
    line_split = line.split(",")
    clean_line_split = [line_split[0]]+line_split[4:-1]
    return (line_split[-1], array([float(x) for x in clean_line_split]))

parsed_data = kddcup_data.map(parse_interaction)
pd_values = parsed_data.values().cache()

#The Sandbox does not have enough memory to process the entire dataset for our tutorial, so we will take a sample of the data.

kdd_sample = pd_values.sample(False, .10, 123)
kdd_sample.count()

# We have taken 10% of the data. The sample() function is taking values without replacement (false), 10% of the total data and is using a the 123 set.seed capability for repeating this sample.

'''
Next, we need to standardize our data. StandardScaler standardizes features by scaling to unit variance and setting the mean to zero using column summary statistics on the samples in the training set. Standardization can improve the convergence rate during the optimization process, and also prevents against features with very large variances exerting an influence during model training.
'''
standardizer = StandardScaler(True, True)

# Compute summary statistics by fitting the StandardScaler
standardizer_model = standardizer.fit(kdd_sample)

# Normalize each feature to have unit standard deviation.
data_for_cluster = standardizer_model.transform(kdd_sample)
