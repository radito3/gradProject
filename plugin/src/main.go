package main

import (
	"fmt"
	"io/ioutil"
	"net/http"

	"code.cloudfoundry.org/cli/plugin"
	"code.cloudfoundry.org/cli/plugin/models"
)

// ApmPlugin is the struct implementing the interface defined by the core CLI.
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
		return "", err
	}
	req.Header.Set("access-token", token)
	resp, err := client.Do(req)
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

func (c *client) manageApmCalls(apmCall string, httpVerb string, appName string,
	query ...string) (string, error) {

	uri := "https://" + c.app.Routes[0].Host + "." + c.app.Routes[0].Domain.Name + "/" +
		c.org + "/" + c.space + "/" + apmCall + "/" + appName

	resp, err := httpCall(httpVerb, uri, c.token)
	if err != nil {
		return "", err
	}
	return resp, nil
}

func getClient(con plugin.CliConnection) (*client, error) {
	app, err := con.GetApp("apmServices")
	if err != nil {
		return nil, err
	}
	org, err := con.GetCurrentOrg()
	if err != nil {
		return nil, err
	}
	space, err := con.GetCurrentSpace()
	if err != nil {
		return nil, err
	}
	token, err := con.AccessToken()
	if err != nil {
		return nil, err
	}
	c := client{org: org.Name, space: space.Name, token: token, app: app}
	return &c, nil
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

	if args[0] == "list-apps" {
		uri := "https://" + client.app.Routes[0].Host + "." + client.app.Routes[0].Domain.Name +
			"/list_repo_apps"
		resp, err := httpCall("GET", uri, client.token)
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

// GetMetadata returns a PluginMetadata struct.
func (c *ApmPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata{
		Name: "apmPlugin",
		Version: plugin.VersionType{
			Major: 6,
			Minor: 3,
			Build: 3,
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
				Name:     "list-apps",
				Alias:    "li",
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
