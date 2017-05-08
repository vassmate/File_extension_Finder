package extension_finder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final public class FileExtensionFinder {

	private final String pathToMap;
	private final String extToFind;
	private HashMap<String, ArrayList<String>> filesMap = new HashMap<>();

	public FileExtensionFinder(String path, String extension) {
		pathToMap = path;
		extToFind = extension;
		if (!isValidateExtension()) {
			throw new Error("Unsupported file extension!");
		}
	}

	/**
	 * Checks if the given String is a valid extension.
	 * 
	 * @return
	 */
	private boolean isValidateExtension() {
		if (extToFind != null) {
			return extToFind.startsWith(".") && extToFind.length() > 1;
		}
		return false;
	}

	/**
	 * Checks if a given File has the extension that we want.
	 * 
	 * @param currentFileName
	 * @return boolean
	 */
	private boolean isValidFile(String currentFileName) {
		int extNameLen = extToFind.length();
		if (currentFileName.length() < (extNameLen + 1)) {
			return false;
		}
		String currFileExt = currentFileName.substring(currentFileName.length() - extNameLen);
		return extToFind.equals(currFileExt);
	}

	/**
	 * Makes a List of String from a String array.
	 * 
	 * @param fileDirNames
	 * @return a List of Strings
	 */
	private List<String> arrayToList(String[] fileDirNames) {
		return fileDirNames != null && fileDirNames.length > 0 ? Stream.iterate(0, f -> f + 1)
				.limit(fileDirNames.length)
				.map(f -> fileDirNames[f])
				.collect(Collectors.toList()) : new ArrayList<>();
	}

	/**
	 * Search for files in the given directory and in it's sub-directories
	 * recursively.
	 * 
	 * @param folder
	 */
	private void searchForFiles(File folder) {
		ArrayList<String> dirs = new ArrayList<>();
		ArrayList<String> files = new ArrayList<>();
		String folderPath = folder.getAbsolutePath();
		try {
			List<String> filesAndDirs = arrayToList(folder.list());
			dirs = (ArrayList<String>) filesAndDirs.stream()
					.map(fd -> folderPath + "\\" + fd)
					.filter(fd -> new File(fd).isDirectory())
					.collect(Collectors.toList());
			files = (ArrayList<String>) filesAndDirs.stream()
					.filter(fd -> isValidFile(fd))
					.collect(Collectors.toList());
			if (!files.isEmpty()) {
				filesMap.put(folderPath, files);
			}
			for (String dir : dirs) {
				searchForFiles(new File(dir));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets all of the files from the given folder and from it's sub-folders
	 * with matching extension in a HashMap.
	 * 
	 * @return filesMap
	 */
	public HashMap<String, ArrayList<String>> getMappedFiles() {
		File folder = new File(pathToMap);
		searchForFiles(folder);
		return filesMap;
	}

}
