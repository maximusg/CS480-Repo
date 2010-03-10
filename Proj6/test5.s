	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$8,%esp
	pushl	$10
	popl	-4(%ebp)
	pushl	$3
	popl	-8(%ebp)
	pushl	-4(%ebp)
	pushl	-8(%ebp)
	popl	%eax
	addl	%eax,0(%esp)
	call	printInt
	addl	$4,%esp
	pushl	-4(%ebp)
	pushl	-8(%ebp)
	popl	%eax
	subl	%eax,0(%esp)
	call	printInt
	addl	$4,%esp
	pushl	-4(%ebp)
	pushl	-8(%ebp)
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	call	printInt
	addl	$4,%esp
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	popl	%eax
	popl	%ecx
	cltd
	idivl	%ecx
	pushl	%eax
	call	printInt
	addl	$4,%esp
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	popl	%eax
	popl	%ecx
	cltd
	idivl	%ecx
	pushl	%edx
	call	printInt
	addl	$4,%esp
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	popl	%eax
	popl	%ecx
	sall	%cl,%eax
	pushl	%eax
	call	printInt
	addl	$4,%esp
.L1:
	leave
	ret
