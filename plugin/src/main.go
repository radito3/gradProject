package main

import (
	"fmt"

	"code.cloudfoundry.org/cli/plugin"
)

// ApmPlugin is the struct implementing the interface defined by the core CLI.
type ApmPlugin struct{}

type command interface {
	execute([]string) error
}

func getCommands(c client) map[string]command {
	return map[string]command{
		"install":        &install{c},
		"update":         &update{c},
		"remove":         &remove{c},
		"list-repo":      &listRepoApps{c},
		"list-installed": &listInstalledApps{c},
	}
}

// Run is the entry point when the core CLI is invoking a command defined
// by a plugin. The first parameter, plugin.CliConnection, is a struct that can
// be used to invoke cli commands. The second paramter, args, is a slice of
// strings. args[0] will be the name of the command, and will be followed by
// any additional arguments a cli user typed in.
func (c *ApmPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	cl, err := getClient(cliConnection)
	if err != nil {
		fmt.Println(err)
		return
	}
	client := *cl

	commands := getCommands(client)

	for key, command := range commands {
		if args[0] == key {
			if err := command.execute(args); err != nil {
				fmt.Println(err)
				return
			}
		}
	}
}

// GetMetadata returns a PluginMetadata struct.
func (c *ApmPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata{
		Name: "apmPlugin",
		Version: plugin.VersionType{
			Major: 7,
			Minor: 1,
			Build: 0,
		},
		MinCliVersion: plugin.VersionType{
			Major: 6,
			Minor: 7,
			Build: 0,
		},
		Commands: []plugin.Command{
			{
				Name:     "install",
				Alias:    "i",
				HelpText: "Command for installing apps",
				UsageDetails: plugin.Usage{
					Usage: "cf install <app_name>",
				},
			},
			{
				Name:     "update",
				Alias:    "u",
				HelpText: "Command for updating apps",
				UsageDetails: plugin.Usage{
					Usage: "cf update <app_name>",
				},
			},
			{
				Name:     "remove",
				Alias:    "rm",
				HelpText: "Command for deleting apps",
				UsageDetails: plugin.Usage{
					Usage: "cf remove <app_name>",
				},
			},
			{
				Name:     "list-repo",
				Alias:    "lsr",
				HelpText: "Command for listing repo apps",
				UsageDetails: plugin.Usage{
					Usage: "cf list-repo",
				},
			},
			{
				Name:     "list-installed",
				Alias:    "lsi",
				HelpText: "Command for listing installed apps",
				UsageDetails: plugin.Usage{
					Usage: "cf list-installed",
				},
			},
		},
	}
}

func main() {
	plugin.Start(new(ApmPlugin))
}
