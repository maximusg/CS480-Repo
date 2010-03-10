	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$8,%esp
	pushl	$101
	popl	-4(%ebp)
	pushl	$2
	pushl	-4(%ebp)
	popl	%eax
	popl	%ecx
	cltd
	idivl	%ecx
	pushl	%eax
	popl	-4(%ebp)
	pushl	-4(%ebp)
	call	printInt
	addl	$4,%esp
.L1:
	leave
	ret
