	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	pushl	$.L2
	call	printStr
	addl	$4,%esp
.L1:
	leave
	ret
	.align	4
.L2:
	.string	"hello world!"
