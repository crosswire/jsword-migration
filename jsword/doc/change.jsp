<%@ page %>

<html>
<head>
  <title>JSword - Changes</title>
  <meta name="keywords" content="bible, free, software, christian, study, gpl, bible software,">
  <meta http-equiv="keywords" content="bible, free, software, christian, study, gpl, bible software,">
  <meta name="distribution" content="global">
  <link rel="stylesheet" href="sword.css" type="text/css">
</head>

<body>

<%@ include file="header.jsp" %>


            <h2>Change History</h2>

            <p>The following is the broad outline of the version of JSword and Project-B 
              (the old name for JSword).</p>
            <ul>
              <li><strong>Version 0.90</strong> - JSWORD - Merging with JSword</li>
              <li><strong>Version 0.80</strong> - SWORD1 - Troy just did a call for 
                Java Bible programs from which to make jsword. There have been many 
                updates - Logging API updates, a new GUI framework and work on the 
                WordNet thesaurus.</li>

              <li><strong>Version 0.75</strong> - THE MAP - New Mapper application, 
                lots of tuning, tweaks and fixes.</li>

              <li><strong>Version 0.73</strong> - THE MUTE - Lots of tweaks to speed 
                things up, history bug fixed, new PassageTally functionality, SerBible 
                is now the preferred format.</li>

              <li><strong>Version 0.72</strong> - THE SHOUT 2 - New lib project, many 
                fixes to servlets and Project.java, Source syntax colouring, Office 
                interface revamp.</li>

              <li><strong>Version 0.71</strong> - THE UNANNOUNCED - 2nd major upload, 
                minor bug fixes on version 0.7</li>

              <li><strong>Version 0.7</strong> - THE ANNOUNCED - Lots of testing, 
                tidying up, and organizing. The Book package has been majorly re-worked, 
                and many of the packages have been sorted out.</li>

              <li><strong>Version 0.6</strong> - THE SERVANT - New servlet interface. 
                Lots of work on the config i/f lots of tidying up. New web pages, 
                mail list and so on.</li>

              <li><strong>Version 0.5</strong> - THE CABBAGE - Lots of changes - RawBible, 
                Search.bestMatch(), SelfTestBase, Some I18N, Config. Some GUI work.</li>

              <li><strong>Version 0.46</strong> - THE FOREIGN APPLET - Quick release 
                as v0.45 with I18N for the Germans.</li>

              <li><strong>Version 0.45</strong> - THE APPLET - Quick release to get 
                together an applet for demo to Sword people.</li>

              <li><strong>Version 0.4</strong> - THE MIX - Merged passage and dictionary. 
                The dictionary package was shrinking, and being simplified, and the 
                classes Passage and Strongs considered core. TallyBoard renamed PassageTally 
                and made to implement Passage. Outstanding work - Events and Editable 
                book names/I18N.</li>

              <li><strong>Version 0.3</strong> - THE SHOUT - The search system wanted 
                TallyBoard, and I revised Passage being a Collection at the same time. 
                The only remaining functionality requests are Sorting out Events, 
                and I18N. Some work on Version. Search - I'm beginning to like this 
                design. I just added ( ) in a few hours without any major changes 
                to the engine at all. I need to tighten up the docs and sort out GrammarParamWord.</li>

              <li><strong>Version 0.2</strong> - THE PLATFORM - Passage - I need to 
                spend some time on the other bits of the system. The TODO list for 
                this is getting more and more streched. What is here is good though. 
                Search - Been through about 100 different search Engine designs, and 
                I'm still not happy. However a working engine is better than none 
                at all.</li>

              <li><strong>Version 0.1</strong> - THE TODDLER - Proof of concept. Quickly 
                hacked-up GUI. Passage - About 80% of the stuff I plan for this library 
                is complete. There are bugs. The most notable of which is that Jude 
                2 does not exist - you must use Jude&nbsp;1:2, and so on. Included 
                is full JavaDoc and a fairly exhaustive SelfTest module. Search - 
                very hacked up and needs re-writing.</li>

            </ul>

<%@ include file="footer.jsp" %>

</body>
</html>
