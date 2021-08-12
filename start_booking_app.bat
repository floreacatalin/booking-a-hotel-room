@echo off

echo Killing processes that might already run on required ports...
FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr :8761') DO TaskKill.exe /PID %%P /T /F  >nul 2>&1
FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr :9101') DO TaskKill.exe /PID %%P /T /F	>nul 2>&1
FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr :9102') DO TaskKill.exe /PID %%P /T /F	>nul 2>&1
FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr :8081') DO TaskKill.exe /PID %%P /T /F	>nul 2>&1
FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr :8082') DO TaskKill.exe /PID %%P /T /F	>nul 2>&1
FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr :9999') DO TaskKill.exe /PID %%P /T /F	>nul 2>&1
FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr :9011') DO TaskKill.exe /PID %%P /T /F	>nul 2>&1
FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr :9012') DO TaskKill.exe /PID %%P /T /F	>nul 2>&1

echo Creating an H2 database cluster with 2 nodes...
start java -cp h2-1.4.200.jar org.h2.tools.Server -tcp -tcpPort 9101 -baseDir h2_server_1 -ifNotExists
start java -cp h2-1.4.200.jar org.h2.tools.Server -tcp -tcpPort 9102 -baseDir h2_server_2 -ifNotExists
start java -cp h2-1.4.200.jar org.h2.tools.CreateCluster -urlSource jdbc:h2:tcp://localhost:9101/./booking_db -urlTarget jdbc:h2:tcp://localhost:9102/./booking_db -user sa -password "password" -serverList localhost:9101,localhost:9102

echo Starting two Eureka instances and waiting 30 seconds...
cd eureka-server
start mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=peer1
start mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=peer2
ping 127.0.0.1 -n 31 > nul

echo Starting two instances of the API server...
cd ..\hotel-booking-api
start mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
start mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082

echo Starting the client...
cd ..\hotel-booking-client
start mvn spring-boot:run

echo Application start-up is complete.
echo This terminal will automatically close in 15 seconds.
ping 127.0.0.1 -n 16 > nul