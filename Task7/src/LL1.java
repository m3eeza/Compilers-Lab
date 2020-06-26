
// T10_37_19030_Mohamed_Ibrahim
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LL1 {

	
	static class Pair implements Comparable<Pair> {
		Character variable;
		Character terminal;

		public Pair(Character v, Character t) {
			this.variable = v;
			this.terminal = t;
		}

		public boolean equals(Object object) {
			Pair o = (Pair) object;
			return (this.variable == o.variable && this.terminal == o.terminal);
		}

		public String toString() {
			return variable + "," + terminal;
		}

		public int hashCode() {
			return new String(this.variable + "" + this.terminal).hashCode();
		}

		public int compareTo(Pair other) {
			return (this.toString().compareTo(other.toString()));
		}

	}

	static class CFG {
		final static Character EPSILON = 'e';
		final static String EPSILON_RULE = "e";

		/**
		 * @param <T>
		 * @param setA
		 * @param setB
		 * @return true if setA is a subset of setB
		 */
		private static <T> boolean isSubset(LinkedHashSet<T> setA, LinkedHashSet<T> setB) {
			@SuppressWarnings({ "unchecked" })
			LinkedHashSet<T> temp = (LinkedHashSet<T>) setA.clone();
			temp.remove(EPSILON);
			return setB.containsAll(temp);
		}

		/**
		 * Creates an instance of the CFG class. This should parse a string
		 * representation of the grammar and set your internal CFG attributes
		 * 
		 * @param grammar A string representation of a CFG
		 */
		Map<Character, LinkedHashSet<String>> cfg;
		Map<Character, LinkedHashSet<Character>> first = new LinkedHashMap<>();
		Map<Character, LinkedHashSet<Character>> follow = new LinkedHashMap<>();
		LinkedHashSet<Character> sigma;
		Map<Pair, String> table = new LinkedHashMap<>();

		public CFG(String input) {
			// input is in the form 'S,ScT,T;T,aSb,iaLb,e;L,SdL,S'
			this.cfg = new LinkedHashMap<>();
			this.sigma = new LinkedHashSet<Character>();
			for (Character c : input.toCharArray()) {
				if (isTerminal(c))
					this.sigma.add(c);
			}
			String[] rules = input.split(";");
			for (String rule : rules) {
				cfg.put(rule.charAt(0),
						new LinkedHashSet<String>(Arrays.asList(rule.substring(2, rule.length()).split(","))));
			}
			this.first = new LinkedHashMap<>();
			this.follow = new LinkedHashMap<>();
			this.First();
			this.Follow();
		}

		public String First() {
			for (Character variable : this.cfg.keySet()) {
				first.put(variable, new LinkedHashSet<Character>());
			}
			for (Character terminal : this.sigma) {
				first.put(terminal, new LinkedHashSet<Character>());
				first.get(terminal).add(terminal);
			}

			boolean change = true;
			while (change) {
				change = false;
				for (Character key : cfg.keySet()) {
					for (String production : cfg.get(key)) {
						if (hasAllEpsilon(production) && !first.get(key).contains(EPSILON)) {
							first.get(key).add(EPSILON);
							change = true;
						} else {
							for (int i = 0; i < production.toCharArray().length; i++) {
								if (i == 0 || hasAllEpsilon(production.substring(0, i))) {
									char currentSymbol = production.charAt(i);
									@SuppressWarnings("unchecked")
									LinkedHashSet<Character> current = (LinkedHashSet<Character>) first
											.get(currentSymbol).clone();
									current.remove(EPSILON);
									if (!first.get(key).containsAll(current)) {
										first.get(key).addAll(current);
										change = true;
									}
								}
							}
						}

					}
				}
			}
			String output = "";
			for (char variable : this.cfg.keySet()) {
				output += variable + ",";
				List<Character> sortedList = new ArrayList<>(first.get(variable));
				Collections.sort(sortedList);
				for (char c : sortedList) {
					output += c;
				}
				output += ";";
			}
			return output.substring(0, output.length() - 1);

		}

		public String Follow() {
			for (Character variable : this.cfg.keySet()) {
				follow.put(variable, new LinkedHashSet<Character>());
			}
			for (Character terminal : this.sigma) {
				follow.put(terminal, new LinkedHashSet<Character>());
			}
			follow.get('S').add('$');
			boolean change = true;
			while (change) {
				change = false;
				for (Character key : cfg.keySet()) {
					for (String production : cfg.get(key)) {
						for (int i = 0, n = production.length(); i < n; i++) {

							if (i == n - 1) {

								if (production.charAt(i) != EPSILON) {
									if (!isSubset(follow.get(key), follow.get(production.charAt(i)))) {
										@SuppressWarnings("unchecked")
										LinkedHashSet<Character> current = (LinkedHashSet<Character>) follow.get(key)
												.clone();
										current.remove(EPSILON);
										follow.get(production.charAt(i)).addAll(current);
										change = true;
									}
								}
							} else {
								if (!isSubset(first.get(production.charAt(i + 1)), follow.get(production.charAt(i)))) {

									@SuppressWarnings("unchecked")
									LinkedHashSet<Character> current = (LinkedHashSet<Character>) first
											.get(production.charAt(i + 1)).clone();
									current.remove(EPSILON);
									follow.get(production.charAt(i)).addAll(current);
									change = true;

								}

								if (first.get(production.charAt(i + 1)).contains(EPSILON) && i + 1 != n - 1) {

									boolean flag = true;
									int count = 1;

									while (flag) {

										if (!isSubset(first.get(production.charAt(i + 1 + count)),
												follow.get(production.charAt(i)))) {
											@SuppressWarnings("unchecked")
											LinkedHashSet<Character> current = (LinkedHashSet<Character>) first
													.get(production.charAt(i + 1 + count)).clone();
											current.remove(EPSILON);
											follow.get(production.charAt(i)).addAll(current);
											change = true;

										}

										if (i + 1 + count < n - 1
												&& first.get(production.charAt(i + 1 + count)).contains(EPSILON)) {

											flag = true;
											count++;
										} else if (i + 1 + count == n - 1
												&& first.get(production.charAt(i + 1 + count)).contains(EPSILON)) {

											if (!isSubset(follow.get(key), follow.get(production.charAt(i)))) {
												@SuppressWarnings("unchecked")
												LinkedHashSet<Character> current = (LinkedHashSet<Character>) follow
														.get(key).clone();
												current.remove(EPSILON);
												follow.get(production.charAt(i)).addAll(current);
												change = true;

											}

											flag = false;
										} else {

											flag = false;
										}
									}
								} else if (first.get(production.charAt(i + 1)).contains(EPSILON) && i + 1 == n - 1) {

									if (!isSubset(follow.get(key), follow.get(production.charAt(i)))) {
										@SuppressWarnings("unchecked")
										LinkedHashSet<Character> current = (LinkedHashSet<Character>) follow.get(key)
												.clone();
										current.remove(EPSILON);
										follow.get(production.charAt(i)).addAll(current);
										change = true;
									}
								}
							}
						}
					}
				}
			}

			String output = "";
			for (char variable : this.cfg.keySet()) {
				output += variable + ",";
				List<Character> sortedList = new ArrayList<>(follow.get(variable));
				Collections.sort(sortedList);
				if (sortedList.contains('$')) {
					sortedList.remove(0);
					sortedList.add('$');
				}
				for (char c : sortedList) {
					output += c;
				}
				output += ";";
			}
			return output.substring(0, output.length() - 1);

		}

		private boolean hasAllEpsilon(String production) {
			for (Character symbol : production.toCharArray()) {
				if (first.get(symbol) != null && !first.get(symbol).contains(EPSILON))
					return false;

			}
			return true;
		}

		private boolean isTerminal(char symbol) {
			return !isVariable(symbol) && symbol != ';' && symbol != ',';
		}

		private boolean isVariable(char symbol) {
			return Character.isUpperCase(symbol);
		}

		private LinkedHashSet<Character> getFirst(String production) {
			LinkedHashSet<Character> output = new LinkedHashSet<Character>();
			if (hasAllEpsilon(production)) {
				output.add(EPSILON);
			}
			for (int i = 0; i < production.toCharArray().length; i++) {
				if (i == 0 || hasAllEpsilon(production.substring(0, i))) {
					char currentSymbol = production.charAt(i);
					@SuppressWarnings("unchecked")
					LinkedHashSet<Character> current = (LinkedHashSet<Character>) first
							.get(currentSymbol).clone();
					current.remove(EPSILON);
					output.addAll(current);
				}
			}
			return output;
		}

		/**
		 * Generates the parsing table for this context free grammar. This should set
		 * your internal parsing table attributes
		 * 
		 * @return A string representation of the parsing table
		 */
		public String table() {
			for (Character key : cfg.keySet()) {
				for (String production : cfg.get(key)) {
					for (Character symbol : getFirst(production)) {
						if (symbol != EPSILON) {
							table.put(new Pair(key, symbol), production);
						}
					}
					if (getFirst(production).contains(EPSILON)) {
						for (Character symbol : follow.get(key)) {
							if (symbol != EPSILON) {
								table.put(new Pair(key, symbol), production);
							}
						}
					}
				}
			}

			List<Character> sortedList = new ArrayList<Character>(sigma);
			Collections.sort(sortedList);
			sortedList.add('$');
			String output = "";

			for (Character key : cfg.keySet()) {
				for (Character terminal : sortedList) {
					Pair pair = new Pair(key, terminal);
					if (table.containsKey(pair))
						output += (pair + "," + table.get(pair) + ";");
				}
			}
			return output.substring(0, output.length() - 1);
		}

		/**
		 * Parses the input string using the parsing table
		 * 
		 * @param s The string to parse using the parsing table
		 * @return A string representation of a left most derivation
		 */
		public String parse(String s) {
			Stack<Character> stack = new Stack<Character>();
			stack.push('$');
			stack.push('S');
			int i = 0;
			ArrayList<String> derivation = new ArrayList<String>();
			derivation.add("S");

			while (true) {
				if (stack.peek() == '$' && i == s.length()) {
					break;
				}
				if (isVariable(stack.peek())) {
					Character terminal = i == s.length() ? '$' : s.charAt(i);
					Pair entry = new Pair(stack.pop(), terminal);
					if (table.containsKey(entry)) {
						String rule = table.get(entry);
						for (int j = rule.length() - 1; j >= 0; j--) {
							if (rule.charAt(j) != EPSILON)
								stack.push(rule.charAt(j));
						}

						derivation.add(derivation.get(derivation.size() - 1).replaceFirst(entry.variable + "",
								rule.replace(EPSILON_RULE, "")));

					} else {
						derivation.add("ERROR");
						break;
					}
				} else {
					if (i < s.length() && s.charAt(i) == stack.peek()) {
						stack.pop();
						++i;
					} else {
						derivation.add("ERROR");
						break;
					}

				}
			}
			return derivation.toString().replaceAll("[\\s\\[\\]]", "");
		}
	}

	public static void main(String[] args) {

		/*
		 * Please make sure that this EXACT code works. This means that the method and
		 * class names are case sensitive
		 */

		String grammar = "S,iST,e;T,cS,a";
		String input1 = "iiac";
		String input2 = "iia";
		CFG g = new CFG(grammar);
		System.out.println(g.table());
		System.out.println(g.parse(input1));
		System.out.println(g.parse(input2));

	}
}
