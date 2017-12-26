package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"

	"code.cloudfoundry.org/cli/plugin"
	"code.cloudfoundry.org/cli/plugin/models"
)

type ApmPlugin struct{}

type client struct {
	org   string
	space string
	token string
	app   plugin_models.GetAppModel
}

func httpCall(method string, uri string, token string) (string, error) {
	client := &http.Client{}
	req, err := http.NewRequest(method, uri, nil)
	if err != nil {
		return "", fmt.Errorf("%s", err)
	}
	req.Header.Set("access-token", token)
	resp, err := client.Do(req)
	if err != nil {
		return "", fmt.Errorf("%s", err)
	}
	defer resp.Body.Close()

	bs, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", fmt.Errorf("Sevice error on Read: %s", err)
	}
	return string(bs), nil
}

func (c *client) manageApmCalls(args ...string) (string, error) {
	var apmCall = args[0]
	var httpVerb = args[1]
	var appName = args[2]
	var uri = fmt.Sprintf("https://%s.%s/%s/%s/%s/%s", c.app.Routes[0].Host, c.app.Routes[0].Domain.Name, c.org, c.space, apmCall, appName)
	resp, err := httpCall(httpVerb, uri, c.token)
	if err != nil {
		return "", fmt.Errorf("%s", err)
	}
	return resp, nil
}

func getClient(con plugin.CliConnection) (*client, error) {
	app, err := cliConnection.GetApp("apmServices") // may change the name getting
	if err != nil {
		return nil, fmt.Errorf("%s", err)
	}
	org, err := cliConnection.GetCurrentOrg()
	if err != nil {
		return nil, fmt.Errorf("%s", err)
	}
	space, err := cliConnection.GetCurrentSpace()
	if err != nil {
		return nil, fmt.Errorf("%s", err)
	}
	token, err := cliConnection.AccessToken()
	if err != nil {
		return nil, fmt.Errorf("%s", err)
	}
	c := &client{org: org.Name, space: space.Name, token: token, app: app}
	return c, nil
}

func (c *ApmPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	cl, err := getClient(cliConnection)
	if err != nil {
		fmt.Println(err)
		return
	}
	client := *cl

	if args[0] == "list-apps" {
		var uri = []string{"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, "/list_repo_apps"}
		resp, err := httpCall("GET", strings.Join(uri, ""), token)
		if err != nil {
			fmt.Println(err)
			return
		}
		fmt.Println(fmt.Sprintf("Available apps:\n%s", resp))
	}

	if args[0] == "install" {
		if len(args) < 2 {
			fmt.Println("Incorrect usage.\nCorrect usage: cf install <app_name>")
			return
		}
		resp, err := client.manageApmCalls("install", "POST", args[1])
		if err != nil {
			fmt.Println(err)
			return
		}
		fmt.Println(resp)
	}

	if args[0] == "update" {
		if len(args) < 2 {
			fmt.Println("Incorrect usage.\nCorrect usage: cf update <app_name>")
			return
		}
		resp, err := client.manageApmCalls("update", "PUT", args[1])
		if err != nil {
			fmt.Println(err)
			return
		}
		fmt.Println(resp)
	}

	if args[0] == "remove" {
		if len(args) < 2 {
			fmt.Println("Incorrect usage.\nCorrect usage: cf remove <app_name>")
			return
		}
		resp, err := client.manageApmCalls("delete", "DELETE", args[1])
		if err != nil {
			fmt.Println(err)
			return
		}
		fmt.Println(resp)
	}
}

func (c *ApmPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata{
		Name: "apmPlugin",
		Version: plugin.VersionType{
			Major: 6,
			Minor: 1,
			Build: 1,
		},
		MinCliVersion: plugin.VersionType{
			Major: 6,
			Minor: 7,
			Build: 0,
		},
		Commands: []plugin.Command{
			{
				Name:     "install",
				HelpText: "Command for installing apps",
				UsageDetails: plugin.Usage{
					Usage: "cf install <app_name>",
				},
			},
			{
				Name:     "update",
				HelpText: "Command for updating apps",
				UsageDetails: plugin.Usage{
					Usage: "cf update <app_name>",
				},
			},
			{
				Name:     "remove",
				HelpText: "Command for deleting apps",
				UsageDetails: plugin.Usage{
					Usage: "cf remove <app_name>",
				},
			},
			{
				Name:     "list-apps",
				HelpText: "Command for listing repo apps",
				UsageDetails: plugin.Usage{
					Usage: "cf list-apps",
				},
			},
		},
	}
}

func main() {
	plugin.Start(new(ApmPlugin))
}
