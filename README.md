PreRequisites : Maven, Java8, Git installed and configured.

On windows O.S - Create a new folder, say, C:\test\RG

git clone https://github.com/gandhiravi/crawler
cd crawler

mvn clean test compile assembly:single
cd target

To get results in JSON : 
java -jar -Djavax.net.ssl.trustStore=C:\test\RG\crawler\src\main\resources\web-crawl.jks -Xms32M -Xmx128M -Xss16M crawler-jar-with-dependencies.jar https://www.prudential.co.uk json 

To get results in YAML : 
java -jar -Djavax.net.ssl.trustStore=C:\test\RG\crawler\src\main\resources\web-crawl.jks -Xms32M -Xmx128M -Xss16M crawler-jar-with-dependencies.jar https://www.prudential.co.uk yaml

 
High level assumption / consideration - Only "a" & "img" tags considered for discovering links by crawler.
On the first page (depth = 1), crawler will fetch all the links ("a" & "img" tags).
On subsequent pages (depth > 1), it will not fetch links in "header" & "footer-main-area", there-by reducing links related to navigation.
Links are added to a map, with a true/false value, which helps in deciding whether the link is to be discovered in depth (helpful in breaking recursive call).
The execution would take around 2 min on a good bandwidth connection, performance can be improved by using multi-threading, or asynchronous approach (Python or Vert.x). The implementation can be extented to provide it as a service(API) along with search functionality.
The file will be copied to ${java.io.tmpdir} folder, details would be on console at the end of execution.