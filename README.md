# Distributed Transcation System Based on 2PC Algorithm

## Background

Distributed transactions are transactions taking place in multiple nodes or partitions. It is supported by various clustered relational systems. It should keep transactions atomicity to make sure nothing goes wrong. In this case, it is not sufficient to send a commit request to all the nodes and independently commit the transaction on each one, so all the nodes must commit on the outcome or they all abort if anything goes wrong. This is called the atomic commit problem.

The two-phase commit (2PC) algorithm is the most common way of implementing atomic commit in distributed transactions and has been used in many distributed database and messaging systems and application servers. 2PC is used internally in some databases and made available to applications in the form of XA transactions. The basic flow of 2PC is that the commit/abort process is split into two phases. A 2PC transaction begins with reading and writing data on multiple participants. When it is ready to commit, the coordinator sends a prepare request to each participant and tracks their response. If the response is "yes", then the coordinator sends out a commit request, and the commit finished. If the response is "no", the coordinator sends an abort request to all participants.

## Install

**Maven**

Maven is a build automation tool used primarily for Java projects.

Install Maven through this link (https://maven.apache.org/)

**Java SE**

JAVA SE provides the java development toolkit, and java runtime environment, etc.

Install JavaSE through this link (https://www.oracle.com/hk/java/technologies/javase-downloads.html)

**MySQL**

MySQL is an open-source relational database management system. Its name is a combination of "My", the name of co-founder Michael Widenius's daughter, and "SQL", the abbreviation for Structured Query Language.

Install MySQL through this link (https://www.mysql.com/downloads/)

**Eclipse Jetty**

Eclipse Jetty is a Java web server and Java Servlet container. While web servers are usually associated with serving documents to people, Jetty is now often used for machine to machine communications, usually within larger software frameworks.

Import the maven dependency:

```
<dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-server</artifactId>
            <version>9.4.40.v20210413</version>
</dependency>
```

## Usage
1. Modify database configuration information.
2. Start the Coordinator server.
3. Start the Order server. The default port of this server is 9001.
4. Start the Inventory server. The default port of this server is 9002.
5. Request the URL "http://localhost:8001/shopping" to use 2pc algorithm to handle the shopping transaction.
6. Request the URL "http://localhost:8001/no_2pc" to handle the shopping transaction without 2pc algorithm.

## System Architecture
### E-commerce Distributed Transaction Application
![demo-pic1](https://github.com/Alberyang/coordinators_2pc/blob/master/architecute_pic/demo-pic1.png)
The algorithm will be used in a distributed e-commerce application.
There are two services Order Service and Inventory Service and the corresponding database.
The server will call the inventory and order services after accepting the user request.

### 2pc(commit) Architecture
![2pc-arthitecture-commit](https://github.com/Alberyang/coordinators_2pc/blob/master/architecute_pic/2PC%20applied%20to%20the%20project(normal).png)
Two-phase commit algorithm can guarantee the data consistency between 
different nodes in the distributed system to some extent.
A request is in multiple microservice call chains, and data processing for all 
services is either all successful or all rolled back.



### 2pc(rollback) Architecture
![2pc-arthitecture-rollback](https://github.com/Alberyang/coordinators_2pc/blob/master/architecute_pic/2PC%20applied%20to%20the%20project(abort).png)
When certain of service fails to ready to commit, it will vote abort to coordinator,
and then the coordinator will send the rollback instruction to all of services in this
distributed system. All of services receiving this message will rollback local trans-
actions respectively to make sure the data consistency of the whole system.

## Code Structure

## Maintainers

| Name         | Email                           |
| ------------ | ------------------------------- |
| wenhai huo   |                                 |
| zeying zhang |                                 |
| haowen shen  |                                 |
| yuyang wang  | yuyawang@student.unimelb.edu.au |
