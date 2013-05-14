package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import struct.Solution;

/**
 * Contains various methods that related to object serialization.
 * @author Adrien Droguet - Sara Tari
 * @see Solution
 */
public class Serialization {
	
	/**
	 * Loads a Solution from the specified file path.
	 * @param serializedName File path
	 * @return A Solution object.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Solution loadSolution(String serializedName) throws FileNotFoundException, IOException,
	ClassNotFoundException {
		System.out.println("Loading serialized Solution " + serializedName);
		FileInputStream fis = new FileInputStream(serializedName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Solution toLoad = (Solution) ois.readObject();
		ois.close();
		fis.close();
		return toLoad;
	}

	/**
	 * Serializes a Solution object into the specified file path.
	 * @param toSave Solution object to save.
	 * @param serializedName File path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void saveSolution(Solution toSave, String serializedName) throws FileNotFoundException,
	IOException {
		System.out.println("Serializing solution into " + serializedName);
		FileOutputStream fos = new FileOutputStream(serializedName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(toSave);
		oos.close();
		fos.close();
	}
}
