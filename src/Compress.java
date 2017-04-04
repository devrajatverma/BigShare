import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class Compress {
	public static void zip(String source, String destinationFilePath, String password) {
		try {
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

			if (password.length() > 0) {
				parameters.setEncryptFiles(true);
				parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
				parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
				parameters.setPassword(password);
			}

			ZipFile zipFile = new ZipFile(destinationFilePath);

			File targetFile = new File(source);
			if (targetFile.isFile()) {
				zipFile.addFile(targetFile, parameters);
			} else if (targetFile.isDirectory()) {
				zipFile.addFolder(targetFile, parameters);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unzip(String targetZipFilePath, String destinationFolderPath, String password) {
		try {
			ZipFile zipFile = new ZipFile(targetZipFilePath);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
			zipFile.extractAll(destinationFolderPath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// test
	/*
	 * public static void main(String[] args) { String source =
	 * "C:\\Users\\RAJAT VERMA\\Desktop\\sample"; // it can be a file or a
	 * folder path String zipFilePath =
	 * "C:\\Users\\RAJAT VERMA\\Desktop\\samplez"; String unzippedFolderPath =
	 * "C:\\Users\\RAJAT VERMA\\Desktop\\sampleuz"; String password = ""; //
	 * keep it EMPTY("") for applying no // password protection
	 * 
	 * Compress.zip(source, zipFilePath, password); Compress.unzip(zipFilePath,
	 * unzippedFolderPath, password); }
	 */
}