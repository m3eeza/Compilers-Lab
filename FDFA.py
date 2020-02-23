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
