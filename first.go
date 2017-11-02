package main

import (
	"fmt"
	"net/http"
	"code.cloudfoundry.org/cli/plugin"
	"io/ioutil"
)

type BasicPlugin struct{}

// Run must be implemented by any plugin because it is part of the
// plugin interface defined by the core CLI.
func (c *BasicPlugin) Run(cliConnection plugin.CliConnection, args []string) {
	if len(args) < 2 {
		fmt.Println("Incorrect usage.\nUsage: cf get-response <uri>")
	}
	// commands for the package manager will be handled here
	if args[0] == "get-response" {
		// url will be changed
		resp, err := http.Get(args[1])

		if err != nil {
			fmt.Println("Sevice error: ", err)
			return
		}

		defer resp.Body.Close()

		bs, readErr := ioutil.ReadAll(resp.Body)

		if readErr != nil {
			fmt.Println("Sevice error: ", readErr)
			return
		}

		fmt.Println("Service One Response: ", string(bs))
	}
}

// GetMetadata must be implemented as part of the plugin interface
// defined by the core CLI.
func (c *BasicPlugin) GetMetadata() plugin.PluginMetadata {
	return plugin.PluginMetadata{
		Name: "getResponsePlugin",
		Version: plugin.VersionType{
			Major: 1,
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
				Name:     "get-response",
				HelpText: "plugin for get request to rest client",
				UsageDetails: plugin.Usage{
					Usage: "cf get-response",
				},
			},
		},
	}
}

// Main will be used to initialize the plugin process,
// as well as any dependencies you might require for your plugin.
func main() {
	// Any initialization for your plugin can be handled here
	//
	// Note: to run the plugin.Start method, we pass in a pointer to the struct
	// implementing the interface defined at "code.cloudfoundry.org/cli/plugin/plugin.go"
	//
	// Note: The plugin's main() method is invoked at install time to collect
	// metadata. The plugin will exit 0 and the Run([]string) method will not be
	// invoked.
	plugin.Start(new(BasicPlugin))
	// Plugin code should be written in the Run([]string) method,
	// ensuring the plugin environment is bootstrapped.
}
