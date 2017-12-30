package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"

	"code.cloudfoundry.org/cli/plugin"
)

type TestAppPlugin struct{}

func getResponse(uri string) (string, error) {
	resp, err := http.Get(uri)
	if err != nil {
		return "", fmt.Errorf("Sevice error on Get: %s", err)
	}
	defer resp.Body.Close()

	bs, readErr := ioutil.ReadAll(resp.Body)
	if readErr != nil {
		return "", fmt.Errorf("Sevice error on Read: %s", readErr)
	}
	return string(bs), nil
}

func (c *TestAppPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	if args[0] == "test-app" {
		app, err := cliConnection.GetApp("test-app")
		if err != nil {
			fmt.Println(err)
			return
		}

		uri := []string{"https://", app.Routes[0].Host, ".", app.Routes[0].Domain.Name, "/Test"}
		resp, err := getResponse(strings.Join(uri, ""))
		if err != nil {
			fmt.Println(err)
			return
		}
		fmt.Println("Service One Response: ", resp)
	}
}

func (c *TestAppPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata{
		Name: "testAppPlugin",
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
				Name:     "test-app",
				HelpText: "Test Application Plugin",
				UsageDetails: plugin.Usage{
					Usage: "cf test-app",
				},
			},
		},
	}
}

func main() {
	plugin.Start(new(TestAppPlugin))
}
