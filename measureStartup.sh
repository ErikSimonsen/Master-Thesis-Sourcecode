#!/bin/sh

jarLocation=$1
# make http requests to the /greeting endpoint until the returned status code is 200, which indicates that the backend is ready to answer
# http requests - then kill the process as the needed serverside messages have been generated (see GreetingResource.java)
loopEndpoint() {
  while true; do
    # curl request in silent mode (-s), redirect output (-o) to the null device, write out the http code after a completed transfer (-w)
    # to be able to match it with the condition
    if [ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8080/greeting)" = "200" ]; then
      # kill the process that is executing the jarfile by its command string (-f searches trough the whole command line, not just the process name)
      # because neither the process name nor the process id are known at this point, because this function is called before .jar is executed.
      pkill -f "java -jar ${jarLocation}"
      break
    fi
    sleep 0.00001
  done
}
# call the function as background process - so it does not block the script
loopEndpoint &
date +"EXECUTE %T.%3N" && java -jar "${jarLocation}" # get the date as hh:mm:ss.mmm and execute the .jar
wait                                                 # wait for all processes of this shell context
