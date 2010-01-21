#! /bin/sh

for i in 1 2 3 4 5 6 7 8 9 10 11 n m
do
	echo "Testing $i"
	echo
	diff test$i.o test$i.s
done