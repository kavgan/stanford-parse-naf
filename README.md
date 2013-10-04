Stanford-parse
===============

This module provides a 'ready to use' KAF wrapper for English Constituent Parser (including head words)
using Stanford CoreNLP API (http://www-nlp.stanford.edu/software/)

It also provides options to format output according to the Stanford CoreNLP API (penn, oneline).

All dependencies and classpath configurations are automatically managed by Maven.

Contents
========

The contents of the module are the following:

    + pom.xml                 maven pom file which deals with everything related to compilation and execution of the module
    + src/                    java source code of the module
    + Furthermore, the installation process, as described in the README.md, will generate another directory:
    target/                 it contains binary executable and other directories


INSTALLATION
============

Installing stanford-parse requires the following steps:

If you already have installed in your machine JDK7 and MAVEN 3, please go to step 3
directly. Otherwise, follow these steps:

1. Install JDK 1.7
-------------------

If you do not install JDK 1.7 in a default location, you will probably need to configure the PATH in .bashrc or .bash_profile:

````shell
export JAVA_HOME=/yourpath/local/java7
export PATH=${JAVA_HOME}/bin:${PATH}
````

If you use tcsh you will need to specify it in your .login as follows:

````shell
setenv JAVA_HOME /usr/java/java17
setenv PATH ${JAVA_HOME}/bin:${PATH}
````

If you re-login into your shell and run the command

````shell
java -version
````

You should now see that your jdk is 1.7

2. Install MAVEN 3
------------------

Download MAVEN 3 from

````shell
wget http://apache.rediris.es/maven/maven-3/3.0.5/binaries/apache-maven-3.0.5-bin.tar.gz
````

Now you need to configure the PATH. For Bash Shell:

````shell
export MAVEN_HOME=/home/ragerri/local/apache-maven-3.0.5
export PATH=${MAVEN_HOME}/bin:${PATH}
````

For tcsh shell:

````shell
setenv MAVEN3_HOME ~/local/apache-maven-3.0.5
setenv PATH ${MAVEN3}/bin:{PATH}
````

If you re-login into your shell and run the command

````shell
mvn -version
````

You should see reference to the MAVEN version you have just installed plus the JDK 7 that is using.

3. Get module source code
--------------------------

````shell
hg clone ssh://hg@bitbucket.org/ragerri/stanford-parse-en
````

4. Move into main directory
---------------------------

````shell
cd stanford-parse
````

5. Install module using maven
-----------------------------

Obtain the Parser model englishPCFG.ser.gz model included in the Stanford parser at the the Stanford NLP Group site:


````shell
http://www-nlp.stanford.edu/software/lex-parser.shtml
````

After you unzip the downloaded module look for the stanford-parser-3.2.0-models.jar in the parser directory and
extract the models:

````shell
jar xf stanford-parser-3.2.0-models.jar
````
and copy it to stanford-parse/src/main/resources/ directory:

````shell
cp edu/models/stanford/nlp/models/lexparser/englishPCFG.ser.gz stanford-parse/src/main/resources/
````

Compile package:

````shell
mvn clean package
````

This step will create a directory called target/ which contains various directories and files.
Most importantly, there you will find the module executable:

stanford-parse-3.2.0.jar

The version reflects the API version of Stanford-CoreNLP used to create this module.

This executable contains every dependency the module needs, so it is completely portable as long
as you have a JVM 1.7 installed.

To install the module as in the maven's user local repository, located in ~/.m2/repository, do this:

````shell
mvn clean install
````

6. USING stanford-parse
========================

The program accepts KAF (<text> and <terms> elements) as input and outputs <constituents> elements in KAF containing the parse trees:

https://github.com/opener-project/kaf/wiki/KAF-structure-overview

To run the program execute:

````shell
cat file.kaf | java -jar $PATH/target/stanford-parse-3.2.0.jar --kaf
````

It also provides Collins and Semantic Head Words and other output formats such as Penn TreeBank. Check the tool help for a description:

````shell
java -jar $PATH/target/stanford-parse-3.2.0.jar -help
````


GENERATING JAVADOC
==================

You can also generate the javadoc of the module by executing:

````shell
mvn javadoc:jar
````

Which will create a jar file core/target/stanford-parse-3.2.0-javadoc.jar


Contact information
===================

````shell
Rodrigo Agerri
IXA NLP Group
University of the Basque Country (UPV/EHU)
E-20018 Donostia-San Sebastián
rodrigo.agerri@ehu.es
````

