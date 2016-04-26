﻿# Repository sbc-treillis

## Context

Work started as a project at [TELECOM Nancy](http://telecomnancy.univ-lorraine.fr/) (Knowledge based system - SBC class)

It aims at mining DBPedia data by comparing data-based classification with existing classifications
hierarchies (DBPedia categories, ontology classes and yago classes)

## Ontology files analyzed

* http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/ : english data of DBPedia (warning: big files)
* http://wiki.dbpedia.org/services-resources/ontology : ontologies (some files are also available on previous link)
* http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/links/ : files of the Yago hierarchy

## Execution

### Lattice creation

Creates a lattice based on a set of pages linked to the dbo:Person ontology class and having a death date.

Crawls objects from Virtuoso server (DBPedia information), computes the lattice and stores it in a file using
JSON format. Statistics are calculated on data set and lattice and saved in files.

Can be executed with the following command:

```shell
java dbpediaanalyzer.main.LatticeGeneration minimalDeathDate maximalDeathDate output statistics-dataset statistics-lattice
```

* *minimalDeathDate*: minimal death date for person's pages to be selected (YYYY-MM-DD, inclusive)
* *maximalDeathDate*: maximal death date for person's pages to be selected (YYYY-MM-DD, exclusive)
* *output*: JSON output file for lattice
* *statistics-dataset*: file used to store data set statistics
* *statistics-lattice*: file used to store lattice statistics

### Lattice analysis

Extracts knowledge from lattice and compares it with the existing DBPedia hierarchies, evaluating each proposal.

Can be executed with the following command:

```shell
java dbpediaanalyzer.main.LatticeAnalysis lattice output comparison-statistics
```

* *lattice*: JSON file corresponding to the lattice to analyze
* *output*: file where comparison results will be written. JSON format is used with a global array containing one JSON object per result.
    These JSON objects contain the following fields:
    * *type* (string): type of the relationship. Possible values:
        * *CONFIRMED_DIRECT*: direct relationship already existing
        * *PROPOSED_INFERRED_TO_DIRECT*: relationship already existing as inferred, proposed to be changed to direct subsumption
        * *PROPOSED_NEW*: relationship not existing inside DBPedia hierarchies
    * *bottom* (string): URI of the bottom object of the proposed subsumption
    * *top* (string): URI of the top object of the proposed subsumption
    * *values* (object): values of the proposed subsumption according to available evaluation strategies. Object written
    with fields: `"strategy-name":computed-value`.
* *comparison-statistics*: file where statistics of comparison results will be written

### Comparison results statistics histograms

Generates histograms of values of knowledge comparison results. One histogram will be generated per ontology class
(Categories, Ontology classes, Yago classes), comparison result type and evaluation strategy.
If there are 0 values for a class, a type and a strategy, related histogram is not produced

Can be executed with the following command:

```shell
python dbpediaresultsgraphs.py comparison-results output-prefix
```

* *comparison-results*: JSON file produced by LatticeAnalysis program to be analyzed
* *output-prefix*: Prefix to be used for output files. Each output file will be named according to the following
   pattern: `output-prefix-class-type-strategy.png`


### Hierarchies statistics

Computes and saves statistics about DBPedia hierarchies used to classify pages (categories, ontology classes and yago classes)

Can be executed with the following command:

```shell
java dbpediaanalyzer.main.HierarchiesStatistics output
```

* *output*: file used to store computed hierarchies statistics

## Dependencies

### Java dependencies

* [Colibri](https://code.google.com/archive/p/colibri-java/) (lattice library) - GNU GPL v2
    * Colibri source code is integrated as Java classes inside the project and is also available as a ZIP file inside
    DBPediaAnalyzer/lib
    * Colibri has a dependency on Java GetOPT, available as a JAR file inside /DBpediaAnalyzer/lib to be linked
* [Jackson](http://wiki.fasterxml.com/JacksonHome) (JSON parsing) - Apache License 2.0
    * Available as JAR files inside /DBPediaAnalyzer/lib/ to be linked

### Python dependencies

* [Matplotlib](http://matplotlib.org/) - [license](http://matplotlib.org/users/license.html)

### Access to a SPARQL endpoint

To execute Java programs, an access to a SPARQL endpoint is needed. Its data base must be loaded with
all the files listed above. Its access can be configured in DBPediaAnalyzer/src/dbpediaanalyzer/io/ServerQuerier by
changing `SERVER_BASE_URL`

For our work, a Virtuoso server has been used. It is not available for outside queries.

## Authors

* Soline Blanc
* Damien Flament
* Thomas Herbeth
* Pierre Monnin
* Sylvain Vissière-Guerinet
