#!/bin/bash

CQL="
CREATE KEYSPACE stocks_exchange WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 2};
CREATE TABLE IF NOT EXISTS stock_sexchange.stocks(
uuid UUID PRIMARY KEY,
sym TEXT,
mc TEXT,
c DOUBLE,
f DOUBLE,
r DOUBLE,
last_price DOUBLE,
last_volume DOUBLE,
lot DOUBLE,
ot TEXT,
change_pc TEXT,
ave_price TEXT,
high_price TEXT,
low_price TEXT,
f_room TEXT,
g1 TEXT,
g2 TEXT,
g3 TEXT,
g4 TEXT,
g5 TEXT,
g6 TEXT,
g7 TEXT,
cwlisted_share TEXT,
industry TEXT,
crawled_time TIMESTAMP);
"

until echo $CQL | cqlsh; do
  echo "cqlsh: Cassandra is unavailable - retry later"
  sleep 2
done &

chmod +x custom-docker-entrypoint.sh
exec /usr/local/bin/docker-entrypoint.sh