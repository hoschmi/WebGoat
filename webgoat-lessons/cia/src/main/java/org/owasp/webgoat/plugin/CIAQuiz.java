package org.owasp.webgoat.plugin;

import org.owasp.webgoat.assignments.AssignmentEndpoint;
import org.owasp.webgoat.assignments.AssignmentPath;
import org.owasp.webgoat.assignments.AttackResult;
import org.owasp.webgoat.session.DatabaseUtilities;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @TODO: Get JSON from file not from hardcoded string
 * add a question: 1. Append new question to JSON string
 * 2. add right solution to solutions array
 * 3. add Request param with name of question to method head
 */
@AssignmentPath("/cia/quiz")
public class CIAQuiz extends AssignmentEndpoint {

    String[] solutions = {"Solution 3", "Solution 1", "Solution 4", "Solution 2"};

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AttackResult completed(@RequestParam String[] question_0_solution, @RequestParam String[] question_1_solution, @RequestParam String[] question_2_solution, @RequestParam String[] question_3_solution) throws IOException {
        boolean correct = false;
        String[][] solutionsInput = {question_0_solution, question_1_solution, question_2_solution, question_3_solution};
        int counter = 0;
        for(String[] sa : solutionsInput) {
            for(String s : sa) {
                if(sa.length == 1 && s.contains(this.solutions[counter])) {
                    correct = true;
                    break;
                } else {
                    correct = false;
                    continue;
                }
            }
            if(!correct) break;
            counter++;
        }
        if(correct) {
            return trackProgress(success().build());
        } else {
            return trackProgress(failed().build());
        }
    }

}
