import java.io.File;
import java.util.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageInspection {

	HandyMethods handy = new HandyMethods();// supporting methods in here

	private Mat src2Gray = new Mat();
	private Mat cannyOutput = new Mat();
	private Scalar green = new Scalar(0, 255, 0);
	private List<ProcessedImage> processedImages = new ArrayList<ProcessedImage>();

	// Constructor
	public ImageInspection() {
	}

	public List<ProcessedImage> processImages(List<File> imageList) {
		
		// loop through every image from the selected directory
		for (File image : imageList) {

//			###################################################################################
//			Canny Edge Detection
//			###################################################################################

			Mat src = Imgcodecs.imread(image.getPath()); // store input image as Mat object
			ProcessedImage img = new ProcessedImage(image.getName(), src.width(), src.height());// represent every image
																								// as an object

			// convert source image(colored) to gray scale image
			Imgproc.cvtColor(src, src2Gray, Imgproc.COLOR_BGR2GRAY);
			img.setGrayImg(src2Gray);

			// uncomment to get thresholds from Histogram Median and comment lines 43 and 44
//			int[] threshVal = handy.getTreshFromHistoMedian(src2Gray);
//			int lowThresh = threshVal[0];
//			int highThresh = threshVal[1];

			// get thresholds through OTSU's algorithm 
			int highThresh = (int) (Imgproc.threshold(src2Gray, new Mat(), 0, 255, Imgproc.THRESH_OTSU));
			int lowThresh = (int) (highThresh / 3);

			// Edge Detection
			Imgproc.Canny(src2Gray, cannyOutput, lowThresh, highThresh, 3, false);// apertureSize and L2gradient -
																					// default values
			img.setThresholds(lowThresh, highThresh);
			img.setEdgedImg(cannyOutput);

//			###################################################################################
//			Find Contours
//			###################################################################################	

			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(cannyOutput, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			
			// display found contours in an image
			Mat contoursImg = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
			Imgproc.drawContours(contoursImg, contours, -1, green, 1);
			img.setContoursImg(contoursImg);

//			###################################################################################
//			EXTRACT COMPONENTS(Bounding Rotated Rectangle Operations)
//			I. Put Rotated Rectangle around each contour
//			II. Apply Filter
//			###################################################################################	

//			###################################################################################
//			I. Put Rotated Rectangle around each Contour
//			###################################################################################	

			MatOfPoint2f[] approxCurve = new MatOfPoint2f[contours.size()];
			RotatedRect[] rotRectArray = new RotatedRect[contours.size()];
			Mat rotRectImg = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);

			for (int i = 0; i < contours.size(); i++) {

				// doing Contour Approximation for each contour to reduce the number of vertices
				// epsilon = 10% of arc length
				double epsilon = 0.1 * Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true);
				approxCurve[i] = new MatOfPoint2f();
				Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approxCurve[i], epsilon, true);
				rotRectArray[i] = Imgproc.minAreaRect(new MatOfPoint2f(approxCurve[i].toArray()));

			}

			handy.convRotRectArrToMat(rotRectArray, rotRectImg);
			img.setRotRectImg(rotRectImg);

//			###################################################################################
//			II. Apply Filter
//			Filter Conditions:
//				1. size > 20x20 pixels
//				2. angle of rotation < |2| degrees
//				3. ignore rectangles that overlap
//			###################################################################################

			List<RotatedRect> components = new ArrayList<RotatedRect>();// filtered rotRectArray

			Mat componentsImg = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);

			for (int i = 0; i < rotRectArray.length; i++) {

				// Conditions 1 and 2
//				if ((rotRectArray[i].size.height > 20 && rotRectArray[i].size.width > 20)
//						&& ((rotRectArray[i].angle >= -90 && rotRectArray[i].angle <= -88)
//								|| (rotRectArray[i].angle <= -0 && rotRectArray[i].angle >= -2))) {

					components.add(rotRectArray[i]);

//				}

			}
			
			// Condition 3
			List<RotatedRect> tempList = new ArrayList<RotatedRect>();
			for (int i = 0; i < components.size() - 1; i++) {
				for (int k = 1; k < components.size(); k++) {
					
					double W_i = components.get(i).size.width;
					double W_k = components.get(k).size.width;
					double H_i = components.get(i).size.height;
					double H_k = components.get(k).size.height;
					double X_i = components.get(i).center.x;
					double X_k = components.get(k).center.x;
					double Y_i = components.get(i).center.y;
					double Y_k = components.get(k).center.y;
					
					if (((W_i - W_k) < 20) && ((H_i - H_k) < 20) && ((X_i - X_k) < 20) && ((Y_i - Y_k) < 20)) {
						if (i != k) {

							tempList.add(components.get(i));
							
						}
					}
				}
			}
			for (int i = 0; i < tempList.size(); i++) {
				components.remove(tempList.get(0));
			}
			
			//
			handy.convRotRectArrToMat(handy.rotRectListToArray(components), componentsImg);
			img.setComponents(components);
			img.setComponentsImg(componentsImg);
			processedImages.add(img);
			
		}
		
		return processedImages;
	}
	
}