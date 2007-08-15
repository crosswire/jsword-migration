/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2006
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.test.internal.osgi;

import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.CommonLogger;
import org.crosswire.common.util.Language;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterException;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.IndexStatusListener;
import org.crosswire.jsword.index.lucene.LuceneIndexManager;
import org.crosswire.jsword.index.lucene.LuceneQueryBuilder;
import org.crosswire.jsword.index.lucene.LuceneQueryDecorator;
import org.crosswire.jsword.index.lucene.LuceneSearcher;
import org.crosswire.jsword.index.query.QueryBuilder;
import org.crosswire.jsword.index.query.QueryBuilderFactory;
import org.crosswire.jsword.index.query.QueryDecorator;
import org.crosswire.jsword.index.query.QueryDecoratorFactory;
import org.crosswire.jsword.index.search.SearchRequest;
import org.crosswire.jsword.index.search.Searcher;
import org.crosswire.jsword.index.search.SearcherFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom.Document;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The main plugin class to be used in the desktop.
 * @author Phillip [phillip at paristano dot org]
 */
public class Activator implements BundleActivator {

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Running Common Tests...");
		testDefaultFilter(context);
		testCustomFilter(context);
		testCustomLogger(context);
		testCustomBookDriver(context);
        testDefaultIndex(context);
		System.out.println("Done!");
		
	}

	/**
	 * @param context
	 */
	private void testDefaultIndex(BundleContext context) {
		IndexManager manager = IndexManagerFactory.getIndexManager();
        if (manager instanceof LuceneIndexManager) {
            System.out.println("Success: Found the lucene index manager");
        } else {
            System.err.println("Error: Could not find the lucene index manager");
        }
        
        QueryBuilder builder = QueryBuilderFactory.getQueryBuilder();
        if (builder instanceof LuceneQueryBuilder) {
            System.out.println("Success: Found the lucene query builder");
        } else {
            System.err.println("Error: Could not find the lucene query builder");
        }
        
        QueryDecorator decorator = QueryDecoratorFactory.getSearchSyntax();
        if (decorator instanceof LuceneQueryDecorator) {
            System.out.println("Success: Found the lucene query decorator");
        } else {
            System.err.println("Error: Could not find the lucene query decorator");
        }
        
        try {
			Searcher searcher = SearcherFactory.createSearcher(new CustomBook());
            if (searcher instanceof LuceneSearcher) {
                System.out.println("Success: Found the lucene searcher");
            } else {
                System.err.println("Error: Could not find the lucene searcher");
            }
		} catch (InstantiationException e) {
            System.err.println("Error: Failed to create dummy searcher: " + e);
		}

	}

	/**
	 * @param context
	 */
	private void testCustomBookDriver(BundleContext context) {
		BookDriver[] drivers = Books.installed().getDrivers();
		int originalCount = drivers.length;
		
		BookDriver myDriver = new CustomBookDriver();
		Hashtable properties = new Hashtable();
		properties.put("bookdriver.id", "mydriver");
		ServiceRegistration reg = context.registerService(BookDriver.class.getName(), myDriver, properties);
		
		drivers = Books.installed().getDrivers();
		
		if (drivers.length != originalCount + 1) {
			System.err.println("Error: My book driver wasn't successfully registered: Expected " + (originalCount + 1) + " drivers available, but only found " + originalCount);
		}

		boolean myDriverFound = false;
		for (int i = 0; i < drivers.length; i++) {
			BookDriver driver = drivers[i];
			if (driver == myDriver) {
				myDriverFound = true;
				break;
			}
		}
		
		if (myDriverFound) {
			System.out.println("Success: My book driver was found.");
		} else {
			System.err.println("Error: My book driver was not found");
		}

		Book myBook = Books.installed().getBook("mybook");
		
		if (myBook == null) {
			System.err.println("Error: Could not find my book after registering my driver.");
		} else {
			System.out.println("Success: Found my book after registering my driver.");
		}
		
		myDriver = null;
		
		reg.unregister();

		drivers = Books.installed().getDrivers();
		if (drivers.length != originalCount) {
			System.err.println("Error: The number of registered book drivers did not decrease after unregistering.");
		}

		myDriverFound = false;
		for (int i = 0; i < drivers.length; i++) {
			BookDriver driver = drivers[i];
			if (driver == myDriver) {
				myDriverFound = true;
				break;
			}
		}
		
		if (myDriverFound) {
			System.err.println("Error: My book driver was found in the registry after it was unregistered.");
		} else {
			System.out.println("Success: My book driver was not found in the registry after unregistering.");
		}
		
		myBook = Books.installed().getBook("mybook");
		
		if (myBook == null) {
			System.out.println("Success: Could not find my book after unregistering my driver.");
		} else {
			System.err.println("Error: Found my book after unregistering my driver.");
		}
		
	}

	/**
	 * @param context
	 */
	private void testCustomLogger(BundleContext context) {
		final boolean[] logCheck = new boolean[] {false};
		CommonLogger myLogger = new CommonLogger() {
			public void log(Level level, String message, Throwable throwable) {
				logCheck[0] = true;
			}
		};

		Hashtable properties = new Hashtable();
		ServiceRegistration reg = context.registerService(CommonLogger.class.getName(), myLogger, properties);
		
		Logger logger = Logger.getLogger(Activator.class);
		logger.info("Testing if my logger gets hit...");
		if (logCheck[0]) {
			System.out.println("Success: My logger was successfully triggered.");
		} else {
			System.err.println("Error: My logger was not triggered through the Logger class.");
		}
		
		reg.unregister();

		logCheck[0] = false;
		
		logger.info("Testing if my logger gets hit...");
		if (logCheck[0]) {
			System.err.println("Error: My logger was triggered after it was unregistered.");
		} else {
			System.out.println("Success: My logger was not triggered after it was unregistered.");
		}
		
	}

	/**
	 * @param context 
	 * 
	 */
	private void testCustomFilter(BundleContext context) {
		Filter myFilter = new Filter() {
			public List toOSIS(Book book, Key key, String plain) throws FilterException {
				return null;
			}
			public Object clone() {
			    try
			    {
					return super.clone();
				}
			    catch (CloneNotSupportedException e)
			    {
			    	return null;
				}
			}
		};
		
		Hashtable properties = new Hashtable();
		properties.put("filter.id", "myfilter");
		ServiceRegistration reg = context.registerService(Filter.class.getName(), myFilter, properties);
		Filter retrievedFilter = FilterFactory.getFilter("myfilter");
		if (retrievedFilter == myFilter) {
			System.out.println("Success: Found my own filter in the registry.");
		} else {
			System.err.println("Error: Could not find my own filter in the registry.");
		}
		retrievedFilter = null;
		
		reg.unregister();
		
		retrievedFilter = FilterFactory.getFilter("myfilter");
		if (retrievedFilter == myFilter) {
			System.err.println("Error: Found my unregistered filter in the registry.");
		} else {
			//the filter is either null or the default filter.
			System.out.println("Success: My filter unregistered successfully.");
		}
	}

	/**
	 * @param context 
	 * 
	 */
	private void testDefaultFilter(BundleContext context) {
		Filter filter = FilterFactory.getDefaultFilter();
		if (filter == null) {
			System.err.println("Error: Missing default filter.");
		} else {
			System.out.println("Success: Default filter found.");
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
	}
	
	private static final class CustomBook implements Book {
		private static final class CustomBookMetaData implements BookMetaData {
			public String getName() {
				return "mybookmetadata";
			}

			public BookCategory getBookCategory() {
				return BookCategory.OTHER;
			}

			public BookDriver getDriver() {
				return new CustomBookDriver();
			}

			public Language getLanguage() {
				return new Language("en");
			}

			public String getInitials() {
				return "mybook";
			}

			public String getOsisID() {
				return null;
			}

			public String getFullName() {
				return null;
			}

			public boolean isSupported() {
				return false;
			}

			public boolean isEnciphered() {
				return false;
			}

			public boolean isLocked() {
				return false;
			}

			public boolean unlock(String unlockKey) {
				return false;
			}

			public String getUnlockKey() {
				return null;
			}

			public boolean isQuestionable() {
				return false;
			}

			public String getDriverName() {
				return "mybookdriver";
			}

			public boolean isLeftToRight() {
				return false;
			}

			public boolean hasFeature(FeatureType feature) {
				return false;
			}

			public URI getLibrary() {
				return null;
			}

			public void setLibrary(URI library) {
			}

			public URI getLocation() {
				return null;
			}

			public void setLocation(URI library) {
			}

			public Map getProperties() {
				return null;
			}

			public Object getProperty(String key) {
				return null;
			}

			public void putProperty(String key, Object value) {
			}

			public IndexStatus getIndexStatus() {
				return null;
			}

			public void setIndexStatus(IndexStatus status) {
			}

			public Document toOSIS() {
				return null;
			}

			public int compareTo(Object o) {
				if (o == null) {
					return 1;
				}
				if (o.getClass().equals(this.getClass())) {
					return 0;
				}
				return -1;
			}
		}
		public BookMetaData getBookMetaData() {
			return new CustomBookMetaData();
		}
		public void setBookMetaData(BookMetaData bmd) {
		}

		public BookData getData(Key key) throws BookException {
			return null;
		}
		public String getRawData(Key key) throws BookException {
			return "";
		}
		public Key find(SearchRequest request) throws BookException {
			return null;
		}
		public Key find(String request) throws BookException {
			return null;
		}
		public String getName() {
			return "mybook";
		}
		public BookCategory getBookCategory() {
			return BookCategory.OTHER;
		}
		public BookDriver getDriver() {
			return new CustomBookDriver();
		}
		public Language getLanguage() {
			return new Language("en");
		}
		public String getInitials() {
			return "mybook";
		}
		public String getOsisID() {
			return null;
		}
		public String getFullName() {
			return "my book";
		}
		public boolean isSupported() {
			return false;
		}
		public boolean isEnciphered() {
			return false;
		}
		public boolean isLocked() {
			return false;
		}
		public boolean unlock(String unlockKey) {
			return false;
		}
		public String getUnlockKey() {
			return null;
		}
		public boolean isQuestionable() {
			return false;
		}
		public String getDriverName() {
			return null;
		}
		public boolean isLeftToRight() {
			return false;
		}
		public boolean hasFeature(FeatureType feature) {
			return false;
		}
		public Map getProperties() {
			return null;
		}
		public Object getProperty(String key) {
			return null;
		}
		public IndexStatus getIndexStatus() {
			return null;
		}
		public void setIndexStatus(IndexStatus status) {
		}
		public void addIndexStatusListener(IndexStatusListener li) {
		}
		public void removeIndexStatusListener(IndexStatusListener li) {
		}
		public void activate(Lock lock) {
		}
		public void deactivate(Lock lock) {
		}
		public Key getGlobalKeyList() {
			return null;
		}
		public Key getValidKey(String name) {
			return null;
		}
		public Key getKey(String name) throws NoSuchKeyException {
			return null;
		}
		public Key createEmptyKeyList() {
			return null;
		}
		public int compareTo(Object o) {
			if (o == null) {
				return 1;
			}
			if (o.getClass().equals(this.getClass())) {
				return 0;
			}
			return -1;
		}
	    public Document toOSIS() {
	    	return null;
	    }
		/* (non-Javadoc)
		 * @see org.crosswire.jsword.book.Book#getOsisIterator(org.crosswire.jsword.passage.Key, boolean)
		 */
		public Iterator getOsisIterator(Key key, boolean allowEmpty)
				throws BookException {
			return null;
		}
		/* (non-Javadoc)
		 * @see org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage.Key)
		 */
		public String getRawText(Key key) throws BookException {
			return null;
		}
		/* (non-Javadoc)
		 * @see org.crosswire.jsword.book.Book#isWritable()
		 */
		public boolean isWritable() {
			return false;
		}
		/* (non-Javadoc)
		 * @see org.crosswire.jsword.book.Book#match(java.lang.String)
		 */
		public boolean match(String name) {
			return false;
		}
		/* (non-Javadoc)
		 * @see org.crosswire.jsword.book.Book#putProperty(java.lang.String, java.lang.Object)
		 */
		public void putProperty(String key, Object value) {
		}
		/* (non-Javadoc)
		 * @see org.crosswire.jsword.book.Book#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
		 */
		public void setAliasKey(Key alias, Key source) throws BookException {
		}
		/* (non-Javadoc)
		 * @see org.crosswire.jsword.book.Book#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
		 */
		public void setRawText(Key key, String rawData) throws BookException {
		}
	}
	
	private static final class CustomBookDriver implements BookDriver {
		private static final Book MY_BOOK = new CustomBook();
		public Book getFirstBook() {
			return MY_BOOK;
		}
		public Book[] getBooks() {
			return new Book[] {MY_BOOK};
		}

		public boolean isWritable() {
			return false;
		}

		public Book create(Book source) throws BookException {
			return null;
		}

		public boolean isDeletable(Book dead) {
			return false;
		}

		public void delete(Book dead) throws BookException {
		}

		public String getDriverName() {
			return "mybookdriver";
		}
	}
}
