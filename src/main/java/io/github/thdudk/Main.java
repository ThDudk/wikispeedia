package io.github.thdudk;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import io.github.thdudk.builders.GraphBuilder;
import io.github.thdudk.builders.GraphBuilderImpl;
import io.github.thdudk.graphs.unweighted.Graph;
import io.github.thdudk.iterators.BreadthFirstIterator;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            // credit
            System.out.println("""
                Credit for the dataset:\s
                (1) Robert West and Jure Leskovec:
                     Human Wayfinding in Information Networks.
                     21st International World Wide Web Conference (WWW), 2012.
                (2) Robert West, Joelle Pineau, and Doina Precup:
                     Wikispeedia: An Online Game for Inferring Semantic Distances between Concepts.
                     21st International Joint Conference on Artificial Intelligence (IJCAI), 2009.""");
            System.out.println(); // newline for readability

            // load the wikispeedia graph
            Graph<String> articleGraph = loadGraph();

            // get start and end articles
            String start;
            while(true) {
                System.out.print("Enter starting article: ");
                Optional<String> result = readValidArticle(reader, articleGraph.getNodes());
                if(result.isPresent()) {
                    start = result.get();
                    break;
                }
            }
            System.out.println();

            String end;
            while(true) {
                System.out.print("Enter goal article: ");
                Optional<String> result = readValidArticle(reader, articleGraph.getNodes());
                if(result.isPresent()) {
                    end = result.get();
                    break;
                }
            }
            System.out.println();

            // print the shortest path between the articles
            BreadthFirstIterator<String> iterator = new BreadthFirstIterator<>(articleGraph, start);
            Map<String, String> BFSTree = new HashMap<>();
            List<String> path = new ArrayList<>();

            while(iterator.hasNext()) {
                String next = iterator.next();
                BFSTree.put(next, iterator.getParent());
                if(next.equals(end)) {
                    // backtrack to get the path
                    String curr = next;
                    path.add(next);

                    while(!curr.equals(start)) {
                        curr = BFSTree.get(curr);
                        path.add(curr);
                    }

                    path = path.reversed(); // because the path is from end to start
                    break;
                }
            }

            if(path.isEmpty()) {
                System.out.println("There is no path from " + start + " to " + end);
            }

            System.out.println("The shortest path from " + start + " to " + end + " is: (length " + path.size() + ")");
            for(String node : path) {
                System.out.println("- " + node);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Graph<String> loadGraph() throws IOException {
        GraphBuilder<String> builder = new GraphBuilderImpl<>();
        TsvParser parser = new TsvParser(new TsvParserSettings());

        // process all article names as nodes
        try (Reader inputReader = new InputStreamReader(new FileInputStream("src/main/resources/articles.tsv"), StandardCharsets.UTF_8)) {
            List<String> articleNames = parser.parseAll(inputReader)
                .stream().map(row -> decodeArticle(row[0]))
                .toList();

            for(String article : articleNames) {
                builder.addNode(article);
            }
        } catch (IOException e) {
            throw new IOException("Failed to parse article names in wikispeedia graph", e);
        }

        // process all hyperlinks as edges
        try (Reader inputReader = new InputStreamReader(new FileInputStream("src/main/resources/links.tsv"), StandardCharsets.UTF_8)) {
            List<String[]> hyperlinks = parser.parseAll(inputReader);

            for(String[] link : hyperlinks) {
                String source = decodeArticle(link[0]);
                String target = decodeArticle(link[1]);
                builder.addDirEdge(source, target);
            }
        } catch (IOException e) {
            throw new IOException("Failed to parse hyperlinks in wikispeedia graph", e);
        }

        return builder.build();
    }
    public static String decodeArticle(String raw) {
        return URLDecoder.decode(raw, StandardCharsets.UTF_8).replaceAll("_", " ");
    }

    public static Optional<String> readValidArticle(BufferedReader reader, Collection<String> validArticles) throws IOException {
        String article = reader.readLine();

        // class to get the distance between two strings
        LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();

        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(a -> levenshtein.apply(a, article)));

        queue.addAll(validArticles);

        List<String> topFive = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            topFive.add(queue.remove());
        }

        // return the top option if the search matches perfectly
        if(levenshtein.apply(topFive.getFirst(), article) == 0) {
            return Optional.of(topFive.getFirst());
        }

        // ask the user to pick from related articles
        System.out.println("Could not find exact article: " + article + ". Other Options:");
        System.out.println("1) " + topFive.get(0));
        System.out.println("2) " + topFive.get(1));
        System.out.println("3) " + topFive.get(2));
        System.out.println("4) " + topFive.get(3));
        System.out.println("5) " + topFive.get(4));
        System.out.println("6) None of the above");

        int ans;
        while(true) {
            System.out.print("article: ");
            try {
                ans = Integer.parseInt(reader.readLine());
                if(ans < 1 || ans > 6) continue; // ensure ans is in range
            } catch (NumberFormatException _) {
                continue;
            }
            break;
        }

        if(ans == 6) return Optional.empty();

        return Optional.of(topFive.get(ans - 1));
    }
}