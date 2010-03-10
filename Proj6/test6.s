	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$8,%esp
	flds	.L2
	subl	$4,%esp
	fstps	0(%esp)
	flds	0(%esp)
	addl	$4,%esp
	fstps	-4(%ebp)
	flds	.L3
	subl	$4,%esp
	fstps	0(%esp)
	flds	0(%esp)
	addl	$4,%esp
	fstps	-8(%ebp)
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	flds	0(%esp)
	addl	$4,%esp
	fadds	0(%esp)
	fstps	0(%esp)
	call	printReal
	addl	$4,%esp
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	flds	0(%esp)
	addl	$4,%esp
	fsubs	0(%esp)
	fstps	0(%esp)
	call	printReal
	addl	$4,%esp
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	flds	0(%esp)
	addl	$4,%esp
	fmuls	0(%esp)
	fstps	0(%esp)
	call	printReal
	addl	$4,%esp
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	flds	0(%esp)
	addl	$4,%esp
	fdivs	0(%esp)
	fstps	0(%esp)
	call	printReal
	addl	$4,%esp
.L1:
	leave
	ret
	.align	4
.L2:
	.float	10.3
	.align	4
.L3:
	.float	3.1
