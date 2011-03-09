#! /usr/bin/python

from random import randint

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
            num_of_each = [None,0,0,0,0,0,0]
            for d in current_dice:
                num_of_each[int(d)] += 1

            self.scores[action] = 0
            if len(action) == 2:
                die = int(action[0])
                self.scores[action] += num_of_each[die] * die
            elif action == '3 of a kind':
                found = False
                for i in range(1,7):
                    if num_of_each[i] >= 3:
                        found = True
                if found:
                    for i in range(1,7):
                        self.scores[action] += num_of_each[i] * i
            elif action == '4 of a kind':
                found = False
                for i in range(1,7):
                    if num_of_each[i] >= 4:
                        found = True
                if found:
                    for i in range(1,7):
                        self.scores[action] += num_of_each[i] * i
            elif action == 'Full house':
                two = False;
                three = False;
                for i in range(1,7):
                    if num_of_each[i] == 2:
                        two = True
                    elif num_of_each[i] == 3:
                        three = True
                if two and three:
                    self.scores[action] = 25;
            elif action == 'Small straight':
                count = 0
                sequence = False
                for i in range(1,7):
                    if num_of_each[i] > 0:
                        sequence = True
                        count += 1
                    else:
                        sequence = False
                        count = 0
                if count >= 4:
                    self.scores[action] = 30
            elif action == 'Large straight':
                 count = 0
                 for i in range(2,6):
                     if num_of_each[i] == 1:
                         count += 1
                 if (count == 4) and (num_of_each[1] == 1 or num_of_each[6] == 1):
                     self.scores[action] = 40
            elif action == 'Yahtzee':
                for i in range(1,7):
                    if num_of_each[i] == 5:
                        self.scores[action] = 50
            elif action == 'Chance':
                for i in range(1,7):
                    self.scores[action] += num_of_each[i] * i

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
            print 'Enter the dice to keep, inside of square brackets, e.g. [1135]'
            keep = '[0]'
            while (keep[0] != '[' or (not self.subset(current_dice, keep[1:-1])) or keep[-1] != ']'):
                keep = raw_input()
            return keep[1:-1]
            
        # Prompt for category to choose if there are no rolls left
        else:
            choice = None
            print 'Choose a category(%s)' % ', '.join(categories)
            while not choice in categories:
                choice = raw_input()
            return choice

Simulation(agent=HumanAgent(), show_output=True)
