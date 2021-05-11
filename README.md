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


## System Architecture

## Code Structure

## Maintainers

| Name         | Email                           |
| ------------ | ------------------------------- |
| wenhai huo   |                                 |
| zeying zhang |                                 |
| haowen shen  |                                 |
| yuyang wang  | yuyawang@student.unimelb.edu.au |
