#! /usr/bin/python

from itertools import product

# Get the initial state of a gridlock problem from a file
def get_init(filename):

    # Get the grid from file, calculate its dimensions
    grid = open(filename, 'r').readlines()
    grid_size = len(grid), len(grid[0]) - 1

    # Create an object for each square in the grid
    squares = ['sq_%d_%d' % (r,c) for r,c in product(range(grid_size[0]), range(grid_size[1]))]

    # Define which squares are next to each other
    next_to = []
    for i in range(grid_size[0]):
        for j in range(grid_size[1] - 1):
            next_to.append('next_to sq_%d_%d sq_%d_%d' % (i,j,i,j+1))
    for i in range(grid_size[0] - 1):
        for j in range(grid_size[1]):
            next_to.append('next_to sq_%d_%d sq_%d_%d' % (i,j,i+1,j))

    # Figure out and represent the location of the goal vehicle, the obstacle
    #  vehicles, and empty squares. The 0's in the grid are the goal vehicle,
    #  and the dashes are empty squares. Obstacle vehicles are identified by
    #  non-zero and non-dash ASCII characters.
    v_to_sq = {}
    for i in range(grid_size[0]):
        for j in range(grid_size[1]):
            if grid[i][j] == '\n':
                continue
            if v_to_sq.get(grid[i][j]) == None:
                v_to_sq[grid[i][j]] = ['sq_%d_%d' % (i,j)]
            else:
                v_to_sq[grid[i][j]].append('sq_%d_%d' % (i,j))

    vehicles = []
    at = []
    empty = []
    for key in v_to_sq.keys():
        if key == '0':
            vehicles.append('v_goal')
            at.append('at v_goal %s' % ' '.join(v_to_sq[key]))
        if key == '-':
            for empty_square in v_to_sq[key]:
                empty.append('empty %s' % empty_square)
        else:
            vehicles.append('v%s' % key)
            at.append('at v%s %s' % (key, ' '.join(v_to_sq[key])))

    # Output information
    squares.sort()
    vehicles.sort()
    next_to.sort()
    at.sort()
    empty.sort()

    print '(:objects'
    for s in squares:
        print '   ' + s
    print
    for v in vehicles:
        print '   ' + v
    print ')\n'

    print '(:init'
    for s in squares:
        print '   (SQUARE %s)' % s
    print
    for v in vehicles:
        print '   (VEHICLE %s)' % s
    print
    for n in next_to:
        print '   (%s)' % n
    print
    for a in at:
        print '   (%s)' % a
    print
    for e in empty: 
        print '   (%s)' % e
    print ')'

FILENAME = 'problem1.txt'
get_init(FILENAME)
