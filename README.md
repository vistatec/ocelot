# Filter Rules #
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
    <value> = 0.0-100.0
    
    <dataCategoryType> = locQualityIssueComment
    <value> = String
    
    <dataCategoryType> = org | person | tool | revOrg | revPerson | revTool | provRef
    <value> = String

    <dataCategoryType> = mtConfidence
    <value> = 0.0-1.0
    
    # Display Rules #
    Display rules are in the format:
      <label>.flag.<type> = <value>
    
    <label> refers to the filter rule the display rules are associated with.
    <value> and <type> can be the following values:
    
    <type> = fill | border
    <value> = #[0-9]{6} - hex representation of RGB
    
    <type> = text
    <value> = String - basically anything, though a single character is recommended.
    
    # Quick Add Rules #
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
    *Hotkeys are limited to CTRL + 0-9

    # XLIFF State Qualifier Display Rules #
    The possible state qualifier rules are:
      id-match = #[0-9]{6} - hex representation of RGB
      exact-match = #[0-9]{6} - hex representation of RGB
      fuzzy-match = #[0-9]{6} - hex representation of RGB
      mt-suggestion = #[0-9]{6} - hex representation of RGB

    All other XLIFF state qualifier types will be ignored.

    # Plugins #
    The default plugin directory is located under <home directory>/.reviewersWorkbench/plugins. The VistaTECWebservice.jar plugin should be placed under that directory. Pushing the export data button will export the LQI and Provenance data of the currently open file to the checked plugins.
