#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
 weblicht-nentities-ws-archetype

 Created using WebLicht NamedEntities Webservice Dropwizard Archetype.

 What is it?
 ===========

 This is a starter project for TCF processing WebLicht Webservice. It can be 
 deployed using Maven 2.0.10 or greater with Java 6.0 or greater. Different from
 Weblicht NamedEntities Webservice Archetype which is for applications runing in 
 a web container, this application can be deployed standalone. Jetty will be used
 as an embedded HTTP server.

 Using it for your own projects
 ============================== 

 This project comes with some basic functionality that can easily be used as the
 basis for your own project implementing WebLicht web-service. 

 This web-service imitates named entities recognizer service. The service 
 processes POST requests containing TCF data with tokens. It uses token 
 annotations to produce named entity annotations. 
 
 It imitates a tool that requires loading a named entities list for identifying 
 named entities. In this web-service example the tool instance is created only 
 once (the corresponding list resource is loaded only once), when the application 
 is created. The example shows the case when the tool is not thread-safe. 
 Therefore, the tool's process() method requires synchronization.

 All the classes below should be renamed to reflect the name of your tool.
 
    de.tuebingen.uni.sfs.calrind.services.NamedEntitiesService.java - is the application definition, 
    use it to define the path to your application and/or add more resources.

    de.tuebingen.uni.sfs.calrind.resources.NamedEntitiesResource.java - is the definition of a 
    resource, in case more resources are required you can use it as a template 
    for any further resources. (Don't forget to add them to your class that
    extends Application, e.g. ReferencesService.java)

    de.tuebingen.uni.sfs.calrind.core.NamedEntitiesTool.java - is the place where an actual 
    implementation of a tool resides. In this template a mock implementation of 
    a named entities recognizer is provided.

 It is recommended that you delete all the testing files when deploying this
 tool in a production environment, especially:

    src/main/webapp/input.xml

 How To Deploy
 =============
 
 Once the application is deployed, it can be accessed using the following URL: 
 
 http://localhost:8080/weblicht-nentities-ws-archetype/annotate/stream

 or 
 
 http://localhost:8080/weblicht-nentities-ws-archetype/annotate/bytes

 Any Maven aware IDE (such as Netbeans or Eclipse) should be able to deploy this
 application using methods provided by the IDE.

 How To Test
 ===========

 You can test this webservice using either "wget" or "curl" along with the input 
 provided as part of this project at src/main/webapp/input.xml

 Run wget:
 wget --post-file=input.xml --header='Content-Type: text/tcf+xml' http://localhost:8080/weblicht-nentities-ws-archetype/annotate/stream

 Or run curl:
 curl -H 'content-type: text/tcf+xml' -d @input.xml -X POST http://localhost:8080/weblicht-nentities-ws-archetype/annotate/stream
 
