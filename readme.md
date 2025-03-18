# Wikispeedia

Small test project that finds the shortest hyperlink path between any two wikipedia articles. 

The project was created as a functional test project for my small graph library [OneGraphLib](https://github.com/ThDudk/OneGraphLib). 

### The project works as described below:
1. The wikispeedia dataset (credit below) is parsed as a graph object.
2. The user is prompted for the starting and ending articles
   * If a given article is not found, the program will ask the user if they meant to choose one of 5 similar articles (found using the Levenshtein distance algorithm)
3. The program will compute the shortest path from the start article to the end using the BFS algorithm

## Dependancies
* [OneGraphLib](https://github.com/ThDudk/OneGraphLib)
* [Apache Commons Text](https://commons.apache.org/proper/commons-text/) - For it's Levenshtein distance algorithm implementation
* [univocity-parsers](https://github.com/uniVocity/univocity-parsers) - For parsing the tsv dataset

## Credit 

The project was made possible thanks to the [Wikispeedia navigation paths dataset](https://snap.stanford.edu/data/wikispeedia.html).  

> Robert West and Jure Leskovec:
    [Human Wayfinding in Information Networks](http://infolab.stanford.edu/~west1/pubs/West-Leskovec_WWW-12.pdf).
    21st International World Wide Web Conference (WWW), 2012.

> Robert West, Joelle Pineau, and Doina Precup:
    [Wikispeedia: An Online Game for Inferring Semantic Distances between Concepts](http://infolab.stanford.edu/~west1/pubs/West-Pineau-Precup_IJCAI-09.pdf).
    21st International Joint Conference on Artificial Intelligence (IJCAI), 2009.