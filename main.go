package main

import (
	"fmt"
	"net/http"
	"code.cloudfoundry.org/cli/plugin"
	"io/ioutil"
	"errors"
	"strings"
)

type ApmPlugin struct {}

func getResponse(uri string) (string, error) {
	resp, err := http.Get(uri)
	if err != nil {
		return "", errors.New(fmt.Sprintf("Sevice error on Get: %s", err))
	}
	defer resp.Body.Close()

	bs, readErr := ioutil.ReadAll(resp.Body)
	if readErr != nil {
		return "", errors.New(fmt.Sprintf("Sevice error on Read: %s", readErr))
	}
	return string(bs), nil
}

func (c *ApmPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	if args[0] != "apm" {
		fmt.Println("Incorrect usage.\nCorrect usage: cf apm <command> [<app_name>]")
		return
	}
	switch {
		case args[1] == "test-app":
			app, err := cliConnection.GetApp("restServiceOne")
			if err != nil {
				fmt.Println(err)
				return
			}
			var uri = []string{"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, "/rest/Test"}
			resp, err := getResponse(strings.Join(uri, ""))
			if err != nil {
				fmt.Println(err)
				return
			}
			fmt.Println("Service One Response: ", resp)

		case args[1] == "list-apps":
			/*list, err := cliConnection.GetApps()
			if err != nil {
				fmt.Println(err)
			} else {
				for i := 0; i < len(list); i++ {
					fmt.Println(list[i].Name)
				}
			}*/
			app, err := cliConnection.GetApp("staticApp")
			if err != nil {
				fmt.Println(err)
				return
			}
			var uri = []string{"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, "/list/"}
			resp, err := getResponse(strings.Join(uri, ""))
			if err != nil {
				fmt.Println(err)
				return
			}
			fmt.Println("Available apps: ", resp)

		case args[1] == "install":
			//check if such an app exists
			fmt.Sprintln("Application %s not found", args[2])

		case args[1] == "update":
			//check if such an app exists
			fmt.Sprintln("Application %s not found", args[2])

		case args[1] == "delete":
			//check if such an app exists
			fmt.Sprintln("Application %s not found", args[2])

		default:
			fmt.Println("Incorrect command.\nCommands are install/update/delete <app_name>")
	}
	
}

func (c *ApmPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata {
		Name: "apmPlugin",
		Version: plugin.VersionType {
			Major: 2,
			Minor: 3,
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
