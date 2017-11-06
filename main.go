package main

import (
	"fmt"
	"net/http"
	"code.cloudfoundry.org/cli/plugin"
	"io/ioutil"
	"os"
	"errors"
)

type ApmPlugin struct{}

func getResponse(args []string) string, error {
	//os.Getenv() is also an option for uri
	resp, err := http.Get(args[2])
	if err != nil {
		return "", errors.New("Sevice error on Get: ", err)
	}
	defer resp.Body.Close()

	bs, readErr := ioutil.ReadAll(resp.Body)
	if readErr != nil {
		return "", errors.New("Sevice error on Read: ", readErr)
	}
	return string(bs)
}

func (c *ApmPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	if args[0] != "apm" {
		fmt.Println("Incorrect usage.\nCorrect usage: cf apm <command> [<app_name>]")
		os.Exit(1)
	}
	switch {
		case args[1] == "test-app":
			resp, err := getResponse(args)
			if err != nil {
				fmt.Println(err)
			} else {
				fmt.Println("Service One Response: ", resp)
			}

		case args[1] == "list-apps":
			//list available apps from repo

		case args[1] == "install":
			//check if such an app exists
			fmt.Println("Application %s not found.", args[2])

		case args[1] == "update":
			//check if such an app exists
			fmt.Println("Application %s not found.", args[2])

		case args[1] == "delete":
			//check if such an app exists
			fmt.Println("Application %s not found.", args[2])

		default:
			fmt.Println("Incorrect command.\nCommands are install/update/delete <app_name>")
	}
	
}

func (c *ApmPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata {
		Name: "apmPlugin",
		Version: plugin.VersionType {
			Major: 2,
			Minor: 0,
			Build: 0,
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
					Usage: "cf apm <command> [<app_name>]",
				},
			},
		},
	}
}

func main() {
	plugin.Start(new(ApmPlugin))
}
