
class LRE:

    def __init__(self, CFG_STRING):
        self.rules = {}
        for rule in CFG_STRING.split(';'):
            rule = rule.split(',')
            self.rules[rule.pop(0)] = rule
        self.V = list(enumerate(self.rules))
        self.LRE()

    def LRE(self):
        for i1, v1 in self.V:
            for i2, v2 in self.V:
                if i1 > i2:
                    self.rules[v1] = self.substitute(v1, v2)
            self._lre(v1)
        return self.__str__()

    def __str__(self):
        output = []

        for _, v in self.V:
            output.append(','.join([v] + [s for s in self.rules[v]]))

            v_prime = v+'\''
            if v_prime in self.rules:
                output.append(
                    ','.join([v_prime] + [s for s in self.rules[v_prime]]))
        return ';'.join(output)

    def _lre(self, v):
        new_rules = []
        is_lr = any(rule[0] == v for rule in self.rules[v])
        if is_lr:
            for rule in self.rules[v]:
                v_prime = v+'\''
                if rule[0] == v:
                    self.rules[v_prime] = self.rules.get(
                        v_prime, []) + [(rule[1:] + v_prime)]
                else:
                    new_rules.append(rule + v_prime)
            self.rules[v_prime].append(' ')
            self.rules[v] = new_rules

    def substitute(self, v1, v2):
        new_rules = []
        for rule in self.rules[v1]:
            if rule[0] == v2:
                new_rules.extend([rule.replace(v2, sub)
                                  for sub in self.rules[v2]])
            else:
                new_rules.append(rule)
        return new_rules


if __name__ == '__main__':
    # epsilon is denoted by ' '
    input = 'S,ScT,T;T,aSb,iaLb,i;L,SdL,S'
    output = LRE(input)
    print(output)
