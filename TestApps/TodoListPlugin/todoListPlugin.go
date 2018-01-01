package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"

	"code.cloudfoundry.org/cli/plugin"
)

//TodoListPlugin ...
type TodoListPlugin struct{}

func getResponse(uri string) (string, error) {
	resp, err := http.Get(uri)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	bs, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}
	return string(bs), nil
}

//Run ...
func (c *TodoListPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	if args[0] == "todo-list" {
		app, err := cliConnection.GetApp("todo-list-elsys")
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

//GetMetadata ...
func (c *TodoListPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata{
		Name: "todoListPlugin",
		Version: plugin.VersionType{
			Major: 1,
			Minor: 0,
			Build: 0,
		},
		MinCliVersion: plugin.VersionType{
			Major: 6,
			Minor: 7,
			Build: 0,
		},
		Commands: []plugin.Command{
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
