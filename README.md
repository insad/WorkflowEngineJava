WorkflowEngineJava
==================

WorkflowEngineJava - component that adds workflow in your application. It can be fully integrated into your application, or be in the form of a specific service (such as a web service).

<h2>Features:</h2>
<ul>
<li>Process scheme generation in runtime</li>
<li>Designer of process scheme</li>
<li>Changing process scheme in runtime is possible</li>
<li>Pre-execution of process (executing of a process from initial activity to final activity without changing state) </li>
<li>Simple version control and upgrade of process scheme</li>
<li>Serialization and saving of parameters on demand</li>
<li>Support SQL/NoSQL databases</li>
</ul>

<h2>Workflow Engine Java includes a html5 designer:</h2>
<a href="https://workflowengine.io/demo/designer"><img src="https://workflowengine.io/images/schemes/scheme.png" alt="graph.jpg" width="580" style="
    border: 1px solid;
    border-color: #3e4d5c;"></a>


<h2>Before start, you need to install the following items</h2>
1. jdk7 or jdk8.<br/>
2. maven3.<br/>
3. set JAVA_HOME in PATH.<br/>
4. set M2_HOME in PATH.
	
<b>Example:</b>
JAVA_HOME=/home/vagrant/jdk1.8.0_121
M2_HOME=/home/vagrant/apache-maven-3.3.9

export JAVA_HOME
export M2_HOME

PATH=$PATH:$HOME/.local/bin:$HOME/bin:$JAVA_HOME/bin:$M2_HOME/bin

<h2>Build</h2>
1. Install oracle's driver from "oracle\driver" folder (It should be done once):<br/>
  mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.1.0 -Dpackaging=jar -Dfile=ojdbc7.jar -DgeneratePom=true <br/>
2. Excecute: "mvn package" for building all samples.

<h2>How to execute samples</h2>

1. MS SQL Server sample:<br/>
   1.1. Create a database and execute scripts according the instruction: sql-server/sql-server-business-sample/DB/readme.txt<br/>
   1.2. Check the connection string sql-server/sql-server-business-sample/src/main/resources/application.properties<br/>
   1.3. Copy MS SQL Server Driver (sqljdbc_xa.dll) from the archive ("sql-server\driver" folder) to "C:\Windows\System32" or download this driver from <a href="https://www.microsoft.com/en-us/download/details.aspx?id=11774">here</a><br/>
   1.4. If you're using native authorization, you need to make the following actions:<br/>
   1.4.1. Open <a href="https://www.microsoft.com/en-us/download/details.aspx?id=11774">this page</a><br/>
   1.4.2. Press Download button<br/>
   1.4.3. Choose <b>enu\sqljdbc_6.0.8112.100_enu.tar.gz</b><br/>
   1.4.4. Press Next button<br/>
   1.4.5. Extract the archive<br/>
   1.4.6. For 32-bits OS - copy sqljdbc_6.0.8112.100_enu\sqljdbc_6.0\enu\auth\x86\sqljdbc_auth.dll to C:\Windows\System32<br/>
   1.4.7. For 64-bits OS - copy qljdbc_6.0.8112.100_enu\sqljdbc_6.0\enu\auth\x64\sqljdbc_auth.dll to C:\Windows\System32<br/>
   1.5. Execute the command: cd sql-server/sql-server-business-sample<br/>
   1.6. Execute the command: mvn spring-boot:run<br/>
   1.7. Open URL in your browser: http://localhost:8080<br/>
2. MySQL sample:<br/>
   2.1. Create a database and execute the script: mysql/DB/CreatePersistenceObjects.sql<br/>
   2.2. Check the connection string mysql/mysql-designer-sample/src/main/resources/application.properties<br/>
   2.3. Check the connection string mysql/mysql-console-sample/src/main/resources/application.properties<br/>
   2.4. Run Designer:<br/>
	2.4.1. cd mysql/mysql-designer-sample<br/>
   	2.4.2. Execute the command for execute : mvn tomcat8:run-war<br/>
   	2.4.3. Open URL in your browser: http://localhost:8080/mysql-designer-sample<br/>
   2.5. Click on "Upload" button and choose "mysql/scheme.xml" file, after that click on "Save scheme"<br/>
   2.6. Run Console app   <br/>
   	2.6.1. Execute the command: cd mysql/mysql-console-sample<br/>
   	2.6.2. Execute the command: mvn package exec:java<br/>
3. Oracle sample:<br/>
   3.1. Create a database and execute the script: oracle/DB/CreatePersistenceObjects.sql<br/>
   3.2. Check the connection string oracle/oracle-designer-sample/src/main/resources/application.properties<br/>
   3.3. Check the connection string oracle/oracle-console-sample/src/main/resources/application.properties<br/>
   3.4. Run Designer:<br/>
	3.4.1. cd oracle/oracle-designer-sample<br/>
   	3.4.2. Execute the command for execute : mvn tomcat8:run-war<br/>
   	3.4.3. Open URL in your browser: http://localhost:8080/oracle-designer-sample<br/>
   3.5. Click on "Upload" button and choose "oracle/scheme.xml" file, after that click on "Save scheme"<br/>
   3.6. Run Console app   <br/>
   	3.6.1. Execute the command: cd oracle/oracle-console-sample<br/>
   	3.6.2. Execute the command: mvn package exec:java<br/>
4. RavenDB sample:<br/>
   4.1. Check the connection string ravendb/ravendb-business-sample/src/main/resources/application.properties<br/>
   4.2. Execute the command: cd ravendb/ravendb-business-sample<br/>
   4.3. Execute the command: mvn spring-boot:run<br/>
   4.4. Open URL in your browser: http://localhost:8080<br/>
   4.5. Click on "Generate Data" button once<br/>
5. MongoDB sample<br/>
   5.1. Check the connection string mongodb/mongodb-business-sample/src/main/resources/application.properties<br/>
   5.2. Execute the command: cd mongodb/mongodb-business-sample<br/>
   5.3. Execute the command: mvn spring-boot:run<br/>
   5.4. Open URL in your browser: http://localhost:8080<br/>
   5.5. Click on "Generate Data" button once<br/>
6. PostgreSQL sample<br/>
   6.1. Create a database and execute the script: postgresql/DB/CreatePersistenceObjects.sql<br/>
   6.2. Check the connection string postgresql/postgresql-designer-sample/src/main/resources/application.properties<br/>
   6.3. Check the connection string postgresql/postgresql-console-sample/src/main/resources/application.properties   <br/>
   6.4. Run Designer:<br/>
	6.4.1. Execute the command: cd postgresql/postgresql-designer-sample<br/>
   	6.4.2. Execute the command: mvn tomcat8:run-war<br/>
   	6.4.3. Open URL in your browser: http://localhost:8080/postgresql-designer-sample<br/>
   6.5. Click on "Upload" button and choose "postgresql/scheme.xml" file, after that click on "Save scheme"<br/>
   6.6. Run Console app     <br/>
	6.6.1. Execute the command: cd postgresql/postgresql-console-sample<br/>
   	6.6.2. Execute the command: mvn package exec:java<br/>
7. Redis sample<br/>
   7.1. Run Redis instance<br/>
   7.2. Check the connection string redis/redis-designer-sample/src/main/resources/application.properties<br/>
   7.3. Check the connection string redis/redis-console-sample/src/main/resources/application.properties<br/>
   7.4. Run Designer:<br/>
	7.4.1. Execute the command: cd redis/redis-designer-sample<br/>
   	7.4.2. Execute the command: mvn tomcat8:run-war<br/>
   	7.4.3. Open URL in your browser: http://localhost:8080/redis-designer-sample<br/>
   7.5. Click on "Upload" button and choose "redis/scheme.xml" file, after that click on "Save scheme"<br/>
   7.6. Run Console app     <br/>
	7.6.1. Execute the command: cd redis/redis-console-sample<br/>
   	7.6.2. Execute the command: mvn package exec:java<br/>
8. Apache Ignite<br/>
   8.1. Start a Ignite node<br/>
   8.2. Check the connection string ignite/ignite-designer-sample/src/main/resources/application.properties<br/>
   8.3. Check the connection string ignite/ignite-console-sample/src/main/resources/application.properties<br/>
   8.4. Run Designer:<br/>
	8.4.1. Execute the command: cd ignite/ignite-designer-sample<br/>
   	8.4.2. Execute the command: mvn tomcat8:run-war<br/>
   	8.4.3. Open URL in your browser: http://localhost:8080/ignite-designer-sample<br/>
   8.5. Click on "Upload" button and choose "ignite/scheme.xml" file, after that click on "Save scheme"<br/>
   8.6. Run Console app     <br/>
	8.6.1. Execute the command: cd ignite/ignite-console-sample<br/>
   	8.6.2. Execute the command: mvn package exec:java<br/>
9. mysql-java-ee-sample<br/>
   9.1.  Install wildfly (http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.zip)<br/>
   9.2.  Download mysql-driver form Maven (example http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/5.1.40/mysql-connector-java-5.1.40.jar)<br/>
   9.3.  Run wildfly<br/>
   9.4.  Open management console<br/>
   9.5.  Deployments -> Add -> Choose mysql-driver (pount 9.2) (mysql-connector-java-5.1.40.jar)<br/>
   9.6.  Configuration -> Datasources -> Non-XA -> Add<br/>
   9.7.  Choose Datasource -> MySQL Datasource -> Next<br/>
   9.8.  Step 1/3: Datasource Attributes: Name: MySqlDS, JNDI Name: java:jboss/datasources/MySqlDS -> Next<br/>
   9.9.  Step 2/3: JDBC Driver: Âêëàäêà Detected Driver -> mysql-connector-java-5.1.40.jar_com.mysql.jdbc.Driver_5_1 -> Next<br/>
   9.10. Step 3/3: Connection Settings: Connection URL: jdbc:mysql://localhost:3306/workflow, Username: root -> Next<br/>
   9.11. Summary: Finish<br/>
   9.12. Execute the command: cd mysql/mysql-java-ee-sample<br/>
   9.13. ÏOpen URL in your browser: http://localhost:8080/mysql-java-ee-sample-1.0-SNAPSHOT/<br/>
   9.14. Click on "Upload" button and choose "mysql/scheme.xml" file, after that click on "Save scheme"<br/>
   9.15. Press Start Process<br/>

-----------------------------------------------------------------------
<h2>Some comments about configuration a port of web-servers</h2>

<h3>1. Tomcat plugin:</h3>
The samples that run via maven tomcat plugin (mvn tomcat8: run-war) start by default web server on port 8080. For change the port, you need to add a parameter -Dmaven.tomcat.port = 8081 in a command line, where 8081 is a new port. 
Example: <br/>
	mvn tomcat8: run-war -Dmaven.tomcat.port = 8081. Other settings<br/>
Maven tomcat plugin - http://tomcat.apache.org/maven-plugin-trunk/.

<h3>2. Spring boot plugin:</h3>
The examples that run via maven spring boot plugin (mvn spring-boot: run) start by default web server on port 8080. In the src / main / resources / application.properties file of each sample there is a setting Server.port = 8080, changing which you can change the port of web server. The port can also be changed from the command line.
For example: <br/>
	mvn spring-boot: run -Dserver.port = 9090.<br/>

<h3>3. Wildfly: </h3>
A port of wildfly might change via configuration in administration console:<br/>
  1. Configuration -> Socket Binding -> View<br/>
  2. standard-sockets -> View<br/>
  3. http -> Edit<br/>
  4. ${jboss.http.port:8080} -> ${jboss.http.port:18080}<br/>
  5. Save<br/>
  6. Reload server(s) now!<br/>
  7. Standalone Server -> Reload -> Confirm<br/>
  8. Done! The port has been changed 8080 to 18080.<br/><br/>


==================

<b>Official web site</b> - <a href="https://workflowengine.io">https://workflowengine.io</a><br/>
<b>Documentation</b> - <a href="https://workflowengine.io/documentation/">https://workflowengine.io/documentation/</a><br/>
<b>Designer</b> - <a href="https://workflowengine.io/demo/designer">https://workflowengine.io/demo/designer</a><br/>
<b>Demo</b> - <a href="https://workflowengine.io/demo/">https://workflowengine.io/demo/</a><br/>
For technical questions, please contact <a href="mailto:wf@optimajet.com?subject=Question from github">wf@optimajet.com</a><br/>
For commercial use, please contact <a href="mailto:sales@optimajet.com?subject=Question from github">sales@optimajet.com</a><br/>

<b>Free limits:</b>
<ul>
<li>Activity: 15</li>
<li>Transition: 25</li>
<li>Command: 5</li>
<li>Schema: 1</li>
<li>Thread: 1</li>
</ul>
