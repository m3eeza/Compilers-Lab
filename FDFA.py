from DFA import DFA

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


class FDFA:

    def __init__(self, fdfa_str):
        self.fdfa_str = fdfa_str
        self.fdfa_output = {}
        self.DFA = self.construct_dfa()


    def construct_dfa(self):
        goal_states = list(self.fdfa_str.split('#')[1].replace(',', ''))
        states_description = self.fdfa_str.split('#')[0].replace(',', '').split(';')
        states = [str(i) for i in range(len(states_description))]
        tf = {}
        for desc in states_description:
            tf[(desc[0], '0')] = desc[1]
            tf[(desc[0], '1')] = desc[2]
            self.fdfa_output[desc[0]] = desc[3:]
        return DFA(states=states, alphabet={'0', '1'}, transition_function=tf, start_state=states[0], goal_states=goal_states)


    def run(self, input_str):
        output = ''
        i = 0
        j = len(input_str)
        while i < j:
            in_goal_state, state = self.DFA.run(input_str[i:j])
            if in_goal_state:
                i = j
                j = len(input_str)
                output += self.fdfa_output[state]
            else:
                j -= 1
                
        if not in_goal_state:
            _, state = self.DFA.run(input_str[i:len(input_str)])
            output += self.fdfa_output[state]
        return output
                

if __name__ == '__main__':
    fdfa_str1 = '0,0,1,00;1,0,2,01;2,3,2,10;3,2,3,11#1,3'
    myFDFA = FDFA(fdfa_str1)
    print(myFDFA.run('00111'))
    print(myFDFA.run('0011100'))
    print(myFDFA.run('110101'))
    print(myFDFA.run('1101010'))
    print(myFDFA.run('000'))

    print('#-##-##-##-##-##-##-##-##-##-#')

    fdfa_str2 = '0,1,0,00;1,3,0,01;2,1,3,10;3,2,3,11#3'
    myFDFA = FDFA(fdfa_str2)
    print(myFDFA.run('10000'))
    print(myFDFA.run('00'))
    print(myFDFA.run('00001'))
    print(myFDFA.run('10101'))
    print(myFDFA.run('10'))
