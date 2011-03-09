from os import system
from random import randint
import math

def product(*args, **kwds):
        "cartesian product"
        # product('ABCD', 'xy') --> Ax Ay Bx By Cx Cy Dx Dy
        # product(range(2), repeat=3) --> 000 001 010 011 100 101 110 111
        pools = map(tuple, args) * kwds.get('repeat', 1)
        result = [[]]
        for pool in pools:
            result = [x+[y] for x in result for y in pool]
        for prod in result:
            yield tuple(prod)

class Game:
	moves = 0
	location = []
	parked = False
	occupied = 0
	reward = 0

	def __init__(self, p):
		#Randomly generate initial state
		if randint(0,1):
			self.location = ('A', 10)
		else:
			self.location = ('B', 1) 
		self.occupied = randint(0, 100) < 110 - 10 * self.location[1]
		if self.location[1] == 1:
			self.occupied = randint(0,100) < 10
		old_state = (self.location, self.occupied, self.parked)
		
		#Play game here
		while self.moves < 200:
			a = p.next_action(old_state)
			self.moves += 1
			if self.location[1] == 1:
				self.occupied = randint(0,100) < 10
			if a == "EXIT":
				if self.parked:
					p.update_q(old_state, a, old_state)
					break
				else:
					self.reward += p.STAGNANT_STATE_REWARD
			elif a == "PARK":
				if (self.parked):
					self.reward += p.STAGNANT_STATE_REWARD
				else: 
					self.parked = True
					self.reward += p.find_reward((self.location, self.occupied, self.parked))
			elif a == "DRIVE":
				if self.location[0] == 'B':
					self.location = (self.location[0], self.location[1] + 1)
					if self.location[1] > 10:
						self.location = ('A', 1)
				else:
					self.location = (self.location[0], self.location[1] - 1)
					if self.location[1] < 1:
						self.location = ('B', 10)
				self.occupied = randint(0, 100) < 110 - 10 * self.location[1]
				self.reward += p.find_reward((self.location, self.occupied, self.parked))
			p.update_q(old_state, a, (self.location, self.occupied, self.parked))
			old_state = (self.location, self.occupied, self.parked)

class Player:
	STAGNANT_STATE_REWARD = -1;
	reward = 0
	BETA = 1
	EPSILON = 1
	policy = {}
	q = {}
	moves = 0
	
	def __init__(self):
		for state_action in product(('A', 'B'), range(1,11), (True, False), (True, False), ("DRIVE", "PARK", "EXIT")):
			self.q[str(state_action)] = [1, 0]

	def find_reward(self, s):
		return 0

	def next_action(self, s):
		#if randint(1, math.floor(math.sqrt(moves / 20 + 1) / self.EPSILON)) > 1:
		if randint(1, 10) > 1:
			bestAction = "DRIVE"
			if self.q[str((s[0][0], s[0][1], s[1], s[2], "PARK"))][0] > self.q[str((s[0][0], s[0][1], s[1], s[2], bestAction))][0]:
				bestAction = "PARK"
			if self.q[str((s[0][0], s[0][1], s[1], s[2], "EXIT"))][0] > self.q[str((s[0][0], s[0][1], s[1], s[2], bestAction))][0]:
				bestAction = "EXIT"
			return bestAction
		return {1 : "DRIVE", 2 : "PARK", 3: "EXIT"}[randint(1,3)]

	def update_q(self, s_prev, action, s_curr):
		self.q[str((s_prev[0][0], s_prev[0][1], s_prev[1], s_prev[2], action))][1] += 1
		maxQ = self.q[str((s_curr[0][0], s_curr[0][1], s_curr[1], s_curr[2], "DRIVE"))][0]
		for a in ("PARK", "EXIT"):
			if self.q[str((s_curr[0][0], s_curr[0][1], s_curr[1], s_curr[2], a))][0] > maxQ:
				maxQ = self.q[str((s_curr[0][0], s_curr[0][1], s_curr[1], s_curr[2], a))][0]
		self.q[str((s_prev[0][0], s_prev[0][1], s_prev[1], s_prev[2], action))][0] += \
			1/float(self.q[str((s_prev[0][0], s_prev[0][1], s_prev[1], s_prev[2], action))][1]) * \
			(self.find_reward(s_prev) + self.BETA * maxQ - self.q[str((s_prev[0][0], s_prev[0][1], s_prev[1], s_prev[2], action))][0])

class Normal_Player(Player):
	STAGNANT_STATE_REWARD = -1
	def find_reward(self, s):
		if not s[2]:
			return self.STAGNANT_STATE_REWARD
		if s[1]:
			return -1000
		reward = 110 - 5*s[0][1]
		if s[0][1] == 1:
			reward -= 200
		return reward

class Impatient_Player(Player):
	STAGNANT_STATE_REWARD = -2
	def find_reward(self, s):
		if not s[2]:
			return self.STAGNANT_STATE_REWARD
		if s[1]:
			return -1000
		reward = 110 - 10*s[0][1]
		if s[0][1] == 1:
			reward -= 100
		return reward

class Random_Player(Player):
	def next_action(self, s):
		if s[2]:
			return "EXIT"
		return {1 : "DRIVE", 2 : "PARK"}[randint(1,2)]
	
	def find_reward(self, s):
		if not s[2]:
			return -1
		if s[1]:
			return -1000
		reward = 110 - 5*s[0][1]
		if s[0][1] == 1:
			reward -= 200
		return reward

class Drive_Player(Player):
	def next_action(self, s):
		if s[2]:
			return "EXIT"
		if s[1]:
			return "DRIVE"
		return {1 : "DRIVE", 2 : "PARK"}[randint(1,2)]
	
	def find_reward(self, s):
		if not s[2]:
			return -1
		if s[1]:
			return -1000
		reward = 110 - 5*s[0][1]
		if s[0][1] == 1:
			reward -= 200
		return reward

class Simple_Random_Player(Player):
	def next_action(self, s):
		if s[2]:
			return "EXIT"
		if s[0][1] == 1:
			return "DRIVE"
		return {1 : "DRIVE", 2 : "PARK"}[randint(1,2)]
	
	def find_reward(self, s):
		if not s[2]:
			return -1
		if s[1]:
			return -1000
		reward = 110 - 5*s[0][1]
		if s[0][1] == 1:
			reward -= 200
		return reward

class Simple_Drive_Player(Player):
	def next_action(self, s):
		if s[2]:
			return "EXIT"
		if s[1]:
			return "DRIVE"
		if s[0][1] > 1 and s[0][1] < 6:
			return "PARK"
		return {1 : "DRIVE", 2 : "PARK"}[randint(1,2)]
	
	def find_reward(self, s):
		if not s[2]:
			return -1
		if s[1]:
			return -1000
		reward = 110 - 5*s[0][1]
		if s[0][1] == 1:
			reward -= 200
		return reward
	
system('CLS')
print 'Pick a player:'
print '1 - Random/Normal'
print '2 - Drive/Normal'
print '3 - Simple Random'
print '4 - Simple Drive'
print '5 - RL/Normal'
print '6 - RL/Impatient'


choice = input(':')
p = {1:Random_Player(), 2:Drive_Player(), 3:Simple_Random_Player(), 4:Simple_Drive_Player(), 5:Normal_Player(), 6:Impatient_Player()}[choice]

ITERATIONS = 1000
moves = 0
for i in range(5 * ITERATIONS):
	moves += Game(p).moves
reward = 0
for i in range(ITERATIONS):
	reward += Game(p).reward
print "Average reward: " + str(float(reward) / ITERATIONS)

stream = open("result%d.txt" % choice, 'w')
stream.write('Average moves: %f\n' % (float(moves) / (5 * ITERATIONS)))
stream.write('Average score (after training): %f\n' % (float(reward) / ITERATIONS))
for sa in product(('A', 'B'), range(1,11), ("DRIVE", "PARK", "EXIT")):
	stream.write('%s\t%f\n' % (str(sa), p.q[str((sa[0], sa[1], False, False, sa[2]))][0]))
stream.write('\n\n\n')
for sa in product(('A', 'B'), range(1,11), (True, False), (True, False), ("DRIVE", "PARK", "EXIT")):
	stream.write('%s\t%f\n' % (str(sa), p.q[str(sa)][0]))
stream.close()
