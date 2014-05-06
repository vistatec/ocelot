Ocelot
======
Ocelot is an open source workbench for working with XLIFF files in a post-editing and language review environment. It implements the localization quality issue and provenance data categories from the proposed ITS 2.0 standard and utilizes the Okapi Framework for parsing XLIFF files.

##Downloading and Running##
A pre-compiled version of Ocelot is available from the [Okapi downloads
page](https://bintray.com/okapi/Distribution/Ocelot).  Ocelot is distributed
as a JAR file that will run on any platform that has Java 1.6 or later
installed. There are also native executables for the Windows and Mac platforms.

##Reporting Issues##
To report a bug or request an enhancement, please create an account 
in the [Ocelot JIRA](https://ocelot.atlassian.net/browse/OC) and track all issues there.

##Requirements##
* Java 1.6
* Maven (for compiling only)

##Build##
Ocelot uses Maven, so all dependencies should be resolved upon build (mvn package). The output jar file will located in the created target folder.

Features
========
## Filter Rules ##
Filter rules are used to selectively display segments that match enabled rules. The configuration is loaded from the <code>rules.properties</code> file, which is located under <code>&lt;home directory&gt;/.ocelot</code> (or can be created from scratch if it does not exist).

    Filter rules are in the format:
    <label>.<dataCategoryType> = <value>
    
    <label> is a String that groups together filter conditions and display rules.
    <dataCategoryType> and <value> can be the following values:
    
    <dataCategoryType> = locQualityIssueType
    <value> = terminology |  mistranslation |  omission | 
              untranslated |  addition |  duplication |  inconsistency | 
              grammar |  legal |  register |  locale-specific-content | 
              locale-violation |  style |  characters |  misspelling | 
              typographical |  formatting |  inconsistent-entities |  numbers | 
              markup |  pattern-problem |  whitespace |  internationalization | 
              length |  uncategorized |  other
    
    <dataCategoryType> = locQualityIssueSeverity
    <value> = <min> - <max>
    where <min> is inclusive (<=) and max is exclusive (>). The possible range of values for locQualityIssueSeverity is 0.0 - 100.0
    
    <dataCategoryType> = locQualityIssueComment
    <value> = String
    
    <dataCategoryType> = org | person | tool | revOrg | revPerson | revTool | provRef
    <value> = String

    <dataCategoryType> = mtConfidence
    <value> = <min>-<max>
    where <min> is inclusive (<=) and max is exclusive (>). The possible range of values for mtConfidence is 0.0 - 1.0
    
## Filter Display Rules ##
Filter display rules control how to display segments that match a particular filter rule. They control how the flag on a segment in the segment view appears. Possible UI types are the border color, background color, and the text. They are specified in the same file as the filter rules (the rules.properties file).

    Display rules are in the format:
      <label>.flag.<type> = <value>
    
    <label> refers to the filter rule the display rules are associated with.
    <value> and <type> can be the following values:
    
    <type> = fill | border
    <value> = #[0-9]{6} - hex representation of RGB
    
    <type> = text
    <value> = String - basically anything, though a single character is recommended.
    
## Quick Add Rules ##

Quick add rules allow you to map the keys CTRL-0 to CTRL-9 (on the Mac, ⌘-0 to
⌘-9) to LQI metadata, allowing you to easily flag common segment issues.  They
are specified in the same file as the filter rules (the rules.properties file).

    Quick add rules are in the format:
      <label>.quickAdd.<type> = <value>
    
    <label> refers to the filter rule the quick add rules are associated with.
    Valid <type> and <value> values are essentially the same as the LQI filter rules, plus the addition of the "hotkey" type:
    
    <type> = locQualityIssueType
    <value> = terminology |  mistranslation |  omission |
              untranslated |  addition |  duplication |  inconsistency |
              grammar |  legal |  register |  locale-specific-content |
              locale-violation |  style |  characters |  misspelling |
              typographical |  formatting |  inconsistent-entities |  numbers |
              markup |  pattern-problem |  whitespace |  internationalization |
              length |  uncategorized |  other
    
    <type> = locQualityIssueSeverity
    <value> = 0.0-100.0
    
    <type> = locQualityIssueComment
    <value> = String
    
    <type> = hotkey
    <value> = 0-9

## XLIFF State Qualifier Display Rules ##
These rules control how the state qualifier attribute of a target in XLIFF will be indicated in the segment view. Each rule controls the background color of the segment number of a segment that has the specified state-qualifier attribute. They are specified in the same file as the filter rules (the rules.properties file).

    The possible state qualifier rules are:
      id-match = #[0-9]{6} - hex representation of RGB
      exact-match = #[0-9]{6} - hex representation of RGB
      fuzzy-match = #[0-9]{6} - hex representation of RGB
      mt-suggestion = #[0-9]{6} - hex representation of RGB

    All other XLIFF state qualifier types will be ignored.

Plugins
=======
The default plugin directory is located under &lt;home directory&gt;/.ocelot/plugins. Plugins must be compiled into a jar file and placed under this directory, or a directory chosen by the plugin manager. All plugins must implement the methods defined in the Plugin.java interface, which are used to display the plugin in the plugin manager UI. There are currently two types of plugins Ocelot supports: ITS and Segment plugins.

ITS plugins are plugins that are interested in the ITS metadata of segments within an open file. The required methods for ITS plugins are given in the ITSPlugin.java interface, so every ITS plugin must implement this interface. There are currently 2 methods, which determine how to handle a particular ITS metadata for a segment. ITS metadata is only sent to plugins when the export data button in the ITS Plugin UI is pushed, which will export the LQI and Provenance data of the currently open file to the enabled ITS plugins.

Segment plugins are plugins that are interested in events such as entering and exiting the edit mode of a target, or the opening and saving of an XLIFF file. The required methods for Segment plugins are given in the SegmentPlugin.java interface, so every Segment plugin must implement this interface. Events are automatically triggered if the plugin is enabled on the segment plugin manager UI.
