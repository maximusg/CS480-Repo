	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$4,%esp
	pushl	$16
	call	malloc
	addl	$4,%esp
	pushl	%eax
	popl	-4(%ebp)
	pushl	-4(%ebp)
	pushl	$3
	pushl	$2
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$4
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	pushl	$7
	popl	%eax
	popl	%ecx
	movl	%eax,0(%ecx)
	pushl	-4(%ebp)
	pushl	$3
	pushl	$2
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$4
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	pushl	0(%eax)
	call	printInt
	addl	$4,%esp
.L1:
	leave
	ret
