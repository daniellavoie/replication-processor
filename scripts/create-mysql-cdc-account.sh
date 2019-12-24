#!/bin/bash

createAccount() {
  docker exec -it replication-mysql mysql -uroot -pmysql --execute="\
    CREATE USER 'mysql-cdc'@'%' IDENTIFIED BY 'mysql-cdc';
    GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'mysql-cdc'@'%';\
    ALTER USER 'mysql-cdc'@'%' IDENTIFIED WITH mysql_native_password BY 'mysql-cdc';\
    FLUSH PRIVILEGES;"
}

echo "Attempting 10 times to create MySQL user for CDC. MySQL server might not be ready yet."

NEXT_WAIT_TIME=0
MAX_WAIT_TIME=10
until createAccount || [ $NEXT_WAIT_TIME -eq $MAX_WAIT_TIME ]; do
   sleep $(( NEXT_WAIT_TIME++ ))
done

if [ $NEXT_WAIT_TIME -eq $MAX_WAIT_TIME ]; then
  echo "Failed to grant replication permissions to mysql"
  exit 1
fi

echo "Successfully granted replication permissions to mysql"

exit 0