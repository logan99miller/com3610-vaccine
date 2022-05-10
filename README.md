As the system requires an SQL database several steps are required before
launching the application. First an SQL server must be setup. This can be
achieved by installing MySQL Community Edition
(https://dev.mysql.com/downloads/workbench/) and creating a new connection by
clicking the + icon with the name “vaccine_system”, hostname “127.0.0.1” and
port “3306” to ensure compatibility with the system. Next create the database
in MySQL by running SQL/create_database_table.sql and optionally populate the
database by running one of the scenario scripts. To run a script you click the
lightning symbol.
When running the script to create a database you may first need to run the 2nd
line (CREATE DATABASE vaccine_system), refresh the schema panel, select the
vaccine_system database and then run the remaining lines. To run lines
individually, highlight the lines you wish to execute and then press the
lightning symbol.
Finally, run the system by opening out/artifcats/vaccine_jar2/vaccine.jar.
Please note you may need to re-adjust the window size when you launch the program
as some OSs will not display any content until then. Also, after logging in with
your database credentials the system may take a while to load.

If you want to compile the code yourself I recommend doing it in and IDE such
as IntelliJ (https://www.jetbrains.com/idea/download/?fromIDE=#section=mac) and
adding a MySQLConnector by adding the mysql:mysql-connector-java in maven or
adding it as a java library.


Icons provided by https://www.flaticon.com/free-icons/open-source.