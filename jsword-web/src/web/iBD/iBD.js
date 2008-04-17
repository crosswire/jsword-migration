var stylesheet = "iBD.xsl";
var verseLimit = 100;

/**
 * Prepare the page for use
 */
function init()
{
    DWRUtil.useLoadingMessage();
    JSword.getInstalledBooks("bookCategory=Bible", loadBooks);
    window.onresize = ibdResize;
}

/*
 * Resize the height of the display area.
 * I tried pixels but it does not work for IE.
 */
function ibdResize()
{
    var top = $("searchBox");
    var bottom = $("display");
    var offset = top.offsetTop + top.offsetHeight + 1;
    var windowHeight = document.body.clientHeight;
    var newHeight = windowHeight - offset;
    bottom.style.height = (newHeight/windowHeight)*100 + "%";
}

/**
 * Load the list of known Books
 */
function loadBooks(data)
{
  DWRUtil.removeAllOptions("books");
  DWRUtil.addOptions("books", data, "0", "1");
}

/**
 * Called when view data has been fetched
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

/**
 * A Bible has been picked. If there is anything to locate or search, then do it.
 */
function pick()
{
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
    JSword.getOSISString(book, ref, verseLimit, loadDisplay);
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
  return DWRUtil.getValue("searchRequest");
}

/**
 * Perform a search.
 */
function setSearch(query)
{
  DWRUtil.setValue("searchRequest", query);
  search();
}

/**
 * Get the passage to locate.
 */
function getPassage()
{
  return DWRUtil.getValue("passageRequest");
}

/**
 * Locate a passage
 */
function setPassage(ref)
{
  DWRUtil.setValue("passageRequest", ref);
  locate();
}

/**
 * Get the selected book.
 */
function getBook()
{
  return DWRUtil.getValue("books");
}

/**
 * Set the book to search or locate against
 */
function setBook(book)
{
  DWRUtil.setValue("books", book);
  pick();
}
