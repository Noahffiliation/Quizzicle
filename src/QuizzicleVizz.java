import org.antlr.v4.runtime.tree.ParseTree;
import java.io.*;
import java.util.*;

public class QuizzicleVizz extends QuizzicleBaseVisitor<Void> {
    private Map<String, String> dictionary = new HashMap<String,String>();
    private ArrayList<String> fc_terms = new ArrayList<String>();
    private ArrayList<String> m_terms = new ArrayList<String>();

    @Override public Void visitFile(QuizzicleParser.FileContext ctx) {
        visitChildren(ctx);
        Collections.shuffle(fc_terms); Collections.shuffle(m_terms);
        ArrayList<String> definitions = new ArrayList<String>();
        for (int i = 0; i < m_terms.size(); i++) {
            definitions.add(dictionary.get(m_terms.get(i)));
        }
        Collections.shuffle(definitions);

        File file = new File("output.html");

        String style = "<style>html,body{background-color: #171717;}table{margin-left:auto; margin-right:auto;}input[type=\"text\"],textarea{background-color:#202020;color:red;font-weight:bold;}h1,h2,td{color: red}li{padding-right:40px;}.flashcards{width:100%;height:100%;overflow:hidden;display:flex;justify-content: center;align-items: center;z-index:-5;}.container {z-index: 1;cursor:pointer;position:relative;background-color: #ececec;width:450px;height: 300px;display:flex;justify-content: center;align-items: center;text-align: center;border: 20px outset #ececec;margin: 10px;}.card {cursor:pointer;display:flex;align-items:center;position:relative;background:transparent;width: 95%;height: 95%;z-index:0;visibility:hidden;position:absolute;/*background-color: rgb(220,220,220);*/}.active {visibility:visible; z-index:5}.text {width:100%;word-break:break-all;font-family: Arial;font-weight: bold;font-size: 16pt;}* {transition: all 1s ease-in-out;}.container.ans,.container.ans .card{transform:rotateY(180deg)}</style>";
        String script = "<script> function flip_card(element, term, definition) { element.classList.toggle(\"ans\"); var p = element.getElementsByClassName(\"text\")[0]; setTimeout(function() { p.innerText = element.classList.contains(\"ans\") ? definition : term; }, 500); } function showHideAnswers() {var div = document.getElementById(\"answer_key\");var btn=document.getElementById(\"answer_btn\");if(div.style.visibility == \"hidden\") {div.style.visibility=\"visible\";div.style.color=\"green\";btn.innerHTML=\"Hide answers\";} else {div.style.color=\"#171717\";div.style.visibility=\"hidden\";btn.innerHTML=\"Show answers\";}}  </script>";

        String html_flash_cards = "";
        for (int i = 0; i < fc_terms.size(); i++) {
            String term = fc_terms.get(i);
            String definition = dictionary.get(term);

            if (i % 5 == 0 && i != 0){
                html_flash_cards += "\n</div>\n";
            }

            if(i % 5 == 0){ // new flash card row every 5 cards
                html_flash_cards += "<div class='flashcards'>\n";
            }

            html_flash_cards += "<div class='container' onclick='flip_card(this,\""+ term +"\",\""+ definition +"\")'> <div class='card active'> <p class='text'>" + term + "</p> </div></div>";
        }

        String flash_cards_h2 = "";
        if (!html_flash_cards.equals("")){
            flash_cards_h2 = "<h2>Flash Cards</h2>";
        }

        String matching = "";
        String matching_button = "";
        String answer_key = "";

        int j;
        for (int i = 0; i < m_terms.size(); i++) {
            String term = m_terms.get(i);
            String definition = definitions.get(i);
            j = i + 1;
            answer_key += term + ": " + dictionary.get(term) + "<br>";
            matching += "<tr><td><ol><li value=" + j + ">" + term + "</li></ol></td><td><input type=\"text\" size=\"2\" maxlength=\"2\">   " + definition + "</td></tr>";
        }

        if (!matching.equals("")) {
            answer_key = "<div style=\"visibility:hidden;text-align:center;margin-top:30px;color:green;\" id=\"answer_key\">" + answer_key + "</div>";
            matching = "<h2>Matching</h2><table>" + matching + "</table>";
            matching_button = "<div style=\"text-align:center;margin-top:30px;\"><button onclick=\"showHideAnswers()\" id=\"answer_btn\">Show answers</button></div>";
        }

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    style + "\n" +
                    script + "\n" +
                    "<title>Quizzicle</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h1 style=\"text-align:center;\">Quizzicle</h1>\n" +
                    flash_cards_h2 + "\n" +
                    html_flash_cards +
                    "\n</div>\n" +
                    matching +
                    "\n" +
                    matching_button +
                    "\n" +
                    answer_key +
                    "\n" +
                    "</body>\n" +
                    "</html>");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override public Void visitConfig(QuizzicleParser.ConfigContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Void visitSettings(QuizzicleParser.SettingsContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Void visitTerms(QuizzicleParser.TermsContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Void visitItems(QuizzicleParser.ItemsContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Void visitItem(QuizzicleParser.ItemContext ctx) {
        int i = 0;
        String stringDef = "";
        while (ctx.children.get(2).getChildCount() > i){
            stringDef += ctx.children.get(2).getChild(i).getText() + " ";
            i++;
        }
        dictionary.put(ctx.children.get(0).getChild(0).getText(), stringDef);
        return visitChildren(ctx);
    }

    @Override public Void visitQuizzes(QuizzicleParser.QuizzesContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Void visitQuiz(QuizzicleParser.QuizContext ctx) {
        ParseTree key_phrase = ctx.children.get(2).getChild(0);

        if (ctx.children.get(0).getText().equals("flash_cards")){
            for (int i = 0 ; i < key_phrase.getChildCount() ; i++) {
                String term = key_phrase.getChild(i).getText();
                term = term.replace(",","");
                fc_terms.add(term);
                System.out.print("");
            }
        }

        if (ctx.children.get(0).getText().equals("matching")){
            for (int i = 0 ; i < key_phrase.getChildCount() ; i++) {
                String term = key_phrase.getChild(i).getText();
                term = term.replace(",","");
                m_terms.add(term);
            }
        }

        return visitChildren(ctx);
    }

    @Override public Void visitKey_phrases(QuizzicleParser.Key_phrasesContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Void visitKey_phrase(QuizzicleParser.Key_phraseContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Void visitDefinition(QuizzicleParser.DefinitionContext ctx) {
        return visitChildren(ctx);
    }

}
