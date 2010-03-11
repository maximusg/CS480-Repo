#! /bin/sh

for i in 1 2 3 4 5 9
do
	echo "Testing $i"
	echo
	diff test$i.o test$i.s
done