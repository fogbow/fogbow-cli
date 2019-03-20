package cloud.fogbow.cli;

import cloud.fogbow.cli.constants.CliCommonParameters;
import cloud.fogbow.cli.constants.Documentation;
import com.beust.jcommander.Parameter;

public abstract class BaseCommand {
    @Parameter(names = CliCommonParameters.URL_COMMAND_KEY, description = Documentation.CommonParameters.URL, required = true)
    protected String url = null;
}
