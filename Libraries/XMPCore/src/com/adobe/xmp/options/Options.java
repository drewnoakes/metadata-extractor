// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.options;

import java.util.HashMap;
import java.util.Map;

import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;

/**
 * The base class for a collection of 32 flag bits. Individual flags are defined as enum value bit
 * masks. Inheriting classes add convenience accessor methods.
 * 
 * @since 24.01.2006
 */
public abstract class Options
{
	/** the internal int containing all options */
	private int options = 0;
	/** a map containing the bit names */
	private Map optionNames = null;


	/**
	 * The default constructor.
	 */
	public Options()
	{
		// EMTPY
	}


	/**
	 * Constructor with the options bit mask. 
	 * 
	 * @param options the options bit mask
	 * @throws XMPException If the options are not correct
	 */
	public Options(int options) throws XMPException
	{
		assertOptionsValid(options);
		setOptions(options);
	}

	
	/**
	 * Resets the options.
	 */
	public void clear()
	{
		options = 0;
	}
	
	
	/**
	 * @param optionBits an option bitmask
	 * @return Returns true, if this object is equal to the given options. 
	 */
	public boolean isExactly(int optionBits)
	{
		return getOptions() == optionBits;
	}


	/**
	 * @param optionBits an option bitmask
	 * @return Returns true, if this object contains all given options. 
	 */
	public boolean containsAllOptions(int optionBits)
	{
		return (getOptions() & optionBits) == optionBits;
	}


	/**
	 * @param optionBits an option bitmask
	 * @return Returns true, if this object contain at least one of the given options. 
	 */
	public boolean containsOneOf(int optionBits)
	{
		return ((getOptions()) & optionBits) != 0;
	}	

	
	/**
	 * @param optionBit the binary bit or bits that are requested
	 * @return Returns if <emp>all</emp> of the requested bits are set or not.
	 */
	protected boolean getOption(int optionBit)
	{
		return (options & optionBit) != 0;
	}


	/**
	 * @param optionBits the binary bit or bits that shall be set to the given value
	 * @param value the boolean value to set
	 */
	public void setOption(int optionBits, boolean value)
	{
		options = value ? options | optionBits : options & ~optionBits;
	}

	
	/**
	 * Is friendly to access it during the tests.
	 * @return Returns the options.
	 */
	public int getOptions()
	{
		return options;
	}


	/**
	 * @param options The options to set.
	 * @throws XMPException 
	 */
	public void setOptions(int options) throws XMPException
	{
		assertOptionsValid(options);
		this.options = options;
	}
	
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj)
	{
		return getOptions() == ((Options) obj).getOptions();
	}
	
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return getOptions();
	}
	
	
	/**
	 * Creates a human readable string from the set options. <em>Note:</em> This method is quite
	 * expensive and should only be used within tests or as
	 * @return Returns a String listing all options that are set to <code>true</code> by their name,
	 * like &quot;option1 | option4&quot;.
	 */
	public String getOptionsString()
	{
		if (options != 0)
		{	
			StringBuffer sb = new StringBuffer();
			int theBits = options;
			while (theBits != 0)
			{
				int oneLessBit = theBits & (theBits - 1); // clear rightmost one bit
				int singleBit = theBits ^ oneLessBit;
				String bitName = getOptionName(singleBit);
				sb.append(bitName);
				if (oneLessBit != 0)
				{	
					sb.append(" | ");
				}	
				theBits = oneLessBit;
			}
			return sb.toString();
		}	
		else
		{
			return "<none>";
		}
	}

	
	/**
	 * @return Returns the options as hex bitmask. 
	 */
	public String toString()
	{
		return "0x" + Integer.toHexString(options);
	}
	
	
	/**
	 * To be implemeted by inheritants.
	 * @return Returns a bit mask where all valid option bits are set.
	 */
	protected abstract int getValidOptions();
	
	
	/**
	 * To be implemeted by inheritants.
	 * @param option a single, valid option bit.
	 * @return Returns a human readable name for an option bit.
	 */
	protected abstract String defineOptionName(int option);
	
	
	/**
	 * The inheriting option class can do additional checks on the options.
	 * <em>Note:</em> For performance reasons this method is only called 
	 * when setting bitmasks directly.
	 * When get- and set-methods are used, this method must be called manually,
	 * normally only when the Options-object has been created from a client
	 * (it has to be made public therefore).
	 * 
	 * @param options the bitmask to check.
	 * @throws XMPException Thrown if the options are not consistent.
	 */
	protected void assertConsistency(int options) throws XMPException
	{
		// empty, no checks
	}
	
	
	/**
	 * Checks options before they are set.
	 * First it is checked if only defined options are used,
	 * second the additional {@link Options#assertConsistency(int)}-method is called.
	 *  
	 * @param options the options to check
	 * @throws XMPException Thrown if the options are invalid.
	 */
	private void assertOptionsValid(int options) throws XMPException
	{
		int invalidOptions = options & ~getValidOptions();
		if (invalidOptions == 0)
		{
			assertConsistency(options);
		}
		else
		{
			throw new XMPException("The option bit(s) 0x" + Integer.toHexString(invalidOptions)
					+ " are invalid!", XMPError.BADOPTIONS);
		}
	}
	
	
	
	/**
	 * Looks up or asks the inherited class for the name of an option bit.
	 * Its save that there is only one valid option handed into the method.
	 * @param option a single option bit
	 * @return Returns the option name or undefined.
	 */
	private String getOptionName(int option)
	{
		Map optionsNames = procureOptionNames();
		
		Integer key = new Integer(option);
		String result = (String) optionsNames.get(key);
		if (result == null)
		{
			result = defineOptionName(option);
			if (result != null)
			{
				optionsNames.put(key, result); 
			}
			else
			{
				result = "<option name not defined>";
			}
		}
		
		return result;
	}


	/**
	 * @return Returns the optionNames map and creates it if required.
	 */
	private Map procureOptionNames()
	{
		if (optionNames == null)
		{	
			optionNames = new HashMap();
		}
		return optionNames;
	}
}
