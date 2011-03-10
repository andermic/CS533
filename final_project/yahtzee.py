#! /usr/bin/python

from random import randint
from random import choice
from math import pow

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
    CATS = ["1s", "2s", "3s", "4s", "5s", "6s",
     '3 of a kind', '4 of a kind', 'Full house', 'Small straight', 
     'Large straight', 'Yahtzee', 'Chance']
    categories = CATS[:]
    scores = {}
	
    # Run the simulation
    def __init__(self, agent, show_output):
        # Keep asking the agent for actions until no more categories remain
        while len(self.categories) != 0:

            action = ''     # Contains the dice kept or the category chosen

            # Give the agent 3 rolls
            for i in range(1,4):
                if show_output:
                    for c in self.CATS:
                        if c in self.scores:
                            print '%s: %d' % (c, self.scores[c])

                # State consists of a 3-tuple: The categories left to
                #  choose from, the number of rolls remaining in this
                #  round, and the current dice.
                rolls_left = 3 - i
                current_dice = ''.join(sorted(action + ''.join([str(randint(1,6)) for i in range(6 - len(action))])))
                state = (self.categories, rolls_left, current_dice)

                if show_output:
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

# Uses a reward function to find the next action
class PlanningAgent(Agent):
	#state_value {FEATURIZED_STATE, reward}
	def get_action(self, state):
		#TODO
		#return action that causes maximum next-state reward (greedy)
		pass
Simulation(agent=StrategicAgent(), show_output=True)
