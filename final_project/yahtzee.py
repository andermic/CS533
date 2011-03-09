from random import randint

# Simulates the yahtzee game
class Simulation:
    CATS = ["1s", "2s", "3s", "4s", "5s", "6s",
     '3 of a kind', '4 of a kind', 'Full house', 'Small straight', 
     'Large straight', 'Yahtzee', 'Chance']
    categories = CATS
    scores = {}

    # Run the simulation
    def __init__(self, agent, show_output):
        # Keep asking the agent for actions until no more categories remain
        while len(categories) != 0:
            if show_output:
                for c in CATS:
                    if c in self.scores:
                        print '%s: %d' % (c, self.scores[c])

            action = ''     # Contains the dice kept or the category chosen

            # Give the agent 3 rolls
            for i in range(1,4):
                # State consists of a 3-tuple: The categories left to
                #  choose from, the number of rolls remaining in this
                #  round, and the current dice.
                rolls_left = 3 - i
                current_dice = ''.join(sorted(action + [randint(1,6) for i in range(6 - len(keep_dice))]))
                state = (categories, rolls_left, current_dice)

                if show_output:
                    print 'Current roll: ' + current_dice
                    print 'Rolls left: ' + rolls_left

                action = agent.get_action(state)

            # action now holds the category chosen. Score it.
            categories.remove(action)
            num_of_each = [None,0,0,0,0,0,0]
            for d in current_dice:
                num_of_each[int(d)] += 1

            self.scores[action] = 0
            if len(action) == 2:
                scores[action] = 
            elif action == '3 of a kind':
            elif action == '4 of a kind':
            elif action == 'Full house':
            elif action == 'Small Straight':
            elif action == 'Large straight':
            elif action == 'Yahtzee':
            elif action == 'Chance':

# Provides actions given the state of the game
class Agent:
    def get_action(self, state):
        return None

# Provides actions by prompting the user to take an action
class HumanAgent(Agent):
    def get_action(self, state):
        pass
        #TODO Ensure action returned is valid.

Simulation(agent=HumanAgent(), show_output=True)
