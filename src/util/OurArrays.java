package util;

/**
 * A stupid class that does stupid things
 * @author Adrien Droguet - Sara Tari
 *
 */
public class OurArrays {

	/**
	 * LOL comparison
	 * @param ecs
	 * @param ecl
	 * @return A satisfying result
	 */
	public static boolean equals(int[][] ecs, int[][] ecl) {
		if (ecs.length != ecl.length) {
			return false;
		}
		for (int i = 0; i < ecs.length; i++) {
			if (ecs[i].length != ecl[i].length)
				return false;
			for (int j = 0; j < ecs[i].length; j++) {
				if (ecs[i][j] != ecl[i][j])
					return false;
			}
		}
		return true;
	}

}
