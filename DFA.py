class DFA:
    current_state = None

    def __init__(self, dfa_str=None, states=None, alphabet=None, transition_function=None, start_state=None, goal_states=None):
        self.dfa_str = dfa_str
        self.states = states
        self.alphabet = alphabet
        self.transition_function = transition_function
        self.start_state = start_state
        self.goal_states = goal_states
        self.current_state = start_state
        if dfa_str:
            self.parser()
        return

    def transition_to_state_with_input(self, input_value):
        if (self.current_state, input_value) not in self.transition_function:   
            return False
        self.current_state = self.transition_function[(self.current_state, input_value)]
        return True

    def in_goal_state(self):
        return self.current_state in self.goal_states

    def go_to_initial_state(self):
        self.current_state = self.start_state
        return

    def run(self, input_list):
        self.go_to_initial_state()
        for inp in input_list: 
            if self.transition_to_state_with_input(inp):
                continue
            else:
                return False
        return self.in_goal_state(), self.current_state

    def parser(self):
        self.goal_states = list(self.dfa_str.split('#')[1].replace(',',''))
        states_description = self.dfa_str.split('#')[0].replace(',', '').split(';')
        self.states = [str(i) for i in range(len(states_description))]
        tf = dict()
        for desc in states_description:
            tf[(desc[0], '0')] = desc[1]
            tf[(desc[0], '1')] = desc[2]
        self.transition_function = tf
        self.alphabet = {'0', '1'}
        self.start_state = self.states[0]
        return





