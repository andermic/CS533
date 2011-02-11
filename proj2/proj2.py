from rand import randint

class Game:
	moves = 0
	location = []
	parked = False
	occupied = 0

	__init__(self, p):
		#Randomly generate initial location
		if randint(0,1):
			self.location = ('A', 10)
		else:
			self.location = ('B', 1) 
		#Play game here
		while True:
			self.occupied = randint(0, 100) < 110 - 10 * location[1]
			if self.location[1] == 1:
				self.occupied = randint(0,100) < 10
			a = p.next_action((self.location, self.occupied, self.parked))
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

class Player:
	reward = 0
	policy = {}
	q = {}
	def __init__(self):
		for state_action in product(('A', 'B'), range(1,11), (True, False), (True, False), ("DRIVE", "PARK", "EXIT")):
			q[str(state_action)] = 0

	def find_reward(self, s):
		return 0

	def next_action(self, s):
		return 0

class Impatient_Player(Player):
	def find_reward(self, s):
		return 0
	
	def next_action(self, s):
		self.reward -= 5
		return 0

class Normal_Player(Player):
	def find_reward(self, s):
		return 0
	
	def next_action(self, s):
		self.reward -= 1
		return 0
