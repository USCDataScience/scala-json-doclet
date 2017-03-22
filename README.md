# Scala Json Doclet

This repository has a scaladoc generator that generates in JSON format.


## Build:

```mvn clean package```

That should produce a jar in `target` dir


## To Run

```
java -jar target/*-jar-with-dependencies.jar -help 

```

Example 

    mkdir docs
    java -jar target/*-jar-with-dependencies.jar  -d docs src/main/scala/**


Note:
All the classes references in source code should be in classpath.
The easiest way to get around is by letting maven resolve all the dependencies and add it inside the jar.
1. go to your project that you want to generate source code
2. do `mvn install` on that project (assuming it is maven)
3. Come back to this repo and add that dependency to `pom.xml`
4. rebuild this project `mvn package`
5. Run the above example. The required classes are already assembled to jar by maven 


## Credits

+ Most of the code in this repo is taken from https://github.com/szeiger/extradoc
 So, the credit goes to the developer of that project.
 Note: the original project seems to be abandoned. It had lots of features, those that are unnecessary for me were removed while porting to Scala 2.11.8 from 2.8.0.


 