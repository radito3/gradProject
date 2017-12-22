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

type client struct {
	org string
	space string
	token string
	app plugin.GetAppModel
}

func httpCall(method string, uri string, token string) (string, error) {
	client := &http.Client{}
	req, err := http.NewRequest(method, uri, nil)
	if err != nil {
		return "", errors.New(fmt.Sprintf("Service error on Creating Request: %s", err))
	}
	req.Header.Set("access-token", token)
	resp, err := client.Do(req)
	if err != nil {
		return "", errors.New(fmt.Sprintf("Service error on Executing Request: %s", err))
	}
	defer resp.Body.Close()

	bs, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", errors.New(fmt.Sprintf("Sevice error on Reading Response Body: %s", err))
	}
	return string(bs), nil
}

func (c *client) manageApmCalls(args ...string) (string, error) {
	apmCall = args[0], httpVerb = args[1], appName = args[2]
	uri = fmt.Sprintf("https://%s.%s/%s/%s/%s/%s", c.app.Routes[0].Host, c.app.Routes[0].Domain.Name, c.org, c.space, apmCall, appName)
	resp, err := httpCall(httpVerb, uri, c.token)
	if err != nil {
		return "", errors.New(fmt.Sprintf("%s", err))
	}
	return resp, nil
}

func (c *ApmPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	if args[0] == "apm" {
		app, err := cliConnection.GetApp("apmServices") // may change the name getting
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
		client := client {org: org.Name, space: space.Name, token: token, app: app}
		switch {
			case args[1] == "list-apps":
				var uri = []string {"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, "/list_repo_apps"}
				resp, err := httpCall("GET", strings.Join(uri, ""), token)
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
				resp, err := client.manageApmCalls("install", "POST", args[2])
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
				resp, err := client.manageApmCalls("update", "PUT", args[2])
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
				resp, err := client.manageApmCalls("delete", "DELETE", args[2])
				if err != nil {
					fmt.Println(err)
					return
				}
				fmt.Println(resp);

			default:
				if len(args) < 2 {
					fmt.Println("Incorrect ammount of arguments")
					return
				}
				fmt.Println("Incorrect command.\nCommands are install|update|delete <app_name>")
		}
	}
}

func (c *ApmPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata {
		Name: "apmPlugin",
		Version: plugin.VersionType {
			Major: 5,
			Minor: 1,
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
				HelpText: "Application Package Manager",
				UsageDetails: plugin.Usage {
					Usage: "cf apm <command> [<app_name>]",
				},
			},
		},
	}
}

func main() {
	plugin.Start(new(ApmPlugin))
}
