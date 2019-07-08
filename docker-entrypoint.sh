#!/bin/bash
set -e

# Copy jar file

if [[ "$1" = 'reindex' ]]; then
  echo -e "Reindexing data..."
  java $(cat config) -reindex true 
else
  echo -e "Listening to mysql events..."
  java $(cat config) -reindex false
fi

exit
