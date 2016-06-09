from __future__ import print_function
import sys

def factorial(n):
    '''computes n * (n - 1) * ... * 1'''
    if n == 1:
        return 1
    else:
        return n * factorial(n - 1)

if __name__ == '__main__':
    for arg in sys.argv[1:]:
        n = int(arg)
        print('the factorial of', n, 
              'is', factorial(n))
