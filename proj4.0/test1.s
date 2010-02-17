Reading file test1
Begin function gcd
local space 0
.L1:
frame pointer
Integer 8
do addition Address to Integer
dereference Integer
frame pointer
Integer 12
do addition Address to Integer
dereference Integer
compare notEqual Boolean
Branch if False Label 2
frame pointer
Integer 8
do addition Address to Integer
dereference Integer
frame pointer
Integer 12
do addition Address to Integer
dereference Integer
compare greater Boolean
Branch if False Label 3
frame pointer
Integer 8
do addition Address to Integer
frame pointer
Integer 8
do addition Address to Integer
dereference Integer
frame pointer
Integer 12
do addition Address to Integer
dereference Integer
do subtraction Integer
do assignment
branch to L4
.L3:
frame pointer
Integer 12
do addition Address to Integer
frame pointer
Integer 12
do addition Address to Integer
dereference Integer
frame pointer
Integer 8
do addition Address to Integer
dereference Integer
do subtraction Integer
do assignment
.L4:
branch to L1
.L2:
frame pointer
Integer 8
do addition Address to Integer
dereference Integer
return from function
End function gcd
Begin function main
local space 8
String Euclids GCD algorithm
push argument Pointer to Character
Global printStr function type
function call Primitive type
frame pointer
Integer -4
do addition Address to Integer
Integer 182
do assignment
frame pointer
Integer -4
do addition Address to Integer
dereference Integer
push argument Integer
Global printInt function type
function call Primitive type
frame pointer
Integer -8
do addition Address to Integer
Integer 258
do assignment
frame pointer
Integer -8
do addition Address to Integer
dereference Integer
push argument Integer
Global printInt function type
function call Primitive type
frame pointer
Integer -8
do addition Address to Integer
dereference Integer
push argument Integer
frame pointer
Integer -4
do addition Address to Integer
dereference Integer
push argument Integer
Global gcd function type
function call Integer
push argument Integer
Global printInt function type
function call Primitive type
End function main
