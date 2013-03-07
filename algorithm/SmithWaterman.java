package algorithm;

public class SmithWaterman {

	/**
	 * Stores the values of all the alignments. 3D array to store values for 3
	 * sequences.
	 */
	private int[][][] cube;
	/**
	 * Used for backtracking. Keeps track from where the transition was made.
	 */
	private int[][][] backtrackcube;
	/**
	 * The given sequences.
	 */
	private String sequence1;
	private String sequence2;
	private String sequence3;
	/**
	 * Provided Blosum matrix.
	 */
	private BlosumMatrix matrix;
	/**
	 * Penalty constant.
	 */
	private final int GAPPenalty = -4;

	/**
	 * Initialisation code. The sequences and the matrix are stored as local
	 * variables.
	 * 
	 * @param matrix
	 * @param sequence1
	 * @param sequence2
	 * @param sequence3
	 */
	public SmithWaterman(BlosumMatrix matrix, String sequence1,
			String sequence2, String sequence3) {
		this.matrix = matrix;
		this.sequence1 = sequence1;
		this.sequence2 = sequence2;
		this.sequence3 = sequence3;
		cube = new int[sequence1.length() + 1][sequence2.length() + 1][sequence3
				.length() + 1];
		backtrackcube = new int[sequence1.length() + 1][sequence2.length() + 1][sequence3
				.length() + 1];
	}

	/**
	 * Returns a sum of blosomValues for 3 given identifiers.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	private int getBlosum(char a, char b, char c) {
		return (matrix.getValue(a, b) + matrix.getValue(b, c) + matrix
				.getValue(c, a));
	}

	/**
	 * Returns a sum of blosomValues for 2 given identifiers.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private int getBlosum(char a, char b) {
		return matrix.getValue(a, b);
	}

	/**
	 * Returns an array, where the first element is the maximum score(value) and
	 * the second element is the index of this value. The index is used for
	 * backtracking.
	 * 
	 * @param array
	 * @return
	 */
	private int[] getMax(int[] array) {
		int max = array[0];
		int maxIndex = 0;
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
				maxIndex = i;
			}
		}
		int[] result = { max, maxIndex };
		return result;
	}

	/**
	 * This is cube creation code. There are 8 possible scores to choose from at
	 * each step of the algorithm. matchValues[0] is 0, because the array is
	 * initialised that way.
	 */
	private void createCube() {
		int[] matchValues = new int[8];
		for (int i = 1; i < cube.length; i++) {
			for (int j = 1; j < cube[0].length; j++) {
				for (int k = 1; k < cube[0][0].length; k++) {
					// Full Match
					matchValues[1] = cube[i - 1][j - 1][k - 1]
							+ getBlosum(sequence1.charAt(i - 1),
									sequence2.charAt(j - 1),
									sequence3.charAt(k - 1)) / 3;
					// Partial Matches
					matchValues[2] = cube[i - 1][j - 1][k]
							+ (2 * GAPPenalty + getBlosum(
									sequence1.charAt(i - 1),
									sequence2.charAt(j - 1))) / 3;
					matchValues[3] = cube[i - 1][j][k - 1]
							+ (2 * GAPPenalty + getBlosum(
									sequence1.charAt(i - 1),
									sequence2.charAt(k - 1))) / 3;
					matchValues[4] = cube[i][j - 1][k - 1]
							+ (2 * GAPPenalty + getBlosum(
									sequence1.charAt(j - 1),
									sequence2.charAt(k - 1))) / 3;
					// Insertion / Deletion (3*Penalty)/3
					matchValues[5] = cube[i - 1][j][k] + GAPPenalty;
					matchValues[6] = cube[i][j - 1][k] + GAPPenalty;
					matchValues[7] = cube[i][j][k - 1] + GAPPenalty;
					// Get maximal score
					int[] maxValues = getMax(matchValues);
					// Save the maximum score in the array
					cube[i][j][k] = maxValues[0];
					// Save the backtracking data.
					backtrackcube[i][j][k] = maxValues[1];
				}
			}
		}
	}

	/**
	 * The method finds the indices of the element with the highest value.
	 * 
	 * @return
	 */
	private int[] getMaxValueIndices() {
		int[] indices = { 0, 0, 0 };
		int max = cube[0][0][0];
		for (int i = 0; i < cube.length; i++) {
			for (int j = 0; j < cube[0].length; j++) {
				for (int k = 0; k < cube[0][0].length; k++) {
					if (cube[i][j][k] >= max) {
						max = cube[i][j][k];
						indices[0] = i;
						indices[1] = j;
						indices[2] = k;
					}
				}
			}
		}
		return indices;
	}

	/**
	 * This method is used for backtracking. The switch helps to determine from
	 * where the transition was made. Backtrack cube keeps track of all the
	 * transitions.
	 * 
	 * @param i
	 * @param j
	 * @param k
	 * @return
	 */
	private int[] getIndices(int i, int j, int k) {
		int[] indices = { i, j, k };
		int max = backtrackcube[i][j][k];
		switch (max) {
		case 1:
			indices[0] = i - 1;
			indices[1] = j - 1;
			indices[2] = k - 1;
			break;
		case 2:
			indices[0] = i - 1;
			indices[1] = j - 1;
			break;
		case 3:
			indices[0] = i - 1;
			indices[2] = k - 1;
			break;
		case 4:
			indices[1] = j - 1;
			indices[2] = k - 1;
			break;
		case 5:
			indices[0] = i - 1;
			break;
		case 6:
			indices[1] = j - 1;
			break;
		case 7:
			indices[2] = k - 1;
			break;
		}
		return indices;
	}

	/**
	 * The aligned sequences are created by careful backtracking from the
	 * highest value. When the index has been decreased that means that a match
	 * has been made and a letter is added to the appropriate sequence.
	 * Otherwise, a deletion/insertion is made and a hyphen is placed in the
	 * string. At the end the strings are reversed.
	 */
	private void printSequences() {
		StringBuilder seq1 = new StringBuilder();
		StringBuilder seq2 = new StringBuilder();
		StringBuilder seq3 = new StringBuilder();
		int[] maxValueIndices = getMaxValueIndices();
		int i = maxValueIndices[0];
		int j = maxValueIndices[1];
		int k = maxValueIndices[2];
		int alignmentSum = cube[i][j][k];
		//System.out.println(i + " " + j + " " + k);
		while (cube[i][j][k] != 0 && (i != 0 || j != 0 || k != 0)) {
			int[] indices = getIndices(i, j, k);
			if (indices[0] < i) {
				seq1.append(sequence1.charAt(i - 1));
			} else {
				seq1.append("-");
			}
			if (indices[1] < j) {
				seq2.append(sequence2.charAt(j - 1));
			} else {
				seq2.append("-");
			}
			if (indices[2] < k) {
				seq3.append(sequence3.charAt(k - 1));
			} else {
				seq3.append("-");
			}
			i = indices[0];
			j = indices[1];
			k = indices[2];
		}
		seq1.reverse();
		seq2.reverse();
		seq3.reverse();
		System.out.println(seq1);
		System.out.println(seq2);
		System.out.println(seq3);
		System.out.println(alignmentSum);
	}

	/**
	 * Code used to run the whole algorithm.
	 */
	public void run() {
		createCube();
		printSequences();
	}
}
