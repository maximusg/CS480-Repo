#! /bin/sh

for i in {1..11} n m
do
	echo "Testing $i"
	echo
	echo diff test$i.o test$i.s
done