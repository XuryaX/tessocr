package com.shaurya.back;

import java.awt.image.*;
import java.util.LinkedList;
import java.util.ArrayList;

import com.shaurya.back.Types.RegionImage;
import com.shaurya.back.Types.TextRegion;

public class GetImageText {

	private BufferedImage image;

	private double merge_densityFactor;

	private int merge_mass;

	private int merge_dist1;

	private double merge_distfac;

	private int merge_dist2;

	public GetImageText(BufferedImage img) {

		image = img;

		merge_densityFactor = 0.5;

		merge_mass = 15;

		merge_dist1 = 4;

		merge_distfac = 1;

		merge_dist2 = 20;
		RescaleOp rescaleOp = new RescaleOp(1.2f, 20.0f, null);
		rescaleOp.filter(image, image);
	}

	public GetImageText(BufferedImage img, double m_densityFactor, int m_mass,
			int m_dist1, double m_distfac, int m_dist2) {

		image = img;
		merge_densityFactor = m_densityFactor;
		merge_mass = m_mass;
		merge_dist1 = m_dist1;
		merge_distfac = m_distfac;
		merge_dist2 = m_dist2;

	}

	/**
	 * 
	 * Only for debugging - prints out the current parameters
	 */

	public void print() {
		System.out.println("m_densityFactor = " + merge_densityFactor);

		System.out.println("m_mass = " + merge_mass);

		System.out.println("m_dist1 = " + merge_dist1);

		System.out.println("m_distfac = " + merge_distfac);

		System.out.println("m_dist2 = " + merge_dist2);

	}

	private int red(int rgb) {

		return (rgb & 0xff0000) >> 16;

	}

	private int green(int rgb) {

		return (rgb & 0x00ff00) >> 8;

	}

	private int blue(int rgb) {

		return rgb & 0xff;

	}

	private int rgb(int red, int green, int blue) {

		return blue + (green << 8) + (red << 16);

	}

	/**
	 * 
	 * Discard boxes that do not appear to contain text
	 */

	private LinkedList<TextRegion> discardNonText(LinkedList<TextRegion> boxes,
			int[][] contrast) {

		int i = 0;

		while (i < boxes.size()) {

			int numberOfStems = 0;

			TextRegion thisBox = (TextRegion) boxes.get(i);

			// Count the stems in this box

			if (thisBox.vstart() != thisBox.vend()) {

				for (int a = thisBox.hstart() + 1; a < thisBox.hend() - 1; a++) {

					int thisStemHeight = 0;

					for (int b = thisBox.vstart() + 1; b < thisBox.vend() - 1; b++)

						if ((contrast[a][b] != 0

						|| contrast[a - 1][b] != 0

						|| contrast[a + 1][b] != 0)

						&& (contrast[a][b - 1] != 0

						|| contrast[a - 1][b - 1] != 0

						|| contrast[a + 1][b - 1] != 0)

						&& (contrast[a][b + 1] != 0

						|| contrast[a - 1][b + 1] != 0

						|| contrast[a + 1][b + 1] != 0))

							thisStemHeight++;

					// a stem must cover at least 70% of a vertical line

					if ((100 * thisStemHeight) / thisBox.height() > 70)

						numberOfStems++;

				}

			}

			if (thisBox.area() < 50

					|| thisBox.aspect() > .2

					|| thisBox.height() < 5

					|| thisBox.width() < 20

					// expect at least one stem for every <height> of <width>

					|| numberOfStems < thisBox.width() / thisBox.height())

				boxes.remove(i--);

			i++;

		}

		return (boxes);

	}

	/**
	 * 
	 * Shrink each box as much as possible
	 */

	private LinkedList<TextRegion> shrink(LinkedList<TextRegion> boxes,
			int[][] contrast) {

		int i = 0;

		while (i < boxes.size()) {

			TextRegion thisBox = (TextRegion) boxes.get(i);

			if (thisBox.hstart() != thisBox.hend()

					&& thisBox.vstart() != thisBox.vend()) {

				int total = 0;

				for (int a = thisBox.hstart(); a < thisBox.hend(); a++)

					for (int b = thisBox.vstart(); b < thisBox.vend(); b++)

						total += contrast[a][b];

				double averagex = total / thisBox.height();

				double averagey = total / thisBox.width();

				int newx1 = thisBox.hstart();

				int newx2 = thisBox.hend();

				int newy1 = thisBox.vstart();

				int newy2 = thisBox.vend();

				boolean moved = true;

				while (newx1 < newx2 && moved) {

					moved = false;

					int t1 = 0, t2 = 0;

					for (int b = thisBox.vstart(); b < thisBox.vend(); b++) {

						t1 += contrast[newx1][b];

						t2 += contrast[newx2][b];

					}

					if (t1 < averagey) {

						newx1++;

						moved = true;

					}

					if (t2 < averagey) {

						newx2--;

						moved = true;

					}

				}

				moved = true;

				while (newy1 < newy2 && moved) {

					moved = false;

					int t1 = 0, t2 = 0;

					for (int a = thisBox.hstart(); a < thisBox.hend(); a++) {

						t1 += contrast[a][newy1];

						t2 += contrast[a][newy2];

					}

					if (t1 < averagex) {

						newy1++;

						moved = true;

					}

					if (t2 < averagex) {

						newy2--;

						moved = true;

					}

				}

				thisBox.sethstart(newx1);

				thisBox.sethend(newx2);

				thisBox.setvstart(newy1);

				thisBox.setvend(newy2);

			}

			i++;

		}

		return (boxes);

	}

	private LinkedList<TextRegion> merge(LinkedList<TextRegion> boxes) {

		boolean change = true;

		while (change == true) {

			change = false;

			int i = 0;

			while (i < boxes.size()) {

				int j = 0;

				while (i < boxes.size() && j < boxes.size()) {

					if (i != j) {

						TextRegion thisBox = (TextRegion) boxes.get(i);

						TextRegion thatBox = (TextRegion) boxes.get(j);

						change = merge(thisBox, thatBox);

						if (change) {

							boxes.set(i, thisBox);

							boxes.remove(j);

							j--;

						}

					}

					j++;

				}

				i++;

			}

		}

		return (boxes);

	}

	private boolean merge(TextRegion thisBox, TextRegion thatBox) {

		int mergex1 = Math.min(thisBox.hstart(), thatBox.hstart());

		int mergex2 = Math.max(thisBox.hend(), thatBox.hend());

		int mergey1 = Math.min(thisBox.vstart(), thatBox.vstart());

		int mergey2 = Math.max(thisBox.vend(), thatBox.vend());

		double mergemass = thisBox.getMass() + thatBox.getMass();

		double mergedensity = mergemass

				/ ((mergex2 - mergex1) * (mergey2 - mergey1));

		double mergeaspect

		= ((double) mergey2 - mergey1) / ((double) mergex2 - mergex1);

		double reasonsToMerge = 0.00;

		if (mergedensity > merge_densityFactor * thisBox.density())

			reasonsToMerge++;

		if (mergedensity > merge_densityFactor * thatBox.density())

			reasonsToMerge++;

		if (mergeaspect < thisBox.aspect())

			reasonsToMerge++;

		if (mergeaspect < thatBox.aspect())

			reasonsToMerge++;

		if (thisBox.getMass() > merge_mass && thatBox.getMass() > merge_mass)

			reasonsToMerge++;

		int maxboxwidth = Math.max(thisBox.width(), thatBox.width());

		if (Math.abs(thisBox.vstart() - thatBox.vstart()) < merge_dist1

				&& Math.abs(thisBox.vend() - thatBox.vstart()) < merge_dist1

				&& (Math.abs(thisBox.hstart() - thatBox.hend()) < merge_distfac
						* maxboxwidth

						|| Math.abs(thisBox.hend() - thatBox.hstart())

						< merge_distfac * maxboxwidth))

			reasonsToMerge++;

		if ((Math.abs(thisBox.vstart() - thatBox.vstart()) < merge_dist2

				|| Math.abs(thisBox.vend() - thatBox.vend()) < merge_dist2)

				&& (Math.abs(thisBox.hstart() - thatBox.hend()) < merge_distfac
						* maxboxwidth

						|| Math.abs(thisBox.hend() - thatBox.hstart())

						< merge_distfac * maxboxwidth))

			reasonsToMerge++;

		if (reasonsToMerge > 3) { // 7 reasons max

			thisBox.sethstart(mergex1);

			thisBox.sethend(mergex2);

			thisBox.setvstart(mergey1);

			thisBox.setvend(mergey2);

			thisBox.setMass(mergemass);

			return true;

		}

		return false;

	}

	private int[][] getContrast() {

		// Find pixels that stand out from the background

		int[][] contrast = new int[image.getWidth()][image.getHeight()];

		int[][] temp = new int[image.getWidth()][image.getHeight()];

		for (int i = 2; i < image.getWidth() - 2; i++)

			for (int j = 2; j < image.getHeight() - 2; j++) {

				int thisPixel = image.getRGB(i, j);

				int left = image.getRGB(i - 1, j);

				int left2 = image.getRGB(i - 2, j);

				int right = image.getRGB(i + 1, j);

				int right2 = image.getRGB(i + 2, j);

				int up = image.getRGB(i, j - 1);

				int down = image.getRGB(i, j + 1);

				int t1 = 60; // thresholds

				int t2 = 80;

				if (Math.abs(blue(thisPixel) - blue(right)) > t1

						|| Math.abs(blue(thisPixel) - blue(left)) > t1

						|| Math.abs(blue(thisPixel) - blue(down)) > t1

						|| Math.abs(blue(thisPixel) - blue(up)) > t1

						|| Math.abs(blue(thisPixel) - blue(right2)) > t2

						|| Math.abs(blue(thisPixel) - blue(left2)) > t2

						|| Math.abs(green(thisPixel) - green(right)) > t1

						|| Math.abs(green(thisPixel) - green(left)) > t1

						|| Math.abs(green(thisPixel) - green(down)) > t1

						|| Math.abs(green(thisPixel) - green(up)) > t1

						|| Math.abs(green(thisPixel) - green(right2)) > t2

						|| Math.abs(green(thisPixel) - green(left2)) > t2

						|| Math.abs(red(thisPixel) - red(right)) > t1

						|| Math.abs(red(thisPixel) - red(left)) > t1

						|| Math.abs(red(thisPixel) - red(down)) > t1

						|| Math.abs(red(thisPixel) - red(up)) > t1

						|| Math.abs(red(thisPixel) - red(right2)) > t2

						|| Math.abs(red(thisPixel) - red(left2)) > t2)

					temp[i][j] = 1;

			}

		// Look for areas of contrast that extend vertically and horizontally

		// but not too far, to eliminate long straight lines (e.g. borders)

		for (int j = 2; j < image.getHeight() - 2; j++)

			for (int i = 2; i < image.getWidth() - 2; i++)

				if (temp[i][j] == 1) {

					int width = 0;

					int height = 0;

					for (int k = 0; i + k < image.getWidth() - 2 && i - k > 2 &&

							(temp[i + k][j] == 1 || temp[i - k][j] == 1)
							&& width++ < 100; k++)

						;

					for (int k = 0;

							j + k < image.getHeight() - 2

							&& j - k > 2

							&& (temp[i][j + k] == 1 || temp[i][j - k] == 1)

							&& height++ < 100;

							k++)

						;

					int totalOnLine = 0;

					for (int k = Math.max(2, i - 40);

							k < Math.min(image.getWidth() - 2, i + 40);

							k++)

						totalOnLine += temp[k][j];

					if (totalOnLine > 7 && width < 100 && height < 100)

						contrast[i][j] = 1;

				}

		return contrast;

	}

	/**
	 * 
	 * Looks for areas of text in an image.
	 * 
	 * @return a LinkedList<TextRegion> of boxes that are likely to contain
	 *         text.
	 */

	public LinkedList<TextRegion> getTextBoxes() {

		LinkedList<TextRegion> boxes = new LinkedList<TextRegion>();

		int[][] contrast = getContrast();

		BufferedImage contrastjpg = new BufferedImage(image.getWidth(),

				image.getHeight(),

				BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++)
				contrastjpg.setRGB(i, j, 0xffffff * contrast[i][j]);

		int contrastOnLine[] = new int[image.getHeight()];

		for (int j = 1; j < image.getHeight() - 1; j++) {

			contrastOnLine[j] = 0;

			for (int a = 0; a < image.getWidth(); a++) {

				contrastOnLine[j] += contrast[a][j];

			}

		}

		for (int j = 1; j < image.getHeight() - 1; j++)

			contrastOnLine[j] = (contrastOnLine[j - 1]

					+ contrastOnLine[j]

							+ contrastOnLine[j + 1]) / 3;

		for (int j = 1; j < image.getHeight() - 1; j++)

			contrastOnLine[j] = (contrastOnLine[j - 1]

					+ contrastOnLine[j]

							+ contrastOnLine[j + 1]) / 3;

		int averageOnLine = 0;

		for (int j = 1; j < image.getHeight() - 1; j++)

			averageOnLine += contrastOnLine[j];

		averageOnLine /= (image.getHeight() - 2);

		boolean intext = false;

		int boxstart = 0;

		int boxaverage = 0;

		int boxlines = 0;

		for (int j = 1; j < image.getHeight() - 1; j++) {

			if (contrastOnLine[j] > averageOnLine && !intext) {

				intext = true;

				boxstart = j;

				boxaverage = contrastOnLine[j];

				boxlines = 1;

			}

			else if (contrastOnLine[j] > averageOnLine) {

				boxaverage += contrastOnLine[j];

				boxlines++;

			}

			else if (contrastOnLine[j] <= averageOnLine && intext) {

				// found vertical limits, now find horizontal.

				intext = false;

				int boxend = j;

				if (boxend - boxstart > 10) {

					// text must be higher than 10 pixels

					boxaverage /= boxlines;

					int contrastOnColumn[]

							= new int[image.getWidth()];

					for (int i = 1; i < image.getWidth() - 1; i++)

						for (int b = boxstart; b < boxend; b++)

							contrastOnColumn[i] += contrast[i][b];

					for (int i = 1; i < image.getWidth() - 1; i++)

						contrastOnColumn[i]

								= (contrastOnColumn[i - 1]

										+ contrastOnColumn[i]

												+ contrastOnColumn[i + 1]) / 3;

					for (int i = 1; i < image.getWidth() - 1; i++)

						contrastOnColumn[i]

								= (contrastOnColumn[i - 1]

										+ contrastOnColumn[i]

												+ contrastOnColumn[i + 1]) / 3;

					int averageOnColumn = 0;

					for (int i = 1; i < image.getWidth() - 1; i++)

						averageOnColumn += contrastOnColumn[i];

					averageOnColumn /= (image.getWidth() - 2);

					boolean intextx = false;

					int boxstartx = 0;

					for (int i = 1; i < image.getWidth() - 1; i++) {

						if (contrastOnColumn[i] > averageOnColumn / 2

								&& !intextx) {

							intextx = true;

							boxstartx = i;

						}

						else if (contrastOnColumn[i] <= averageOnColumn / 2

								&& intextx) {

							intextx = false;

							int boxendx = i;

							// found horizontal limits,

							// now (if necessary) shrink

							// vertical limits

							int newcount = 0;

							int tempboxstart = boxstart;

							int tempboxend = boxend;

							while (tempboxstart < boxend

									&& newcount == 0) {

								for (int a = boxstartx; a < boxendx; a++)

									newcount += contrast[a][tempboxstart];

								if (newcount < 2)

									tempboxstart++;

							}

							newcount = 0;

							while (tempboxstart < boxend && newcount == 0) {

								for (int a = boxstartx; a < boxendx; a++)

									newcount += contrast[a][tempboxend];

								if (newcount < 2)

									tempboxend--;

							}

							TextRegion thisBox

							= new TextRegion(boxstartx,

									tempboxstart,

									boxendx,

									tempboxend,

									image.getWidth(),

									image.getHeight(),

									boxaverage);

							boxes.add(thisBox);

						}

					}

				}

			}

		}

		// System.out.println( boxes.size() + " bounding boxes" );

		shrink(boxes, contrast);

		boxes = merge(boxes);

		shrink(boxes, contrast);

		// System.out.println( boxes.size() + " bounding boxes after merge" );

		boxes = discardNonText(boxes, contrast);

		// System.out.println( boxes.size() + " bounding boxes after delete" );

		return (shrink(boxes, contrast));

	}

	/**
	 * Set Image Background with a constant color
	 */

	private void setimg(BufferedImage img, int width, int height, int r, int g,
			int bl) {

		for (int a = 0; a < width; a++) {

			for (int b = 0; b < height; b++) {
				img.setRGB(a, b, rgb(r, g, bl));
			}
		}
	}

	/**
	 * Set Image Background from another image
	 */

	private void setimg(BufferedImage img, BufferedImage src, int width,
			int height, boolean mono) {
		int r, g, bl;
		for (int a = 0; a < width; a++) {

			for (int b = 0; b < height; b++) {

				int color = src.getRGB(a, b);
				if (mono == true) {
					r = g = bl = (red(color) + blue(color) + green(color)) / 3;
				} else {
					r = red(color);
					bl = blue(color);
					g = green(color);
				}

				img.setRGB(a, b, rgb(r, g, bl));
			}
		}
	}

	/**
	 * Set Image Background from part of another Image
	 */
	private void setimg(BufferedImage img, BufferedImage src, int width,
			int height, int xstart, int xend, int ystart, int yend, boolean mono) {
		int r, g, bl;
		int x = xstart;
		int y;
		for (int a = 0; x < xend; a++, x++) {
			y = ystart;
			for (int b = 0; y < yend; b++, y++) {

				int color = src.getRGB(x, y);

				if (mono == true) {
					r = g = bl = (red(color) + blue(color) + green(color)) / 3;
				} else {
					r = red(color);
					bl = blue(color);
					g = green(color);
				}
				img.setRGB(a, b, rgb(r, g, bl));
			}
		}
	}

	/**
	 * Copy Regions of text from source image to arrays of Sliced Buffered
	 * Images
	 */

	public ArrayList<RegionImage> isolateText(LinkedList<TextRegion> lbox) {

		ArrayList<RegionImage> imgs = new ArrayList<RegionImage>();
		ArrayList<TextRegion> boxes = new ArrayList<TextRegion>(lbox);

		// fill text boxes with color

		for (int i = 0; i < boxes.size(); i++) {

			TextRegion thisBox = boxes.get(i);
			int width = thisBox.width() + 50;
			int height = thisBox.height() + 50;
			BufferedImage outputx = new BufferedImage(width,

					height,

					BufferedImage.TYPE_INT_RGB);

			setimg(outputx, width, height, 255, 255, 255);// Set white
			// background

			int x1 = Math.max(1, thisBox.hstart() - 25);
			int x2 = Math.min(image.getWidth() - 2, thisBox.hend() + 25);
			int y1 = Math.max(1, thisBox.vstart() - 25);
			int y2 = Math.min(image.getHeight() - 2, thisBox.vend() + 25);

			setimg(outputx, image, outputx.getWidth(), outputx.getHeight(), x1,
					x2, y1, y2, true); // Copy current text box to this image
			// snippet
			imgs.add(new RegionImage(outputx, thisBox.density()));

		}

		return (imgs);

	}

	/**
	 * Set Red Border For text Regions- For Debugging
	 */
	public BufferedImage SetRedBorder(LinkedList<TextRegion> lbox) {

		ArrayList<TextRegion> boxes = new ArrayList<TextRegion>(lbox);

		BufferedImage outputimage

		= new BufferedImage(image.getWidth(),

				image.getHeight(),

				BufferedImage.TYPE_INT_RGB);

		setimg(outputimage, image, image.getWidth(), image.getHeight(), true); // make
		// everything
		// monochrome

		// draw red border around each text box

		int RED = 0xff0000;

		for (int i = 0; i < boxes.size(); i++) {

			TextRegion thisBox = (TextRegion) boxes.get(i);

			int x1 = Math.max(1, thisBox.hstart());

			int x2 = Math.min(image.getWidth() - 2, thisBox.hend());

			int y1 = Math.max(1, thisBox.vstart());

			int y2 = Math.min(image.getHeight() - 2, thisBox.vend());

			for (int a = x1; a < x2; a++) {

				outputimage.setRGB(a, thisBox.vstart(), RED);
				outputimage.setRGB(a, thisBox.vend(), RED);

			}

			for (int a = y1; a < y2; a++) {

				outputimage.setRGB(thisBox.hstart(), a, RED);
				outputimage.setRGB(thisBox.hend(), a, RED);

			}

		}

		return (outputimage);
	}

	public ArrayList<RegionImage> getRegionImage() {
		return isolateText(getTextBoxes());
	}
}