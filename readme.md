**What is it:**

This is a GitHub+Twitter API mashup tool.

**What does it do:** 

The tool will search github for top matches using the GitHub search APIs. 
Using the full name of these projects, we then go to Twitter APIs to find recent tweets. 
The Final data will be printed on the console as a JSON.
It accepts the query string as input. If not provided, it will use 'reactive' as the default search term.

**How to run.**


first time run:
In order to use the twitter APIs, we need an OAuth token. 
So for the first run, you need to provide your app's client key and client secret in the resource.properties file.
This is sensitive information, and you may choose to encrypt it before saving in the file. I support simple Password Based Encryption.
Here's what you need to do:
Checkout project or unzip the package and run:

    mvn clean install
    java -cp target\api-mashup-1.0-SNAPSHOT.jar com.workday.test.EncryptUtils <StringToBeEncrypted>
    
This will give you the encrypted Strings for your key and secret. 
Save these in src\main\resources\resource.properties file as:

    twitter.consumer.key.encrypted=<EncryptedKey>
    twitter.consumer.secret.encrypted=<EncryptedSecret>

Following this, you must run maven again
    
    mvn clean install

Normal runs:

    java -jar target\api-mashup-1.0-SNAPSHOT.jar
    
This will run the tool for the default query string - 'reactive'
Or

    java -jar target\api-mashup-1.0-SNAPSHOT.jar <QueryString>

If you are running this behind a proxy, you can configure your http/https proxy in the resource properties.

    system.http.proxyHost=proxyhostname
    system.http.proxyPort=proxyportname
    system.https.proxyHost=proxyhostname
    system.https.proxyPort=proxyportname

**Technical Details:**

Based on the requirement, I decided to use Akka Actors for designing the solution. Specifically, it was designed so to cater the following:


Async: Tasks are mostly Async and the downstream actors need not wait until the upstream has completed their tasks. This is especially handy when we are fetching the tweets. The logger does not have to wait for the next tweets to be fetched.

Resiliance: the supervisor actor has strategies in place for fault tolerance. 

Scalability: Scaling up is easy with Akka - can be horizontally scaled, by moving actors to different systems or even creating load balanced actors. I have not implemented this because it isn't needed right now.

**The flow:**

1. Main method will accept the query string or set the default string 'reactive'
2. It will check resource.properties and set system properties.
3. Crete the Akka actor system and create the MashupSupervisor actor.
4. Start the flow by invoking 'tell' to the supervisor.
5. Wait for user input. If user wishes to terminate, she can do so by hitting enter.

Actors:
1. MashupSupervisor creates all the other actors in the system, feeds them their dependencies and also configures their downstream paths.
2. LoggerActor is the downstream of TwitterActor is a downstream of GithubActor.
3. Supervisor also decides strategies for error handling in case of exceptions. I have defined 3 - restart, resume and stop.
4. After initializing, it will 'tell' GithubActor, passing the query string in the message.
5. Github actor will connect to github search API, create objects from responses and then 'tell' the downstream for each project.
6. Tell being an inherently async operation, the TwitterActor starts its job immediately.
7. It fetches tweets for the project name and creates the final response object. Which is then passed in the message to LoggerActor.
8. LoggerActor starts printing out the MashedData object in JSON format on System Console.
9. Since this is an Akka actor system, the program does not terminate when the flow is done, because there is no concept of 'done'. The actors are always listening for more messages. In order to forcefully terminate the program the printing is done (OR at any point during the execution), just hit enter.

 