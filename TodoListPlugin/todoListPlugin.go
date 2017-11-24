package main

import (
	"fmt"
	"net/http"
	"code.cloudfoundry.org/cli/plugin"
	"io/ioutil"
	"errors"
	"strings"
)

type TodoListPlugin struct {}

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

func (c *TodoListPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	if args[0] == "todo-list" {
		app, err := cliConnection.GetApp("todoList")
		if err != nil {
			fmt.Println(err)
			return
		}
	
		var uri = []string{"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, "/TodoList"}
		resp, err := getResponse(strings.Join(uri, ""))
		if err != nil {
			fmt.Println(err)
			return
		}
		fmt.Println(resp)
	}
}

func (c *TodoListPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata {
		Name: "todoListPlugin",
		Version: plugin.VersionType {
			Major: 1,
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
				Name:     "todo-list",
				HelpText: "TodoList Application Plugin",
				UsageDetails: plugin.Usage{
					Usage: "cf todo-list",
				},
			},
		},
	}
}

func main() {
	plugin.Start(new(TodoListPlugin))
}
