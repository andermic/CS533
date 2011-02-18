from random import randint
from itertools import product

class Game:
	moves = 0
	location = []
	parked = False
	occupied = 0

	def __init__(self, p):
		#Randomly generate initial state
		if randint(0,1):
			self.location = ('A', 10)
		else:
			self.location = ('B', 1) 
		self.occupied = randint(0, 100) < 110 - 10 * location[1]
		if self.location[1] == 1:
			self.occupied = randint(0,100) < 10
		old_state = (self.location, self.occupied, self.parked)
		
		#Play game here
		while self.moves < 200:
			a = p.next_action(old_state)
			self.occupied = randint(0, 100) < 110 - 10 * location[1]
			if self.location[1] == 1:
				self.occupied = randint(0,100) < 10
			if a == "EXIT":
				return
			elif a == "PARK":
				self.parked = True
			elif a == "DRIVE":
				if location[0] == 'B':
					self.location[1] += 1
					if self.location[1] > 10:
						self.location[0] = 'A'
						self.location[1] = 1
				else:
					self.location[l] -= 1
					if self.location[1] < 1:
						self.location[0] = 'B'
						self.location[1] = 10
			self.moves += 1
			new_state = (self.location, self.occupied, self.parked)
			p.update_q(old_state, a, (self.location, self.occupied, self.parked))
			old_state = new_state
		stream = open("result.txt", 'w')
		for sa in product(('A', 'B'), range(1,11), (True, False), (True, False), ("DRIVE", "PARK", "EXIT")):
			stream.write('%s - %d\n' % (str(sa), q[str(sa)]))
		stream.close()

class Player:
	reward = 0
	ALPHA = 0.5
	BETA = 0.5
	policy = {}
	q = {}
	def __init__(self):
		for state_action in product(('A', 'B'), range(1,11), (True, False), (True, False), ("DRIVE", "PARK", "EXIT")):
			q[str(state_action)] = 1

	def find_reward(self, s):
		return 0

	def next_action(self, s):
		qSum = 0
		for a in ("DRIVE", "PARK", "EXIT"):
			qSum += q[str(s[0][0], s[0][1], s[1], s[2], a)]
		actionValue = randint(1, qSum)
		
		if actionValue <= q[str(s[0][0], s[0][1], s[1], s[2], "DRIVE")]:
			return "DRIVE"
		if actionValue <= q[str(s[0][0], s[0][1], s[1], s[2], "DRIVE")] + q[str(s[0][0], s[0][1], s[1], s[2], "PARK")]:
			return "PARK"
		return "EXIT"

	def update_q(self, s_prev, action, s_curr):
		maxQ = q[str(s_curr[0][0], s_curr[0][1], s_curr[1], s_curr[2], "DRIVE")]
		for a in ("PARK", "EXIT"):
			if q[str(s_curr[0][0], s_curr[0][1], s_curr[1], s_curr[2], a)] > maxQ:
				maxQ = q[str(s_curr[0][0], s_curr[0][1], s_curr[1], s_curr[2], a)]
		q[str(s_prev[0][0], s_prev[0][1], s_prev[1], s_prev[2], action)] += \
			ALPHA * (self.find_reward(s_prev) + BETA * maxQ - q[str(s_prev[0][0], s_prev[0][1], s_prev[1], s_prev[2], action)])

class Impatient_Player(Player):
	def find_reward(self, s):
		if not s[2]:
			return -2
		if s[1]:
			return -1000
		reward = 110 - 10*s[0][1]
		if s[0][1] == 1:
			reward -= 100
		return reward

class Normal_Player(Player):
	def find_reward(self, s):
		if not s[2]:
			return -1
		if s[1]:
			return -2000
		reward = 110 - 5*s[0][1]
		if s[0][1] == 1:
			reward -= 200
		return reward

Game(Impatient_Player())