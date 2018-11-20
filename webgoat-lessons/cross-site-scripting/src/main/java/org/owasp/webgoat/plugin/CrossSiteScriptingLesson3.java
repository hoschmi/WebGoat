package org.owasp.webgoat.plugin.mitigation;

import org.owasp.webgoat.assignments.AssignmentEndpoint;
import org.owasp.webgoat.assignments.AssignmentHints;
import org.owasp.webgoat.assignments.AssignmentPath;
import org.owasp.webgoat.assignments.AttackResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AssignmentPath("CrossSiteScripting/attack3")
//@AssignmentHints(value = {"SqlStringInjectionHint-mitigation-10b-1", "SqlStringInjectionHint-mitigation-10b-2", "SqlStringInjectionHint-mitigation-10b-3"})
public class CrossSiteScriptingLesson3 extends AssignmentEndpoint {

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AttackResult completed(@RequestParam String editor) {
        String regex1 = "(?=.*PreparedStatement.*)(?=.*setString.*)(?=.*\\=\\?.*|.*\\=\\s\\?.*)";
        editor = editor.replaceAll("\\<.*?>","");
        boolean hasImportant = this.check_text(regex1, editor.replace("\n", "").replace("\r", ""));
        List<Diagnostic> hasCompiled = this.compileFromString(editor);
        String errors = "";
        if(hasImportant && hasCompiled.size() < 1) {
            return trackProgress(success().build());
        } else if(hasCompiled.size() > 1) {
            for(Diagnostic d : hasCompiled) {
                errors += d.getMessage(null) + "\n";
            }
        }
        return trackProgress(failed().output(errors).build());
    }

    private List<Diagnostic> compileFromString(String s) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector diagnosticsCollector = new DiagnosticCollector();
        StandardJavaFileManager fileManager  = compiler.getStandardFileManager(diagnosticsCollector, null, null);
        JavaFileObject javaObjectFromString = getJavaFileContentsAsString(s);
        Iterable fileObjects = Arrays.asList(javaObjectFromString);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);
        Boolean result = task.call();
        List<Diagnostic> diagnostics = diagnosticsCollector.getDiagnostics();
        return diagnostics;
    }

    private SimpleJavaFileObject getJavaFileContentsAsString(String s){
        StringBuilder javaFileContents = new StringBuilder("import java.sql.*; public class TestClass { public static void main(String[] args) {" + s + "}}");
        JavaObjectFromString javaFileObject = null;
        try{
            javaFileObject = new JavaObjectFromString("TestClass.java", javaFileContents.toString());
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return javaFileObject;
    }

    class JavaObjectFromString extends SimpleJavaFileObject {
        private String contents = null;
        public JavaObjectFromString(String className, String contents) throws Exception{
            super(new URI(className), Kind.SOURCE);
            this.contents = contents;
        }
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return contents;
        }
    }

    private boolean check_text(String regex, String text) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if(m.find())
            return true;
        else return false;
    }
}
