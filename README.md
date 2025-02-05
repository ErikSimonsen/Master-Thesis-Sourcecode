# An IO thread and a worker thread walk into a bar: a microbenchmark story

This repository contains the source code, results and scripts used to generate the data used in my master thesis. 

Originally inspired and  forked from the repository providing the data for the Quarkus blog post `An IO thread and a worker thread walk into a bar: a microbenchmark story`:
	https://github.com/johnaohara/quarkus-iothread-workerpool
	
Docker is used to create the environment available to the System Under Test

[wrk2](https://github.com/giltene/wrk2) is used to drive the load from the client machine to the server running the System Under Test.  To understand why wrk2, please read http://highscalability.com/blog/2015/10/5/your-load-generator-is-probably-lying-to-you-take-the-red-pi.html  

Running the benchmark is managed by a [qDup](https://github.com/Hyperfoil/qDup)  script.  qDup is an automation tool that provides a way to coordinate multiple terminal shell connections for queuing performance tests and collecting output files  

Result parsing is provided by a custom jbang script

Timing of system startup and results graphing is provided by [node.js](https://nodejs.org/en/) scripts

## Running the benchmark

### Pre-requsites

 - Docker
 - [sdkman](https://sdkman.io/)
 - [node.js](https://nodejs.org/en/)
 - [qDup](https://github.com/Hyperfoil/qDup/releases/tag/release-0.6.3)
 - [wrk2](https://github.com/giltene/wrk2)
 - [jbang](https://github.com/maxandersen/jbang)

### Setup

1. Ensure [docker](https://docs.docker.com/get-docker/) daemon is running on the server-host that you wish to run the
   applications at. Please refer to Docker installation documentation for your particular operating system.

2. Install [node.js](https://nodejs.org/en/) on the server-host that will be used to run the benchmark applications, and
   the host (probably your current machine) that will be used to generate the result graphs.

3. Install [sdkman](https://sdkman.io/install) on the server-host that will be used to run the benchmark applications,
   and the host (probably your current machine) that will be used to generate the result graphs from the data.

4. Install [jbang](https://github.com/maxandersen/jbang) on the host (probably your current machine) that will be used to generate the graphs from the
   generated data.

    ```shell script
    $ sdk install jbang
    ```

4. Build and install [wrk2](https://github.com/giltene/wrk2/wiki/Installing-wrk2-on-Linux) on the client-host machine
   that will be used to drive load to the server

   CentOS / RedHat / Fedora (of course dnf can also be used)

    ```shell script
    sudo yum -y groupinstall 'Development Tools'
    sudo yum -y install openssl-devel git zlib-devel
    git clone https://github.com/giltene/wrk2.git
    cd wrk2
    make
    # move the executable to somewhere in your PATH
    # to find your PATH
    sudo echo $PATH
    # Select appropriate PATH
    # CentOS 7 with cPanel PATH example: sudo cp wrk /usr/local/bin
    sudo cp wrk /somewhere/in/your/PATH
    ```
    
    Ubuntu/Debian (clean box)
    
    ```shell script
    sudo apt-get update
    sudo apt-get install -y build-essential libssl-dev git zlib1g-dev
    git clone https://github.com/giltene/wrk2.git
    cd wrk2
    make
    # move the executable to somewhere in your PATH
    sudo cp wrk /usr/local/bin
    ```
4. Ensure that you are able to open a remote SSH connection to the client and server machines from your current machine, without the need to enter a password.

    You can do this by adding your public ssh key to `~/.ssh/authorized_keys` on the client and server machines.
    Note that on all machines which you are connecting to via SSH an SSH-Server has to be running.
    Some helful links:
    https://docs.fedoraproject.org/en-US/fedora/rawhide/system-administrators-guide/infrastructure-services/OpenSSH/#s2-ssh-configuration-sshd
    https://docs.fedoraproject.org/en-US/fedora/rawhide/system-administrators-guide/infrastructure-services/OpenSSH/#s3-ssh-configuration-keypairs-generating
5. If you modifiy the System Resource-Constraints of the Docker Containers be sure to set the Vert.x EventLoop-Thread Pool size accordingly (
   because Vert.x does not know about the --cpu constraint so it still uses all cpu cores (see application.properties for more information).
)
6. Modify the following lines inside the qDup-scripts directory `scripts/qDup/*-benchmark.yaml` to point to your client and server machines

    ```yaml
   ...
    hosts:
      client: {USER}@{CLIENT_HOST}:22
      server: {USER}@{SERVER_HOST}:22
   ...
     TEST_ENDPOINT : http://{SERVER_HOST}:8080/hello/Bob
     ENVIRONMENT_URL: http://{SERVER_HOST}:8080/environment
   ...
    ``` 

   where;
   - `{USER}` is the username you wish to connect to the remote machine with
   - `{CLIENT_HOST}` is the fully qualified domain name of the client machine to run generate load
   - `{SERVER_HOST}` is the fully qualified domain name of the server machine with the docker daemon already running in
     step (1)

7. Run the benchmark script with
   qDup: `java -jar {path_to_qDup}/qDup-0.6.3-uber.jar -B ./results/data ./scripts/qDup/{script_name}.yaml`.

   N.B. this script may appear to freeze, it takes approx 30 mins to run and will not always write output to the
   terminal.
   Alternatively: Run the script executeQDupScripts.sh to run the 4 qDup scripts once, each for a different mode:
   	- both applications run on jvm mode and only static resources are requested
   	- both applications run on jvm mode and only dynamic resources (from a db container) are requested
   	- both applications run on native mode and only static resources are requested
   	- both applications run on native mode and only dynamic resources (from a db container) are requested
   This will produce 4 directories (jvm-static, jvm-db, native-static, native-db) in ./results/data with all the data for each script-run.
   executeQDupScripts.sh does nothing else than just calling the above code-snipped 4 times with a different {script_name}
8. After the run has complete, process the run data with `processResults.sh`

    ```shell script
    $ ./processResults.sh 4 {SERVER_HOST} {CLIENT_HOST}
    ```   

   where;
    - `4` is the number of cpus (this is used to calculate the % cpu utilization)
    - `{CLIENT_HOST}` is the full qualified hostname of the client machine defined in  `scripts/qDup/benchmark.yaml` in step (5)
    - `{SERVER_HOST}` is the fully qualified domain name of the server machine defined in  `scripts/qDup/benchmark.yaml` in step (5)
	
    If you have used the executeQDupScripts.sh script mentioned above you have to use {TEST_RUN_DIR}/{SERVER_HOST} and {TEST_RUN_DIR}/{CLIENT_HOST} instead.
    for example:
     
    ```shell script
    $ ./processResults.sh 4 jvm-static/{SERVER_HOST} jvm-static/{CLIENT_HOST}
    ```  
9. Results and graphs will be available in `./results/runResult.json` and `./results/graphs/` respectively.
