from DFA import DFA


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
        return output if output else 'ERROR!'
                

if __name__ == '__main__':
    fdfa_str1 = '0,0,1,00;1,2,1,01;2,0,3,10;3,3,3,11#0,1,2'
    myFDFA = FDFA(fdfa_str1)
    print(myFDFA.fdfa_output)
    print(myFDFA.run('100'))
    print(myFDFA.run('101'))
    print(myFDFA.run('110'))
    print(myFDFA.run('10110'))
    print(myFDFA.run('011'))
    print('#-##-##-##-##-##-##-##-##-##-#')
    fdfa_str2 = '0,1,0,00;1,1,2,01;2,3,2,10;3,3,3,11#1,2'
    myFDFA = FDFA(fdfa_str2)
    print(myFDFA.fdfa_output)
    print(myFDFA.run('011'))
    print(myFDFA.run('110'))
    print(myFDFA.run('00101'))
    print(myFDFA.run('100100'))
    print(myFDFA.run('100'))
    print(myFDFA.run('111'))
