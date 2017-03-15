#install wget
yum install wget

# install mvn
wget http://mirror.cc.columbia.edu/pub/software/apache/maven/maven-3/3.0.5/binaries/apache-maven-3.0.5-bin.tar.gz
sudo tar xzfv apache-maven-3.0.5-bin.tar.gz -C /usr/local
cd /usr/local
sudo ln -s apache-maven-3.0.5 maven
sudo vi /etc/profile.d/maven.sh
# add the following lines
#export M2_HOME=/usr/local/maven
#export PATH=${M2_HOME}/bin:${PATH}
mvn -version

#create JAR
mvn package

# run it
./bin/spark-submit \
--class pt.uc.student.aclima.central_aggregator.Database.JavaSparkSQLInitializer \
--master local \
~/Central_Aggregator/Database/target/spark-examples-1.0-SNAPSHOT.jar
