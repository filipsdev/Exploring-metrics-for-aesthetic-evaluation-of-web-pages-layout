import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import org.opencv.core.Core;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class Main {

	public static void main(String[] args) {

		// load library first
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//		###################################################################################
//		Select images from file system
//		###################################################################################	

		HandyMethods handy = new HandyMethods();
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.showSaveDialog(null);
		File userSelect = fc.getSelectedFile();
		List<File> imageFiles = new ArrayList<File>();
		String resultsPath = "";

		// get images from selected directory and create directory called 'results'
		if (userSelect.isDirectory()) {

			File[] dirContent = userSelect.listFiles();

			if (dirContent != null) {
				for (File file : dirContent) {
					if (handy.isImage(file)) {

						imageFiles.add(file);

					}
				}
				if (imageFiles.size() > 0) {

					File resultsFolder = new File(fc.getSelectedFile() + "\\results");
					resultsFolder.mkdir();
					resultsPath = resultsFolder.getPath();
					System.out.println("number of images detected: " + imageFiles.size() + "\n");

				} else {

					System.out.println("There are no images in the selected directory!");

				}
			}
		} else {

			// get selected image and create directory called 'results'
			if (userSelect.isFile() && handy.isImage(userSelect)) {

				imageFiles.add(userSelect);
				File resultsFolder = new File(fc.getCurrentDirectory() + "\\results");
				resultsFolder.mkdir();
				resultsPath = resultsFolder.getPath();

			} else {

				System.out.println("The selected file is not an image!");

			}
		}

//		###################################################################################
//		Process images and get Components
//		###################################################################################	

		ImageInspection inspector = new ImageInspection();
		List<ProcessedImage> processedImages = new ArrayList<ProcessedImage>();
		processedImages = inspector.processImages(imageFiles);

//		###################################################################################
//		Use Components to calculate Metrics
//		###################################################################################	

		Metrics metrics = new Metrics();
		File readMe = new File(resultsPath + "\\readMe.txt");

		for (ProcessedImage img : processedImages) {

			metrics.calcEquilibrium(img);
			metrics.calcDensity(img);
			metrics.calcSimplicity(img);
			System.out.println(img.toString());

//			###################################################################################
//			Save results to file
//			###################################################################################	

			// saving images in 'results' directory
			handy.saveImgToDir(resultsPath, img.getName(), "Gray", img.getGrayImg());
			handy.saveImgToDir(resultsPath, img.getName(), "Edge", img.getEdgedImg());
			handy.saveImgToDir(resultsPath, img.getName(), "Con", img.getContoursImg());
			handy.saveImgToDir(resultsPath, img.getName(), "RotRect", img.getRotRectImg());
			handy.saveImgToDir(resultsPath, img.getName(), "Comp", img.getComponentsImg());

			try {

				FileWriter fw = new FileWriter(readMe, true);

				// write to readMe.txt
				fw.write(img.toString() + "\n");
				fw.flush();
				fw.close();

			} catch (Exception e) {

				System.out.println("Exception in edgeDetection()!");
				e.printStackTrace();

			}
		}

//		###################################################################################
//		When user selects single image all phases of the image processing are displayed
//		###################################################################################	

		if (imageFiles.size() == 1) {

			HighGui.imshow("src", Imgcodecs.imread(userSelect.getPath()));
			HighGui.imshow("grayscale img", processedImages.get(0).getGrayImg());
			HighGui.imshow("edged image", processedImages.get(0).getEdgedImg());
			HighGui.imshow("contours", processedImages.get(0).getContoursImg());
			HighGui.imshow("Rect Array(all rectangles)", processedImages.get(0).getRotRectImg());
			HighGui.imshow("Components(filtered rectangles)", processedImages.get(0).getComponentsImg());
			HighGui.waitKey(0);
			System.exit(0);

		}
	}
}
