package dbpediaanalyzer.serverlink;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dbpediaanalyzer.io.SparqlResponse;

/**
 * Java requests to HTTP Server. Automatic crawling of server answer and auto-binding to given model class. Server answer must be JSON 
 */
@Deprecated
public final class JSONReader {
    /**
     * Server address
     */
    private static final String SERVER_CRAN = "http://localhost:8890/sparql/?default-graph-uri=http%3A%2F%2Fdbpedia.org&format=application%2Fsparql-results%2Bjson&debug=on&query=";
    private static ObjectMapper mapper = new ObjectMapper();

    public JSONReader() {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Crawls all children and parents of DBPedia hierarchy type (category, ontology, yago class)
     * 
     * @param request SPARQL request to execute
     * @return children and parents list
     * @throws IOException when request is incorrect
     */
    public static List<ChildAndParent> getChildrenAndParents(String request) throws IOException {
        SparqlResponse response = new SparqlResponse();

        try {
            response = mapper.readValue(new URL(SERVER_CRAN + request), SparqlResponse.class);
        } catch (JsonParseException | JsonMappingException | MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}