#! /usr/bin/python

from random import randint
from random import choice
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

def cat_score(category, current_dice):
	num_of_each = [None,0,0,0,0,0,0]
	score = 0
	for d in current_dice:
		num_of_each[int(d)] += 1

	if len(category) == 2:
		die = int(category[0])
		score = num_of_each[die] * die
	elif category == '3 of a kind':
		found = False
		for i in range(1,7):
			if num_of_each[i] >= 3:
				found = True
		if found:
			for i in range(1,7):
				score += num_of_each[i] * i
	elif category == '4 of a kind':
		found = False
		for i in range(1,7):
			if num_of_each[i] >= 4:
				found = True
		if found:
			for i in range(1,7):
				score += num_of_each[i] * i
	elif category == 'Full house':
		two = False;
		three = False;
		for i in range(1,7):
			if num_of_each[i] == 2:
				two = True
			elif num_of_each[i] == 3:
				three = True
		if two and three:
			score = 25
	elif category == 'Small straight':
		count = 0
		sequence = False
		for i in range(1,7):
			if num_of_each[i] > 0:
				sequence = True
				count += 1
				if count >= 4:
					score = 30
					break
			else:
				sequence = False
				count = 0
	elif category == 'Large straight':
		 count = 0
		 for i in range(2,6):
			 if num_of_each[i] == 1:
				 count += 1
		 if (count == 4) and (num_of_each[1] == 1 or num_of_each[6] == 1):
			 score = 40
	elif category == 'Yahtzee':
		for i in range(1,7):
			if num_of_each[i] == 5:
				score = 50
	elif category == 'Chance':
		for i in range(1,7):
			score += num_of_each[i] * i
	return score

# Simulates the yahtzee game
class Simulation:
    CATS = ['Yahtzee', '4 of a kind', 'Large straight', 'Full house', 
	  '3 of a kind', 'Small straight', "6s", "5s",  "4s",  "3s", "2s",
	  "1s", 'Chance']
    categories = CATS[:]
    scores = {}
	
    # Run the simulation
    def __init__(self, agent, show_output):
        # Keep asking the agent for actions until no more categories remain
        while len(self.categories) != 0:

            action = ''     # Contains the dice kept or the category chosen

            # Give the agent 3 rolls
            for i in range(1,4):
                # State consists of a 3-tuple: The categories left to
                #  choose from, the number of rolls remaining in this
                #  round, and the current dice.
                rolls_left = 3 - i
                current_dice = ''.join(sorted(action + ''.join([str(randint(1,6)) for i in range(6 - len(action))])))
                state = (self.categories, rolls_left, current_dice)

                if show_output:
                    for c in self.CATS:
                        if c in self.scores:
                            print '%s: %d' % (c, self.scores[c])
                    print 'Current roll: ' + '-'.join(current_dice)
                    print 'Rolls left this round: ' + str(rolls_left)

                action = agent.get_action(state)
                print
                print

            # action now holds the category chosen. Score it.
            self.categories.remove(action)
            self.scores[action] = cat_score(action, current_dice)
        total_score = sum([i for i in self.scores.values()])
        if sum([self.scores[i] for i in self.scores.keys() if len(i) == 2]) >= 63:
            total_score += 35
        print 'Total Score: %d' % total_score
		
# Provides actions given the state of the game
class Agent:
    def get_action(self, state):
        return None

# Provides actions by prompting the user to take an action
class HumanAgent(Agent):
    # Finds subsets of a string, helper function for get_action
    def subset(self, super, sub):
        for char in sub:
            if super.find(char) == -1:
                return False
            else:
                super = super.replace(char,'',1)
        return True

    def get_action(self, state):
        categories = state[0]
        rolls_left = state[1]
        current_dice = state[2]
        
        # Prompt for dice to keep if there are still rolls left
        if rolls_left != 0:
            print 'Enter the dice to keep, e.g. 1135'
            keep = '[0]'
            while not self.subset(current_dice, keep):
                keep = raw_input()
            return keep
            
        # Prompt for category to choose if there are no rolls left
        else:
            choice = None
            print 'Choose a category(%s)' % ', '.join(categories)
            while not choice in categories:
                choice = raw_input()
            return choice

# Provides actions randomly
class RandomAgent(Agent):
	def get_action(self, state):
		categories = state[0]
		rolls_left = state[1]
		current_dice = state[2]
		
		# Randomly choose dice to keep
		if rolls_left != 0:
			keep = ''
			for d in current_dice:
				if randint(0,1) == 1:
					keep = keep + d;
			return keep
		
		# Randomly choose category
		return choice(categories)

# Selects dice based on simple preferences
# Chooses most profitable action
class GreedyAgent(Agent):
	def get_action(self, state):
		categories = state[0]
		rolls_left = state[1]
		current_dice = state[2]
		
		# Use a 'little' logic to determine which dice to keep
		if rolls_left != 0:
			keep = ''
			num_of_each = [None,0,0,0,0,0,0]
			dominant_die = 0
			max_duplicates = max(num_of_each)
			for d in current_dice:
				num_of_each[int(d)] += 1
			if max_duplicates > 2:
				for i in range(1,6):
					if num_of_each[i] > 2:
						dominant_die = i
						break
					elif num_of_each[i] == 2:
						dominant_die = i
			
			# Booleans to make the logic later a little cleaner
			straight = ('Small straight' in categories) or ('Large straight' in categories)
			kind = (('3 of a kind' in categories) or ('4 of a kind' in categories)) and (dominant_die > 3)
			yahtzee = 'Yahtzee' in categories
			fHouse = 'Full house' in categories
			chance = ('Chance' in categories) and (dominant_die > 4)
						
			# If we have a pretty good chance of a yahtzee, a good number score, a good 'kind' score, or a good chance score
			if (max_duplicates >= 4) and ((yahtzee) or (str(dominant_die) + 's' in categories) or (kind) or (chance)):
				keep = ''.join(current_dice)
			
			# If we have a good chance of a good 'kind' score or a good 'chance' score
			elif (max_duplicates == 3) and ((kind) or (chance)):
				keep = ''.join(dominant_die for i in range(1, 3))
			
			# If we have a good chance of a full house
			elif (max_duplicates == 3) and (fHouse):
				keep = ''.join(dominant_die for i in range(1, 3))
				for i in range(6,1):
					if num_of_each[i] == 2:
						keep = ''.join(current_dice)
					elif num_of_each[i] == 1:
						keep = keep + str(i)
						break
						
			# If we might have a good chance of a full house or straight
			elif (max_duplicates == 2) and ((fHouse) or (straight)):
				if fHouse:
					for i in range (dominant_die - 1, 1):
						if num_of_each[i] == 2:
							keep = ''.join(dominant_die for j in range(1,2)).join(i for k in range(1,2))
							break
				else:
					for i in range (dominant_die - 1, 1):
						if num_of_each[i] == 2:
							return ''
					for i in range(1,6):
						if num_of_each[i] > 0:
							keep = keep + str(i)

			# If we have a straight
			elif (max_duplicates == 1) and (straight):
				keep = ''.join(current_dice)
			
			return keep
		# Choose category which gives the max score
		selected_category = ''
		max_score = -1
		for category in categories:
			temp = cat_score(category, current_dice)
			if max_score < temp:
				max_score = temp
				selected_category = category
		return selected_category
		
# Selects dice based on simple preferences
# Chooses action closest to maximum potential
class StrategicAgent(Agent):
	CAT_MAX_VALUE = {"1s" : 5, "2s" : 10, "3s" : 15, "4s" : 20, "5s" : 25, "6s" : 30,
     '3 of a kind' : 30, '4 of a kind' : 30, 'Full house' : 25, 'Small straight' : 30, 
     'Large straight' : 40, 'Yahtzee' : 50, 'Chance' : 30}
	def get_action(self, state):
		categories = state[0]
		rolls_left = state[1]
		current_dice = state[2]
		
		# Use a 'little' logic to determine which dice to keep
		if rolls_left != 0:
			keep = ''
			num_of_each = [None,0,0,0,0,0,0]
			dominant_die = 0
			max_duplicates = max(num_of_each)
			for d in current_dice:
				num_of_each[int(d)] += 1
			if max_duplicates > 2:
				for i in range(1,6):
					if num_of_each[i] > 2:
						dominant_die = i
						break
					elif num_of_each[i] == 2:
						dominant_die = i
			
			# Booleans to make the logic later a little cleaner
			straight = ('Small straight' in categories) or ('Large straight' in categories)
			kind = (('3 of a kind' in categories) or ('4 of a kind' in categories)) and (dominant_die > 3)
			yahtzee = 'Yahtzee' in categories
			fHouse = 'Full house' in categories
			chance = ('Chance' in categories) and (dominant_die > 4)
						
			# If we have a pretty good chance of a yahtzee, a good number score, a good 'kind' score, or a good chance score
			if (max_duplicates >= 4) and ((yahtzee) or (str(dominant_die) + 's' in categories) or (kind) or (chance)):
				keep = ''.join(current_dice)
			
			# If we have a good chance of a good 'kind' score or a good 'chance' score
			elif (max_duplicates == 3) and ((kind) or (chance)):
				keep = ''.join(dominant_die for i in range(1, 3))
			
			# If we have a good chance of a full house
			elif (max_duplicates == 3) and (fHouse):
				keep = ''.join(dominant_die for i in range(1, 3))
				for i in range(6,1):
					if num_of_each[i] == 2:
						keep = ''.join(current_dice)
					elif num_of_each[i] == 1:
						keep = keep + str(i)
						break
						
			# If we might have a good chance of a full house or straight
			elif (max_duplicates == 2) and ((fHouse) or (straight)):
				if fHouse:
					for i in range (dominant_die - 1, 1):
						if num_of_each[i] == 2:
							keep = ''.join(dominant_die for j in range(1,2)).join(i for k in range(1,2))
							break
				else:
					for i in range (dominant_die - 1, 1):
						if num_of_each[i] == 2:
							return ''
					for i in range(1,6):
						if num_of_each[i] > 0:
							keep = keep + str(i)

			# If we have a straight
			elif (max_duplicates == 1) and (straight):
				keep = ''.join(current_dice)
			
			return keep
		# Choose category which gives the max score
		selected_category = ''
		max_potential = -1
		for category in categories:
			temp = pow(cat_score(category, current_dice), 2) / float(self.CAT_MAX_VALUE[category])
			if max_potential < temp:
				max_potential = temp
				selected_category = category
		return selected_category

# Learns an action policy with filtered state features 
class LearningAgent(Agent):
	#policy = {FEATURIZED_STATE, '1s'}
	def get_action(self, state):
		#TODO
		#return policy[FEATURIZED_STATE]
		pass

# Uses a UniformBandit algorithm for determining which dice to keep based on score
class PlanningAgentGreedy(Agent):
	MAX_ITERATIONS_PER_PERMUTATION = 100
	
	def max_score(self, state):
		categories = state[0]
		current_dice = state[2]
		max_score = -1
		
		for category in categories:
			temp = cat_score(category, current_dice)
			if max_score < temp:
				max_score = temp
		return max_score
		
	def best_dice(self, state):
		categories = state[0]
		rolls_left = state[1]
		current_dice = state[2]
		best_permutation = ''
		best_score = -1
		
		# Base case, return the current dice and the configuration's max potential
		if rolls_left == 0:
			return (''.join(current_dice), self.max_score(state))
		
		# permutation => 00000, 00001, 00010, ..., 11111
		for permutation in product(range(2), repeat=5):
			keep_permutation = ''
			permutation_score = 0
			for i in range(0,5):
				if permutation[i] == 1:
					keep_permutation = keep_permutation + str(current_dice[i])
			for n in range(1, self.MAX_ITERATIONS_PER_PERMUTATION):
				new_dice = ''.join(sorted(keep_permutation + ''.join([str(randint(1,6)) for i in range(6 - len(keep_permutation))])))
				permutation_score = (self.best_dice((categories, rolls_left - 1, new_dice))[1] - permutation_score) / float(n)
			if permutation_score > best_score:
				best_permutation = keep_permutation
				best_score = permutation_score
		return (best_permutation, best_score);
	
	def get_action(self, state):
		categories = state[0]
		rolls_left = state[1]
		current_dice = state[2]
		
		if rolls_left != 0:
			return self.best_dice(state)[0]
			
		# Choose category which fulfills its potential the best
		selected_category = ''
		max_score = -1
		for category in categories:
			temp = cat_score(category, current_dice)
			if max_score < temp:
				max_score = temp
				selected_category = category
		return selected_category
		
# Uses a UniformBandit algorithm for determining which dice to keep based on category potentials
class PlanningAgentStrategic(Agent):
	MAX_ITERATIONS_PER_PERMUTATION = 10
	CAT_MAX_VALUE = {"1s" : 5, "2s" : 10, "3s" : 15, "4s" : 20, "5s" : 25, "6s" : 30,
     '3 of a kind' : 30, '4 of a kind' : 30, 'Full house' : 25, 'Small straight' : 30, 
     'Large straight' : 40, 'Yahtzee' : 50, 'Chance' : 30}
	
	def max_potential(self, state):
		categories = state[0]
		current_dice = state[2]
		max_potential = -1
		
		for category in categories:
			temp = cat_score(category, current_dice) / float(self.CAT_MAX_VALUE[category])
			if max_potential < temp:
				max_potential = temp
		return max_potential
		
	def best_dice(self, state):
		categories = state[0]
		rolls_left = state[1]
		current_dice = state[2]
		best_permutation = ''
		best_potential = -1
		
		# Base case, return the current dice and the configuration's max potential
		if rolls_left == 0:
			return (''.join(current_dice), self.max_potential(state))
		
		# permutation => 00000, 00001, 00010, ..., 11111
		for permutation in product(range(2), repeat=5):
			keep_permutation = ''
			permutation_potential = 0
			for i in range(0,5):
				if permutation[i] == 1:
					keep_permutation = keep_permutation + str(current_dice[i])
			for n in range(1, self.MAX_ITERATIONS_PER_PERMUTATION):
				new_dice = ''.join(sorted(keep_permutation + ''.join([str(randint(1,6)) for i in range(6 - len(keep_permutation))])))
				permutation_potential = (self.best_dice((categories, rolls_left - 1, new_dice))[1] - permutation_potential) / float(n)
			if permutation_potential > best_potential:
				best_permutation = keep_permutation
				best_potential = permutation_potential
		return (best_permutation, best_potential);
	
	def get_action(self, state):
		categories = state[0]
		rolls_left = state[1]
		current_dice = state[2]
		
		if rolls_left != 0:
			return self.best_dice(state)[0]
			
		# Choose category which fulfills its potential the best
		selected_category = ''
		max_potential = -1
		for category in categories:
			temp = cat_score(category, current_dice) / float(self.CAT_MAX_VALUE[category])
			if max_potential < temp:
				max_potential = temp
				selected_category = category
		return selected_category

Simulation(agent=PlanningAgentGreedy(), show_output=True)
