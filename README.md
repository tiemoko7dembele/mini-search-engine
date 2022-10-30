# Mini Full-Text Search Engine

![Dassault Systèmes](https://github.com/tiemoko7dembele/mini-search-engine/blob/master/docs/images/dassaultsystemes.png?raw=true)


## Architecture 


## How to build ? 
### Clone
Clone the repository:
```
git clone https://github.com/tiemoko7dembele/mini-search-engine.git
```
or
```
git clone git@github.com:tiemoko7dembele/mini-search-engine.git
```

Move to the project directory : 
```
cd mini-search-engine
```

### Build
To build, run :

```
mvn clean install 
```
Or 
```
mvn clean install -DskipTests=true -Pcli 
```





## How to use ?
To use this module in "batch" mode, run :
```
java -jar cli/target/search-engine-cli.jar --mode batch --mode <execution-mode> --input-file <input-file> --to-search <text-to-search> --stop-words-file <stop-words-file>
```

To use this module in "console" mode, run :
```
java -jar cli/target/search-engine-cli.jar --mode console
```

 ### More about Arguments  
  ```
 --mode console 
 --mode <execution-mode> --input-file <input-file> --to-search <text-to-search> [options]
 --version 
 ```

     [REQUIRED]                       
     execution-mode:             "console" or "batch"

     input-file:                 Uri of "txt" or any other file 
                                 File System Storage
                                 Argument required in "batch" mode 

     text-to-search:             Full text to search in the file 
                                 UTF-8
                                 Argument required in "batch" mode 

     [Optional]                       
     stop-words-file:            Uri of "txt" or any other file that contains all stop words (only one by line)
                                 File System Storage


## Setting
- JDK : Amazon corretto-11.0.17


## Features to implement
- Get input file from web URL
- Number lemmatization


