﻿# Repository sbc-treillis

## Context

Work started as a project at [TELECOM Nancy](http://telecomnancy.univ-lorraine.fr/) (Knowledge based system - SBC class)

It aims at checking the soundness and completeness of DBpedia ontologies (DBpedia categories, DBpedia Ontology and
YAGO) w.r.t. data. To do so, we classify DBpedia pages w.r.t. page properties in a concept lattice using FCA
(Formal Concept Analysis). Then, we define the annotation of a concept lattice to associate ontology classes to a
concept. Finally, implications are extracted from the lattice and compared with the axioms of the three considered
ontologies.

## Ontology files analyzed

* http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/ : english data of DBpedia
* http://wiki.dbpedia.org/services-resources/ontology : ontologies (some files are also available on previous link)
* http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/links/ : files of the YAGO ontology

## Execution

### Lattice generation

Creates a lattice based on a set of pages linked to the dbo:Person ontology class and having a death date.

Crawls objects from Virtuoso server (DBpedia information), computes the lattice and stores it in a file using
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

Extracts knowledge from lattice and compares it with the existing DBpedia ontologies, evaluating each proposal.

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
        * *PROPOSED_NEW*: relationship not existing inside DBpedia ontologies
    * *bottom* (string): URI of the bottom object of the proposed subsumption
    * *top* (string): URI of the top object of the proposed subsumption
    * *values* (object): values of the proposed subsumption according to available evaluation strategies. Object written
    with fields: `"strategy-name":computed-value`.
* *comparison-statistics*: file where statistics of comparison results will be written

### Comparison results statistics histograms

Generates histograms of values of knowledge comparison results. One histogram will be generated per ontology
(Categories, Ontology classes, YAGO classes), comparison result type and evaluation strategy.

If there are 0 values for an ontology, a type and a strategy, related histogram is not produced.

This script fetches comparison results from a MongoDB database. The latter has to be set up with the output file
of the lattice analysis program.

Can be executed with the following command:

```shell
python dbpediaresultsgraphs.py mongodb mongocollection output-prefix
```

* *mongodb*: name of the MongoDB database to use to fetch comparison results
* *mongocollection*: name of the MongoDB collection of documents where comparison results are stored
* *output-prefix*: prefix to be used for output files. Each output file will be named according to the following
   pattern: `output-prefix-class-type-strategy.png`

### Hierarchies statistics

Computes and saves statistics about DBpedia ontologies used to classify pages (categories, ontology classes and YAGO classes)

Can be executed with the following command:

```shell
java dbpediaanalyzer.main.HierarchiesStatistics output
```

* *output*: file used to store computed hierarchies statistics

If cycles are detected within an ontology, they will be stored inside a file named output-ontology-name-cycles.

### Data Info

This program can be used to display information or compute various results on hierarchy elements. It has the
following features:

* Display information about an ontology (parents and children)
* Find path between two ontology classes
* Display Lower Common Ancestor of two ontology classes
* Display distance from closest top level class of an ontology class

Can be executed with the following command:

```shell
java dbpediaanalyzer.main.DataInfo
```

### Lattice Statistics

It is possible to (re)compute statistics on a lattice that has previously been generated with the lattice generation
program. To execute this program, use the following command:

```shell
java dbpediaanalyzer.main.LatticeStatisticsComputation lattice output
```

* *lattice*: the lattice on which statistics will be computed (previously generated with lattice generation program)
* *output*: file used to store computed lattice statistics

### Data Set Statistics

It is possible to compute statistics on a data set without generating the associate lattice. This can be helpful
to recompute statistics while avoiding lattice generation (which takes a lot of time). To execute this program, use the
following command:

```shell
java dbpediaanalyzer.main.DataSetStatisticsComputation minimalDeathDate maximalDeathDate output
```

* *minimalDeathDate*: minimal death date for person's pages to be selected (YYYY-MM-DD, inclusive)
* *maximalDeathDate*: maximal death date for person's pages to be selected (YYYY-MM-DD, exclusive)
* *output*: file used to store data set statistics

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
* [Pymongo](https://docs.mongodb.org/getting-started/python/query/)

### Access to a SPARQL endpoint

To execute Java programs, an access to a SPARQL endpoint is needed. Its data base must be loaded with
all the files listed above. Its access can be configured in DBPediaAnalyzer/src/dbpediaanalyzer/io/ServerQuerier by
changing `SERVER_BASE_URL`

For our work, a Virtuoso server has been used. It is not available for outside queries.

### Access to a MongoDB database

To execute the Python script generating histograms on comparison results' values, a
MongoDB database must be set up and loaded with the output file of the lattice analysis program.
To do so, you can use the following command:

```shell
mongoimport --jsonArray --db dbName --collection collectionName --file outputFromLatticeAnalysis
```


## License

This program is under GNU GPL v3 (see LICENSE file for more info).

## Authors

* Soline Blanc
* Damien Flament
* Thomas Herbeth
* Pierre Monnin
* Sylvain Vissière-Guerinet
