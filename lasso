#!/bin/sh
LASSO_CLASSPATH="/Users/nathan/projects/wellhead/lasso/build/lasso.jar"
for file in /Users/nathan/projects/wellhead/lasso/lib/* 
do
    LASSO_CLASSPATH=$LASSO_CLASSPATH:$file
done;
java -cp $LASSO_CLASSPATH com.wellhead.lasso.Main "$@"