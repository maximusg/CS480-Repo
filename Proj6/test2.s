	.globl	gcd
	.type	gcd,@function
gcd:
	pushl	%ebp
	movl	%esp,%ebp
.L2:
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	je	.L3
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jle	.L4
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	subl	%eax,0(%esp)
	popl	8(%ebp)
	jmp	.L5
.L4:
	pushl	12(%ebp)
	pushl	8(%ebp)
	popl	%eax
	subl	%eax,0(%esp)
	popl	12(%ebp)
.L5:
	jmp	.L2
.L3:
	pushl	8(%ebp)
	popl	%eax
	jmp	.L1
.L1:
	leave
	ret
	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$8,%esp
	pushl	$.L7
	call	printStr
	addl	$4,%esp
	pushl	$182
	popl	-4(%ebp)
	pushl	-4(%ebp)
	call	printInt
	addl	$4,%esp
	pushl	$258
	popl	-8(%ebp)
	pushl	-8(%ebp)
	call	printInt
	addl	$4,%esp
	pushl	-8(%ebp)
	pushl	-4(%ebp)
	call	gcd
	addl	$8,%esp
	pushl	%eax
	call	printInt
	addl	$4,%esp
.L6:
	leave
	ret
	.align	4
.L7:
	.string	"Euclids GCD algorithm"
