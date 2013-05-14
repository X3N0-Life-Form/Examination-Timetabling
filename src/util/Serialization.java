package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import struct.Solution;

public class Serialization {
	public static Solution loadSolution(String serializedName) throws FileNotFoundException, IOException,
	ClassNotFoundException {
		FileInputStream fis = new FileInputStream(serializedName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Solution toLoad = (Solution) ois.readObject();
		ois.close();
		fis.close();
		return toLoad;
	}

	public static void saveSolution(Solution toSave, String serializedName) throws FileNotFoundException,
	IOException {
		FileOutputStream fos = new FileOutputStream(serializedName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(toSave);
		oos.close();
		fos.close();
	}
}
