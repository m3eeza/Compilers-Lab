from DFA import DFA

class NFA:
    dfa_states = {}

    def __init__(self, nfa_string):

        zero_trans, one_trans, epsilon_trans, accepted = nfa_string.split('#')
        self.accepted_states = accepted.split(',')
        self.zero_trans = [t.split(',') for t in zero_trans.split(';')]
        self.one_trans = [t.split(',') for t in one_trans.split(';')]
        self.epsilon_trans = [t.split(',') for t in epsilon_trans.split(';')]

    def construct_dfa(self):
        # Go through state 0, find its epsilon closure
        queue = [('0', )]
        while queue:
            current_states = queue.pop(0)
            current_states = self._find_epsilon_closure_set(current_states)
            state_key = tuple(sorted(current_states))
            if state_key not in self.dfa_states:
                zeros = tuple(sorted(target for src, target in self.zero_trans if src in current_states))
                ones = tuple(sorted(target for src, target in self.one_trans if src in current_states))
                queue.append(self._find_epsilon_closure_set(zeros))
                queue.append(self._find_epsilon_closure_set(ones))
                self.dfa_states[state_key] = (
                    self._find_epsilon_closure_set(zeros),
                    self._find_epsilon_closure_set(ones)
                )
        dfa_states = self.dfa_states
        states = [state for state in dfa_states]
        goal_states = list(filter(lambda x: any(s in x for s in self.accepted_states), states))
        tf = dict()
        for state in dfa_states:
            tf[(state,'0')] = dfa_states[state][0]    
            tf[(state,'1')] = dfa_states[state][1] 
        start_state = states[0]
        return DFA(states=states, alphabet={'0', '1'}, transition_function=tf, start_state=start_state, goal_states=goal_states)
        

    def _find_epsilon_closure_set(self, states):
        result = tuple()
        for s in states:
            result += self._find_epsilon_closure(s)
        return tuple(sorted(set(result)))

    def _find_epsilon_closure(self, state):
        result = (state, )
        targets = [target for src, target in self.epsilon_trans if src == state] if self.epsilon_trans[0][0] else []
        if not targets:
            return (state, )
        for target in targets:
            result += self._find_epsilon_closure(target)
        return tuple(sorted(set(result)))


if __name__ == '__main__':
    
    
    # NFA_STRING_1 = '0,1;1,2;2,3#0,0;1,1;2,3;3,3#1,0;2,1;3,2#1,2,3'
    # NFA = NFA(NFA_STRING_1)
    # DFA = NFA.construct_dfa()
    # print(DFA.run('0100'))
    # print(DFA.run('1111'))
    # print(DFA.run('01000'))
    # print(DFA.run('00'))
    # print(DFA.run('1101100'))

    NFA_STRING_2 = '0,1;1,3;3,3#0,2;2,3;3,3#1,2;3,2#3'
    NFA = NFA(NFA_STRING_2)
    DFA = NFA.construct_dfa()
    print(DFA.run('0101100'))
    print(DFA.run('010101'))
    print(DFA.run('111010'))
    print(DFA.run('10100'))
    print(DFA.run('10101'))



    