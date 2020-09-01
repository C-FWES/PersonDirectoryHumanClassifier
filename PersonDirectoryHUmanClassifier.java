import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PersonDirectoryPageClassfier {


    public static void main(String[] args) {
        String filepath = "/Users/wesleyou/ICS3U/src/main/java/logLikelyhood.csv";
        List<String> text = new ArrayList<>();
        text.add("i at of");
        System.out.println(isDirectoryPage(text, filepath));
    }

    private static boolean isDirectoryPage(List<String> text, String filePath) {
        boolean result = true;
        List<String> paragraphs = processText(text);
        Map<String, Double> logLikelyhoods = readLogLikelyhoods(filePath);
        result = predict(logLikelyhoods, paragraphs);
        return result;
    }


    private static List<String> processText(List<String> text) {
        //combine list <string> to one big string
        String page = "";
        for (String s : text) {
            page = page + " " + s;

        }
        page = page.toLowerCase();
        //to lower case
        List<String> punctuations = Arrays.asList(",", ".", "\"", "'", "@");
        String[] paragraphTokens = page.split(" ");
        ; // split page use the example below
        List<String> tokens = new ArrayList<>();
        //replace each of them with " " + pun +" "
        for (int i = 0; i < paragraphTokens.length; i++) {
            String token = paragraphTokens[i];
            if (token.trim().length() == 0) {
                continue;
            }
            boolean splitPunctuation = false;
            for (String punctuation : punctuations) {
                if (token.endsWith(punctuation)) {
                    tokens.add(token.substring(0, token.length() - 1));
                    tokens.add(token.substring(token.length() - 1, token.length()));
                    splitPunctuation = true;
                    break;
                }
            }
            if (!splitPunctuation) {
                tokens.add(token);
            }

        }
        return tokens;

    }

    private static Map<String, Double> readLogLikelyhoods(String filePath) {
        Map<String, Double> result = new HashMap<>();
        String fileContent = " ";
        try {
            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] paragraphTokens = line.split(String.valueOf('\t'));
                result.put(paragraphTokens[0], Double.valueOf(paragraphTokens[1]));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result;
    }

    private static boolean predict(Map<String, Double> logLikelyhoods, List<String> paragraphs) {
        boolean result = true;
        double accumulateValue = 0.0;

        for (String word : paragraphs) {
            if (StringUtils.isNumeric(word)) {
                accumulateValue = accumulateValue + logLikelyhoods.get("$number$");
            }
            if (!logLikelyhoods.containsKey(word)) {
                accumulateValue = accumulateValue + logLikelyhoods.get("$ignore$");
            } else {
                accumulateValue = accumulateValue + logLikelyhoods.get(word);
            }
        }
        
        if (accumulateValue < 0) {
            result = false;
        }
        return result;
    }


}
