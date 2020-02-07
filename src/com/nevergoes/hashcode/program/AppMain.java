package com.nevergoes.hashcode.program;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.nevergoes.hashcode.files.FileUtils;
import com.nevergoes.hashcode.parser.Parser;
import com.nevergoes.hashcode.parser.Row;

public class AppMain {

	public static void main(String[] args) throws Exception {

		processFile("a_example.in");
		processFile("b_small.in");
		processFile("c_medium.in");
		processFile("d_quite_big.in");
		processFile("e_also_big.in");
	}

	private static void processFile(String fileName) throws IOException {

		System.out.println();
		System.out.println("--------------------");
		System.out.println(fileName);
		System.out.println("--------------------");
		System.out.println();

		String textData = FileUtils.readFile(fileName);

		Parser p = new Parser(textData);

		final int M = p.valueAt(0, 0, Integer.class);
		final int N = p.valueAt(0, 1, Integer.class);

		Function<Row, int[]> mapFcn = (r) -> {

			int[] vals = new int[N];

			for (int i = 0; i < N; i++) {
				vals[i] = r.valueAt(i, int.class);
			}

			return vals;
		};

		int[] vals = p.mapRow(1, mapFcn);

		float c1 = 0.05f * vals[vals.length - 1];
		float c2 = 0.1f * vals[vals.length - 1];

		// idx 2nd group
		int idx1 = -1;
		int idx2 = -1;

		for (int i = 0; i < N; i++) {

			if (vals[i] > c1 && idx1 == -1) {
				idx1 = i;
			} else if (vals[i] > c2 && idx1 != -1 && idx2 == -1) {
				idx2 = i;
			}
		}

		int[] fine = new int[idx1];
		int[] medium = new int[idx2];
		int[] coarse = new int[N - idx2];

		for (int i = 0; i < idx1; i++) {
			fine[i] = vals[i];
		}

		for (int i = idx1; i < idx2; i++) {
			medium[i - idx1] = vals[i];
		}

		for (int i = idx2; i < N; i++) {
			coarse[i - idx2] = vals[i];
		}

		float coarseTarget = 0.99998f * M;

		float mediumTarget = 0.000018f * M;

		float fineTarget = M - coarseTarget - mediumTarget;

		if (Math.abs(fineTarget + mediumTarget + coarseTarget - M) > 0.01f) {
			// bad data
			System.exit(-1);
		}

		List<Combination> coarseCombinations = new ArrayList<>();

		for (int i = coarse.length - 1; i >= 0; i--) {

			Combination combination = new Combination(vals);

			combination.add(i + idx2);

			coarseCombinations.add(combination);

			int result = coarse[i];

			for (int j = i - 1; j >= 0; j--) {

				result += coarse[j];

				if (result > coarseTarget) {
					break;
				}

				combination.add(j + idx2);
			}
		}

		List<Combination> mediumCombinations = new ArrayList<>();

		for (int i = medium.length - 1; i >= 0; i--) {

			Combination combination = new Combination(vals);

			combination.add(i + idx1);

			mediumCombinations.add(combination);

			int result = medium[i];

			for (int j = i - 1; j >= 0; j--) {

				result += medium[j];

				if (result > mediumTarget) {
					break;
				}

				combination.add(j + idx1);
			}
		}

		List<Combination> fineCombinations = new ArrayList<>();

		for (int i = fine.length - 1; i >= 0; i--) {

			Combination combination = new Combination(vals);

			combination.add(i);
			fineCombinations.add(combination);

			int result = fine[i];

			for (int j = i - 1; j >= 0; j--) {

				result += fine[j];

				if (result > fineTarget) {
					break;
				}

				combination.add(j);
			}
		}

		int bestCoarseIdx = -1;
		int bestMediumIdx = -1;
		int bestFineIdx = -1;

		int bestDifference = Integer.MAX_VALUE;

		for (int i = 0; i < coarseCombinations.size(); i++) {

			int coarseSum = coarseCombinations.get(i).sum();

			if (mediumCombinations.size() == 0 && fineCombinations.size() == 0) {

				int diff = M - coarseSum;

				if (coarseSum <= M && diff < bestDifference) {

					bestCoarseIdx = i;

					bestDifference = diff;

					if (coarseSum == M) {
						break;
					}
				}

			} else if (mediumCombinations.size() == 0) {

				for (int j = 0; j < fineCombinations.size(); j++) {

					int fineSum = fineCombinations.get(j).sum();

					int coarseFineSum = coarseSum + fineSum;
					int diff = M - coarseFineSum;

					if (coarseFineSum <= M && diff < bestDifference) {

						bestCoarseIdx = i;
						bestFineIdx = j;

						bestDifference = diff;

						if (coarseFineSum == M) {
							break;
						}
					}
				}

			} else if (fineCombinations.size() == 0) {

				for (int k = 0; k < mediumCombinations.size(); k++) {

					int mediumSum = mediumCombinations.get(k).sum();

					int coarseMediumSum = coarseSum + mediumSum;
					int diff = M - coarseMediumSum;

					if (coarseMediumSum <= M && diff < bestDifference) {

						bestCoarseIdx = i;
						bestMediumIdx = k;

						bestDifference = diff;

						if (coarseMediumSum == M) {
							break;
						}
					}
				}

			} else {

				for (int k = 0; k < mediumCombinations.size(); k++) {

					int mediumSum = mediumCombinations.get(k).sum();

					int coarseMediumSum = coarseSum + mediumSum;

					for (int j = 0; j < fineCombinations.size(); j++) {

						int fineSum = fineCombinations.get(j).sum();

						int coarseMediumFineSum = coarseMediumSum + fineSum;
						int diff = M - coarseMediumFineSum;

						if (coarseMediumFineSum <= M && diff < bestDifference) {

							bestCoarseIdx = i;
							bestMediumIdx = k;
							bestFineIdx = j;

							bestDifference = diff;

							if (coarseMediumFineSum == M) {
								break;
							}
						}
					}
				}
			}
		}

		int score = M - bestDifference;
		float relativeScore = (float) score / M;

		System.out.println("Score: " + score + " / " + M + " ("
				+ String.format(Locale.US, "%.2f", (relativeScore * 100.0f)) + "%)");

		int nPizzas = 0;

		Combination bestCoarseCombination = null;
		Combination bestMediumCombination = null;
		Combination bestFineCombination = null;

		if (bestCoarseIdx != -1) {
			bestCoarseCombination = coarseCombinations.get(bestCoarseIdx);
			nPizzas += bestCoarseCombination.indices.size();
		}

		if (bestMediumIdx != -1) {
			bestMediumCombination = mediumCombinations.get(bestMediumIdx);
			nPizzas += bestMediumCombination.indices.size();
		}

		if (bestFineIdx != -1) {
			bestFineCombination = fineCombinations.get(bestFineIdx);
			nPizzas += bestFineCombination.indices.size();
		}

		StringBuilder sbOut = new StringBuilder();

		sbOut.append(nPizzas + "\n");

		if (bestFineIdx != -1) {
			for (int i = bestFineCombination.indices.size() - 1; i >= 0; i--) {
				sbOut.append(bestFineCombination.indices.get(i) + " ");
			}
		}

		if (bestMediumIdx != -1) {
			for (int i = bestMediumCombination.indices.size() - 1; i >= 0; i--) {
				sbOut.append(bestMediumCombination.indices.get(i) + " ");
			}
		}

		if (bestCoarseIdx != -1) {
			for (int i = bestCoarseCombination.indices.size() - 1; i >= 0; i--) {
				sbOut.append(bestCoarseCombination.indices.get(i) + " ");
			}
		}

		sbOut.deleteCharAt(sbOut.length() - 1);

		Path outPath = Paths.get(System.getenv("userprofile")).resolve("hashcode-results")
				.resolve(fileName.substring(0, fileName.length() - 3) + ".out");

		FileUtils.writeFile(outPath, sbOut.toString());

		System.out.println("Generated file: " + outPath.toFile().getAbsolutePath());
	}
}

class Combination {

	List<Integer> indices;
	int[] values;

	int sum = -1;

	public Combination(int[] values) {
		indices = new ArrayList<>();
		this.values = values;
	}

	public int sum() {
		if (sum == -1) {
			sum = indices.stream().mapToInt(i -> values[i]).sum();
		}
		return sum;
	}

	public void add(int index) {
		indices.add(index);
	}
}