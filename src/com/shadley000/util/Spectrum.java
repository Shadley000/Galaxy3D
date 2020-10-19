package com.shadley000.util;

public class Spectrum {

	final public static int RADIO = 0;
	final public static int MICROWAVE = 1;
	final public static int INFARED = 2;
	final public static int VISIBLE = 3;
	final public static int ULTRAVIOLET = 4;
	final public static int XRAY = 5;
	final public static int GAMMARAY = 6;

	double spectrum[] = new double[7];

	public Spectrum() {
		for (int i = 0; i < spectrum.length; i++)
			spectrum[i] = 0.0;
	}

	public double getValue(int frequency) {
		return spectrum[frequency];
	}
	
	public Spectrum scale( double factor)
	{
		Spectrum scaledSpectrum = new Spectrum();
		for (int i = 0; i < spectrum.length; i++)
			scaledSpectrum.spectrum[i] = factor*spectrum[i];
		return scaledSpectrum;
	}
	
	public Spectrum divide( double factor)
	{
		Spectrum scaledSpectrum = new Spectrum();
		for (int i = 0; i < spectrum.length; i++)
			scaledSpectrum.spectrum[i] = spectrum[i]/factor;
		return scaledSpectrum;
	}
	
	public Spectrum scale( Spectrum s)
	{
		Spectrum scaledSpectrum = new Spectrum();
		for (int i = 0; i < spectrum.length; i++)
			scaledSpectrum.spectrum[i] = s.getValue(i)*spectrum[i];
		return scaledSpectrum;
	}
	
	public Spectrum add( Spectrum s)
	{
		Spectrum sumSpectrum = new Spectrum();
		for (int i = 0; i < spectrum.length; i++)
			sumSpectrum.spectrum[i] = spectrum[i]+s.getValue(i);
		return sumSpectrum;
	}
}
