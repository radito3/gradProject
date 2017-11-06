package main

import (
	"fmt"
	"net/http"
	"code.cloudfoundry.org/cli/plugin"
	"io/ioutil"
	//os
)

type ApmPlugin struct{}

func (c *ApmPlugin) Run(cliConnection plugin.CliConnection, args []string) {

	if args[0] != "apm" {
		fmt.Println("Incorrect usage.\nCorrect usage: cf apm <command>")
		os.Exit(1)
	}

	switch {
		case args[1] == "test-app":
			//uri could be from os.Getenv()
			resp, err := http.Get(args[2])
		
			if err != nil {
				fmt.Println("Sevice error on Get: ", err)
				return
			}
		
			defer resp.Body.Close()
		
			bs, readErr := ioutil.ReadAll(resp.Body)
		
			if readErr != nil {
				fmt.Println("Sevice error on Read: ", readErr)
				return
			}
		
			fmt.Println("Service One Response: ", string(bs))

		case args[1] == "list-apps":
			//list available apps from repo

		case args[1] == "install":
			//check for correct app name
			fmt.Println("Incorrect application name.")

		case args[1] == "update":
			//check for correct app name
			fmt.Println("Incorrect application name.")

		case args[1] == "delete":
			//check for correct app name
			fmt.Println("Incorrect application name.")

		default:
			fmt.Println("Incorrect command.\nCommands are install/update/delete <app_name>")
	}
	
}

func (c *ApmPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata {
		Name: "apmPlugin",
		Version: plugin.VersionType {
			Major: 1,
			Minor: 1,
			Build: 2,
		},
		MinCliVersion: plugin.VersionType {
			Major: 6,
			Minor: 7,
			Build: 0,
		},
		Commands: []plugin.Command {
			{
				Name:     "apm",
				HelpText: "application package manager",
				UsageDetails: plugin.Usage{
					Usage: "cf apm <command>",
				},
			},
		},
	}
}

func main() {
	plugin.Start(new(ApmPlugin))
}
