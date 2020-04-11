//T10_37_19030_Mohamed_Ibrahim

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class CFG {
	final static Character epsilon = 'e';

	/**
	 * @param <T>
	 * @param setA
	 * @param setB
	 * @return true if setA is a subset of setB
	 */
	private static <T> boolean isSubset(LinkedHashSet<T> setA, LinkedHashSet<T> setB) {
		@SuppressWarnings({ "unchecked" })
		LinkedHashSet<T> temp = (LinkedHashSet<T>) setA.clone();
		temp.remove(epsilon);
		return setB.containsAll(temp);
	}

	public static void main(String[] args) {
		String input = "S,ScT,T;T,aSb,iaLb,e;L,SdL,S";
		CFG cfg = new CFG(input);
		String firstEncoding = cfg.First();
		String followEncoding = cfg.Follow();
		System.out.println("First: " + firstEncoding);
		System.out.println("Follow: " + followEncoding);
	}

	Map<Character, LinkedHashSet<String>> cfg;
	Map<Character, LinkedHashSet<Character>> first = new LinkedHashMap<>();

	Map<Character, LinkedHashSet<Character>> follow = new LinkedHashMap<>();

	LinkedHashSet<Character> sigma;

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
					if (hasAllEpsilon(production, first) && !first.get(key).contains(epsilon)) {
						first.get(key).add(epsilon);
						change = true;
					} else {
						for (int i = 0; i < production.toCharArray().length; i++) {
							if (i == 0 || hasAllEpsilon(production.substring(0, i), first)) {
								char currentSymbol = production.charAt(i);
								@SuppressWarnings("unchecked")
								LinkedHashSet<Character> current = (LinkedHashSet<Character>) first.get(currentSymbol)
										.clone();
								current.remove(epsilon);
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

							if (production.charAt(i) != epsilon) {
								if (!isSubset(follow.get(key), follow.get(production.charAt(i)))) {
									@SuppressWarnings("unchecked")
									LinkedHashSet<Character> current = (LinkedHashSet<Character>) follow.get(key)
											.clone();
									current.remove(epsilon);
									follow.get(production.charAt(i)).addAll(current);
									change = true;
								}
							}
						} else {
							if (!isSubset(first.get(production.charAt(i + 1)), follow.get(production.charAt(i)))) {

								@SuppressWarnings("unchecked")
								LinkedHashSet<Character> current = (LinkedHashSet<Character>) first
										.get(production.charAt(i + 1)).clone();
								current.remove(epsilon);
								follow.get(production.charAt(i)).addAll(current);
								change = true;

							}

							if (first.get(production.charAt(i + 1)).contains(epsilon) && i + 1 != n - 1) {

								boolean flag = true;
								int count = 1;

								while (flag) {

									if (!isSubset(first.get(production.charAt(i + 1 + count)),
											follow.get(production.charAt(i)))) {
										@SuppressWarnings("unchecked")
										LinkedHashSet<Character> current = (LinkedHashSet<Character>) first
												.get(production.charAt(i + 1 + count)).clone();
										current.remove(epsilon);
										follow.get(production.charAt(i)).addAll(current);
										change = true;

									}

									if (i + 1 + count < n - 1
											&& first.get(production.charAt(i + 1 + count)).contains(epsilon)) {

										flag = true;
										count++;
									} else if (i + 1 + count == n - 1
											&& first.get(production.charAt(i + 1 + count)).contains(epsilon)) {

										if (!isSubset(follow.get(key), follow.get(production.charAt(i)))) {
											@SuppressWarnings("unchecked")
											LinkedHashSet<Character> current = (LinkedHashSet<Character>) follow
													.get(key).clone();
											current.remove(epsilon);
											follow.get(production.charAt(i)).addAll(current);
											change = true;

										}

										flag = false;
									} else {

										flag = false;
									}
								}
							} else if (first.get(production.charAt(i + 1)).contains(epsilon) && i + 1 == n - 1) {

								if (!isSubset(follow.get(key), follow.get(production.charAt(i)))) {
									@SuppressWarnings("unchecked")
									LinkedHashSet<Character> current = (LinkedHashSet<Character>) follow.get(key)
											.clone();
									current.remove(epsilon);
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
		System.out.println(output);
		return output.substring(0, output.length() - 1);

	}

	private boolean hasAllEpsilon(String production, Map<Character, LinkedHashSet<Character>> first) {
		for (Character symbol : production.toCharArray()) {
			if (first.get(symbol) != null && !first.get(symbol).contains(epsilon))
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

}
/*
 *
 * [c, a, b, i, d] [[a, i, $, c, b, d], [a, c, i, b, d], [$, c, b, d], [a], [a,
 * c, d, i, b]]
 */
