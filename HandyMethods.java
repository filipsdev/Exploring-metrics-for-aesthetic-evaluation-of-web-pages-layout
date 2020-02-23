
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class HandyMethods {

	// delete selected directory
	public boolean deleteDir(File dir) {
		
		File[] contents = dir.listFiles();
		
		if (contents != null) {
			for (File f : contents) {
				if (!Files.isSymbolicLink(f.toPath())) {
					
					deleteDir(f);
					
				}
			}
		}
		
		if (!dir.delete()) {
			return true;
		} else {
			return false;
		}
		
	}

	// get median value from gray scale image
	public int getMedianFromGray(Mat gray) {

		ArrayList<Mat> listOfMat = new ArrayList<>();
		listOfMat.add(gray);
		MatOfInt channels = new MatOfInt(0);
		Mat mask = new Mat();
		Mat hist = new Mat(256, 1, CvType.CV_8UC1);
		MatOfInt histSize = new MatOfInt(256);
		MatOfFloat ranges = new MatOfFloat(0, 256);

		Imgproc.calcHist(listOfMat, channels, mask, hist, histSize, ranges);

		double t = gray.rows() * gray.cols() / 2;
		double total = 0;
		int med = -1;
		for (int row = 0; row < hist.rows(); row++) {
			
			double val = hist.get(row, 0)[0];
			if ((total <= t) && (total + val >= t)) {
				
				med = row;
				break;
				
			}
			total += val;
		}
		return med;
	}

	// convert List<MatOfPoint> to List<MatOfPoint2f>
	public List<MatOfPoint2f> ListMOP_To_ListMOP2f(List<MatOfPoint> List_MOP) {
		
		List<MatOfPoint2f> list_MOP2f = new ArrayList<MatOfPoint2f>();
		for (MatOfPoint point : List_MOP) {
			
			MatOfPoint2f point2f = new MatOfPoint2f(point.toArray());
			list_MOP2f.add(point2f);
			
		}
		return list_MOP2f;
	}

	// check if a file is an image file
	public boolean isImage(File file) {
		
		if (file.getName().endsWith(".PNG") || file.getName().endsWith(".png") || file.getName().endsWith(".jpg")
				|| file.getName().endsWith(".JPG") || file.getName().endsWith(".jpeg")
				|| file.getName().endsWith(".JPEG") || file.getName().endsWith(".bmp")
				|| file.getName().endsWith(".BMP")) {
			
			return true;
			
		}
		
		return false;
		
	}

	// save Mat to directory
	void saveImgToDir(String resultsPath, String imgName, String addToImgName, Mat mat) {

			Imgcodecs.imwrite(resultsPath + "\\"
					+ imgName.substring(0, imgName.length() - 4) + addToImgName
					+ imgName.substring(imgName.length() - 4,
							imgName.length()),
					mat);
	}

	public void convRotRectArrToMat(RotatedRect[] rotRectArray, Mat mat) {
		
		for (int i = 0; i < rotRectArray.length; i++) {


		Point[] vertices = new Point[4];// in rectPoints the four vertices will be stored
		rotRectArray[i].points(vertices);
		for (int j = 0; j < 4; j++) {
			
			Imgproc.line(mat, vertices[j], vertices[(j + 1) % 4], new Scalar(0, 255, 0), 1);
			
		}
	}
		
//	HighGui.imshow("mat", mat);
//	HighGui.waitKey(0);
//	System.exit(0);	
	
	}

	public RotatedRect[] rotRectListToArray(List<RotatedRect> rotRectList) {
		
		RotatedRect[] array = new RotatedRect[rotRectList.size()];
		for(int i=0; i<rotRectList.size(); i++) {
			
			array[i] = rotRectList.get(i);
			
		}
		return array;
	}

	public int[] getTreshFromHistoMedian(Mat src2Gray) {
		
		int[] threshVal = new int[2];
		Mat gaussOutput = new Mat();
		Size ksize = new Size(3, 3);
		double sigmaX = 0;
		Imgproc.GaussianBlur(src2Gray, gaussOutput, ksize, sigmaX);// do blur to reduce the noise
		int histMedian = getMedianFromGray(gaussOutput);
		double sigma = 0.33;// default value, can be modified to achieve better results
		int lowThresh = (int) (Math.max(0, (1 - sigma) * histMedian));
		int highThresh = (int) (Math.min(255, (1 + sigma) * histMedian));
		threshVal[0] = lowThresh;
		threshVal[1] = highThresh;
		
//		System.out.println(String.format("histogram thresholds: \nlowThresh = %d\nhighThresh = %d\n", lowThresh, highThresh));

		return threshVal;
	}

	public double findNumVerticalAlignPoints(ProcessedImage img) {
		
		double compMin[] = new double[img.getComponents().size()];
		double min = img.getGrayImg().size().width;
		int n_vap = 0;
		for(int i=0; i<img.getComponents().size(); i++) {
			
			compMin[i] = img.getComponents().get(i).center.x - (img.getComponents().get(i).size.width / 2);
			if(min > compMin[i]) {
				min = compMin[i];
			}
		}
		if(min < img.getGrayImg().size().width) {
			n_vap =1;
			for(int i=0; i<img.getComponents().size(); i++) {
				
				if((compMin[i] - min) < 10) {
					n_vap++;
				}
				
			}
			n_vap--;
		}
		return n_vap;
	}
	
	public double findNumHirizontalAlignPoints(ProcessedImage img) {
		
		double compMin[] = new double[img.getComponents().size()];
		double min = img.getGrayImg().size().height;
		int n_hap = 0;
		for(int i=0; i<img.getComponents().size(); i++) {
			
			compMin[i] = img.getComponents().get(i).center.y - (img.getComponents().get(i).size.height / 2);
			if(min > compMin[i]) {
				min = compMin[i];
			}
		}
		if(min < img.getGrayImg().size().height) {
			n_hap =1;
			for(int i=0; i<img.getComponents().size(); i++) {
				
				if((compMin[i] - min) < 10) {
					n_hap++;
				}
				
			}
			n_hap--;
		}
		return n_hap;
	}
	
	public void removeOverlapRect(List<RotatedRect> components) {
		
		

	}

}
