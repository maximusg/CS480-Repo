	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	pushl	$20
	pushl	$3
	popl	%eax
	subl	%eax,0(%esp)
	call	printInt
	addl	$4,%esp
.L1:
	leave
	ret
