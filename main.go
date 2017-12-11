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

func httpResponse(method string, uri string, token string) (string, error) {
	client := &http.Client{}
	req, err := http.NewRequest(method, uri, nil)
	if err != nil {
		return "", errors.New(fmt.Sprintf("%s", err))
	}
	req.Header.Set("access-token", token)
	resp, err := client.Do(req)
	if err != nil {
		return "", errors.New(fmt.Sprintf("%s", err))
	}
	defer resp.Body.Close()

	bs, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", errors.New(fmt.Sprintf("Sevice error on Read: %s", err))
	}
	return string(bs), nil
}

func (c *ApmPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	if args[0] == "apm" {
		app, err := cliConnection.GetApp("apmServices")
		if err != nil {
			fmt.Println(err)
			return
		}
		org, err := cliConnection.GetCurrentOrg()
		if err != nil {
			fmt.Println(err)
			return
		}
		space, err := cliConnection.GetCurrentSpace()
		if err != nil {
			fmt.Println(err)
			return
		}
		token, err := cliConnection.AccessToken()
		if err != nil {
			fmt.Println(err)
			return
		}
		switch {
			case args[1] == "list-apps":
				var uri = []string {"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, "/list_repo_apps"}
				resp, err := httpResponse("GET", strings.Join(uri, ""), token)
				if err != nil {
					fmt.Println(err)
					return
				}
				fmt.Println(fmt.Sprintf("Available apps:\n%s", resp))

			case args[1] == "install":
				if len(args) < 3 {
					fmt.Println("Incorrect usage.\nCorrect usage: cf apm install <app_name>")
					return
				}
				var uri = []string {"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, fmt.Sprintf("/%s/%s/install/%s", org.Name, space.Name, args[2])}
				resp, err := httpResponse("POST", strings.Join(uri, ""), token)
				if err != nil {
					fmt.Println(err)
					return
				}
				fmt.Println(resp);

			case args[1] == "update":
				if len(args) < 3 {
					fmt.Println("Incorrect usage.\nCorrect usage: cf apm update <app_name>")
					return
				}
				var uri = []string {"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, fmt.Sprintf("/%s/%s/update/%s", org.Name, space.Name, args[2])}
				resp, err := httpResponse("PUT", strings.Join(uri, ""), token)
				if err != nil {
					fmt.Println(err)
					return
				}
				fmt.Println(resp);

			case args[1] == "delete":
				if len(args) < 3 {
					fmt.Println("Incorrect usage.\nCorrect usage: cf apm delete <app_name>")
					return
				}
				var uri = []string {"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, fmt.Sprintf("/%s/%s/delete/%s", org.Name, space.Name, args[2])}
				resp, err := httpResponse("DELETE", strings.Join(uri, ""), token)
				if err != nil {
					fmt.Println(err)
					return
				}
				fmt.Println(resp);

			default:
				fmt.Println("Incorrect command.\nCommands are install/update/delete <app_name>")
		}
	}
}

func (c *ApmPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata {
		Name: "apmPlugin",
		Version: plugin.VersionType {
			Major: 4,
			Minor: 2,
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
