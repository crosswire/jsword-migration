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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: simple.xsl 1767 2008-02-17 14:25:49Z dmsmith $
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
// Define how the OSIS document that is returned from JSword is styled.
var stylesheet = "iBD.xsl";

// Prevent the server from being hammered.
var verseLimit = 100;
var verseStart= 0;
var total= 0;

/**
 * Prepare the page for use
 */
function init()
{
  // Use a Google styled "Loading" message in the upper right corner
  dwr.util.useLoadingMessage();

  // Display the current SWORD path as a diagnostic.
  //JSword.getSwordPath(loadDiagnostic);

  // Populate the books dropdown.
  // The last argument is an asynchronous callback
  JSword.getInstalledBooks("bookCategory=Bible", loadBooks);

  // Constrain the display area to be within the boundary of the window.
  window.onresize = ibdResize;
  ibdResize();
}

/*
 * Resize the height of the display area.
 */
function ibdResize()
{
  var top             = $("searchBox");
  var bottom          = $("display");
  var offset          = top.offsetTop + top.offsetHeight + 1;
  var windowHeight    = document.body.clientHeight;
  var newHeight       = windowHeight - offset;
  // I tried pixels but it does not work for IE.
  bottom.style.height = (newHeight / windowHeight) * 100 + "%";
}

/**
 * Load the list of known Books
 */
function loadBooks(data)
{
  // Empty the list.
  dwr.util.removeAllOptions("books");
  // Then populate it with data, using column "0" as the key and "1" as the display value
  // Use "0", "0" to only show the books "initials"
  dwr.util.addOptions("books", data, "0", "0");
}

/**
 * Called when book data has been fetched
 */
function loadDisplay(data)
{
  var processor = new XSLTProcessor();
  var xslDoc    = Sarissa.getDomDocument();
  xslDoc.async  = false;
  xslDoc.load(stylesheet);
  processor.importStylesheet(xslDoc);
  var parser    = new DOMParser();
  var dom       = parser.parseFromString(data, "text/xml");
  Sarissa.updateContentFromNode(dom, $("display"), processor);
}
function displayTotal(data)
{
	total=data;
  dwr.util.setValue("total", data);
	
}
/**
 * A Bible has been picked. If there is anything to locate or search, then do it.
 */
function pick()
{
  // When the book changes, take what ever is in locate and get it.
  // If that doesn't work then try what ever is in search.
  locate() || search();
}

/**
 * Locate a passage.
 */
function locate()
{
  var book = getBook();
  var ref  = getPassage();
  if (book && ref)
  {
    JSword.getOSISString(book, ref, verseStart,verseLimit, loadDisplay);
	cardinality();
    return true;
  }
  return false;
}

function prev()
{
	verseStart=verseStart-verseLimit;
	if (verseStart<0){
		verseStart=0;
	}
  var book = getBook();
  var ref  = getPassage();
  if (book && ref)
  {
    JSword.getOSISString(book, ref, verseStart,verseLimit, loadDisplay);
    return true;
  }
  return false;
}
function next()
{
	verseStart=verseStart+verseLimit;
	if (verseStart>total){
		verseStart=total-verseLimit-1;
	}
  var book = getBook();
  var ref  = getPassage();
  if (book && ref)
  {
    JSword.getOSISString(book, ref, verseStart,verseLimit, loadDisplay);
    return true;
  }
  return false;
}
/**
 * getCardinality.
 */
function cardinality()
{
  var book = getBook();
  var ref  = getPassage();
  if (book && ref)
  {
    JSword.getCardinality(book, ref, displayTotal);
    return true;
  }
  return false;
}

/**
 * Perform a search
 */
function search()
{
  var book   = getBook();
  var search = getSearch();
  if (book && search)
  {
    // Get the reference for the search
    // and asynchrounously load it in to the locate box
    JSword.search(book, search, setPassage);
    return true;
  }
  return false;
}

/**
 * Get the search request.
 */
function getSearch()
{
  return dwr.util.getValue("searchRequest");
}

/**
 * Perform a search.
 */
function setSearch(query)
{
  // Whenever we stuff a value into search request
  dwr.util.setValue("searchRequest", query);
  // do the search
  search();
  // Allow this to be used in an anchor that ignores its href
  return false;
}

/**
 * Get the passage to locate.
 */
function getPassage()
{
  return dwr.util.getValue("passageRequest");
}

/**
 * Locate a passage
 */
function setPassage(ref)
{
  // whenever we stuff a value in locate
  // Note: search merely stuffs a value here.
  dwr.util.setValue("passageRequest", ref);
  // go get the content.
  locate();
  // Allow this to be used in an anchor that ignores its href
  return false;
}

/**
 * Get the selected book.
 */
function getBook()
{
  return dwr.util.getValue("books");
}

/**
 * Set the book to search or locate against
 */
function setBook(book)
{
  // When ever a book is set
  dwr.util.setValue("books", book);
  // See if there is something we can locate or search.
  pick();
  // Allow this to be used in an anchor that ignores its href
  return false;
}

function loadDiagnostic(data)
{
  var html = "";
  for (var i = 0; i < data.length; i++)
  {
    html += data[i] + ":";
  }
  DWRUtil.setValue("diagnostic", html);
}
