package org.mltestbed.util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Image
{

	public Image() throws Exception
	{
		JFrame frame = new JFrame();
		frame.setTitle("Random Pixel Dots On Image with Timer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		initComponents(frame);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{

		// create frame and components on EDT
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					new Image();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}

	private void initComponents(JFrame frame) throws Exception
	{

		final BufferedImage bi = ImageIO.read(new URL(
				"http://2.bp.blogspot.com/_KI3IRH6RxSs/S-uuLsgGJ3I/AAAAAAAAA5E/AA5mWBMLIvo/s1600/mat-black-lg.jpg"));
		final ColorPanel cPanel = new ColorPanel(bi);
		frame.add(cPanel);

		// create timer to color random pixels
		Timer timer = new Timer(2000, new AbstractAction()
		{
			int xMax = bi.getWidth(), yMax = bi.getHeight();
			Random rand = new Random();

			@Override
			public void actionPerformed(ActionEvent ae)
			{

				int x = rand.nextInt(xMax);
				int y = rand.nextInt(yMax);

				if (cPanel.drawDot(x, y))
				{
					System.out.println(
							"Drew white dot at: (" + x + "," + y + ")");
				} else
				{
					System.out.println(
							"Cant draw white dot at: (" + x + "," + y + ")");
				}

			}
		});

		timer.start();
	}
	public BufferedImage createBG(int height, int width, int colour)
	{
		BufferedImage bimg = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < width; y++)
		{
			for (int x = 0; x < height; x++)
			{
				int value = colour << 16 | colour << 8 | colour;
				bimg.setRGB(x, y, value);
			}
		}
		return bimg;
	}
	public void showImage(int xPoint, int yPoint, int[][] pixelArray) throws IOException
	{
		int width = pixelArray.length;
		int height = pixelArray[0].length;
		// Two dim array conversion to a bufferedImage
		BufferedImage bimg = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < width; y++)
		{
			for (int x = 0; x < height; x++)
			{

				int tempValue = pixelArray[y][x];// (pixelArray[y][x] == 1) ? 255 : 0;
				int value = tempValue << 16 | tempValue << 8 | tempValue;
				bimg.setRGB(x, y, value);

			}
		}

		JFrame canvas = new JFrame();
		canvas.setSize(bimg.getWidth(), bimg.getHeight());
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setTitle("Contour");
		Container pane = canvas.getContentPane();
		ColorPanel panel = new ColorPanel(bimg);
		pane.add(panel);
		canvas.setVisible(true);
	}
	public static void saveToFile(BufferedImage img, String type, String fn)
			throws FileNotFoundException, IOException
	{

		File outputfile = new File(fn);
		ImageIO.write(img, type, outputfile);
	}
}

class ColorPanel extends JPanel
{

	private BufferedImage bimg;
	private Dimension dims;

	public ColorPanel(BufferedImage image)
	{
		bimg = image;
		dims = new Dimension(bimg.getWidth(), bimg.getHeight());
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(bimg, 0, 0, null);
	}

	// this method will allow the changing of image
	public void setBufferedImage(BufferedImage newImg)
	{
		bimg = newImg;
	}

	// ths method will colour a pixel white
	public boolean drawDot(int x, int y)
	{

		if (x > dims.getWidth() || y > dims.getHeight())
		{
			return false;
		}

		bimg.setRGB(x, y, 0xFFFFFFFF);// white

		repaint();
		return true;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return dims;
	}
}