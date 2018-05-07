# A service to host cx files temporarily

To deploy create a run.sh file in the directory and put this in it:

#!/bin/bash

#rest service will be on the default port 8286
#Need to configure a proxy if the prefix is on a different route. 
#For example in this case the service is on port 8286 and the path for
#getting the file is at http://dev.ndexbio.org/tempcx/xxx
nohup java -Xmx1g -jar  -Dhost.prefix=http://dev.ndexbio.org/tempcx/ app-1.0-SNAPSHOT.jar & 1>out
