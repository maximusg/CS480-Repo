Reading file test2
Begin function isqrt
local space 12
frame pointer
Integer -4
do addition Address to Integer
Integer 0
do assignment
frame pointer
Integer -8
do addition Address to Integer
frame pointer
Integer 8
do addition Address to Integer
dereference Integer
do assignment
.L1:
frame pointer
Integer -8
do addition Address to Integer
dereference Integer
frame pointer
Integer -4
do addition Address to Integer
dereference Integer
compare notEqual Boolean
Branch if False Label 2
frame pointer
Integer -12
do addition Address to Integer
frame pointer
Integer -4
do addition Address to Integer
dereference Integer
frame pointer
Integer -8
do addition Address to Integer
dereference Integer
do addition Integer
Integer 2
do division Integer
do assignment
frame pointer
Integer -12
do addition Address to Integer
dereference Integer
frame pointer
Integer -12
do addition Address to Integer
dereference Integer
do multiplication Integer
frame pointer
Integer 8
do addition Address to Integer
dereference Integer
compare greater Boolean
Branch if False Label 3
frame pointer
Integer -8
do addition Address to Integer
frame pointer
Integer -12
do addition Address to Integer
dereference Integer
do assignment
branch to L4
.L3:
frame pointer
Integer -4
do addition Address to Integer
frame pointer
Integer -12
do addition Address to Integer
dereference Integer
do assignment
.L4:
branch to L1
.L2:
frame pointer
Integer -4
do addition Address to Integer
dereference Integer
return from function
End function isqrt
Begin function main
local space 0
Integer 104
push argument Integer
Global isqrt function type
function call Integer
push argument Integer
Global printInt function type
function call Primitive type
End function main
