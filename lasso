#!/bin/sh
LASSO_CLASSPATH="/Users/nathan/projects/wellhead/lasa/build/lasa.jar"
for file in /Users/nathan/projects/wellhead/lasa/lib/* 
do
    echo $file
    LASSO_CLASSPATH=$LASSO_CLASSPATH:$file
done;
java -cp $LASSO_CLASSPATH org.jlas.Main "$@"