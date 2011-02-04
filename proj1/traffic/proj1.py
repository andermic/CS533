#! /usr/bin/python

from itertools import product
from sys import argv

# Convert a tuple of numbers to the name of a square
def make_sq(tuple):
    return 'sq_%d_%d' % (tuple[0], tuple[1])

# Get the initial state of a gridlock problem from a file
def get_init(filename):

    # Get the grid from file, calculate its dimensions
    grid = open(filename, 'r').readlines()
    grid_size = len(grid), len(grid[0]) - 1

    # Create an object for each square in the grid
    squares = ['sq_%d_%d' % (r,c) for r,c in product(range(grid_size[0]), range(grid_size[1]))]

    # Define which squares are next to each other
    next_to_right = []
    next_to_up = []
    for i in range(grid_size[0]):
        for j in range(grid_size[1] - 1):
            next_to_right.append('next_to_right sq_%d_%d sq_%d_%d' % (i,j,i,j+1))
    for i in range(grid_size[0] - 1):
        for j in range(grid_size[1]):
            next_to_up.append('next_to_up sq_%d_%d sq_%d_%d' % (i+1,j,i,j))

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
                v_to_sq[grid[i][j]] = [(i,j)]
            else:
                v_to_sq[grid[i][j]].append((i,j))

    vehicle2h = []
    vehicle2v = []
    vehicle3h = []
    vehicle3v = []
    at = []
    empty = []
    for key in v_to_sq.keys():
        if key == '-':
            for empty_square in v_to_sq[key]:
                empty.append('empty %s' % make_sq(empty_square))
        else:
            v = v_to_sq[key]
            if len(v) == 2:
                if v[0][0] == v[1][0]:
                    vehicle2h.append('v%s' % key)
                else:
                    vehicle2v.append('v%s' % key)
            elif len(v) == 3:
                if v[0][0] == v[1][0]:
                    vehicle3h.append('v%s' % key)
                else:
                    vehicle3v.append('v%s' % key)
            if len(v_to_sq[key]) == 2:
                at.append('at_2 v%s %s' % (key, ' '.join([make_sq(i) for i in v_to_sq[key]])))
            elif len(v_to_sq[key]) == 3:
                at.append('at_3 v%s %s' % (key, ' '.join([make_sq(i) for i in v_to_sq[key]])))

    goal = []
    goal.append('(:goal (and')
    goal.append('   (at v0 sq_%d_%d sq_%d_%d)' % (v_to_sq['0'][0][0],
     grid_size[1]-2, v_to_sq['0'][0][0], grid_size[1]-1))
    goal.append('))')

    # Output information
    for i in [squares, vehicle2h, vehicle2v, vehicle3h, vehicle3v, next_to_up,
     next_to_right, at, empty]:
        i.sort()

    print '(:objects'
    for s in squares:
        print '   ' + s
    print
    for v in vehicle2h:
        print '   ' + v
    print
    for v in vehicle2v:
        print '   ' + v
    print
    for v in vehicle3h:
        print '   ' + v
    print
    for v in vehicle3v:
        print '   ' + v
    print ')\n'

    print '(:init'
    for s in squares:
        print '   (SQUARE %s)' % s
    print
    for v in vehicle2h:
        print '   (VEHICLE_2H %s)' % v
    print
    for v in vehicle2v:
        print '   (VEHICLE_2V %s)' % v
    print
    for v in vehicle3h:
        print '   (VEHICLE_3H %s)' % v
    print
    for v in vehicle3v:
        print '   (VEHICLE_3V %s)' % v
    print
    for n in next_to_right:
        print '   (%s)' % n
    print
    for n in next_to_up:
        print '   (%s)' % n
    print
    for a in at:
        print '   (%s)' % a
    print
    for e in empty: 
        print '   (%s)' % e
    print ')\n'

    for g in goal:
        print g

get_init(argv[1])
