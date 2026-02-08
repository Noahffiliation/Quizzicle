package com.quizzicle;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuizzicleVizzTest {
    private static final Path OUTPUT_PATH = Path.of("output.html");
    private static boolean originalExists;
    private static String originalContent;

    @BeforeAll
    static void captureOriginalOutput() throws Exception {
        originalExists = Files.exists(OUTPUT_PATH);
        if (originalExists) {
            originalContent = Files.readString(OUTPUT_PATH);
        }
    }

    @AfterEach
    void cleanupOutput() throws Exception {
        Files.deleteIfExists(OUTPUT_PATH);
    }

    @AfterAll
    static void restoreOriginalOutput() throws Exception {
        if (originalExists) {
            Files.writeString(OUTPUT_PATH, originalContent);
        } else {
            Files.deleteIfExists(OUTPUT_PATH);
        }
    }

    @Test
    void createsFlashCardsAndMatchingHtml() throws Exception {
        String input =
                """
                        config {
                          mode: student;
                          include_answer_key: true;
                        }
                        terms {
                          Term1: Alpha Beta;
                          Term2: Gamma;
                        }
                        flash_cards {
                          Term1, Term2;
                        }
                        matching {
                          Term1, Term2;
                        }
                        """;

        String html = runVisitor(input);

        assertTrue(html.contains("Flash Cards"));
        assertTrue(html.contains("Matching"));
        assertTrue(html.contains("Term1"));
        assertTrue(html.contains("Term2"));
        assertTrue(html.contains("Alpha Beta"));
        assertTrue(html.contains("answer_key"));
    }

    @Test
    void omitsMatchingSectionWhenNotRequested() throws Exception {
        String input =
                """
                        terms {
                          Term1: Alpha Beta;
                        }
                        flash_cards {
                          Term1;
                        }
                        """;

        String html = runVisitor(input);

        assertTrue(html.contains("Flash Cards"));
        assertFalse(html.contains("<h2>Matching</h2>"));
        assertFalse(html.contains("<button onclick=\"showHideAnswers()\" id=\"answer_btn\">"));
    }

    @Test
    void mainGeneratesOutputFromDefaultInput() throws Exception {
        Files.deleteIfExists(OUTPUT_PATH);

        com.quizzicle.Test.main(new String[0]);

        assertTrue(Files.exists(OUTPUT_PATH));
    }

    @Test
    void logsWhenOverwritingExistingOutput() throws Exception {
        Files.writeString(OUTPUT_PATH, "old content");
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(stdout, true, StandardCharsets.UTF_8));
        try {
            String input =
                    """
                            terms {
                              Term1: Alpha;
                            }
                            flash_cards {
                              Term1;
                            }
                            """;
            CharStream stream = CharStreams.fromString(input);
            QuizzicleLexer lexer = new QuizzicleLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            QuizzicleParser parser = new QuizzicleParser(tokens);
            ParseTree tree = parser.file();

            QuizzicleVizz visitor = new QuizzicleVizz();
            visitor.visit(tree);
        } finally {
            System.setOut(originalOut);
        }

        String outputLog = stdout.toString(StandardCharsets.UTF_8);
        assertTrue(outputLog.contains("output.html already exists; overwriting."));
        assertFalse(Files.readString(OUTPUT_PATH).contains("old content"));
    }

    private String runVisitor(String input) throws Exception {
        Files.deleteIfExists(OUTPUT_PATH);
        CharStream stream = CharStreams.fromString(input);
        QuizzicleLexer lexer = new QuizzicleLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QuizzicleParser parser = new QuizzicleParser(tokens);
        ParseTree tree = parser.file();

        QuizzicleVizz visitor = new QuizzicleVizz();
        visitor.visit(tree);

        assertTrue(Files.exists(OUTPUT_PATH));
        return Files.readString(OUTPUT_PATH);
    }
}
