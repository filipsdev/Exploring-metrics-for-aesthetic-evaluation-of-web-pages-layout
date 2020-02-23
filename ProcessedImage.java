import java.util.List;

import org.opencv.core.*;

public class ProcessedImage {

	private String imgName;
	private int frameWidth;
	private int frameHeight;
	private List<RotatedRect> components;
	private double equilibrium, density, simplicity;
	private int lowThresh;
	private int highThresh;
	// images
	private Mat grayImg, edgedImg, contoursImg, rotRectImg, componentsImg;

	// --- <Constructor> --- | --- <Constructor> --- | --- <Constructor> ---//
	public ProcessedImage(String imgName, int frameWidth, int frameHeight) {
		this.imgName = imgName;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
	}

	// --- <getters & setters> --- | --- <getters & setters> --- | --- <getters &
	// setters> ---//

	protected void setEquilibrium(double equilibrium) {
		this.equilibrium = equilibrium;
	}
	protected void setDensity(double density) {
		this.density = density;
	}
	public void setSimplicity(double simplicity) {
		this.simplicity = simplicity;
	}


	protected List<RotatedRect> getComponents() {
		return components;
	}

	protected int getWidth() {
		return this.frameWidth;
	}

	protected int getHeight() {
		return this.frameHeight;
	}

	protected String getName() {
		return this.imgName;
	}

	protected void setGrayImg(Mat grayImg) {
		this.grayImg = grayImg;
	}

	protected Mat getGrayImg() {
		return this.grayImg;
	}

	protected void setThresholds(int lowThresh, int highThresh) {
		this.lowThresh = lowThresh;
		this.highThresh = highThresh;
	}

	protected int getLowThresh() {
		return this.lowThresh;
	}

	protected int getHighThresh() {
		return this.highThresh;
	}

	protected void setEdgedImg(Mat edgedImg) {
		this.edgedImg = edgedImg;
	}

	protected Mat getEdgedImg() {
		return this.edgedImg;
	}

	protected void setContoursImg(Mat contoursImg) {
		this.contoursImg = contoursImg;
	}

	protected Mat getContoursImg() {
		return this.contoursImg;
	}

	protected void setRotRectImg(Mat rotRectImg) {
		this.rotRectImg = rotRectImg;
	}

	protected Mat getRotRectImg() {
		return this.rotRectImg;
	}

	protected void setComponentsImg(Mat componentsImg) {
		this.componentsImg = componentsImg;
	}

	public void setComponents(List<RotatedRect> components) {
		this.components = components;
	}

	public Mat getComponentsImg() {
		return this.componentsImg;
	}

	public String toString() {
		return String.format(
				"image name: %s\nlowThresh = %d\nhighThresh = %d\nEquilibrium = %.4f\nDensity = %.4f\nSimplicity = %.4f\n",
				this.imgName, this.lowThresh, this.highThresh, this.equilibrium, this.density, this.simplicity);
	}

}
