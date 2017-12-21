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

type clientFunctions interface {
	install()
	update()
	delete()
}

type client struct {
	org string
	space string
	token string
	argv []string
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

func checkArrLen(arr []string) bool {
	return len(arr) < 3
}

func manageApmCalls(c client, args ...string) (string, error) {
	var uri = []string {"https://", c.app.Routes[0].Host, ".", c.app.Routes[0].Domain.Name, fmt.Sprintf("/%s/%s/%s/%s", c.org, c.space, args[0], args[1])}
	resp, err := httpCall(args[2], strings.Join(uri, ""), c.token)
	if err != nil {
		return "", errors.New(fmt.Sprintf("%s", err))
	}
	return resp, nil
}

func (c *client) install() (string, error) {
	//this array length checking could be in the Run::switch
	//that way I could directly pass on the app name to these functions
	//and omit the argv field in 'client'
	if checkArrLen(c.argv) {
		return "", errors.New(fmt.Sprintf("Incorrect usage.\nCorrect usage: cf apm install <app_name>")
	}
	resp, err := manageApmCalls(c, "install", c.argv[2], "POST") 
	if err != nil {
		return "", errors.New(fmt.Sprintf("%s", err))
	}
	return resp, nil
}

func (c *client) update() (string, error) {
	if checkArrLen(c.argv) {
		return "", errors.New(fmt.Sprintf("Incorrect usage.\nCorrect usage: cf apm update <app_name>")
	}
	resp, err := manageApmCalls(c, "update", c.argv[2], "PUT") 
	if err != nil {
		return "", errors.New(fmt.Sprintf("%s", err))
	}
	return resp, nil
}

func (c *client) delete() (string, error) {
	if checkArrLen(c.argv) {
		return "", errors.New(fmt.Sprintf("Incorrect usage.\nCorrect usage: cf apm delete <app_name>")
	}
	resp, err := manageApmCalls(c, "delete", c.argv[2], "DELETE") 
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
		client := client {org: org.Name, space: space.Name, token: token, argv: args, app: app}
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
				resp, err := client.install()
				if err != nil {
					fmt.Println(err)
					return
				}
				fmt.Println(resp);

			case args[1] == "update":
				resp, err := client.update()
				if err != nil {
					fmt.Println(err)
					return
				}
				fmt.Println(resp);

			case args[1] == "delete":
				resp, err := client.delete()
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
