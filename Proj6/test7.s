	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$8,%esp
	pushl	$4
	popl	-4(%ebp)
	pushl	-4(%ebp)
	negl	0(%esp)
	call	printInt
	addl	$4,%esp
	pushl	-4(%ebp)
	fildl	0(%esp)
	fstps	0(%esp)
	flds	.L2
	subl	$4,%esp
	fstps	0(%esp)
	flds	0(%esp)
	addl	$4,%esp
	fadds	0(%esp)
	fstps	0(%esp)
	flds	0(%esp)
	addl	$4,%esp
	fstps	-8(%ebp)
	pushl	-8(%ebp)
	flds	0(%esp)
	fchs
	fstps	0(%esp)
	call	printReal
	addl	$4,%esp
.L1:
	leave
	ret
	.align	4
.L2:
	.float	3.2
