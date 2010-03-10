	.globl	isqrt
	.type	isqrt,@function
isqrt:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$12,%esp
	pushl	$.L2
	call	printStr
	addl	$4,%esp
	pushl	$0
	popl	-4(%ebp)
	pushl	8(%ebp)
	popl	-8(%ebp)
.L3:
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	addl	$1,0(%esp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jle	.L4
	pushl	$.L5
	call	printStr
	addl	$4,%esp
	pushl	$2
	pushl	-4(%ebp)
	pushl	-8(%ebp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	popl	%ecx
	cltd
	idivl	%ecx
	pushl	%eax
	popl	-12(%ebp)
	pushl	-4(%ebp)
	call	printInt
	addl	$4,%esp
	pushl	-12(%ebp)
	call	printInt
	addl	$4,%esp
	pushl	-8(%ebp)
	call	printInt
	addl	$4,%esp
	pushl	-12(%ebp)
	pushl	-12(%ebp)
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	pushl	8(%ebp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jle	.L6
	pushl	-12(%ebp)
	popl	-8(%ebp)
	jmp	.L7
.L6:
	pushl	-12(%ebp)
	popl	-4(%ebp)
.L7:
	jmp	.L3
.L4:
	pushl	$.L8
	call	printStr
	addl	$4,%esp
	pushl	-4(%ebp)
	popl	%eax
	jmp	.L1
.L1:
	leave
	ret
	.align	4
.L2:
	.string	"starting"
	.align	4
.L5:
	.string	"values"
	.align	4
.L8:
	.string	"returning"
	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	pushl	$10820
	call	isqrt
	addl	$4,%esp
	pushl	%eax
	call	printInt
	addl	$4,%esp
.L9:
	leave
	ret
