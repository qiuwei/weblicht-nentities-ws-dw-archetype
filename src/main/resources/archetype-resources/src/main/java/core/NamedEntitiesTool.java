#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core;

import eu.clarin.weblicht.wlfxb.api.TextCorpusProcessor;
import eu.clarin.weblicht.wlfxb.api.TextCorpusProcessorException;
import eu.clarin.weblicht.wlfxb.tc.api.NamedEntitiesLayer;
import eu.clarin.weblicht.wlfxb.tc.api.TextCorpus;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.tc.api.TokensLayer;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerTag;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NamedEntitiesTool implements TextCorpusProcessor {

    private static final EnumSet<TextCorpusLayerTag> requiredLayers = EnumSet.of(TextCorpusLayerTag.TOKENS);
    public static final String MODEL_NE_PATH = "/models/nelist.txt";
    private static final String LOC_INDICATOR = "nach";
    private final Map<String,String> nelist;
    private final Map<String,Double> neprob;


    // this global variable should have different values for each document,
    // i.e. it should not be shared among the clients; this makes the class
    // non thread-safe, imitating non thread-safe tool
    private Map<String,Double> docneprob;


    public NamedEntitiesTool()  throws TextCorpusProcessorException {

        nelist = new HashMap<String,String>();
        neprob = new HashMap<String,Double>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(MODEL_NE_PATH), "UTF-8"));
            String line;
            while((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    String[] splitted = line.split("${symbol_pound}");
                    nelist.put(splitted[0],splitted[1]);
                    neprob.put(splitted[0],Double.parseDouble(splitted[2]));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(NamedEntitiesTool.class.getName()).log(Level.SEVERE, null, ex);
            throw new TextCorpusProcessorException("Failed reading the model", ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(NamedEntitiesTool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    @Override
    public EnumSet<TextCorpusLayerTag> getRequiredLayers() {
        return requiredLayers;
    }

    // if your tool is non thread-safe, wrap non-tread-safe calls to it in a
    // synchronized method as shown in this example, or, alternatively (not shown
    // here) syncronize calls to its non-tread-safe methods
    @Override
    public synchronized void process(TextCorpus textCorpus) throws TextCorpusProcessorException {

        // create named entities layer, it is empty first; specify named entity types
        // CoNLL2002 (means that named entity tags PER/LOC/ORG/MISC will be used)
        NamedEntitiesLayer nesLayer = textCorpus.createNamedEntitiesLayer("CoNLL2002");
        // get layer(s) to be processed
        TokensLayer tokensLayer = textCorpus.getTokensLayer();
        createDocumentNeProbs(tokensLayer);
        for (int i = 0; i < tokensLayer.size(); i++) {
            Token token = tokensLayer.getToken(i);
            if (docneprob.containsKey(token.getString()) && docneprob.get(token.getString()) >= 0.6) {
                // create and add named entity to the named entities layer
                nesLayer.addEntity(nelist.get(token.getString()), token);
            }
        }

    }

    private void createDocumentNeProbs(TokensLayer tokensLayer) {
        docneprob = new HashMap<String,Double>();
        for (int i = 0; i < tokensLayer.size(); i++) {
            Token token = tokensLayer.getToken(i);
            if (nelist.containsKey(token.getString())) {
                if (docneprob.get(token.getString()) == null) {
                    docneprob.put(token.getString(), neprob.get(token.getString()));
                }
                if (nelist.get(token.getString()).equals("LOC") && i > 0
                        && tokensLayer.getToken(i-1).getString().equals(LOC_INDICATOR)) {
                    docneprob.put(token.getString(), docneprob.get(token.getString()) + 0.1);
                }

            }
        }
    }

}

