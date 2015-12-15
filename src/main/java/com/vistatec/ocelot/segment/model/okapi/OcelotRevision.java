package com.vistatec.ocelot.segment.model.okapi;

import java.text.ParseException;
import java.util.Date;

import net.sf.okapi.lib.xliff2.changeTracking.Item;
import net.sf.okapi.lib.xliff2.changeTracking.Revision;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * This class represents a revision of an Okapi Fragment.
 */
public class OcelotRevision {

	/** pattern for the <code>datetime</code> field. */
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";

	/** Date formatter. */
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
	        DATETIME_PATTERN);

	/** the version of this revision. */
	private String version;

//	/** The author of this revision. */
//	private String author;

	/** The date and time when this revision has been created. */
	private String datetime;

	/** The display text for this revision. */
	private String text;

//	/** The fragment for this revision. */
//	private Fragment fragment;

	public OcelotRevision() {
	    // TODO Auto-generated constructor stub
    }
	
	public OcelotRevision(Revision okapiRevision, Item item) {
		
		version = okapiRevision.getVersion();
//		author = okapiRevision.getAuthor();
		datetime = okapiRevision.getDatetime();
		text = item.getText();
//		fragment = item.getFragment();
    }
	
	/**
	 * Gets the version of this revision.
	 * 
	 * @return the version of this revision.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version of this revision.
	 * 
	 * @param version
	 *            the version of this revision.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

//	/**
//	 * Gets the author of this revision.
//	 * 
//	 * @return the author of this revision.
//	 */
//	public String getAuthor() {
//		return author;
//	}
//
//	/**
//	 * Sets the author of this revision.
//	 * 
//	 * @param author
//	 *            the author of this revision.
//	 */
//	public void setAuthor(String author) {
//		this.author = author;
//	}

	/**
	 * Gets the date and time when the revision has been created .
	 * 
	 * @return the date and time formatted as a String using the following
	 *         pattern <code>yyyy-MM-dd'T'HH:mm:ssX</code>.
	 */
	public String getDatetimeAsString() {
		return datetime;
	}

	/**
	 * Sets the date and time when the revision has been created .
	 * 
	 * @param datetime
	 *            the date and time formatted as a String using the following
	 *            pattern <code>yyyy-MM-dd'T'HH:mm:ssX</code>..
	 */
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	/**
	 * Gets the date and time when the revision has been created.
	 * 
	 * @return the date and time when the revision has been created.
	 */
	public Date getDatetimeAsDate() {
		Date date = null;
		if (datetime != null) {
			try {
				date = dateFormatter.parse(datetime);
			} catch (ParseException e) {
				// TODO handle exception
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * Sets the date and time when the revision has been created.
	 * 
	 * @param date
	 *            the date and time when the revision has been created.
	 */
	public void setDatetime(Date date) {

		if (date != null) {
			datetime = dateFormatter.format(date);
		}
	}

	/**
	 * Gets the revision text.
	 * 
	 * @return the revision text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the revision text.
	 * 
	 * @param text
	 *            the revision text.
	 */
	public void setText(String text) {
		this.text = text;
	}

//	/**
//	 * Gets the fragment for this revision.
//	 * 
//	 * @return the fragment for this revision.
//	 */
//	public Fragment getFragment() {
//		return fragment;
//	}
//
//	/**
//	 * Sets the fragment for this revision.
//	 * 
//	 * @param fragment
//	 *            the fragment for this revision.
//	 */
//	public void setFragment(Fragment fragment) {
//		this.fragment = fragment;
//	}

//	public static void main(String[] args) {
//
//		SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_PATTERN);
//		System.out.println(formatter.format(new Date()));
//		Calendar calDate = GregorianCalendar.getInstance(TimeZone
//		        .getTimeZone("GMT-5:00"));
//		calDate.set(GregorianCalendar.YEAR, 1994);
//		calDate.set(GregorianCalendar.MONTH, GregorianCalendar.NOVEMBER);
//		calDate.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 5);
//		calDate.set(GregorianCalendar.HOUR_OF_DAY, 8);
//		calDate.set(GregorianCalendar.MINUTE, 15);
//		calDate.set(GregorianCalendar.SECOND, 30);
//		System.out.println(formatter.format(calDate.getTime()));
//	}
}
