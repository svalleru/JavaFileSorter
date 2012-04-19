import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.TreeMap;

/*
 * author: svalleru
 */
public class JavaFileSorter {

	private static class Sort implements Runnable {
		private String fname;

		public Sort(String fname) {
			this.fname = fname;
		}

		public void run() {
			// Sorts any given file and stores as filename.sorted
			// System.out.println(Thread.currentThread().getName() + fname);
			try {
				BufferedReader reader = new BufferedReader(
						new FileReader(fname));
				Map<String, String> map = new TreeMap<String, String>();
				String line = "";
				while ((line = reader.readLine()) != null) {
					map.put(getField(line), line);
				}

				reader.close();
				BufferedWriter writer = new BufferedWriter(new FileWriter(fname
						+ ".sorted"));
				for (String val : map.values()) {
					writer.write(val);
					writer.newLine();
				}
				writer.close();
			} catch (Exception e) {// TBI
			}
		}
	}

	public static class Splitter {

		private final static String NEWLINE = System
				.getProperty("line.separator");

		public static void getData(String filename, int lines)
				throws IOException {
			try {
				// opens the file in a string buffer
				BufferedReader bufferedReader = new BufferedReader(
						new FileReader(filename));
				StringBuffer stringBuffer = new StringBuffer();

				// performs the splitting
				String line;
				int i = 0;
				int counter = 1;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
					stringBuffer.append(NEWLINE);
					i++;
					if (i >= lines) {
						// saves the lines in the file
						saveFile(stringBuffer, filename + counter);
						// creates a new buffer, so the old can get garbage
						// collected.
						stringBuffer = new StringBuffer();
						i = 0;
						counter++;
					}
				}
				bufferedReader.close();
			} catch (IOException e) {
				throw new IOException("unable to read from file: " + filename);
			}
		}

		private static void saveFile(StringBuffer stringBuffer, String filename) {
			String path = (new File("")).getAbsolutePath();
			File file = new File(path + "/" + filename);
			FileWriter output = null;
			try {
				output = new FileWriter(file);
				output.write(stringBuffer.toString());
				// System.out.println("file " + path + filename + " written");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				try {
					output.close();
				} catch (IOException e) {
					// do nothing the file wasn't been even opened
				}
			}
		}

	}

	// Methods
	private static String getField(String line) {
		return line.split(" ")[0];// extract value you want to sort on
	}

	private int getCount(String fname) throws IOException {
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(fname));
			while ((reader.readLine()) != null)
				;
			return reader.getLineNumber();
		} catch (Exception ex) {
			return -1;
		} finally {
			if (reader != null)
				reader.close();
		}

	}

	public static void main(String[] args) throws IOException {

		String fileName = args[0]; // filename
		int numthreads = Integer.parseInt(args[1]); // concurrency for sorting
		int linecount, splitcount;
		JavaFileSorter M = new JavaFileSorter();
		linecount = M.getCount(fileName);
		splitcount = linecount / numthreads;
		long so = System.currentTimeMillis();
		try {
			Splitter.getData(fileName, splitcount);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long se = System.currentTimeMillis();
		System.out.println("Time Elapsed for Splitting: " + (se - so) + "ms");
		long o = System.currentTimeMillis();
		for (int i = 1; i <= numthreads; i++) {

			Thread t = new Thread(new Sort(fileName + i));
			t.start();
		}
		long e = System.currentTimeMillis();
		System.out.println("Time Elapsed for Sorting: " + (e - o) + "ms");

	}
}