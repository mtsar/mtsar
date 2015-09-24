/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.cli;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import mtsar.MechanicalTsarVersion;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.ProcessDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.dropwizard.MechanicalTsarApplication;
import mtsar.dropwizard.MechanicalTsarConfiguration;
import net.sourceforge.argparse4j.inf.Namespace;

import javax.script.*;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * The code is partially based on the REPL implementation available on https://gist.github.com/aadnk/5507053.
 */
public class ConsoleCommand extends EnvironmentCommand<MechanicalTsarConfiguration> {
    private final MechanicalTsarApplication application;
    private boolean running = true;

    public ConsoleCommand(Application<MechanicalTsarConfiguration> application) {
        super(application, "js", "run a JavaScript console");
        this.application = (MechanicalTsarApplication) application;
    }

    protected void run(Environment environment, Namespace namespace, MechanicalTsarConfiguration configuration) {
        final ScriptEngine engine = getScriptEngine();
        final Scanner input = new Scanner(System.in);
        final PrintStream output = System.out;
        final StringBuilder builder = new StringBuilder();

        for (int lines = 0; running; ) {
            System.out.print("# ");
            System.out.flush();
            try {
                builder.append(input.nextLine());
            } catch (NoSuchElementException e) {
                running = false;
            }

            if (running && evaluate(engine, output, builder.toString(), ++lines)) {
                lines = 0;
                builder.setLength(0);
            }
        }

        input.close();
    }

    private boolean evaluate(ScriptEngine engine, PrintStream out, String statements, int lines) {
        try {
            out.println("> " + engine.eval(statements));
            out.flush();
            return true;
        } catch (ScriptException e) {
            if (e.getLineNumber() != lines) {
                e.printStackTrace(out);
                return true;
            }
        }
        return false;
    }

    private ScriptEngine getScriptEngine() {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

        bindings.put("version", application.getLocator().getService(MechanicalTsarVersion.class));
        bindings.put("processes", application.getProcesses());
        bindings.put("processDAO", application.getLocator().getService(ProcessDAO.class));
        bindings.put("workerDAO", application.getLocator().getService(WorkerDAO.class));
        bindings.put("taskDAO", application.getLocator().getService(TaskDAO.class));
        bindings.put("answerDAO", application.getLocator().getService(AnswerDAO.class));

        return engine;
    }
}
