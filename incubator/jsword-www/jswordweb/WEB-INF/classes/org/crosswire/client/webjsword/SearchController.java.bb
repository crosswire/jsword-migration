/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.crosswire.client.webjsword;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.xml.transform.TransformerException;
import org.apache.commons.httpclient.NameValuePair;
import org.crosswire.bibledesktop.book.ParallelBookPicker;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookComparators;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatusListener;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.RocketPassage;
import org.crosswire.jsword.util.ConverterFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Yiguang Hu
 */
public class SearchController {
    private String book;
    private int chapter=1;
    private int verse=1;
    private String searchkey;
    private String startbook="gen";
    private String endbook="rev";
    private int startchapter=1;
    private int endchapter=20;
    private int startverse=1;
    private int endverse=2;

    public String getEndbook() {
        return endbook;
    }

    public void setEndbook(String endbook) {
        this.endbook = endbook;
    }

    public int getEndchapter() {
        return endchapter;
    }

    public void setEndchapter(int endchapter) {
        this.endchapter = endchapter;
    }

    public int getEndverse() {
        return endverse;
    }

    public void setEndverse(int endverse) {
        this.endverse = endverse;
    }

    public String getStartbook() {
        return startbook;
    }

    public void setStartbook(String startbook) {
        this.startbook = startbook;
    }

    public int getStartchapter() {
        return startchapter;
    }

    public void setStartchapter(int startchapter) {
        this.startchapter = startchapter;
    }

    public int getStartverse() {
        return startverse;
    }

    public void setStartverse(int startverse) {
        this.startverse = startverse;
    }
    public String getSearchkey() {
        return searchkey;
    }

    public void setSearchkey(String searchkey) {
        this.searchkey = searchkey;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getVerse() {
        return verse;
    }

    public void setVerse(int verse) {
        this.verse = verse;
    }
    private String bibletext;

    public String getBibletext() {
        return bibletext;
    }

    public void setBibletext(String bibletext) {
        this.bibletext = bibletext;
    }
    private static final Books books=Books.installed();
    private ParallelBookPicker biblePicker;
    private transient Book[] selected;
        private Key key;

    public SearchController(){
                // search() and version() rely on this returning only Books indexed by verses
        biblePicker = new ParallelBookPicker(BookFilters.getBibles(), BookComparators.getInitialComparator());
    //    biblePicker.addBookListener(this);
        selected = biblePicker.getBooks();
        if (selected.length > 0)
        {

            key = selected[0].createEmptyKeyList();
        }
        else
        {
            // The application has started and there are no installed bibles.
            // Should always get a key from book, unless we need a PassageTally
            // But here we don't have a book yet.
            key = new RocketPassage();
        }

    }
    
    /**
     * What are the currently selected Books?
     */
    public Book[] getBooks()
    {
        return (Book[]) selected.clone();
    }

    /**
     * What is the first currently selected book?
     */
    public Book getFirstBook()
    {
        return selected != null && selected.length > 0 ? selected[0] : null;
    }
    public Book getLastBook()
    {                       
        return selected != null && selected.length > 0 ? selected[selected.length-1] : null;
    }
               
    public List<SelectItem> getBooknames(){
        ArrayList<SelectItem> bk=new ArrayList<SelectItem>();
        List<Book> bks=books.getBooks(BookFilters.getOnlyBibles());
        for (Book b:bks)
            bk.add(new SelectItem(b.getName(),b.getName()));    
        bk.add(new SelectItem("KJV","KJV"));
        return bk;
    }
    private String bible="KJV";

    public String getBible() {
        return bible;
    }

    public void setBible(String bible) {
        this.bible = bible;
    }
    
public String readplaintext() throws BookException, NoSuchKeyException
      {
          
          Book bibleb = books.getBook(bible);
          
          Key key = bibleb.getKey(book+" "+chapter+" "+verse); //$NON-NLS-1$
          BookData data = new BookData(bibleb, key);
          
          bibletext= OSISUtil.getCanonicalText(data.getOsisFragment());  
          return "success";
}
  String styledresults="";

    public String getStyledresults() {
        return styledresults;
    }

    public void setStyledresults(String styledresults) {
        this.styledresults = styledresults;
    }
public String searchandshowstyled() throws BookException, NoSuchKeyException, TransformerException, SAXException
      {
          
          Book bibleb = books.getBook(bible);
          
          Key key =getSearchresultKey();// bibleb.getKey(book+" "+chapter+" "+verse); //$NON-NLS-1$
          
          BookData data = new BookData(bibleb, key);
          SAXEventProvider osissep=data.getSAXEventProvider();
          Converter styler=ConverterFactory.getConverter(); 
          TransformingSAXEventProvider htmlsep=(TransformingSAXEventProvider)styler.convert(osissep);
          BookMetaData bmd=bibleb.getBookMetaData();
          boolean direction=bmd.isLeftToRight();
          htmlsep.setParameter("direction", direction?"ltr":"rtl");
          styledresults=XMLUtil.writeToString(htmlsep);
         // bibletext= OSISUtil.getCanonicalText(data.getOsisFragment());  
          return "success";
}
private Key getSearchresultKey(){
  
    Book bibleb = books.getBook(bible); 
    String query=searchkey;
    Key key=null;
    String ranges=startbook+" "+startchapter+":"+startverse+"-"+endbook+" "+endchapter+":"+endverse;        
 try{ 
    if (searchkey!=null&& !searchkey.equals("")){
    
        query="+["+ranges+"] "+searchkey;
     key=bibleb.find(query);
      }else {
       key=bibleb.getKey(ranges);
}   }catch (Exception e){
    e.printStackTrace();
}
    
    return key;
}
private List<NameValuePair> searchresults=new ArrayList<NameValuePair>();
public String searchandshow() throws NoSuchKeyException, BookException, TransformerException, SAXException{
  try{
      Key key=getSearchresultKey();
          Book bibleb = books.getBook(bible); 
//System.out.println("q:"+qury+  " "+searchkey);
//System.out.println(searchkey);       
//Key key = bibleb.find(query); //$NON-NLS-1$
          //Key key = bibleb.find(ranges); //$NON-NLS-1$
          String path="xsl/cswing/simple.xsl";
          URL xslurl=ResourceUtil.getResource(path);
          Iterator rangeIter=((Passage)key).rangeIterator(RestrictionType.CHAPTER);
          searchresults.clear();
          while(rangeIter.hasNext()){
          Key range=(Key)rangeIter.next();
          
              BookData data = new BookData(bibleb, range);
              
          SAXEventProvider osissep=data.getSAXEventProvider();
          //Converter styler=ConverterFactory.getConverter(); 
         // TransformingSAXEventProvider htmlsep=new TransformingSAXEventProvider(NetUtil.toURI(xslurl),osissep);
          
          //BookMetaData bmd=bibleb.getBookMetaData();
          //boolean direction=bmd.isLeftToRight();
          //htmlsep.setParameter("direction", direction?"ltr":"rtl");
          NameValuePair nm=new NameValuePair();
          nm.setValue(OSISUtil.getCanonicalText(data.getOsisFragment()));
        //  nm.setValue(XMLUtil.writeToString(htmlsep));
          
          nm.setName(range.getName());
          searchresults.add(nm);
          }
  }catch (Exception e){
      //System.out.println("xxxxx");
      e.printStackTrace();
  }
          // bibletext= OSISUtil.getCanonicalText(data.getOsisFragment());  
          return "success";
}
public List<NameValuePair> getSearchresults(){
    return searchresults;
}
public void buildIndex(){
 // An installer knows how to install books
         Installer installer = null;
         
         InstallManager imanager = new InstallManager();
 
        // Ask the Install Manager for a map of all known module sites
         Map installers = imanager.getInstallers();
 
         // Get all the installers one after the other
         Iterator iter = installers.keySet().iterator();
IndexManager indexManager = IndexManagerFactory.getIndexManager();

       while (iter.hasNext())
         {
             String name = (String) iter.next();
             installer = (Installer) installers.get(name);
             try{
             installer.reloadBookList();
                List availableBooks = installer.getBooks();
 
          Book book = (Book) availableBooks.get(0);
 

if (!indexManager.isIndexed(book))
{
    indexManager.scheduleIndexCreation(book);
}
             }catch (Exception e){

                 e.printStackTrace();
             }
         }          
}
public void installBook(String booktoinstall){
 
         // An installer knows how to install books
         Installer installer = null;
         
         InstallManager imanager = new InstallManager();
 
        // Ask the Install Manager for a map of all known module sites
         Map installers = imanager.getInstallers();
 
         // Get all the installers one after the other
         Iterator iter = installers.keySet().iterator();
         while (iter.hasNext())
         {
             String name = (String) iter.next();
             installer = (Installer) installers.get(name);
             try{
             installer.reloadBookList();
             }catch (Exception e){
                 System.out.println("exception to load book for "+name);
                 e.printStackTrace();
             }
         }
 
         // If we know the name of the installer we can get it directly
         installer = imanager.getInstaller("CrossWire"); //$NON-NLS-1$
 
         // Now we can get the list of books
         try
         {
             installer.reloadBookList();
         }
         catch (InstallException e)
         {
             e.printStackTrace();
         }
 
         // Get a list of all the available books
         List availableBooks = installer.getBooks();
 
         // get some available books. In this case, just one book.
         availableBooks = installer.getBooks(new MyBookFilter(booktoinstall)); //$NON-NLS-1$
 
         Book book = (Book) availableBooks.get(0);
 
         if (book != null)
         {
             System.out.println("Book " + book.getInitials() + " is available"); //$NON-NLS-1$ //$NON-NLS-2$
 
             // Delete the book, if present
             // At the moment, JSword will not re-install. Later it will, if the remote version is greater.
             try
             {
                 if (Books.installed().getBook(booktoinstall) != null) //$NON-NLS-1$
                 {
                     // Make the book unavailable.
                     // This is normally done via listeners.
                     Books.installed().removeBook(book);
 
                     // Actually do the delete
                     // This should be a call on installer.
                     book.getDriver().delete(book);
                 }
             }
             catch (BookException e1)
             {
                 e1.printStackTrace();
             }
 
             try
             {
                 // Now install it. Note this is a background task.
                 installer.install(book);
             }
             catch (InstallException e)
             {
                 e.printStackTrace();
             }
         }
     }

static class MyBookFilter implements BookFilter
     {
         public MyBookFilter(String bookName)
         {
             name = bookName;
         }
 
         public boolean test(Book bk)
         {
             return bk.getInitials().equals(name);
         }
 
         private String name;
     }
 
     /**
      * A simple BooksListener that actually does nothing.
      */
     static class MyBooksListener implements BooksListener
     {
         /* (non-Javadoc)
          * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
          */
         public void bookAdded(BooksEvent ev)
         {
         }
 
         /* (non-Javadoc)
          * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
          */
         public void bookRemoved(BooksEvent ev)
         {
         }
     }
     public static void main(String[] args){
         SearchController sc=new SearchController();
sc.buildIndex();
    //     sc.installBook("ESV");
     }
}
