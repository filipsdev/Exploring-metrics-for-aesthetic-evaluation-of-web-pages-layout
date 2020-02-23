
public class Metrics {
	
	HandyMethods handy = new HandyMethods();

	// calculate equilibrium
	public void calcEquilibrium(ProcessedImage img) {
		
		double EM, EM_x, EM_y;// equilibrium and equilibrium components along the axis
		double sumOne = 0, sumTwo = 0, sumThree = 0, sumFour = 0;// needed to calculate the formula
		int n, b_frame, h_frame;
		
			n = img.getComponents().size();// number of objects(rectangles) on the frame
			double[] a = new double[n];// object(rectangle) area
			int x_c = img.getWidth() / 2;// 'x' coordinate of the center of the frame
			int y_c = img.getHeight() / 2;// 'y' coordinate of the center of the frame
			b_frame = img.getWidth();// width of the frame
			h_frame = img.getHeight();// height of the frame
			
			// calculations for the 'x' axis component
			for (int i = 0; i < n; i++) {
				double[] x = new double[n];// 'x' coordinate of the center of the object(rectangle)
				a[i] = img.getComponents().get(i).size.width * img.getComponents().get(i).size.height;
				x[i] = img.getComponents().get(i).center.x;
				
				sumOne = a[i] * (x[i] - x_c);
				sumTwo += a[i];				
			}
			// 'x' axis component
			EM_x = (2 * sumOne) / (n * b_frame * sumTwo);
			
			// calculations for the 'y' axis component
			for (int i = 0; i < n; i++) {
				double[] y = new double[n];// 'y' coordinate of the center of the object(rectangle)
				a[i] = img.getComponents().get(i).size.width * img.getComponents().get(i).size.height;
				y[i] = img.getComponents().get(i).center.y;

				sumThree = a[i] * (y[i] - y_c);
				sumFour += a[i];
			}
			// 'y' axis component
			EM_y = (2 * sumThree) / (n * h_frame * sumFour);
			
			// calculate equilibrium
			if(EM_x < 0) {
				EM_x = EM_x - (2 * EM_x);
			}
			if(EM_y < 0) {
				EM_y = EM_y - (2 * EM_y);
			}
			EM = 1 - ((EM_x + EM_y)/2);
			
			// setting the equilibrium parameter(argument) for every image object
			img.setEquilibrium(EM);
		}

	// calculate density
	public void calcDensity(ProcessedImage img) {
		
		double frameArea = img.getGrayImg().size().area();
		double sum_compArea = 0;
		for(int i=0; i<img.getComponents().size(); i++) {
			
			sum_compArea += img.getComponents().get(i).size.height * img.getComponents().get(i).size.width;
			
		}
		
		double DM = 1 - (sum_compArea/frameArea);
		img.setDensity(DM);
	}

	// calculate cohesion
	public void calcCohesion(ProcessedImage img) {
		
//		double CM, CM_fl, CM_lo;
//		double t_fl
//		for(int i=0; i<img.getComponents().size(); i++) {
//			
//			double b_i = img.getComponents().get(i).size.width;// width of component 'i'
//			double h_i = img.getComponents().get(i).size.height;// height of component 'i'
//			double b_layout = handy.findLayoutWidthFromComponents(img.getComponents(), img.getGrayImg());// width of the layout
//			double h_layout = handy.findLayoutHeightFromComponents(img.getComponents(), img.getGrayImg());// height of the layout
//			double b_frame = img.getGrayImg().size().width;// width of the frame
//			double h_frame = img.getGrayImg().size().height;// height of the frame
//			int n = img.getComponents().size();// number of components on frame
//			
//
//		}
//		
//		CM = (Math.abs(CM_fl) + Math.abs(CM_lo)) / 2;
//		img.setCohesion(CM);
		
		
	}

	public void calcSimplicity(ProcessedImage img) {
		
		double SMM;// simplicity measure
		double n_vap = handy.findNumVerticalAlignPoints(img);// number vertical alignment points
		double n_hap = handy.findNumHirizontalAlignPoints(img);// number horizontal alignment points
		double n = img.getComponents().size();// number objects on frame
				
		SMM = (3 / (n_vap + n_hap + n));
		img.setSimplicity(SMM);
	}
}